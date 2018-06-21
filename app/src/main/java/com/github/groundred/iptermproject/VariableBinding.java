package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

public class VariableBinding implements BERSerializable {
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

    @Override
    public void encodeBER(OutputStream os) throws IOException {
        int length = getBERPayloadLength();
        BER.encodeHeader(os, BER.SEQUENCE, length);
        oid.encodeBER(os);
        variable.encodeBER(os);
    }

    @Override
    public final int getBERLength() {
        int length = getBERPayloadLength();
        length += BER.getBERLengthOfLength(length) + 1;
        return length;
    }

    @Override
    public int getBERPayloadLength() {
        return oid.getBERLength() + variable.getBERLength();
    }

    @Override
    public void decodeBER(BERInputStream inputStream) throws IOException {
        BER.MutableByte type = new BER.MutableByte();
        int length = BER.decodeHeader(inputStream, type);
        long startPos = inputStream.getPosition();

        oid.decodeBER(inputStream);
        variable.decodeBER(inputStream);

    }

    public OID getOid() {
        return oid;
    }

    public Variable getVariable() {
        return variable;
    }
}
