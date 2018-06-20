package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;

import java.io.OutputStream;

public class OID {

    private int[] value = new int[0];

    public OID() {
    }

    public OID(String oid) {
        value = parseOID(oid);
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

    public void encodeBER(OutputStream os) throws java.io.IOException {
        BER.encodeOID(os, BER.OID, value);
    }

    public int getBERLength() {
        int length = BER.getOIDLength(value);
        return length + BER.getBERLengthOfLength(length) + 1;
    }


}
