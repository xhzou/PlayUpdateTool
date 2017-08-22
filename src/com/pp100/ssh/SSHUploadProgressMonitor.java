package com.pp100.ssh;

import javax.swing.ProgressMonitor;

import com.jcraft.jsch.SftpProgressMonitor;

public class SSHUploadProgressMonitor implements SftpProgressMonitor {

	ProgressMonitor monitor;
	long count = 0;
	long max = 0;
	long maxLength = -1L;
	String fileName;

	public void init(int op, String src, String dest, long max) {
		this.max = max;
		if (maxLength < 0) {
			maxLength = max;
			fileName = src;
		}
		monitor = new ProgressMonitor(null, ((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": " + src, "", 0,
				(int) max);
		count = 0;
		percent = -1;
		monitor.setProgress((int) this.count);
		monitor.setMillisToDecideToPopup(50000);
	}

	public long percent = -1;

	public boolean count(long count) {
		this.count += count;

		if (percent >= this.count * 100 / maxLength) {
			return true;
		}
		percent = this.count * 100 / maxLength;
		String msg = "Completed " + this.count + "(" + percent + "%) out of " + maxLength + ".";
		System.out.println(msg);
		monitor.setNote(msg);
		monitor.setProgress((int) this.count);

		return !(monitor.isCanceled());
	}

	public void end() {
		monitor.close();
	}

}
