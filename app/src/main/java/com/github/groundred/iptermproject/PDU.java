package com.github.groundred.iptermproject;


import android.util.Log;

import com.github.groundred.iptermproject.ber.BER;
import com.github.groundred.iptermproject.ber.BERInputStream;
import com.github.groundred.iptermproject.ber.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PDU implements BERSerializable {

    // PDU type (4 byte)
    public static final int GET_REQUEST = BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0;
    public static final int GET_NEXT_REQUEST = BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 1;
    public static final int GET_RESPONSE = BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 2;
    public static final int SET_REQUEST = BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 3;

    //PDU Error Status (4 byte)

    public static final int NO_ERROR = 0;
    public static final int TOO_BIG = 1;
    public static final int NO_SUCH_NAME = 2;
    public static final int BAD_VALUE = 3;
    public static final int READ_ONLY = 4;
    public static final int GENERAL_ERROR = 5;
    public static final int NO_ACCESS = 6;
    public static final int WRONG_TYPE = 7;
    public static final int WRONG_LENGTH = 8;
    public static final int WRONG_ENCODING = 9;
    public static final int WRONG_VALUE = 10;
    public static final int NO_CREATION = 11;
    public static final int INCONSISTENT_VALUE = 12;
    public static final int RESOURCE_UNAVAILABLE = 13;
    public static final int COMMIT_FAILED = 14;
    public static final int UNDO_FAILED = 15;
    public static final int AUTHORIZATION_ERROR = 16;
    public static final int NOT_WRITABLE = 17;
    public static final int INCONSISTENT_NAME = 18;

    private int type = GET_REQUEST;
    private int request_Id;
    private int error_status = 0;
    private int error_Index = 0;
    private List<VariableBinding> variableBindings = new ArrayList<>();

    public PDU() {
        request_Id = new Random().nextInt(Integer.MAX_VALUE) + 1;
    }

    public PDU(VariableBinding vb) {
        request_Id = new Random().nextInt(Integer.MAX_VALUE) + 1;
        this.variableBindings.add(vb);
    }

    public void addVariableBinding(VariableBinding vb) {
        variableBindings.add(vb);
    }

    @Override
    public void encodeBER(OutputStream os) throws IOException {
        BER.encodeHeader(os, type, getBERLengthPDU());

        BER.encodeInteger(os, BER.INTEGER, request_Id);
        BER.encodeInteger(os, BER.INTEGER, error_status);
        BER.encodeInteger(os, BER.INTEGER, error_Index);

        int vbLength = 0;
        for (VariableBinding vb : variableBindings) {
            vbLength += vb.getBERLength();
        }
        BER.encodeHeader(os, BER.SEQUENCE, vbLength);
        for (VariableBinding vb : variableBindings) {
            vb.encodeBER(os);
        }

    }

    // length for all PDU with type, length
    @Override
    public int getBERLength() {
        int length = getBERLengthPDU();
        length += BER.getBERLengthOfLength(length) + 1;

        return length;
    }

    @Override
    public int getBERPayloadLength() {
        return getBERLengthPDU();
    }

    @Override
    public void decodeBER(BERInputStream inputStream) throws IOException {
        BER.MutableByte mutableByte = new BER.MutableByte();
        int length = BER.decodeHeader(inputStream, mutableByte);
        int pduStartPos = (int) inputStream.getPosition();

        this.type = mutableByte.getValue();

        mutableByte = new BER.MutableByte();
        request_Id = BER.decodeInteger(inputStream, mutableByte);
        error_status = BER.decodeInteger(inputStream, mutableByte);
        error_Index = BER.decodeInteger(inputStream, mutableByte);

        if (error_status != 0) {
            switch (error_status) {
                case TOO_BIG:
                    Log.e("PDU error", "Too Big");
                    break;
                case NO_SUCH_NAME:
                    Log.e("PDU error", "No Such Name");
                    break;
                case BAD_VALUE:
                    Log.e("PDU error", "Bad Value");
                    break;
                case READ_ONLY:
                    Log.e("PDU error", "Read Only");
                    break;
                case GENERAL_ERROR:
                    Log.e("PDU error", "General Error");
                    break;
                case NO_ACCESS:
                    Log.e("PDU error", "No Access");
                    break;
                case WRONG_TYPE:
                    Log.e("PDU error", "Wrong Type");
                    break;
                case WRONG_LENGTH:
                    Log.e("PDU error", "Wrong Length");
                    break;
                case WRONG_ENCODING:
                    Log.e("PDU error", "Wrong Encoding");
                    break;
                case WRONG_VALUE:
                    Log.e("PDU error", "Wrong Value");
                    break;
                case NO_CREATION:
                    Log.e("PDU error", "No Creation");
                    break;
                case INCONSISTENT_VALUE:
                    Log.e("PDU error", "Inconsistent Value");
                    break;
                case RESOURCE_UNAVAILABLE:
                    Log.e("PDU error", "Resource Unavailable");
                    break;
                case COMMIT_FAILED:
                    Log.e("PDU error", "Commit Fail");
                    break;
                case UNDO_FAILED:
                    Log.e("PDU error", "Undo Failed");
                    break;
                case AUTHORIZATION_ERROR:
                    Log.e("PDU error", "Authorization Error");
                    break;
                case NOT_WRITABLE:
                    Log.e("PDU error", "Not Writable");
                    break;
                case INCONSISTENT_NAME:
                    Log.e("PDU error", "Inconsistent Name");
                    break;
                default:
                    break;

            }
        }


        mutableByte = new BER.MutableByte();
        int variabelBindingLength = BER.decodeHeader(inputStream, mutableByte);

        int variableStartPos = (int) inputStream.getPosition();
        variableBindings = new ArrayList<VariableBinding>();

        while (inputStream.getPosition() - variableStartPos < variabelBindingLength) {
            VariableBinding vb = new VariableBinding();
            vb.decodeBER(inputStream);
            variableBindings.add(vb);
        }

    }

    // length for PDU
    public int getBERLengthPDU() {
        int length = getBERVariableLength(variableBindings);
        length += BER.getBERLengthOfLength(length) + 1;

        length += BER.getBERIntegerLength(request_Id);
        length += BER.getBERIntegerLength(error_Index);
        length += BER.getBERIntegerLength(error_status);

        return length;
    }

    // length for all Variable Binding
    public static int getBERVariableLength(List<VariableBinding> variableBindings) {
        int length = 0;
        for (VariableBinding variableBinding : variableBindings) {
            length += variableBinding.getBERLength();
        }
        return length;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<VariableBinding> getVariableBindings() {
        return variableBindings;
    }
}
