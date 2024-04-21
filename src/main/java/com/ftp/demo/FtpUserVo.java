package com.ftp.demo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FtpUserVo {
    private boolean isConnect;
    private boolean isLogin;
    private String loginId;
    private String loginPw;
    private int port;
    private String host;

}
