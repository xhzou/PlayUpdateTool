package com.pp100.test;

/**
 * @author xhzou
 * @version 2.0
 * @created 2016年6月30日 下午7:45:46
 */
public class MainTest {
	public static void main(String[] args) {

		String nameClass = "controllers/wechat/service/WechatAction$GameMediaHandler";
		String[] namesArrays = { "controllers/wechat/service/WechatAction" };
		for (String name : namesArrays) {
			System.out.println(name);
			if (nameClass.contains(name)) {
				System.out.println("true");
			}
		}
	}
}
