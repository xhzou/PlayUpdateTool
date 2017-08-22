package com.pp100.workflow;

import static com.pp100.ssh.SSHConstants.WORKFLOW_RUNTYPE_COMMAND;
import static com.pp100.ssh.SSHConstants.WORKFLOW_RUNTYPE_UPLOAD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.pp100.exception.RemotingException;
import com.pp100.frame.DropDragFrame;
import com.pp100.models.SSHUserInfo;
import com.pp100.models.WorkFlowItemInfo;
import com.pp100.ssh.Print;
import com.pp100.ssh.SSHConstants;
import com.pp100.ssh.SSHRemoteManager;
import com.pp100.ssh.SSHUploadFileSftp;
import com.pp100.utils.PropertiesUtil;
import com.pp100.utils.XmlUtils;

public class WorkFlowManager {
    private static WorkFlowManager instance;
    private SSHRemoteManager remoteManager = SSHRemoteManager.getInstgance();
    private Map<String, List<WorkFlowItemInfo>> mapItems = new HashMap<String, List<WorkFlowItemInfo>>();
    String xmlPath = "conf/workflow.xml";

    public static void main(String[] args) {
        SSHUserInfo userInfo = new SSHUserInfo();
        userInfo.setIp(PropertiesUtil.getProperties("dev.evn.ip"));
        userInfo.setUserName(PropertiesUtil.getProperties("dev.evn.username"));
        userInfo.setPassWord(PropertiesUtil.getProperties("dev.evn.password"));
        userInfo.setRemotePath(PropertiesUtil.getProperties("dev.evn.path"));

        try {
            WorkFlowManager.getInstance().startWorkFlow(userInfo);
        } catch (RemotingException e) {
            e.printStackTrace();
        }
    }

    public synchronized static WorkFlowManager getInstance() {
        if (null == instance) {
            instance = new WorkFlowManager();
        }
        return instance;
    }

    public void  startWorkFlow(SSHUserInfo userInfo) throws RemotingException {
        DropDragFrame.log("begin startWorkFlow...");
        WorkFlowManager workFlowManager = WorkFlowManager.getInstance();
        workFlowManager.initWorkFlowItems();

        String key = "default_base";
        List<WorkFlowItemInfo> allItem = new ArrayList<WorkFlowItemInfo>();
        workFlowManager.getAllItems(key, allItem);

        for (WorkFlowItemInfo info : allItem) {
            String runType = info.getRunType();
            String value = info.getValue();

            DropDragFrame.log(runType + "  is " + value);
            
            if (WORKFLOW_RUNTYPE_UPLOAD.equalsIgnoreCase(runType)) {
                userInfo.setUploadFiles(new String[] { value });
                new SSHUploadFileSftp().upload(userInfo);
            } else if (WORKFLOW_RUNTYPE_COMMAND.equalsIgnoreCase(runType)) {
                userInfo.setCommand(value);
                workFlowManager.remoteManager.remoteExec(userInfo);
            } else {
                DropDragFrame.log(runType + " not support. value= " + value);
            }
        }
    }

    /**
     * 解析 xml
     * 
     * @author xhzou
     * @created 2016年8月20日 下午3:25:11
     */
    private void initWorkFlowItems() {
        try {
            Element root = XmlUtils.XmlParse(xmlPath);// 解析XML
            List<Element> list = root.getChildren();
            for (Element e : list) // 遍历 items
            {
                List<Element> subList = e.getChildren();
                List<WorkFlowItemInfo> listItem = new ArrayList<WorkFlowItemInfo>(subList.size());
                for (Element subE : subList) {
                    WorkFlowItemInfo info = new WorkFlowItemInfo();
                    info.setRunType(subE.getAttributeValue("runType"));
                    info.setValue(subE.getTextNormalize());
                    listItem.add(info);
                }
                mapItems.put(e.getAttributeValue("name"), listItem);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllItems(String key, List<WorkFlowItemInfo> allItem) {
        List<WorkFlowItemInfo> tmpItem = mapItems.get(key);
        if (null == tmpItem || tmpItem.size() < 1) {
            return;
        }
        for (WorkFlowItemInfo info : tmpItem) {
            String runType = info.getRunType();
            String value = info.getValue();
            if (SSHConstants.WORKFLOW_RUNTYPE_REF.equalsIgnoreCase(runType)) {
                getAllItems(value, allItem);
            } else {
                allItem.add(info);
            }
        }
    }
}
