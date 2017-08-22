package com.pp100.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pp100.exception.RemotingException;
import com.pp100.models.SSHUserInfo;

public class SSHRemoteManager {
    private static SSHRemoteManager instance;
    private JSch jsch = new JSch();
    private Session session;
    private Channel channel;
    private int timeout = 30000;

    public static void main(String[] args) {
        SSHUserInfo info = new SSHUserInfo();
        info.setUserName("root");
        info.setPassWord("elc@123");
        info.setIp("192.168.6.11");
        info.setCommand("cat /root/install.log");
        try {
            System.out.println("output:\n " + SSHRemoteManager.getInstgance().remoteExec(info));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSHRemoteManager getInstgance() {
        if (null == instance) {
            instance = new SSHRemoteManager();
        }
        return instance;
    }

    public String remoteExec(SSHUserInfo info) throws RemotingException {
        StringBuffer buffer = new StringBuffer();
        try {
            initSession(info);
            channel = session.openChannel(SSHConstants.CHANNEL_TYPE_EXEC);
            ((ChannelExec) channel).setCommand(info.getCommand());
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            waitExecFinished(in);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        } finally {
            close();
        }
        return buffer.toString();
    }

    public int remoteExecAndResult(SSHUserInfo info) throws RemotingException, IOException {
        return remoteExecAndResult(info, null);
    }

    public int remoteExecAndResult(SSHUserInfo info, StringBuffer buffer) throws RemotingException, IOException {
        int result = SSHConstants.FAILED;
        try {
            initSession(info);
            channel = session.openChannel(SSHConstants.CHANNEL_TYPE_EXEC);
            ((ChannelExec) channel).setCommand(appendGetResultCommand(info.getCommand()));
            channel.connect();
            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String lastLine = null;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (null != buffer) {
                    buffer.append(line);
                }
                lastLine = line;
            }
            if (null != lastLine && !lastLine.isEmpty()) {
                try {
                    result = Integer.parseInt(lastLine.trim());
                } catch (NumberFormatException e) {
                    result = SSHConstants.FAILED;
                }
            }
            waitExecFinished(in);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        } finally {

            close();
        }
        return result;
    }

    public void uploadFile(SSHUserInfo info, File f, String newRemotePath) throws RemotingException, FileNotFoundException {
        try {
            initSessionChannel(info, SSHConstants.CHANNEL_TYPE_SFTP);
            ((ChannelSftp) channel).cd(newRemotePath);
            ((ChannelSftp) channel).put(new FileInputStream(f), f.getName());
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * 追加获取命令执行是否成功的命令
     * 
     * @param command 需执行的命令
     * @return
     */
    private String appendGetResultCommand(String command) {
        if (null != command && !command.isEmpty()) {
            if (command.endsWith(";")) {
                command += "echo $?";
            } else {
                command += ";echo $?";
            }
        }
        return command;
    }

    private void initSession(SSHUserInfo info) throws JSchException {
        session = jsch.getSession(info.getUserName(), info.getIp(), SSHUserInfo.SSH_PORT);
        session.setPassword(info.getPassWord());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(timeout);
    }

    private void initSessionChannel(SSHUserInfo info, String channType) throws JSchException {
        initSession(info);
        channel = session.openChannel(channType);
        channel.connect();
    }

    private void close() {
        if (null != session && session.isConnected()) {
            session.disconnect();
        }
    }

    private void waitExecFinished(InputStream in) throws IOException {
        while (true) {
            if (channel.isClosed()) {
                if (in.available() > 0)
                    continue;
                Print.info("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(500);
            } catch (Exception ee) {
            }
        }
    }

}
