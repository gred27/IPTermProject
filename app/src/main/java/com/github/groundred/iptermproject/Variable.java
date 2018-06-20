package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class Variable<T> {
    T variable;

    public Variable(T variable) {
        this.variable = variable;
    }

    public void encodeBER(OutputStream os) throws IOException {
        if(variable instanceof String) {
            if(variable.equals("NULL")) {
                os.write(BER.NULL);
                os.write(0);
            }
        } else if (variable instanceof OID) {
            ((OID) variable).encodeBER(os);
        } else if (variable instanceof Integer) {
            BER.encodeInteger(os, BER.INTEGER, ((Integer) variable).intValue());

        } else if (variable instanceof OctetString) {
            ((OctetString) variable).encodeBER(os);
        }
    }

    public int getBERLength(){
        if(variable instanceof String) {
            if(variable.equals("NULL")) {
                return 2;
            }
        } else if (variable instanceof OID) {
            return ((OID) variable).getBERLength();
        } else if (variable instanceof Integer) {
            return BER.getBERIntegerLength((Integer) variable);
        } else if (variable instanceof OctetString) {
            return ((OctetString) variable).getBERLength();
        }

        return -1;
    }

}
