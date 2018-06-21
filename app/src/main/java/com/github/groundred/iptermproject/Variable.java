package com.github.groundred.iptermproject;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;

import java.io.IOException;
import java.io.OutputStream;

public class Variable<T> {
    T variable;
    String variableType = new String();

    public Variable(T variable) {
        this.variable = variable;
    }

    public void encodeBER(OutputStream os) throws IOException {
        if (variable instanceof String) {
            if (variable.equals("NULL")) {
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

    public int getBERLength() {
        if (variable instanceof String) {
            if (variable.equals("NULL")) {
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

    public void decodeBER(BERInputStream is) throws IOException {
        is.mark((int) is.getPosition());
        BER.MutableByte type = new BER.MutableByte();
        int length2 = BER.decodeHeader(is, type);

        is.reset();

        switch (type.getValue()) {
            case BER.INTEGER:
                variableType = "INTEGER";
                Integer tmp = BER.decodeInteger(is, type);
                variable = (T) tmp;
                break;
            case BER.COUNTER:
                variableType = "COUNTER";
                Integer tmp1 = BER.decodeInteger(is, type);
                variable = (T) tmp1;
                break;
            case BER.GAUGE:
                variableType = "GAUGE";
                Long tmp2 = BER.decodeUnsignedInteger(is, type);
                variable = (T) tmp2;
                break;
            case BER.TIMETICKS:
                variableType = "TIMETICK";
                Integer tmp3 = BER.decodeInteger(is, type);
                variable = (T) tmp3;
                break;
            case BER.OCTETSTRING:
                variableType = "OCTETSTRING";
                OctetString oct = new OctetString();
                oct.decodeBER(is);
                variable = (T) oct;
                break;
            case BER.NULL:
                variableType = "NULL";
                BER.decodeNull(is, type);
                variable = (T) "Null";
                break;
            case BER.OID:
                variableType = "OID";
                OID oid = new OID();
                oid.decodeBER(is);
                variable = (T) oid;
                break;
            case (byte) BER.ENDOFMIBVIEW:
                variableType = "endOfMibView";
                BER.decodeNull(is, type);
                variable = (T) "END";
            default:
                break;
        }

    }

    @Override
    public String toString() {
        return variableType + ":" + variable.toString();
    }
}
