package com.github.groundred.iptermproject;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UdpSendAndListen implements Runnable {
    @Override
    public void run() {
//        boolean run = true;
//        try {
//            DatagramSocket udpSocket = new DatagramSocket(portVal);
//            InetAddress serverAddr = InetAddress.getByName(ipVal);
//            byte[] buf = ("FILES").getBytes();
//            DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, port);
//            udpSocket.send(packet);
//            while (run) {
//                try {
//                    byte[] message = new byte[8000];
//                    DatagramPacket packet = new DatagramPacket(message,message.length);
//                    Log.i("UDP client: ", "about to wait to receive");
//                    udpSocket.setSoTimeout(10000);
//                    udpSocket.receive(packet);
//                    String text = new String(message, 0, p.getLength());
//                    Log.d("Received text", text);
//                } catch (IOException e) {
//                    Log.e(" UDP client has IOException", "error: ", e);
//                    run = false;
//                    udpSocket.close();
//                } catch (SocketTimeoutException e) {
//                    Log.e("Timeout Exception","UDP Connection:",e);
//                    run = false;
//                    udpSocket.close();
//                }
//            }
//        } catch (SocketException e) {
//            Log.e("Socket Open:", "Error:", e);
//        }
    }
}
