package com.github.groundred.iptermproject;

public class OctetString {

    private byte type = BER.OCTETSTRING;
    private byte[] community = new byte[0];
    private byte[] encodedCommunity;


    public OctetString() {

    }

    public OctetString(String communityString) {
        this.community = communityString.getBytes();
    }


    public byte[] encodeBER() {
       encodedCommunity =  BER.encodeOctetString(type,getCommunity());
       return encodedCommunity;
    }

    public byte[] getCommunity() {
        return community;
    }
}
