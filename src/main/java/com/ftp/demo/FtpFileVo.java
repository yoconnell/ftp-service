package com.ftp.demo;

import lombok.Builder;
import lombok.Data;

import java.util.Calendar;

@Builder
@Data
public class FtpFileVo {
    private String fileNm;
    private Calendar rgstTime;
}
