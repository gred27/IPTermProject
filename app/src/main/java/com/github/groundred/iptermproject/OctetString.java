package com.github.groundred.iptermproject;

import android.util.Log;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OctetString implements BERSerializable {

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

    @Override
    public int getBERPayloadLength() {
        return encodedCommunity.length;
    }

    @Override
    public void decodeBER(BERInputStream is) throws IOException {
        BER.MutableByte type = new BER.MutableByte();
        byte[] tmpStringByte = BER.decodeString(is, type);
        if (type.getValue() != BER.OCTETSTRING) {
            Log.e("Error", "not String");
            throw new IOException();
        }
        encodedCommunity = tmpStringByte;
        community = new String(tmpStringByte, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return community;
    }
}
