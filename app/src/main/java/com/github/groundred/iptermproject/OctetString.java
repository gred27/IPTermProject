package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class OctetString {

    private byte type = BER.OCTETSTRING;
    private byte[] community = new byte[0];
    private byte[] encodedCommunity;

    OutputStream os = new BEROutputStream();

    public OctetString() {

    }

    public OctetString(String communityString) {
        this.community = communityString.getBytes();
    }


    public byte[] encodeBER() throws IOException {
       BER.encodeString(os, type,encodedCommunity);
       return encodedCommunity;
    }

    public byte[] getCommunity() {
        return community;
    }
}
