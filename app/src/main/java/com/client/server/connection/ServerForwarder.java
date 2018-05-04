package com.client.server.connection;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServerForwarder {

    private String fileName;
    private FileInputStream fileInputStream;
    private List<String> serverAnswers = new ArrayList<>();

    public ServerForwarder(File image) throws FileNotFoundException {
        fileInputStream = new FileInputStream(image);
        fileName = fileInputStream.hashCode() + ".png";
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
            sftpChannel.put(fileInputStream, fileName, ChannelSftp.APPEND);
            sftpChannel.disconnect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("java -jar Server.jar " + fileName);
            channel.connect();

            BufferedReader input =
                    new BufferedReader(new InputStreamReader(channel.getInputStream()));
            String serverAnswer;
            while ((serverAnswer = input.readLine()) != null) {
                serverAnswers.add(serverAnswer);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String getMostLikelyResult() {
        if (!serverAnswers.isEmpty()) {
            return serverAnswers.get(1).split(" = ")[1];
        }
        return "I do not know :(";
    }

}
