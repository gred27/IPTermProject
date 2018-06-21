package com.github.groundred.iptermproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Context context;
    private static final String TAG = "SNMP CLIENT";

    private Button btnSendGetRequest;
    private Button btnSendSetRequest;
    private Button btnSendWalkRequest;

    private EditText etGetRequest;
    private EditText etSetRequest;
    private EditText etWalkRequest;

    private StringBuffer logResult = new StringBuffer();
    private TextView tvLog;
    private TextView tvWalkLog;

    private static final String address = "kuwiden.iptime.org";
    private static final String port = "11161";

    private static String OIDVALUE;
    private static final int SNMP_VERSION = CommunityMessage.version2c;

    private static String community = "public";
    private static String writeCommunity = "write";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        // Initialize UI
        btnSendGetRequest = (Button) findViewById(R.id.btn_snmpGet);
        btnSendSetRequest = (Button) findViewById(R.id.btn_snmpSet);
        btnSendWalkRequest = (Button) findViewById(R.id.btn_snmpWalk);

        etGetRequest = (EditText) findViewById(R.id.et_snmpGet);
        etSetRequest = (EditText) findViewById(R.id.et_snmpSet);

        tvLog = (TextView) findViewById(R.id.tv_getResult);
        tvWalkLog = (TextView) findViewById(R.id.tv_getWalkResult);

        // set onClick listener

        btnSendGetRequest.setOnClickListener((View v) -> {
                    MyAsynTask mAsynTask = new MyAsynTask();
                    mAsynTask.execute(String.valueOf(PDU.GET_REQUEST), etGetRequest.getText().toString(), community);
                }
        );

        btnSendSetRequest.setOnClickListener((View v) -> {
            MyAsynTask myAsynTask = new MyAsynTask();
            myAsynTask.execute(String.valueOf(PDU.SET_REQUEST), etGetRequest.getText().toString(), writeCommunity);
        });

        btnSendWalkRequest.setOnClickListener(v -> {
            MyAsynTask myAsynTask = new MyAsynTask();
            myAsynTask.execute(String.valueOf(PDU.GET_NEXT_REQUEST), "1.3.6.1.2.1.1.1.0", community);
        });
    }

    @SuppressLint("LongLogTag")
    private void sendSnmpRequest(int type, String cmd, String community) throws Exception {
        try {

            InetAddress serverAddr = InetAddress.getByName(address);
            int trapRcvPort = Integer.parseInt(port);

            PDU pdu = new PDU();
            if (type == PDU.SET_REQUEST) {
                Variable v = checkInputType(etSetRequest.getText().toString());
                pdu.addVariableBinding(new VariableBinding(new OID(cmd), v));
            } else if (type == PDU.GET_REQUEST) {
                pdu.addVariableBinding(new VariableBinding(new OID(cmd)));
            }

            pdu.setType(type);

            CommunityMessage communityMessage = new CommunityMessage(community, pdu);

            BEROutputStream berOutputStream = new BEROutputStream();
            communityMessage.makeSendPacket(berOutputStream);

            byte[] sendData = berOutputStream.getBuffer().array();

//            byte sendData[] = {(byte) 0x30, (byte) 0x2b, // Sequence type, Length from here: 98 byte.
//                    (byte) 0x02, (byte) 0x01, (byte) 0x01, // v2c
//                    (byte) 0x04, (byte) 0x06, (byte) 0x70, (byte) 0x75,
//                    (byte) 0x62, (byte) 0x6c, (byte) 0x69, (byte) 0x63,
//                    (byte) 0xa0, (byte) 0x1e, (byte) 0x02, (byte) 0x04,
//                    (byte) 0x0f, (byte) 0xb1, (byte) 0x41, (byte) 0x0a,
//                    (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x02,
//                    (byte) 0x01, (byte) 0x00, (byte) 0x30, (byte) 0x10,
//                    (byte) 0x30, (byte) 0x0e, (byte) 0x06, (byte) 0x0a,
//                    (byte) 0x2b, (byte) 0x06, (byte) 0x01, (byte) 0x02,
//                    (byte) 0x01, (byte) 0x02, (byte) 0x02, (byte) 0x01,
//                    (byte) 0x07, (byte) 0x01, (byte) 0x05, (byte) 0x00
//            };
            DatagramPacket dataPacket = new DatagramPacket(sendData, sendData.length, serverAddr, trapRcvPort);

            // Send trap.
            DatagramSocket dataSocket = new DatagramSocket();
            dataSocket.send(dataPacket);

            try {
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message, message.length);
                Log.i("UDP client: ", "about to wait to receive");

                dataSocket.setSoTimeout(10000);
                dataSocket.receive(packet);
                String text = new String(message, 0, packet.getLength());
                Log.d("Received text", text);

                String massage = new String(packet.getData());
                byte[] messageByte = packet.getData();
                ByteBuffer inputBuffer = ByteBuffer.wrap(messageByte);
                BERInputStream inputStream = new BERInputStream(inputBuffer);

                CommunityMessage responseMessage = new CommunityMessage();
                responseMessage.decodePacket(inputStream, messageByte);

                List<VariableBinding> vbs = responseMessage.getPdu().getVariableBindings();

                StringBuilder tmp = new StringBuilder();

                for (VariableBinding vb : vbs
                        ) {
                    tmp.append(vb.getOid().toString() + " " + vb.getVariable().toString() + "\n");
                }


                logResult.append(tmp.toString());
//                for (byte b : messageByte
//                        ) {
//
//                    tmp.append(Integer.toHexString(b)).append(" ");
//                }
//                tvLog.setText(tmp.toString());

                Log.d("message", massage);
                Log.d("byte", messageByte.toString());
                Log.d("byte", tmp.toString());

            } catch (IOException e) {
                Log.e(" UDP client has IOException", "error: ", e);
                dataSocket.close();
            }

            dataSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("LongLogTag")
    private void sendSnmpWalkRequest(int type, String cmd, String community) throws Exception {
        try {
            String vbType = null;
            InetAddress serverAddr = InetAddress.getByName(address);
            int trapRcvPort = Integer.parseInt(port);

            do {
                PDU pdu = new PDU();
                pdu.addVariableBinding(new VariableBinding(new OID(cmd)));
                pdu.setType(type);

                CommunityMessage communityMessage = new CommunityMessage(community, pdu);

                BEROutputStream berOutputStream = new BEROutputStream();
                communityMessage.makeSendPacket(berOutputStream);

                byte[] sendData = berOutputStream.getBuffer().array();
                DatagramPacket dataPacket = new DatagramPacket(sendData, sendData.length, serverAddr, trapRcvPort);

                // Send trap.
                DatagramSocket dataSocket = new DatagramSocket();
                dataSocket.send(dataPacket);

                try {
                    byte[] message = new byte[8000];
                    DatagramPacket packet = new DatagramPacket(message, message.length);
                    Log.i("UDP client: ", "about to wait to receive");

                    dataSocket.setSoTimeout(10000);
                    dataSocket.receive(packet);
                    String text = new String(message, 0, packet.getLength());
                    Log.d("Received text", text);

                    byte[] messageByte = packet.getData();
                    ByteBuffer inputBuffer = ByteBuffer.wrap(messageByte);
                    BERInputStream inputStream = new BERInputStream(inputBuffer);

                    CommunityMessage responseMessage = new CommunityMessage();
                    responseMessage.decodePacket(inputStream, messageByte);

                    List<VariableBinding> vbs = responseMessage.getPdu().getVariableBindings();

                    StringBuilder tmp = new StringBuilder();

                    for (VariableBinding vb : vbs
                            ) {
                        tmp.append("oid :" + vb.getOid().toString() + " " + vb.getVariable().toString() + "\n");
                    }


                    logResult.append(tmp.toString());

//                    tvWalkLog.setText(tmp);


//                    for (byte b : messageByte
//                            ) {
//
//                        tmp.append(Integer.toHexString(b)).append(" ");
//                    }

//                    tvLog.setText(tmp.toString());

                    Log.d("byte", messageByte.toString());
                    Log.d("result", tmp.toString());

                    cmd = vbs.get(0).getOid().toString();
                    vbType = vbs.get(0).getVariable().variableType;

                } catch (IOException e) {
                    Log.e(" UDP client has IOException", "error: ", e);
                    dataSocket.close();
                }

                dataSocket.close();


            } while (!vbType.equals("endOfMibView"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MyAsynTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                if (Integer.parseInt(strings[0]) == PDU.GET_NEXT_REQUEST) {
                    sendSnmpWalkRequest(Integer.parseInt(strings[0]), strings[1], strings[2]);
                } else {
                    sendSnmpRequest(Integer.parseInt(strings[0]), strings[1], strings[2]);
                }
            } catch (Exception e) {
                Log.d(TAG,
                        "Error sending snmp request - Error: " + e.getMessage());
            }
            return Integer.parseInt(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer aInt) {
            super.onPostExecute(aInt);
            if (aInt == PDU.GET_NEXT_REQUEST) {
                tvWalkLog.setText(logResult.toString().substring(0, 1000));
                try {
                    FileWriter fw = new FileWriter("snmpwalklog.txt", false);
                    fw.write(tvWalkLog.toString());
                    fw.flush();
                    fw.close();

                    logResult.setLength(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                tvLog.setText(logResult.toString());
                logResult.setLength(0);
            }
        }
    }


    public Variable checkInputType(String input) {
        Pattern pInt = Pattern.compile("(^[0-9]*$)");
        Pattern pOID = Pattern.compile(("^([1-9][0-9]{0,3}|0)(\\.([1-9][0-9]{0,3}|0)){5,13}$"));

        Matcher m1 = pInt.matcher(input);
        Matcher m2 = pOID.matcher(input);

        int i;
        OID oid;

        if (m1.find()) {
            i = Integer.parseInt(input);
            return new Variable(i);
        } else if (m2.find()){
            oid = new OID(input);
            return new Variable(oid);
        }

        return new Variable(input);
    }
}
