package com.taotao.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 提供SFTP处理文件服务
 *
 * @author krm-hehongtao
 * @date 2016-02-29
 *
 */
public class SFTPUtil {
    private JSch jSch = null;
    private ChannelSftp sftp = null;// sftp主服务
    private Channel channel = null;
    private Session session = null;

    private String hostName = "119.29.217.159";// 远程服务器地址
    private int port = 22;// 端口
    private String userName = "root";// 用户名
    private String password = "hhfand&426";// 密码

    public SFTPUtil(String hostName, int port, String userName, String password) {
        this.hostName = hostName;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    /**
     * 连接登陆远程服务器
     *
     * @return
     */
    public boolean connect() throws Exception {
        try {
            jSch = new JSch();
            session = jSch.getSession(userName, hostName, port);
            session.setPassword(password);

            session.setConfig(this.getSshConfig());
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
            System.out.println("登陆成功:" + sftp.getServerVersion());

        } catch (JSchException e) {
            System.err.println("SSH方式连接FTP服务器时有JSchException异常!");
            System.err.println(e.getMessage());
            throw e;
        }
        return true;
    }

    /**
     * 关闭连接
     *
     * @throws Exception
     */
    private void disconnect() throws Exception {
        try {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
            if (channel.isConnected()) {
                channel.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取服务配置
     *
     * @return
     */
    private Properties getSshConfig() throws Exception {
        Properties sshConfig = null;
        try {
            sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");

        } catch (Exception e) {
            throw e;
        }
        return sshConfig;
    }

    /**
     * 下载远程sftp服务器文件
     *
     * @param remotePath
     * @param remoteFilename
     * @param localFilename
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFilename, String localFilename)
            throws SftpException, IOException, Exception {
        FileOutputStream output = null;
        boolean success = false;
        try {
            if (null != remotePath && remotePath.trim() != "") {
                sftp.cd(remotePath);
            }

            File localFile = new File(localFilename);
            // 有文件和下载文件重名
            if (localFile.exists()) {
                System.err.println("文件: " + localFilename + " 已经存在!");
                return success;
            }
            output = new FileOutputStream(localFile);
            sftp.get(remoteFilename, output);
            success = true;
            System.out.println("成功接收文件,本地路径：" + localFilename);
        } catch (SftpException e) {
            System.err.println("接收文件时有SftpException异常!");
            System.err.println(e.getMessage());
            return success;
        } catch (IOException e) {
            System.err.println("接收文件时有I/O异常!");
            System.err.println(e.getMessage());
            return success;
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
                // 关闭连接
                disconnect();
            } catch (IOException e) {
                System.err.println("关闭文件时出错!");
                System.err.println(e.getMessage());
            }
        }
        return success;
    }

    /**
     * 上传文件至远程sftp服务器
     *
     * @param remotePath
     * @param remoteFilename
     * @param localFileName
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFilename, String localFileName)
            throws SftpException, Exception {
        boolean success = false;
        FileInputStream fis = null;
        try {
            // 更改服务器目录
            if (null != remotePath && remotePath.trim() != "") {
                sftp.cd(remotePath);
            }
            File localFile = new File(localFileName);
            fis = new FileInputStream(localFile);
            // 发送文件
            sftp.put(fis, remoteFilename);
            success = true;
            System.out.println("成功发送文件,本地路径：" + localFileName);
        } catch (SftpException e) {
            System.err.println("发送文件时有SftpException异常!");
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("发送文件时有异常!");
            System.err.println(e.getMessage());
            throw e;
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                // 关闭连接
                disconnect();
            } catch (IOException e) {
                System.err.println("关闭文件时出错!");
                System.err.println(e.getMessage());
            }
        }
        return success;
    }

    /**
     * 上传文件至远程sftp服务器
     *
     * @param remotePath
     * @param remoteFilename
     * @param input
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFilename, InputStream input)
            throws SftpException, Exception {
        boolean success = false;
        try {
            // 更改服务器目录
            if (null != remotePath && remotePath.trim() != "") {
                sftp.cd(remotePath);
            }

            // 发送文件
            sftp.put(input, remoteFilename);
            success = true;
        } catch (SftpException e) {
            System.err.println("发送文件时有SftpException异常!");
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("发送文件时有异常!");
            System.err.println(e.getMessage());
            throw e;
        } finally {
            try {
                if (null != input) {
                    input.close();
                }
                // 关闭连接
                disconnect();
            } catch (IOException e) {
                System.err.println("关闭文件时出错!");
                System.err.println(e.getMessage());
            }

        }
        return success;
    }

    /**
     * 删除远程文件
     *
     * @param remotePath
     * @param remoteFilename
     * @return
     * @throws Exception
     */
    public boolean deleteFile(String remotePath, String remoteFilename) throws Exception {
        boolean success = false;
        try {
            // 更改服务器目录
            if (null != remotePath && remotePath.trim() != "") {
                sftp.cd(remotePath);
            }

            // 删除文件
            sftp.rm(remoteFilename);
            System.err.println("删除远程文件" + remoteFilename + "成功!");
            success = true;
        } catch (SftpException e) {
            System.err.println("删除文件时有SftpException异常!");
            e.printStackTrace();
            System.err.println(e.getMessage());
            return success;
        } catch (Exception e) {
            System.err.println("删除文件时有异常!");
            System.err.println(e.getMessage());
            return success;
        } finally {
            // 关闭连接
            disconnect();
        }
        return success;
    }

    /**
     * 遍历远程文件
     *
     * @param remotePath
     * @return
     * @throws Exception
     */
    public List<String> listFiles(String remotePath) throws SftpException {
        List<String> ftpFileNameList = new ArrayList<String>();
        Vector<LsEntry> sftpFile = sftp.ls(remotePath);
        LsEntry isEntity = null;
        String fileName = null;
        Iterator<LsEntry> sftpFileNames = sftpFile.iterator();
        while (sftpFileNames.hasNext()) {
            isEntity = (LsEntry) sftpFileNames.next();
            fileName = isEntity.getFilename();
            System.out.println(fileName);
            ftpFileNameList.add(fileName);
        }
        return ftpFileNameList;
    }

    /**
     * 判断路径是否存在
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public  boolean isExist(String remotePath) throws SftpException {
        boolean flag = false;
        try {
            sftp.cd(remotePath);
            System.out.println("存在路径：" + remotePath);
            flag = true;
        } catch (SftpException sException) {

        } catch (Exception Exception) {
        }
        return flag;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 测试方法
     *
     */
    public static void main(String[] args) {
        try {
            SFTPUtil sftp = new SFTPUtil("119.29.217.159", 22, "root", "hhfand&426");
            System.out.println(new StringBuffer().append(" 服务器地址: ")
                    .append(sftp.getHostName()).append(" 端口：").append(sftp.getPort())
                    .append("用户名：").append(sftp.getUserName()).append("密码:")
                    .append(sftp.getPassword().toString()));
            sftp.connect();
            if (sftp.isExist("/data/wwwroot/default")) {
                sftp.listFiles("/data/wwwroot/default");
//                sftp.downloadFile("/data/wwwroot/default", "S123456_20150126.csv",
//                        "D:\\S123456_20150126.csv");
                 sftp.uploadFile("/data/wwwroot/default", "hello.jpg", "C:\\Users\\Administrator.OTFZ4AK3YCW3FCY\\Desktop\\hello.JPG");
//                 sftp.deleteFile("\\", "test.txt");
            }
        } catch (Exception e) {
            System.out.println("异常信息：" + e.getMessage());
        }
    }
}