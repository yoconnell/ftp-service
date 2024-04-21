package com.ftp.demo;

public enum FtpUserEnum {
    EASY_USER_FTP("user_ID","user_PW");
    private final String loginID;
    private final String loginPW;

    public String getLoginID() {
        return loginID;
    }

    public String getLoginPW() {
        return loginPW;
    }

    FtpUserEnum(String loginID, String loginPW) {
        this.loginID = loginID;
        this.loginPW = loginPW;
    }
}
