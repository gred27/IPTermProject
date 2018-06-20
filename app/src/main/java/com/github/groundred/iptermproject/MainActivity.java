package com.github.groundred.iptermproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

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
                    mAsynTask.execute(etGetRequest.getText().toString());
                }
        );
    }

    @SuppressLint("LongLogTag")
    private void sendSnmpRequest(String cmd) throws Exception {
        try {

            InetAddress serverAddr = InetAddress.getByName(address);
            int trapRcvPort = Integer.parseInt(port);

            PDU pdu = new PDU();
            pdu.addVariableBinding(new VariableBinding(new OID(cmd)));
            pdu.setType(PDU.GET_REQUEST);

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
                byte[] mbyte;
                mbyte = packet.getData();

                StringBuilder tmp = new StringBuilder();

                for (byte b : mbyte
                        ) {

                    tmp.append(Integer.toHexString(b)).append(" ");
                }
                Log.d("message", massage);
                Log.d("byte", messageByte.toString());
                Log.d("byte", tmp.toString());

            } catch (IOException e) {
                Log.e(" UDP client has IOException", "error: ", e);
                dataSocket.close();
            }


//            tvLog.setText(massage);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MyAsynTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                sendSnmpRequest(strings[0]);
            } catch (Exception e) {
                Log.d(TAG,
                        "Error sending snmp request - Error: " + e.getMessage());
            }
            return null;
        }
    }
}
