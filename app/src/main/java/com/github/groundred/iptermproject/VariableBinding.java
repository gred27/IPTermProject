package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;

import java.io.IOException;
import java.io.OutputStream;

public class VariableBinding {
    private OID oid;
    private Variable variable;

    public VariableBinding() {
        oid = new OID();
        variable = new Variable("NULL");
    }

    public VariableBinding(OID oid) {
        this.oid = oid;
        variable = new Variable("NULL");
    }

    public VariableBinding(OID oid, Variable variable) {
        this.oid = oid;
        this.variable = variable;
    }

    public void encodeBER(OutputStream os) throws IOException {
        int length = getBERPayloadLength();
        BER.encodeHeader(os, BER.SEQUENCE, length);
        oid.encodeBER(os);
        variable.encodeBER(os);
    }

    public final int getBERLength() {
        int length = getBERPayloadLength();
        length += BER.getBERLengthOfLength(length) + 1;
        return length;
    }

    public int getBERPayloadLength() {
        return oid.getBERLength() + variable.getBERLength();
    }


}
