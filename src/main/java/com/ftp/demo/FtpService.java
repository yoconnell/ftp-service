package com.ftp.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FtpService {
    @Autowired
    FtpUtil ftpUtil;
    @Autowired
    FtpClientConfig ftpClient;

    public void connectNlogin(FtpUserVo userVo) throws IOException {
        boolean connectNlogin = ftpUtil.connect(ftpClient.ftpClient(),userVo.getHost(),userVo.getPort());
        if(connectNlogin){
            userVo.setConnect(true);
            connectNlogin = ftpUtil.logIn(ftpClient.ftpClient(),userVo.getLoginId(),userVo.getLoginPw());
            if(connectNlogin){
                userVo.setLogin(true);
                log.info("CONNECT : {} / LOGIN : {}", userVo.isConnect() , userVo.isLogin());
            }
        }
    }

    public void disconnectNlogout(FtpUserVo userVo) throws IOException {
        boolean isConnect = ftpClient.ftpClient().isConnected();
        if(isConnect){
            ftpUtil.disconnect(ftpClient.ftpClient());
            if(!ftpClient.ftpClient().isConnected()){
                userVo.setConnect(false);
                userVo.setLogin(false);
            }else{
                log.info("FTP : {} / USER: {} IS NOT LOGOUT OR DISCONNECT ", userVo.getHost() , userVo.getLoginId() );
            }
        }
    }

    public boolean uploadFile(FtpUserVo userVo ,FileVo fileVo){
        if(userVo.isConnect() && userVo.isLogin()){
            //connect되고 login 되어있을 때, 시도
            return ftpUtil.upload(ftpClient.ftpClient(), fileVo.getLocalPath(), fileVo.getFtpFolder(), fileVo.getFtpPath());
        } else {
            log.info("FTP : {} / USER: {} IS NOT LOGIN OR CONNECT ", userVo.getHost() , userVo.getLoginId() );
        }
        return false;
    }

    public boolean downloadFile(FtpUserVo userVo ,FileVo fileVo) throws FileNotFoundException {
        if(userVo.isConnect() && userVo.isLogin()){
            //connect되고 login 되어있을 때, 시도
            return ftpUtil.download(ftpClient.ftpClient(),fileVo.getLocalPath(),fileVo.getFtpPath());
        }else{
            log.info("FTP : {} / USER: {} IS NOT LOGIN OR CONNECT ", userVo.getHost() , userVo.getLoginId() );
        }
        return false;
    }

    public List<FtpFileVo> getList(FtpUserVo userVo , FileVo fileVo) throws IOException {
        if(userVo.isConnect() && userVo.isLogin()){
            //connect되고 login 되어있을 때, 시도
            return ftpUtil.getList(ftpClient.ftpClient(),fileVo.getFtpFolder());
        }else{
            log.info("FTP : {} / USER: {} IS NOT LOGIN OR CONNECT ", userVo.getHost() , userVo.getLoginId() );
        }
        return null;
    }

    public FtpUserVo generateFtpUserVo(String userNm,String dstFtp){
        FtpEnum ftpEnum = FtpEnum.valueOf(dstFtp);
        FtpUserEnum ftpUserEnum = FtpUserEnum.valueOf(userNm);

        FtpUserVo userVo = FtpUserVo.builder()
                .host(ftpEnum.getHost())
                .port(ftpEnum.getPort())
                .loginId(ftpUserEnum.getLoginID())
                .loginPw(ftpUserEnum.getLoginPW())
                .build();

        log.info("USER ID : {}" , userVo.getLoginId());
        return userVo;
    }

}
