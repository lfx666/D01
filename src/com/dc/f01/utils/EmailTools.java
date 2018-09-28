package com.dc.f01.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 邮箱工具类 Copyright: Copyright (c) 2014 Company: Fixed Star (XiaMen) Version:
 * 1.0.0 Author: gary Date: 2014-10-14 Description:
 */
public class EmailTools {

	private static Log log = LogFactory.getLog(EmailTools.class);

	private static Properties properties;

	private static String userName;

	private static String password;

	private static String sender;

	private static String protocol;

	private static String host;

	private static String port;

	private EmailTools() {
	}

	public static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			userName = PropertiesUtils.findPropertiesKey("username");
			password = PropertiesUtils.findPropertiesKey("password");
			sender = PropertiesUtils.findPropertiesKey("sender");
			protocol = PropertiesUtils.findPropertiesKey("protocol");
			host = PropertiesUtils.findPropertiesKey("host");
			port = PropertiesUtils.findPropertiesKey("port");
			properties.setProperty("mail.smtp.auth", "true");// 设置验证机制
			properties.setProperty("mail.transport.protocol", protocol);// 发送邮件协议
			properties.setProperty("mail.smtp.host", host);// 设置邮箱服务器地址
			properties.setProperty("mail.smtp.port", port);
		}
		return properties;
	}

	/**
	 * 发送邮箱
	 * 
	 * @param recipient
	 * @param title
	 * @param content
	 * @throws Exception 
	 */
	public static void SendMail(String recipient, String subject, String content) throws Exception {
		FSAuthenticator authenticator = new FSAuthenticator(userName, password);
		Session session = Session.getInstance(getProperties(), authenticator);
		session.setDebug(true);
		Message message = new MimeMessage(session);
		StringBuffer sb = new StringBuffer();
		sb.append(content);
		Transport transport = null;
		try {
			message.setFrom(new InternetAddress(sender));
			message.setSubject(subject);
			message.setRecipients(RecipientType.TO,InternetAddress.parse(recipient));
			message.setSentDate(new Date());
			message.setSubject(MimeUtility.encodeText(subject, "gb2312", "B"));
			message.setContent(sb.toString(), "text/html;charset=gb2312");
			log.info("Message constructed");
			transport = session.getTransport();
			transport.connect(userName, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			log.info("message sent sucessfully!");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				if (transport != null && transport.isConnected())
					transport.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[]arg){
//		EmailTools.SendMail("fixedstar_test@163.com", "我在测试邮件系统", "我们在做邮箱系统,麻烦请查收邮件");
	}
}
