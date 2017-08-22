package com.pp100.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * @author xhzou
 * @version 2.0
 * @created 2015年10月25日 下午5:21:00
 */
public class FileUtil {
    /**
     * 使用文件通道的方式复制文件
     * 
     * @author xhzou
     * @version 1.0
     * @created 2015年10月25日 下午5:21:20
     * @param s 源文件
     * @param d 复制到的新文件
     */
    public static void fileChannelCopy(File s, File d) {

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {

            fi = new FileInputStream(s);
            fo = new FileOutputStream(d);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            IOUtil.closeStream(fi, in, fo, out);
        }

    }

    /**
     * 递归删除文件及文件夹
     * 
     * @author xhzou
     * @version 1.0
     * @created 2015年10月25日 下午5:37:48
     * @param path
     */
    public static void delFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            if (f.isDirectory()) {
                File[] fs = f.listFiles();
                if (fs.length > 0) {
                    for (File file : fs) {
                        delFile(file.getAbsolutePath());
                    }
                }
            }
            f.delete();
        }
    }

    /**
     * 获取目录中的所有文件列表
     * 
     * @author xhzou
     * @version 1.0
     * @created 2016年1月20日 下午9:07:05
     * @param list
     * @param path
     */
    public static void getAllFiles(List<String> list, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(list, f.getPath());
                } else {
                    list.add(f.getPath());
                }
            }

        } else {
            list.add(file.getPath());
        }
    }

    /**
     * 写文件
     * 
     * @author xhzou
     * @created 2016年8月6日 下午3:41:48
     * @param file 文件
     * @param content 内容
     */
    public static void writeFile(File file, String content) {
        if (null == file || null == content || content.isEmpty()) {
            System.out.println("content is null.");
            return;
        }
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(bw);
        }
    }

    public static InetAddress getInetAddress() {

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("unknown host!");
        }
        return null;

    }

    public static String getHostIp(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); // get the ip address
        return ip;
    }

    /**
     * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
     * 
     * @param filePath
     */
    public static void readTxtFile(String filePath) {
        try {
            String title = "提现提醒";
            StringBuffer content = new StringBuffer();
            content.append("尊敬的用户，");
            content.append("壹佰金融将接入银行存管，平台历史红包现金及体验金收益暂时无法接入，小佰提醒您在4月30日之前进行提现（需投资才能提现），4月30日后此部分金额将自动转化为双倍以上代金券。若您有任何疑问，欢迎致电平台客服电话400-800-2999，壹佰金融将竭诚为您服务。");
            
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.trim().isEmpty()) {
                       return;
                    }

                    System.out
                            .println("INSERT INTO `t_messages` (`sender_user_id`, `sender_supervisor_id`, `time`, `receiver_user_id`, `receiver_supervisor_id`, `title`, `content`, `is_reply`, `message_id`, `is_erased`, `delete_time`, `read_time`) "
                                    + " VALUES ('0', '1', now(), "
                                    + lineTxt.trim()
                                    + ", '0', '"
                                    + title
                                    + "', '"
                                    + content
                                    + "', 0, '0', '0', NULL, NULL);");
                    // System.out.println(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void main(String argv[]) {
        String filePath = "D:\\123.txt";
        // "res/";
        readTxtFile(filePath);
    }

    /**
     * 文件是否存在
     * 
     * @author xhzou
     * @version 4.0.0
     * @created 2016年8月20日 下午3:18:17
     * @param pathName
     * @return
     */
    public static boolean exists(String pathName) {
        return new File(pathName).exists();
    }

}
