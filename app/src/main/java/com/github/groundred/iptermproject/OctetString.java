package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BEROutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class OctetString {

    private byte type = BER.OCTETSTRING;
    private String community;
    private byte[] encodedCommunity;

    public OctetString() {
    }

    public OctetString(String communityString) {
        this.community = communityString;
        this.encodedCommunity = communityString.getBytes();
    }


    public void encodeBER(OutputStream os) throws IOException {
       BER.encodeString(os, type, encodedCommunity);
    }

    public int getBERLength() {
        int length = encodedCommunity.length + BER.getBERLengthOfLength(encodedCommunity.length) + 1;
        return length;
    }
    public String getCommunity() {
        return community;
    }
}
