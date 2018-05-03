package com.client.server.connection;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ServerForwarder {

    private FileInputStream fileInputStream;

    public ServerForwarder(File image) throws FileNotFoundException {
        fileInputStream = new FileInputStream(image);
    }

    public void connect() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(UserInfo.USER_NAME, UserInfo.HOST, UserInfo.PORT);
            session.setPassword(UserInfo.PASSWORD);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            sftpChannel.put(fileInputStream, "/home/sergey/image.png");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
