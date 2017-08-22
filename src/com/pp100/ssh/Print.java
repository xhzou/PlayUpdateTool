package com.pp100.ssh;

public class Print {
	public static void info(String msg) {
		System.out.println(" [ INFO ] " + msg);
	}

	public static void warn(String msg) {
		System.out.println(" [ WARN ] " + msg);
	}

	public static void error(String msg) {
		System.out.println(" [ ERROR ] " + msg);
	}

	public static void fatal(String msg) {
		System.out.println(" [ FATAL ] " + msg);
	}
}
