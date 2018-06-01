package com.github.groundred.iptermproject;

public class BER {

    // BER : Type + Length + value

    // BER type variable
    // Tag class(2bit) + P/C (1bit) + Tag Number(5bit)

    // Tag class
    public static final byte UNIVERSAL          = 0;             // 00 00 0000
    public static final byte APPLICATION        = 1<<6;             // 01 00 0000
    public static final byte CONTEXT_SPECIFIC   = (byte) (2<<6);    // 10 00 0000
    public static final byte PRIVATE            = (byte) (3<<6);    // 11 00 0000

    // Primitive & Constructed
    // Primitive   : The contents octets directly encode the element value.
    // Constructed : The contents octets contain 0, 1, or more element encodings.
    public static final byte PRIMITIVE      = 0; // 00 0 0000
    public static final byte CONSTRUCTOR    = 1<<5; // 00 1 0000

    // Tag Number
    public static final byte BOOLEAN        = UNIVERSAL|PRIMITIVE|1; // 00 00 0001
    public static final byte INTEGER        = UNIVERSAL|PRIMITIVE|2; // 00 00 0010
    public static final byte BITSTRING      = UNIVERSAL|PRIMITIVE|3; // 00 00 0011
    public static final byte OCTETSTRING    = UNIVERSAL|PRIMITIVE|4; // 00 00 0100
    public static final byte NULL           = UNIVERSAL|PRIMITIVE|5; // 00 00 0101
    public static final byte OID            = UNIVERSAL|PRIMITIVE|6; // 00 00 0110
    public static final byte SEQUENCE       = UNIVERSAL|CONSTRUCTOR|10; // 00 10 1010
    public static final byte IP_ADDRESS     = APPLICATION|PRIMITIVE|0;



    // BER Length
    // MSB 0 : short 0 ~ 127
    // 7 bit : Number of Octets in the value field
    // MSB 1 : Long  128 ~
    // 7 bit : Number of Octets in length indicator
    // 0x00     ~   0x7F        : 0x00
    // 0x80     ~   0xFF        : 0x81
    // 0xFF     ~   0xFFFF      : 0x82
    // 0xFFFF   ~   0xFFFFFF    : 0x83

    public static final byte SHORT_LENGTH = 0;
    public static final byte LONG_LENGTH = (byte) (1<<7);




    public static byte[] encodeInteger(byte type, int integer) throws IllegalArgumentException {

        int mask = 0xff800000; // 1111 1111 1000 0000 0000 0000
        int intSize = 4;
        int offset = 0;

        // get Integer byte size
        while( (((integer & mask) == 0) || ((integer & mask) == mask)) && (intSize > 1) ) {
            intSize--;
            integer <<= 8;
        }

        if (intSize > 4) {
            throw new IllegalArgumentException("BER encode error: INTEGER too long.");
        }

        byte[] encodedInteger = new byte[1+1+intSize]; // type (1byte) + BER Length(1) + IntegerSize (byte)

        //insert type, Length
        encodedInteger[offset++] = type;
        encodedInteger[offset++] = (byte) intSize;

        // insert Value
        mask = 0xff000000;

        while (intSize-- > 0) {
            encodedInteger[offset++] = (byte) ((integer & mask) >> 24);
            integer <<= 8;
        }

        return encodedInteger;
    }


    public static byte[] encodeOctetString(byte type, byte[] string) {

        int length = string.length;
        int BERLengthSize = calcBERLengthSize(length);
        byte[] encodeBERLength = encodeLength(BERLengthSize, length);
        byte[] encodeString = new byte[1+encodeBERLength.length+string.length];

        System.arraycopy(type,0,encodeString,0,1);
        System.arraycopy(encodeBERLength,0,encodeString,1,encodeBERLength.length+1);
        System.arraycopy(string,0,encodeString,encodeBERLength.length+1,encodeBERLength.length+string.length+1);

        return encodeString;
    }


    // Lengt 를 규칙에 맞게 byte 로 인코딩
    private static byte[] encodeLength(int BERLengthSize, int length) {

        byte[] lengthEncoded = new byte[BERLengthSize];

        if (length < 0){
            lengthEncoded[0] = (byte)0x84;
            lengthEncoded[1] = (byte) ((length>>24)&0xFF);
            lengthEncoded[2] = (byte) ((length>>16)&0xFF);
            lengthEncoded[3] = (byte) ((length>>8)&0xFF);
            lengthEncoded[4] = (byte) (length&0xFF);
            return lengthEncoded;
        }
        else if (length <= 127) {
            lengthEncoded[0] = (byte)length;
            return lengthEncoded;
        } else if (length <= 0xFF) {
            lengthEncoded[0] = (byte)0x81;
            lengthEncoded[1] = (byte)length;
            return lengthEncoded;
        } else if (length <= 0xFFFF) {
            lengthEncoded[0] = (byte)0x82;
            lengthEncoded[1] = (byte) ((length>>8)&0xFF);
            lengthEncoded[2] = (byte) (length&0xFF);
            return lengthEncoded;
        } else if (length <= 0xFFFFFF) {
            lengthEncoded[0] = (byte)0x83;
            lengthEncoded[1] = (byte) ((length>>16)&0xFF);
            lengthEncoded[2] = (byte) ((length>>8)&0xFF);
            lengthEncoded[3] = (byte) (length&0xFF);
            return lengthEncoded;
        } else {
            lengthEncoded[0] = (byte)0x84;
            lengthEncoded[1] = (byte) ((length>>24)&0xFF);
            lengthEncoded[2] = (byte) ((length>>16)&0xFF);
            lengthEncoded[3] = (byte) ((length>>8)&0xFF);
            lengthEncoded[4] = (byte) (length&0xFF);
            return lengthEncoded;
        }
    }

    // Length 가 몇 byte 를 차지하는지 계산
    private static int calcBERLengthSize(int length) {
        if (length < 0) {
            return 5;
        }
        else if (length <= 127) {
            return 1;
        } else if (length <= 0xFF) {
            return 2;
        } else if (length <= 0xFFFF) {
            return 3;
        } else if (length <= 0xFFFFFF) {
            return  4;
        }

        return 5;
    }

    public static byte[] integerToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

}
