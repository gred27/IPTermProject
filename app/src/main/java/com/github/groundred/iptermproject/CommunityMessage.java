package com.github.groundred.iptermproject;

import android.util.Log;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static android.os.Build.ID;

// community-Based SNMP Version2 Message Format
public class CommunityMessage {

    // Version (4 byte) + community (variable) + PDU
    public static final int version2c = 1;


    private int version = version2c;
    private OctetString community; // octet string
    private PDU pdu;


    private long timeout = 1000;
    private int maxSizeRequestPDU = 65535;

    public CommunityMessage() {
    }

    public CommunityMessage(String community, PDU pdu) {
        this.community = new OctetString(community);
        this.pdu = pdu;
    }

    public void makeSendPacket(BEROutputStream os) throws IOException {

        int length = pdu.getBERLength();
        length += community.getBERLength();
        length += BER.getBERIntegerLength(version);

        ByteBuffer buf = ByteBuffer.allocate(length +
                BER.getBERLengthOfLength(length) + 1);
        // set the buffer of the outgoing message
        os.setBuffer(buf);

        // encode the message
        BER.encodeHeader(os, BER.SEQUENCE, length);
        BER.encodeInteger(os,BER.INTEGER, version);

        community.encodeBER(os);
        pdu.encodeBER(os);

    }

    public void decodePacket(BERInputStream is, byte[] packet) throws IOException {
        BER.MutableByte mutableByte = new BER.MutableByte();
        int totalLength = BER.decodeHeader(is,mutableByte);

        if (mutableByte.getValue() != BER.SEQUENCE) {
            Log.e("Error","not Sequence");
            throw new IOException();
        }

        //get Version
        int version = BER.decodeInteger(is,mutableByte);
        if(mutableByte.getValue() != BER.INTEGER) {
            Log.e("Error","not Integer");
            throw new IOException();
        }
        this.version = version;

        //get Community
        OctetString community = new OctetString();
        community.decodeBER(is);
        this.community = community;

        //get PDU
        PDU pdu = new PDU();
        pdu.decodeBER(is);

        this.pdu = pdu;

    }


    public PDU getPdu() {
        return pdu;
    }
}
