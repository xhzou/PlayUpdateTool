package com.pp100.main;

import java.io.IOException;

import com.pp100.frame.DropDragFrame;

/**
 * @author xhzou
 * @version 2.0
 * @created 2015年10月25日 下午5:41:02
 */
public class PlayUpdateTool {
	public static void main(String[] args) throws IOException {
		if (args.length > 0 && "cmd".equalsIgnoreCase(args[0])) {
			DropDragFrame frame = new DropDragFrame(true);
			frame.setVisible(false);
		} else {
			DropDragFrame frame = new DropDragFrame();
			frame.setVisible(true);
		}
	}
}
