package com.pp100.frame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.pp100.models.SSHUserInfo;
import com.pp100.utils.FileUtil;
import com.pp100.utils.PropertiesUtil;
import com.pp100.utils.zip.ZipCompressor;
import com.pp100.workflow.WorkFlowManager;

/**
 * @author xhzou
 * @version 2.0
 * @created 2015年10月25日 下午4:00:53
 */
public class DropDragFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton btnClose = new JButton("C 关闭");
    private JButton btnGenerate = new JButton("G 生成");
    private JTextField txtCodePath = new JTextField();
    private JTextField txtPlayPath = new JTextField("3rd_tools/play1.2.7");
    private JTextArea txtAreaFileList = new JTextArea();
    private static JTextArea txtAreaLog = new JTextArea();
    private JCheckBox cbxAutoDeploy = new JCheckBox("自动部署");
    private JLabel lblVersion = new JLabel("2016.01 ShenZhen China zxiaohui@vip.qq.com");
    private boolean isCMDMode = false;
    private Process process = null;

    String specifyJava = "";
    String specifyHtml = "";
    String specifyOther = "";
    private String compileLogInfo = System.getProperty("java.io.tmpdir") + "/compile_info.log";

    String pathSeparator = System.getProperty("path.separator");

    public DropDragFrame() {
        initFrameInfo();
        initControl();
        initConfValue();
        bindEvent();
    }

    /**
     * 仅用于 CMD 模式
     * 
     * @param codePath
     * @param playPath
     */
    public DropDragFrame(boolean _isCMDMode) {
        this();
        isCMDMode = _isCMDMode;

        try {
            getCMDAllFiles();
        } catch (IOException e) {
            logError(e.getMessage());
            e.printStackTrace();
            return;
        }

        // 启动编译 准备好局部更新包
        createVersionFile();
    }

    private void getCMDAllFiles() throws FileNotFoundException, IOException {
        String regex = PropertiesUtil.getProperties("regex", "^\\s(app/|conf/|public/){1}.*\\s+\\|\\s+(\\d+\\s+\\++\\-*|Bin)\\s*$");
        String replace_regex = PropertiesUtil.getProperties("replace_regex", "\\|(\\d+\\s+\\++\\-*|Bin)\\s*");

        String tmpFilePath = System.getProperty("java.io.tmpdir") + "/changelist.log";
        File file = new File(tmpFilePath);
        if (!file.exists()) {
            System.out.println("file not exits");
            throw new FileNotFoundException("specify " + tmpFilePath + " file is not exists...");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuffer bufFiles = new StringBuffer();
        String data = null;
        while ((data = reader.readLine()) != null) {
            if (data.matches(regex)) {
                data = data.replaceAll("\\s", "").replaceAll(replace_regex, "");
                bufFiles.append("/").append(data).append("\n");
            }
        }
        reader.close();
        log("update file list: \n" + bufFiles);
        if (bufFiles.toString().isEmpty()) {
            throw new IOException("未找到修改或新增文件.详情请查看  " + tmpFilePath);
        }
        txtAreaFileList.setText(bufFiles.toString());
    }

    /*
     * 用于 svn 模式，hg 在此方面有差异 private void getCMDAllFiles() throws FileNotFoundException, IOException {
     * 
     * String tmpFilePath=System.getProperty("java.io.tmpdir") + "/changelist.log"; File file = new File(tmpFilePath); if (!file.exists()) {
     * throw new FileNotFoundException("specify "+tmpFilePath+" file is not exists..."); } BufferedReader reader = new BufferedReader(new
     * InputStreamReader(new FileInputStream(file))); StringBuffer bufFiles = new StringBuffer(); String strA="A       "; String
     * strM="M       "; String data=null; while ((data = reader.readLine()) != null) { if (data.startsWith(strA) || data.startsWith(strM)) {
     * data = data.replace(strA, "").replace(strM, "").replace("\\", "/"); bufFiles.append("/").append(data).append("\n"); } }
     * reader.close(); println("update file list: "+bufFiles); if(bufFiles.toString().isEmpty()){ throw new IOException("未找到修改或新增文件.详情请查看  "
     * + tmpFilePath); } areaFileList.setText(bufFiles.toString()); }
     */
    private void initConfValue() {
        txtCodePath.setText(PropertiesUtil.getProperties("code_path", "D:/code/p2p/trunk/code/com.shovesoft.sp2p"));
    }

    /**
     * 初始化控件信息
     * 
     * @author xhzou
     * @version 1.0
     * @created 2015年10月25日 下午5:42:38
     */
    private void initControl() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createTitledBorder("文件列表"));
        scrollPane.setViewportView(txtAreaFileList);
        txtAreaFileList.setColumns(70);
        txtAreaFileList.setRows(14);

        btnClose.setMnemonic('C');
        btnGenerate.setMnemonic('G');
        cbxAutoDeploy.setMnemonic('A');

        txtCodePath.setPreferredSize(new Dimension(250, 20));
        txtPlayPath.setPreferredSize(new Dimension(350, 20));
        lblVersion.setPreferredSize(new Dimension(650, 20));

        JPanel panel = new JPanel();
        panel.add(new JLabel("代码路径："));
        panel.add(txtCodePath);
        panel.add(new JLabel("Play路径："));
        panel.add(txtPlayPath);
        txtPlayPath.setEditable(false);

        JScrollPane scrollPaneLog = new JScrollPane();
        scrollPaneLog.setBorder(BorderFactory.createTitledBorder("日志信息"));
        txtAreaLog.setColumns(70);
        txtAreaLog.setRows(10);
        scrollPaneLog.setViewportView(txtAreaLog);

        panel.add(scrollPane);
        panel.add(scrollPaneLog);
        panel.add(cbxAutoDeploy);
        panel.add(btnGenerate);
        panel.add(btnClose);
        panel.add(lblVersion);

        this.add(panel);
    }

    /**
     * 初始化窗体信息
     * 
     * @author xhzou
     * @version 1.0
     * @created 2015年10月25日 下午5:42:29
     */
    private void initFrameInfo() {
        this.setTitle("Play 局部编译+局部更新包生成器+自动部署包 V4.1.0 by 邹小辉");
        this.setSize(800, 630);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            this.setIconImage(ImageIO.read(this.getClass().getResource("/images/icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 绑定按钮事件
     * 
     * @author xhzou
     * @version 1.0
     * @created 2015年10月25日 下午5:42:16
     */
    private void bindEvent() {
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        btnGenerate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                txtAreaLog.setText("");
                createVersionFile();
            }
        });
    }

    private void createVersionFile() {
        if (isCMDMode) {
            log("please wait...");
        } else {
            if (txtAreaFileList.getText().isEmpty()) {
                logWarn("not specify any files...");
                return;
            }
        }
        String[] specifyFiles = txtAreaFileList.getText().split("\n");

        // 1.编译包
        initSpecifyFileClassify(specifyFiles);
        long start = System.currentTimeMillis();

        if (!specifyJava.isEmpty()) {
            int exitCode = execCommand(getCompileCmd());

            if (0 != exitCode) {
                logFatal("compile error... program exit.");
                return;
            }
        }

        String codePath = txtCodePath.getText().trim();
        // if (cbxOnlyJava.isSelected()) {
        // println("compile all java,not contains template.");
        // execCommand("cmd /c start " + codePath + "/precompiled");
        // return;
        // }

        // 2.准备局部更新包文件
        File applicationFile = copyCompileFiles(specifyFiles, codePath);

        // 3.分完类后打开此 tmp 路径 ，供手工打包上传，并执行自动化部署脚本
        if (isCMDMode) {
            log("update package path=" + applicationFile.getAbsolutePath());
        } else {
            if (cbxAutoDeploy.isSelected()) {
                autoDeploy2Server();
            } else {
                execCommand("cmd /c start " + applicationFile.getAbsolutePath());
            }
        }
        long seconds = (System.currentTimeMillis() - start) / 1000;
        log("finish. use time=" + seconds + "s");
    }

    /**
     * 自动部署至服务器
     * 
     * @author xhzou
     * @version 4.0.0
     * @created 2016年8月20日 下午3:13:10
     */
    private void autoDeploy2Server() {
        // 1.压缩 zip 包
        String tmpdir = System.getProperty("java.io.tmpdir");
        String zipFile = tmpdir + "/application.zip";

        ZipCompressor zc = new ZipCompressor(zipFile);
        try {
            zc.compress(tmpdir + "/application");
        } catch (Exception e) {
            logError(e.getMessage());
            return;
        }

        if (!FileUtil.exists(zipFile)) {
            logError("compress package has probleam,please check!");
            return;
        }

        log("compress application.zip package success!");
        
        // 2.调用工作流
        SSHUserInfo userInfo = new SSHUserInfo();
        userInfo.setIp(PropertiesUtil.getProperties("dev.evn.ip"));
        userInfo.setUserName(PropertiesUtil.getProperties("dev.evn.username"));
        userInfo.setPassWord(PropertiesUtil.getProperties("dev.evn.password"));
        userInfo.setRemotePath(PropertiesUtil.getProperties("dev.evn.path"));
        
        log(userInfo.getIp() +" remotePath: "+ userInfo.getRemotePath());
        
        try {
            WorkFlowManager.getInstance().startWorkFlow(userInfo);
        } catch (Exception e) {
            logError(e.getMessage());
        }

    }

    /**
     * 准备局部更新包文件
     * 
     * @author xhzou
     * @version 1.0
     * @created 2016年1月24日 下午2:03:09
     * @param specifyFiles 指定要局部打包的文件数组
     * @param codePath 代码路径
     * @return
     */
    private File copyCompileFiles(String[] specifyFiles, String codePath) {
        String tmpApplicationPath = System.getProperty("java.io.tmpdir") + "/application";
        File applicationFile = new File(tmpApplicationPath);
        // 1.在 tmp 下生成 application 目录，其下有三个子目录 app precompiled public
        // 存在则删除并重新创建
        if (applicationFile.exists()) {
            FileUtil.delFile(applicationFile.getAbsolutePath());
            applicationFile.mkdir();
        }

        for (String filePath : specifyFiles) {
            if (null == filePath || filePath.isEmpty()) {
                continue;
            }
            filePath = filePath.trim().replace("\\", "/");// 去除左右空格
            File codeFile = new File(codePath + filePath);// 代码源文件
            if (!codeFile.exists() && !filePath.contains("$")) // 过滤掉 $1*.java
                                                               // 的情况
            {
                logWarn("source file not exists. " + filePath);
                continue;
            }

            String path = getCompilePath(filePath);
            if (path.isEmpty()) {
                continue;
            }

            // 复制预编译后的文件到 %tmp%/application
            File sFile = new File(codePath + path);// 预编译源文件
            if (!sFile.exists() || sFile.isDirectory()) {
                logWarn("precompiled file not exists ... " + filePath);
                continue;
            }
            File dFile = new File(tmpApplicationPath + "/" + path);// 目标文件
            dFile.getParentFile().mkdirs();
            FileUtil.fileChannelCopy(sFile, dFile);

            // 检测是否存在内部类的 class 文件
            if (sFile.getName().contains(".class")) {
                File[] subClass = sFile.getParentFile().listFiles();
                for (File f : subClass) {
                    if (f.getName().contains(sFile.getName().replace(".class", "") + "$")) {
                        FileUtil.fileChannelCopy(f, new File(dFile.getParent() + "/" + f.getName()));
                    }
                }
            }
            
            // 复制java 或 html 源文件
            if (filePath.endsWith(".java")) {
                File file = new File(tmpApplicationPath + "/" + filePath);
                file.getParentFile().mkdirs();
                FileUtil.fileChannelCopy(new File(codePath + filePath), file);
            }
        }
        return applicationFile;
    }

    private int execCommand(String openCmd) {
        int exitCode = -1;
        try {
            process = Runtime.getRuntime().exec(openCmd);
            exitCode = process.waitFor();

            // File logFile = new File(compileLogInfo);
            // String content = IOUtil.getInstance().getScreen(process.getInputStream());
            // FileUtil.writeFile(logFile, content);
        } catch (Exception ex) {
            logError(ex.getMessage());
        }
        return exitCode;
    }

    /**
     * 初始指定文件的分类
     * 
     * @param specifyFiles
     */
    private void initSpecifyFileClassify(String[] specifyFiles) {
        if (null == specifyFiles) {
            return;
        }
        StringBuffer bufHtml = new StringBuffer();
        StringBuffer bufJava = new StringBuffer();
        StringBuffer bufOther = new StringBuffer();

        for (String s : specifyFiles) {
            // 20160820 兼容前后端分离的静态文件打包 start
            if (isStaticIndexHtml(s) || s.contains("/static")) {
                continue;
            }
            // end

            if (s.endsWith(".java")) {
                bufJava.append(s).append(pathSeparator);
            } else if (s.endsWith(".html") || s.endsWith(".control")) {
                bufHtml.append(s).append(pathSeparator);
            } else {
                bufOther.append(s).append(pathSeparator);
            }
        }

        specifyHtml = bufHtml.toString();
        // 20160630兼容子类的情况与play同版本的jar一起使用
        specifyJava=bufJava.toString().replaceAll("\\\\", "/").replace(".java", "").replaceFirst("app/", "");;

        specifyOther = bufOther.toString();
    }

    /**
     * @author xhzou
     * @version 4.0.0
     * @created 2016年8月20日 下午2:09:22
     * @param filePath
     * @return
     */
    private boolean isStaticIndexHtml(String filePath) {
        return "/index.html".equals(filePath);
    }

    private String getCompileCmd() {
//        String userDir = System.getProperty("user.dir");
        String applicationPath = txtCodePath.getText().trim();
        String playPath = txtPlayPath.getText().trim();
        // String playPath = "3rd_tools/play1.2.7";

        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append("3rd_tools/java/bin/java");
        cmdBuf.append(" -javaagent:" + playPath + "/framework/play-1.2.7.jar");
        cmdBuf.append(" -Dprecompile=yes -DskipTemplates=true -XX:-UseSplitVerifier -Dfile.encoding=utf-8 -XX:CompileCommand=exclude");
        cmdBuf.append(",jregex/Pretokenizer,next -Xdebug -Xrunjdwp:transport=dt_socket,address=0,server=y,suspend=n");
        cmdBuf.append(" -Xms256m -Xmx512m -XX:PermSize=256M -XX:MaxPermSize=512m");
        cmdBuf.append(" -Dplay.debug=yes");
        cmdBuf.append(" -classpath " + applicationPath + "/conf").append(pathSeparator);
        cmdBuf.append(playPath + "/framework/play-1.2.7.jar").append(pathSeparator);
        cmdBuf.append(applicationPath + "/lib/*").append(pathSeparator);
        cmdBuf.append(playPath + "/framework/lib/*").append(pathSeparator);
        cmdBuf.append(" -Dapplication.path=" + applicationPath);
        cmdBuf.append(" -Dplay.id='' ");
        cmdBuf.append(" -DspecifyJava=" + specifyJava);
        cmdBuf.append(" play.server.Server  ");

        System.out.println(cmdBuf);
        logInfo(cmdBuf);
        return cmdBuf.toString();
    }

    /**
     * 获取编译后文件路径
     * 
     * @author xhzou
     * @version 1.0
     * @created 2016年1月24日 下午1:57:47
     * @param filePath
     * @return
     */
    private String getCompilePath(String filePath) {
        String path = "";
        boolean isFtlFile = filePath.endsWith(".ftl");

        if (filePath.contains("app/views/")) {
            path = filePath;
        } else if (filePath.contains("app/") && !isFtlFile) {
            path = "/precompiled/java/" + filePath;
            path = path.replace(".java", ".class").replaceFirst("app/", "");
        } else if (filePath.contains("public/") || filePath.contains("conf/") || isFtlFile || filePath.contains("static/")
                || isStaticIndexHtml(filePath)) {
            path = filePath;
        } else {
            logWarn(" source file not support... " + filePath);
        }

        return path;
    }

    public static void logInfo(StringBuffer msg) {
        log(msg.toString());
    }

    public static void logWarn(String msg) {
        refreshLogInfo("[WARN] " + msg);
    }

    public static void logError(String msg) {
        refreshLogInfo("[ERROR] " + msg);
    }

    public static void logFatal(String msg) {
        refreshLogInfo("[FATAL] " + msg);
    }

    public static void log(String msg) {
        refreshLogInfo("[INFO] " + msg);
    }

    /**
     * 即时刷新正在执行的日志信息
     * @author xhzou
     * @version 4.0.0
     * @created 2016年8月20日 下午6:17:48
     * @param msg
     */
    private static void refreshLogInfo(String msg) {
        txtAreaLog.append(msg + "\n");

        txtAreaLog.paintImmediately(txtAreaLog.getBounds());
    }
}
