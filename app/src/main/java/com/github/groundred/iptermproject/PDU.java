package com.github.groundred.iptermproject;

import static com.github.groundred.iptermproject.BER.CONSTRUCTOR;
import static com.github.groundred.iptermproject.BER.CONTEXT_SPECIFIC;

public class PDU {

    // PDU type (4 byte)
    public static final int GET_REQUEST    = CONTEXT_SPECIFIC|CONSTRUCTOR|0;
    public static final int GET_NEXT_REQUEST  = CONTEXT_SPECIFIC|CONSTRUCTOR|1;
    public static final int GET_RESPONSE   = CONTEXT_SPECIFIC|CONSTRUCTOR|2;
    public static final int SET_REQUEST    = CONTEXT_SPECIFIC|CONSTRUCTOR|3;

    //PDU Error Status (4 byte)

    public static final int NO_ERROR            = 0;
    public static final int TOO_BIG             = 1;
    public static final int NO_SUCH_NAME        = 2;
    public static final int BAD_VALUE           = 3;
    public static final int READ_ONLY           = 4;
    public static final int GENERAL_ERROR       = 5;
    public static final int NO_ACCESS           = 6;
    public static final int WRONG_TYPE          = 7;
    public static final int WRONG_LENGTH        = 8;
    public static final int WRONG_ENCODING      = 9;
    public static final int WRONG_VALUE         = 10;
    public static final int NO_CREATION         = 11;
    public static final int INCONSISTENT_VALUE  = 12;
    public static final int RESOURCE_UNAVAILABLE= 13;
    public static final int COMMIT_FAILED       = 14;
    public static final int UNDO_FAILED         = 15;
    public static final int AUTHORIZATION_ERROR = 16;
    public static final int NOT_WRITABLE        = 17;
    public static final int INCONSISTENT_NAME   = 18;

    private int type;
    private int request_Id;
    private int error_status;
    private int error_Index;
    private String variable_Bindings;

    public PDU (){

    }



}
