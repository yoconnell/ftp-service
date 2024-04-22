package com.ftp.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/test")
public class FtpController {
    @Autowired
    FtpService ftpService;

    @GetMapping(value="/download")
    public String download(@RequestParam(value ="파일 이름",defaultValue = "test.txt") String fileNm ,
                             @RequestParam(value ="FTP 폴더",defaultValue = "./SND/") String ftpFolder ,
                             @RequestParam(value ="로컬 폴더",defaultValue = "./FTP/") String localFolder ) throws IOException, InterruptedException {
        FtpUserVo userVo = ftpService.generateFtpUserVo("EASY_USER_FTP","EASY_FTP");
        ftpService.connectNlogin(userVo);
        FileVo fileVo = FileVo.builder()
                        .ftpFileNm(fileNm)
                        .ftpFolder(ftpFolder)
                        .localFileNm(fileNm)
                        .localFolder(localFolder)
                        .ftpPath(ftpFolder+fileNm)
                        .localPath(localFolder+fileNm)
                        .build();
        ftpService.downloadFile(userVo,fileVo);
        return "result";
    }
    @GetMapping(value="/upload")
    public String upload(@RequestParam(value ="파일 이름",defaultValue = "test.txt") String fileNm ,
                         @RequestParam(value ="FTP 폴더",defaultValue = "./SND/") String ftpFolder ,
                         @RequestParam(value ="로컬 폴더",defaultValue = "./FTP/") String localFolder
                         ) throws IOException, InterruptedException {
        FtpUserVo userVo = ftpService.generateFtpUserVo("EASY_USER_FTP","EASY_FTP");
        ftpService.connectNlogin(userVo);
        FileVo fileVo = FileVo.builder()
                        .ftpFileNm(fileNm)
                        .ftpFolder(ftpFolder)
                        .localFileNm(fileNm)
                        .localFolder(localFolder)
                        .ftpPath(ftpFolder+fileNm)
                        .localPath(localFolder+fileNm)
                        .build();

        ftpService.uploadFile(userVo,fileVo);
        return "result";
    }

    @GetMapping(value="/getList")
    public String getList(
            @RequestParam(value ="접근 USER",defaultValue = "EASY_USER_FTP") String userNm,
            @RequestParam(value ="FTP 정보",defaultValue = "EASY_FTP") String dstFtp
    ) throws IOException, InterruptedException {
        FtpUserVo userVo = ftpService.generateFtpUserVo(userNm,dstFtp);
        ftpService.connectNlogin(userVo);
        FileVo fileVo = FileVo.builder()
                        .ftpFolder("./SND/")
                        .build();
        ftpService.getList(userVo,fileVo);
        return "result";
    }


    @GetMapping(value="/disConnect")
    public String disConnect(
            @RequestParam(value ="접근 USER",defaultValue = "EASY_USER_FTP") String userNm,
            @RequestParam(value ="FTP 정보",defaultValue = "EASY_FTP") String dstFtp
    ) throws IOException, InterruptedException {
        FtpUserVo userVo = ftpService.generateFtpUserVo(userNm,dstFtp);
        ftpService.disconnectNlogout(userVo);
        return "result";
    }

    @GetMapping(value="/connectNLogin")
    public String connectNLogin(
            @RequestParam(value ="접근 USER",defaultValue = "EASY_USER_FTP") String userNm,
            @RequestParam(value ="FTP 정보",defaultValue = "EASY_FTP") String dstFtp) throws IOException, InterruptedException {
        FtpUserVo userVo = ftpService.generateFtpUserVo(userNm,dstFtp);
        ftpService.connectNlogin(userVo);
        return "result";
    }
}
