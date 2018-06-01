package com.github.groundred.iptermproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    Context context;


    private static final String TAG = "SNMP CLIENT";

    private Button sendBtn;
    private StringBuffer logResult = new StringBuffer();
    private TextView tvLog;

    private static final String address = "kuwiden.iptime.org";
    private static final String port = "11161";
    private static final String OIDVALUE = "1.3.6.1.2.1.2.2.1.7.1";
    private static final int SNMP_VERSION = CommunityMessage.version2c;
    private static String community = "public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        // Initialize UI
        sendBtn = (Button) findViewById(R.id.btn_getClick);
        tvLog = (TextView) findViewById(R.id.tv_getResult);
        // set onClick listener
        sendBtn.setOnClickListener((View v) -> {
                    mAsyncTask.execute();
                }
        );
    }

    private void sendSnmpRequest(String cmd) throws Exception {
        try {

            InetAddress serverAddr = InetAddress.getByName(address);
            int trapRcvPort = Integer.parseInt(port);

            PDU pdu = new PDU();
            CommunityMessage communityMessage = new CommunityMessage(community, pdu);

            // Create trap data.
            byte sendData[] = {(byte)0x30, (byte)0x2b, // Sequence type, Length from here: 98 byte.
                    (byte)0x02, (byte)0x01, (byte)0x01, // v2c
                    (byte)0x04, (byte)0x06, (byte)0x70, (byte)0x75,
                    (byte)0x62, (byte)0x6c, (byte)0x69, (byte)0x63,
                    (byte)0xa0, (byte)0x1e, (byte)0x02, (byte)0x04,
                    (byte)0x0f, (byte)0xb1, (byte)0x41, (byte)0x0a,
                    (byte)0x02, (byte)0x01, (byte)0x00, (byte)0x02,
                    (byte)0x01, (byte)0x00, (byte)0x30, (byte)0x10,
                    (byte)0x30, (byte)0x0e, (byte)0x06, (byte)0x0a,
                    (byte)0x2b, (byte)0x06, (byte)0x01, (byte)0x02,
                    (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x01,
                    (byte)0x07, (byte)0x01, (byte)0x05, (byte)0x00
            };
            DatagramPacket dataPacket = new DatagramPacket(sendData, sendData.length, serverAddr, trapRcvPort);

            // Send trap.
            DatagramSocket dataSocket = new DatagramSocket();
            dataSocket.send(dataPacket);

            dataSocket.receive(dataPacket);

            String massage = new String(dataPacket.getData());
            byte[] messageByte = dataPacket.getData();
            byte[] mbyte = new byte[50];
            mbyte = dataPacket.getData();

            StringBuilder tmp = new StringBuilder();

            for (byte b:mbyte
                 ) {

                tmp.append(Integer.toHexString(b)).append(" ");
            }
            Log.d("message", massage);
            Log.d("byte",messageByte.toString());
            Log.d("byte",tmp.toString());

//            tvLog.setText(massage);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    AsyncTask<Void, Void, Void> mAsyncTask = new AsyncTask<Void, Void, Void>() {

        protected void onPreExecute() {
        }



        @Override
        protected Void doInBackground(Void... params) {
            try {
                sendSnmpRequest(OIDVALUE);
            } catch (Exception e) {
                Log.d(TAG,
                        "Error sending snmp request - Error: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {

        }

    };
}
