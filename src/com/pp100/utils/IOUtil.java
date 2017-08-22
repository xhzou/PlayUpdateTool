package com.pp100.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO包中的工具类，包含关闭各种流、根据输入流读取字符串
 * 
 */
public class IOUtil {

    private static IOUtil instance;

    public static IOUtil getInstance() {
        if (null == instance) {
            instance = new IOUtil();
        }
        return instance;
    }

    /**
     * 关闭输入输出流
     * 
     * @param streams 输入输出流
     */
    public static void closeStream(Closeable... streams) {
        if (null == streams) {
            return;
        }

        for (Closeable stream : streams) {
            if (null != stream) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据输入流读取流之中的内容
     * 
     * @param inStream 输入流
     * @return
     */
    public String getScreen(InputStream inStream) {
        String result = "";
        if (null == inStream) {
            System.out.println("inStream is null.");
            return result;
        }
        byte[] buffer = new byte[10240];
        int read;
        int index = 0;
        try {
            while ((read = inStream.read()) != -1) {
                buffer[index] = (byte) read;
                index++;
            }
            result = new String(buffer, 0, index, System.getProperty("sun.jnu.encoding"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("getStringByInputStream result: "+result);
        return result;
    }

    public static void closeStreams(Closeable... streams) {
        if (null == streams || streams.length < 1) {
            return;
        }
        for (Closeable c : streams) {
            if (null != c) {
                try {
                    c.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
