package com.ftp.demo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileVo {
    private String localPath;
    private String ftpPath;
    private String localFileNm;
    private String ftpFileNm;
    private String ftpFolder;
    private String localFolder;
}
