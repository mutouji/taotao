package com.taotao.controller;

import com.taotao.common.utils.FtpUtil;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.IDUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by HuHaifan on 2017/4/19.
 */
public class FTPTest {

    @Test
    public void testFTPClient() throws IOException {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        String basePath = "/data/wwwroot/default";
        String filePath = "/hello";
        String filename = "hello.jpg";
        FileInputStream input = new FileInputStream(new File("C:\\Users\\Administrator.OTFZ4AK3YCW3FCY\\Desktop\\hello.JPG"));
        int reply;
        ftp.connect("123.207.18.157", 21);// 连接FTP服务器
        // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
        System.out.println(ftp.login("ftp", "ftptest"));// 登录
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            System.out.println(result + "1");
        }
        System.out.println(ftp.changeWorkingDirectory(basePath));
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.storeFile( IDUtils.genImageName(), input);


    }

    @Test
    public void redisSync() {
        HttpClientUtil.doGet("http://localhost:8081/rest/cache/sync/content/89");
    }
}