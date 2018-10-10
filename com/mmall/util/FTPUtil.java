package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ${aimimi2015} on 2017/6/15.
 */

@Slf4j
public class FTPUtil {

//    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);


    public static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    public static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    public static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        log.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        log.info("开始连接ftp服务器,结束上传,上传结果:{}");
        return result;
    }
    //remote指的是上传到ftp那个文件夹下的相对路径       List<File>预备实现批量上传
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);   //缓冲区大小
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);    //以二进制文件格式传输
                ftpClient.enterLocalPassiveMode();    //打开本地的被动模式
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }

            } catch (IOException e) {
                log.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                //释放链接
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;

    }
    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);

        } catch (IOException e) {

            log.error("链接ftp服务器异常",e);
        }

        return isSuccess;

    }


    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}

