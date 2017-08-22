package com.pp100.ssh;

public class SSHConstants {

    /** 远程命令执行失败 */
    public static final int SUCCESS = 0;
    /** 远程命令执行成功 */
    public static final int FAILED = 0;

    public static final String MKDIR = "mkdir -p ";
    /** 传输或下载文件夹 */
    public static final String CHANNEL_TYPE_SFTP = "sftp";
    /** 执行命令 */
    public static final String CHANNEL_TYPE_EXEC = "exec";

    public static final String WORKFLOW_RUNTYPE_UPLOAD = "upload";

    public static final String WORKFLOW_RUNTYPE_COMMAND = "command";

    public static final String WORKFLOW_RUNTYPE_SHELL = "shell";

    public static final String WORKFLOW_RUNTYPE_REF = "ref";

}
