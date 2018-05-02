package com.client.server;
import android.graphics.Point;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class ServerDriver {

    private static final int SSH_PORT = 2244;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024;
    private String JSONString;

    ServerDriver(List<Point> points) {
        JSON json = new JSON(points);
        JSONString = json.getJSONString();
        List<String> lines;
        lines = connectAndExecuteListCommand(HOSTNAME, USERNAME, PASSWORD);
        System.out.println(lines);
    }

    public List<String> connectAndExecuteListCommand(String host, String username, String password) {
        List<String> lines = new ArrayList<>();
        try {
            String command = "java -jar Server.jar " + JSONString;
            Session session = initSession(host, username, password);
            Channel channel = initChannel(command, session);
            InputStream in = channel.getInputStream();
            channel.connect();

            String dataFromChannel = getDataFromChannel(channel, in);
            lines.addAll(Arrays.asList(dataFromChannel.split("\n")));
            channel.disconnect();
            session.disconnect();
        } catch (Exception ignored) {
        }
        return lines;
    }

    private Session initSession(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, SSH_PORT);
        session.setPassword(password);

        UserInfo userInfo = new Info();
        session.setUserInfo(userInfo);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(CONNECTION_TIMEOUT);

        return session;
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);

        return channel;
    }

    private String getDataFromChannel(Channel channel, InputStream in)
            throws IOException, InterruptedException {

        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[BUFFER_SIZE];

        while (!channel.isClosed()) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(tmp, 0, i));
            }
            sleep(1000);
        }
        int exitStatus = channel.getExitStatus();
        System.out.println("exit-status: " + exitStatus);
        return result.toString();
    }

}
