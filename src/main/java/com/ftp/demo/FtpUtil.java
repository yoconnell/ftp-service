package com.ftp.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FtpUtil {
      /**  접속. 로그인 . 리스트 . 업로드 . 다운로드 . METHOD 구현 **/

      /** 1. 접속 - connect / disconnect   **/
    /** 접속
    * package org.apache.commons.net;
    * public void connect(InetAddress host, int port)
    *
    * ftpClient.connect(server, port);
    * FTP 클라이언트를 지정된 서버와 포트로 연결하는 데 사용됩니다.
    * server 변수는 서버의 주소를 나타내고, port 변수는 연결할 포트를 나타냅니다.
    *
    * ftpClient.getReplyString() : FTP 클라이언트 객체가 마지막으로 수신한 응답에 대한 문자열을 반환합니다.
    * 이것은 FTP 서버로부터의 마지막 응답에 대한 정보를 제공합니다.
    *
    * ftpClient.setSoTimeout() : 응답 대기 시간
    * 일반적으로 소켓에서 데이터가 도착할 때까지 기다리되,
    * 지정된 시간 내에 데이터가 도착하지 않으면 작업이 SocketTimeoutException을 던지도록 설정됩니다.
    * 이를 통해 프로그램이 데이터를 기다리는 동안 무기한 대기하는 것을 방지할 수 있습니다.
    *
    **/
      public boolean connect(
              FTPClient ftpClient,
              String host,
              int port
      ){
          boolean isConnected = ftpClient.isConnected();
          if(!isConnected){
              try
              {
                  ftpClient.connect(host,port);
                  String replyConnect = ftpClient.getReplyString();
                  log.info("CONNECT REPLY : {} " , replyConnect);
                  ftpClient.setSoTimeout(1000 * 60 * 60); // 1 hour connect
              } catch (SocketException e) {
                  throw new RuntimeException(e);
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
          }
          return isConnected;
      }

      public void disconnect(
              FTPClient ftpClient
      ) throws IOException {
          ftpClient.disconnect();
      }



    /** 2. 로그인 - login/logout **/

    /** 로그인
    * public boolean login(String username, String password)
    *
    * enterLocalPassiveMode();
    * 액티브 모드(Active Mode):
    * 서버는 클라이언트의 연결을 받아들이고, 데이터를 클라이언트에게 직접 전송합니다.
    * 클라이언트의 방화벽이나 NAT 라우터가 있을 경우, 서버에서 클라이언트로의 연결이 차단될 수 있습니다.
    * 패시브 모드(Passive Mode): <- 보통 이거 쓴다.
    * 서버가 클라이언트에게 데이터 포트를 제공하고, 클라이언트가 해당 포트로 연결을 생성합니다.
    * 클라이언트가 서버로부터 데이터를 직접 수신합니다.
    * 클라이언트 측 방화벽이나 NAT 라우터가 있어도 데이터 전송에 문제가 없습니다
    *
    *
    * setKeepAlive(true) :
    * keep-alive 기능을 활성화하면 클라이언트와 서버 간의 제어 연결이 일정 시간마다 유지되어서,
    * 연결이 유효하고 활성 상태로 유지
    *
    * setFileType(FTP.BINARY_FILE_TYPE) :
    * FTP 프로토콜은 두 가지 유형의 파일 전송을 지원합니다: ASCII 모드와 이진(Binary) 모드.
    * ASCII 모드: 텍스트 파일을 전송할 때 사용됩니다. 이 모드에서는 텍스트 파일의 줄바꿈 문자를 서로 다른 운영 체제 간에 변환합니다. 이는 파일을 서로 다른 운영 체제 간에 전송할 때 발생하는 줄바꿈 문자의 차이를 보상하기 위한 것입니다.
    * 이진 모드: 이진 파일(예: 이미지, 동영상, 실행 파일 등)을 전송할 때 사용됩니다. 이 모드에서는 파일의 데이터를 그대로 전송하며, 어떤 변환도 수행하지 않습니다.
    * 보통은 파일의 데이터를 보존하기 위해 BINARY로 주고 받음
    **/
    public boolean logIn(FTPClient ftpClient, String loginId, String loginPw) throws IOException {

        boolean isLogIn = ftpClient.login(loginId, loginPw);
        String replyLogIn = ftpClient.getReplyString();
        log.info("CONNECT REPLY : {} ", replyLogIn);

        if (isLogIn) {
            log.info("LOGIN SUCCESS : ID - {}", loginId);
            ftpClient.enterLocalPassiveMode(); // passive mode
            ftpClient.setKeepAlive(true); // alert " i am alive "
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // as original data is
            ftpClient.setSoTimeout(1000 * 60 * 60); // 1 hour login
            return true;
        } else {
            log.info("LOGIN FAIL : ID - {}, PW - {}", loginId, loginPw);
            ftpClient.disconnect(); // 로그인 실패시 disconnect
            return false;
        }
    }

    public boolean logOut(FTPClient ftpClient) throws  IOException{
        boolean isLogOut = ftpClient.logout();
        return isLogOut;
    }


    /** 3. 리스트 가져오기 **/

    /**
     *  FTPFile[] listFiles(String pathname) 메서드는 현재 작업 디렉토리에서 파일 및 디렉토리 목록을 가져오는 데 사용됩니다.
     * 이 메서드는 현재 작업 디렉토리에 있는 파일과 디렉토리를 나타내는 FTPFile 객체의 배열을 반환합니다.
     *
     *
     * getRawListing() 메서드는 FTPFile 객체가 나타내는 파일이나 디렉토리에 대한 원시 목록을 반환
     * getName(): 파일 또는 디렉토리의 이름을 반환합니다.
     * getSize(): 파일의 크기를 반환합니다.
     * getTimestamp(): 파일의 수정 시간을 반환합니다.
     * isFile(): 파일인지 여부를 반환합니다.
     * isDirectory(): 디렉토리인지 여부를 반환합니다.
     * getPermission(): 파일이나 디렉토리의 퍼미션(권한)을 반환합니다.
     * getLink(): 파일이나 디렉토리에 대한 링크 정보를 반환합니다.
     * getUser(): 파일이나 디렉토리를 만든 사용자를 반환합니다.
     * getGroup(): 파일이나 디렉토리를 만든 그룹을 반환합니다.
     * */
    public List<FtpFileVo> getList(FTPClient ftpClient, String ftpFolder) throws IOException {
        List<FtpFileVo> ftpList = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(ftpFolder);
            if(ftpFiles.length>0){
                log.info("FIND FILES IN THE FOLDER ({})",ftpFolder);
                for(FTPFile ftpFile : ftpFiles){
                    FtpFileVo fileVo = FtpFileVo.builder()
                                    .fileNm(ftpFile.getName())
                                    .rgstTime(ftpFile.getTimestamp())
                                    .build();
                    log.info("FIND FILES IN THE FOLDER ({})",fileVo.getFileNm());
                    ftpList.add(fileVo);
                }
            }else{
                log.info("NO FILE IN THE FOLDER ({})",ftpFolder);
            }
            return  ftpList;
        } catch (IOException e) {
            log.error("LISTING ERROR: {}", e.getMessage());
            throw new RuntimeException(e);
        }finally{
            return ftpList;
        }
    }

    public boolean isFileInFtp(FTPClient ftpClient, String ftpFolder, String fileNm) throws IOException {
        boolean isIt = false;
        try {
            List<FtpFileVo> ftpList = getList(ftpClient, ftpFolder);
            if (ftpList != null && !ftpList.isEmpty()) {
                isIt = ftpList.stream()
                        .anyMatch(ftpFileVo -> ftpFileVo.getFileNm().equals(fileNm));
            }
            log.info("RESULT {} IN {}: {}", fileNm, ftpFolder, isIt);
        } catch (IOException e) {
            log.error("CHECKING FAIL : {}", e.getMessage());
        }
        return isIt;
    }


    /** 4. FTP 다운로드 **/

    /**
     *  FTPFile[] listFiles(String pathname) 메서드는 현재 작업 디렉토리에서 파일 및 디렉토리 목록을 가져오는 데 사용됩니다.
     * 이 메서드는 현재 작업 디렉토리에 있는 파일과 디렉토리를 나타내는 FTPFile 객체의 배열을 반환합니다.

     * FileOutputStream은 파일에 바이트 데이터를 쓰기 위한 출력 스트림입니다.
     * 이 클래스는 파일을 생성하거나 이미 존재하는 파일을 덮어쓰는 데 사용됩니다. 데이터 쓰기: write() 메서드 /  파일 닫기: 파일 작업이 완료되면 close() 메서드
     * */

    public boolean download(FTPClient ftpClient, String localPath, String ftpPath) throws FileNotFoundException {
        boolean isDownloaded = false;
        try{
            OutputStream outputStream = new FileOutputStream(localPath);
            isDownloaded = ftpClient.retrieveFile(ftpPath ,outputStream);
            if(isDownloaded){
                log.info("local file path : {}, ftp file path: {}", localPath,ftpPath);
                log.info("isDownload : {}", isDownloaded);
            }else{
                log.info("local file path : {}, ftp file path: {}", localPath,ftpPath);
                log.info("isDownload : {}", isDownloaded);
            }
            outputStream.close();
        }catch (FileNotFoundException e) {
            log.error("CANNOT FIND FILE : {}", e.getMessage());
        } catch (IOException e) {
            log.error(" DOWNLOADING ERROR : {}", e.getMessage());
        } finally {
            return  isDownloaded;
        }
    }

    /** 5. FTP 업로드  **/

    /**
     *  makeDirectory() 메서드는 호출된 시점에서 해당 경로에 디렉토리를 만들지 않는 경우와 만들 경우를 구분하기 위해 다음과 같은 방식으로 동작합니다:
      해당 경로에 이미 디렉토리가 존재하는 경우: 이 경우에는 새로운 디렉토리를 만들 필요가 없으므로 makeDirectory() 메서드는 그냥 종료됩니다.
      이때는 true를 반환하지 않고, 대신에 false를 반환합니다. 즉, 이미 디렉토리가 존재하므로 새 디렉토리를 만들지 않았다는 사실을 알려줍니다.
      해당 경로에 디렉토리가 없는 경우: 이 경우에는 새로운 디렉토리를 만들어야 합니다. makeDirectory() 메서드는 새 디렉토리를 성공적으로 만들면 true를 반환합니다.

     *storeFile() 메서드는 FTP 클라이언트가 로컬 파일을 FTP 서버로 업로드하는 데 사용되는 메서드입니다.
     * */
    public boolean upload(FTPClient ftpClient, String localPath, String ftpFolder, String ftpPath) {
        boolean isUploaded = false;

        try {
            InputStream inputStream = new FileInputStream(localPath);

            ftpClient.makeDirectory(ftpFolder);

            isUploaded = ftpClient.storeFile(ftpPath, inputStream);
            if (isUploaded) {
                log.info("local file path : {}, ftp file path: {}", localPath,ftpPath);

                log.info("isUploaded : {}", isUploaded);
            } else {
                log.info("isUploaded : {}", isUploaded);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            log.error("CANNOT FIND FILE : {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(" UPLOADING ERROR ERROR : {}", e.getMessage());
            throw new RuntimeException(e);
        }finally {
            return isUploaded;
        }

    }



}
