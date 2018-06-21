package com.github.groundred.iptermproject;

import android.util.Log;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

public class OID implements BERSerializable {

    private String oid;
    private int[] OIDArray = new int[0];

    public OID() {
    }

    public OID(String oid) {
        this.oid = oid;
        OIDArray = parseOID(oid);
    }

    public int[] parseOID(String oid) {
        String parsedStirng[] = oid.split("\\.");
        int[] parseOIDValue = new int[parsedStirng.length];

        for (int i = 0; i < parsedStirng.length; i++) {
            int tmp = Integer.parseInt(parsedStirng[i]);
            parseOIDValue[i] = tmp;
        }
        return parseOIDValue;
    }

    @Override
    public void encodeBER(OutputStream os) throws java.io.IOException {
        BER.encodeOID(os, BER.OID, OIDArray);
    }

    @Override
    public int getBERLength() {
        int length = BER.getOIDLength(OIDArray);
        return length + BER.getBERLengthOfLength(length) + 1;
    }

    @Override
    public int getBERPayloadLength() {
        return BER.getOIDLength(OIDArray);
    }

    @Override
    public void decodeBER(BERInputStream inputStream) throws IOException {
        BER.MutableByte type = new BER.MutableByte();
        int[] decodedOID = BER.decodeOID(inputStream, type);
        if (type.getValue() != BER.OID) {
            Log.e("Error", "not OID");
            throw new IOException();
        }
        this.OIDArray = decodedOID;

        StringBuilder tmp = new StringBuilder();
        for (int i : OIDArray
                ) {
            tmp.append(".").append(i);
        }
        tmp.deleteCharAt(0);
        oid = tmp.toString();
    }

    @Override
    public String toString() {
        return oid;
    }

    public int[] getOIDArray() {
        return OIDArray;
    }

    public String getOid() {
        return oid;
    }
}
