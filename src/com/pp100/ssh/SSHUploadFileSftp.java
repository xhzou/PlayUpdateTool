package com.pp100.ssh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.pp100.exception.RemotingException;
import com.pp100.models.SSHUserInfo;

public class SSHUploadFileSftp {
	
	private SSHRemoteManager manager = SSHRemoteManager.getInstgance();
	private String basePath;

	public static void main(String[] args) {
		SSHUploadFileSftp sftp = new SSHUploadFileSftp();
		SSHUserInfo info = new SSHUserInfo();
		info.setUserName("root");
		info.setPassWord("123Admin");
		info.setIp("192.168.1.111");
		info.setRemotePath("/opt/xhzou");
		info.setUploadFiles(new String[] { "D:\\ADB\\123" });
		sftp.upload(info);
	}

	public void upload(SSHUserInfo info) {
		try {
			String remotePath = info.getRemotePath();
			System.out.println("remotePath:" + remotePath);
			if (!createFolder(info, remotePath)) {
				return;
			}
			for (String s : info.getUploadFiles()) {
				basePath = s;
				upload2Remote(info, new File(s), remotePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void upload2Remote(SSHUserInfo info, File f, String newRemotePath)
			throws RemotingException, FileNotFoundException {
		if (f.isDirectory()) {
			uploadFile(info, f);
		} else if (f.isFile()) {
			manager.uploadFile(info, f, newRemotePath);
		} else {
			Print.warn("file " + f.getAbsolutePath() + " not exists.");
		}
	}

	private void uploadFile(SSHUserInfo info, File file) {
		try {
			String newRemotePath = getNewRemotePath(info, file);
			if (!createFolder(info, newRemotePath)) {
				return;
			}
			File[] allFiles = file.listFiles();
			for (File f : allFiles) {
				upload2Remote(info, f, newRemotePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getNewRemotePath(SSHUserInfo info, File file) {
		String absolutePath = file.getAbsolutePath();
		StringBuffer tmpPath = new StringBuffer(info.getRemotePath());
		String newPath;
		if (absolutePath.endsWith("\\")) {
			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("\\"));
		}
		if (absolutePath.equalsIgnoreCase(basePath)) {
			newPath = tmpPath.toString();
		} else {
			tmpPath.append("/").append(file.getAbsolutePath().replace(basePath, ""));
		}
		newPath = tmpPath.toString().replace("\\", "/");
		newPath = newPath.replace("//", "/");
		System.out.println("newRemotePath: " + newPath);
		return newPath;
	}

	private boolean createFolder(SSHUserInfo info, String path) throws RemotingException, IOException {
		info.setCommand(SSHConstants.MKDIR + path);
		if (SSHConstants.SUCCESS != manager.remoteExecAndResult(info)) {
			Print.error(SSHConstants.MKDIR + " " + path + " failed.");
			return false;
		}
		return true;
	}
}
