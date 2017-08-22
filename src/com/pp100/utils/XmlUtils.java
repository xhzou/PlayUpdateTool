package com.pp100.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.pp100.models.WorkFlowItemInfo;
import com.pp100.ssh.SSHConstants;

public class XmlUtils {
    public static Element XmlParse(String xmlPath) throws JDOMException, IOException {
        SAXBuilder jdom = new SAXBuilder();
        InputStream file = new FileInputStream(xmlPath);
        Document doc = jdom.build(file);// 获得文档对象
        Element root = doc.getRootElement();// 获得根节点
        return root;
    }

}
