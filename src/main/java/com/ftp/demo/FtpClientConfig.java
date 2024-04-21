package com.ftp.demo;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FtpClientConfig {
    @Bean
    public FTPClient ftpClient(){
        FTPClient ftpClient = new FTPClient();
        return ftpClient;
    }
}
