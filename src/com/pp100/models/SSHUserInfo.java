package com.pp100.models;

public class SSHUserInfo {
	public static final int SSH_PORT = 22;
	private String ip;
	private String userName;
	private String passWord;
	private String command;
	private String remotePath;
	private String[] uploadFiles;

	public SSHUserInfo() {

	}

	public SSHUserInfo(String ip, String userName, String passWord, String command, String remotePath,
			String[] uploadFiles) {
		this(ip, userName, passWord, command);
		this.remotePath = remotePath;
		this.uploadFiles = uploadFiles;
	}

	public SSHUserInfo(String ip, String userName, String passWord, String command) {
		this(ip, userName, command);
		this.passWord = passWord;
	}

	public SSHUserInfo(String ip, String userName, String command) {
		super();
		this.ip = ip;
		this.userName = userName;
		this.command = command;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String[] getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(String[] uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserName() {
		return userName;

	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
