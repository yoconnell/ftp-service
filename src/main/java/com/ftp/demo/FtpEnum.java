package com.ftp.demo;

public enum FtpEnum {
    EASY_FTP("127.0.0.1",21);
    private final String host;
    private final int port;

    FtpEnum(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
