package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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



}
