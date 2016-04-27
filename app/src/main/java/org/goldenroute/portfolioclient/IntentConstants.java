package org.goldenroute.portfolioclient;

public interface IntentConstants {
    int RC_PROFILE = 1;
    int RC_ADD_PORTFOLIO = 2;
    int RC_EDIT_PORTFOLIO = 3;
    int RC_ADD_TRANSACTION = 4;
    int RC_EDIT_TRANSACTION = 5;
    int RC_EMAIL = 6;

    String ARG_TYPE = "type";
    String ARG_POS = "pos";
    String ARG_PID = "pid";
    String ARG_TID = "tid";

    String NEW_LINE = System.getProperty("line.separator");
}
