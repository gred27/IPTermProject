package com.github.groundred.iptermproject;

// community-Based SNMP Version2 Message Format
public class CommunityMessage {

    // Version (4 byte) + community (variable) + PDU
    public static final int version2c = 1;


    private int version = version2c;
    private String community; // octet string
    private PDU pdu;


    private long timeout = 1000;
    private int maxSizeRequestPDU = 65535;

    public CommunityMessage() {
    }

    public CommunityMessage(String community, PDU pdu) {
        this.community = community;
        this.pdu = pdu;
    }



}
