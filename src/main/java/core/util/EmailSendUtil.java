package core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.sound.midi.SysexMessage;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import core.sysconst.SysEmailConst;
import core.vo.EMailVO;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 17:01
 */
public class EmailSendUtil {
    private static Logger logger = LogUtil.getLogger(EmailSendUtil.class);
    public static final String EMAILBOTTOM = "";
    private static StringBuffer SingleText = new StringBuffer(); // 存放纯文本
    private static StringBuffer bodyBlodString = new StringBuffer(); // 存放内容，HTML格式文件
    private static StringBuffer attachmentName = new StringBuffer(); // 存放附件名称，可能有多个，用,隔开
    private static List<Map<String, Object>> contentList = new ArrayList<Map<String, Object>>(); // 正文内容,可能包含图片
    private static List<Map<String, Object>> attachmentList = new ArrayList<Map<String, Object>>(); // 附件内容

    /**
     * 发送系统邮件</br>
     * <p>
     * 接收人， 邮件主题必输入
     * </P>
     * <p>
     * 多个收件人， 抄送人， 密送人，附件以分号（;）分割
     * </p>
     * <p>
     * 附件的大小不可超过系统设置的最大值（目前为1MB）
     * </p>
     * <p/>
     * 邮件内容后面会添加“系统邮件，请勿回复！”的提示
     * </P>
     *
     * @param to      收件人
     * @param cc      抄送人
     * @param bcc     密送人
     * @param title   主题
     * @param content 内容
     * @param file    附件
     * @return true-发送成功;false-发送失败
     * @throws Exception
     * @author zhaolimin
     * @date 2014-3-26 上午10:20:27
     */
    public static boolean sendSysEmail(String to, String cc, String bcc,
                                       String title, String content, String file) throws Exception {
        return sendSysEmail(to, cc, bcc, title, null, content, file, 1);
    }

    /**
     * 发送系统邮件-可设置回复邮箱</br>
     * <p>
     * 接收人， 邮件主题必输入
     * </P>
     * <p>
     * 多个收件人， 抄送人， 密送人，回复邮箱，附件以分号（;）分割
     * </p>
     * <p>
     * 若回复邮箱为空则回复邮箱为发件人
     * </p>
     * <p>
     * 附件的大小不可超过系统设置的最大值（目前为1MB）
     * </p>
     * <p/>
     * flag=1, 在邮件内容后面回家上"系统邮件，请勿回复！"的提示
     * </P>
     *
     * @param to      收件人
     * @param cc      抄送人
     * @param bcc     密送人
     * @param replyTo 回复邮箱
     * @param title   主题
     * @param content 内容
     * @param file    附件
     * @return true-发送成功;false-发送失败
     * @throws Exception
     */
    public static boolean sendSysEmail(String to, String cc, String bcc,
                                       String title, String replyTo, String content, String file,
                                       Integer flag) throws Exception {
        if (null != to) {
            String host = SysEmailConst.EMAILHOST;// 获取主机
            String userName = SysEmailConst.EMAILSENDUSER;// 获取用户
            String pwd = SysEmailConst.EMAILSENDPWD;// 获取密码
            // by yuxi
            String nick = SysEmailConst.NICK;// 获取昵称
            String from = "";
            if (userName.indexOf("@") > 0) {
                from = userName;
            } else {
                from = userName + SysEmailConst.EMAILSUFFIX;// 根据用户和系统中存放的邮件后缀拼写发件人
            }
            if (flag == 1) {
                content += EMAILBOTTOM;// 在邮件内容后面加上“系统邮件，请勿回复！”的提示
            }
            EMailVO eMail = new EMailVO(host, userName, nick, from, pwd, to,
                    cc, bcc, replyTo, title, content, file);// 以全参构造邮件实体类
            boolean result = sendEmail(eMail);// 发送
            if (!result) {
                LogUtil.error(EmailSendUtil.class,
                        "系统邮件发送错误信息：" + eMail.getErrorMsg());
                System.out.println("系统邮件发送错误信息：" + eMail.getErrorMsg());
            }
            return result;
        }
        return false;
    }

    /**
     * 发送邮件</br>
     * <p>
     * 发送主机， 发件人，发件用户名（一般为发件人@前面的那一部分）， 发件密码， 接收人， 邮件主题必输入
     * </p>
     * <p>
     * 多个收件人， 抄送人， 密送人，附件以分号（;）分割
     * </P>
     * <p>
     * 附件的大小不可超过系统设置的最大值（目前为1MB）
     * </p>
     *
     * @param eMail 邮件实体类
     * @return true-发送成功;false-发送失败,若返回false，在EMailVO中errorMsg存放有错误信息
     * @throws Exception
     */
    public static boolean sendEmail(EMailVO eMail) throws Exception {
        boolean valid = validEmailConfig(eMail);// 对邮件信息进行校验
        if (!valid) {
            return false;
        }
        Properties properties = new Properties();
        properties.put("mail.smtp.host", eMail.getHost());// 存储发送邮件服务器的信息
        properties.put("mail.smtp.auth", "true");// 同时通过验证
        Session session = Session.getDefaultInstance(properties);// 根据属性新建一个邮件会话
        MimeMessage message = new MimeMessage(session);// 由邮件会话新建一个消息对象
        setFrom(eMail, message);// 添加发件人
        setReplyTo(eMail, message);// 添加回复邮箱
        setTo(eMail, message);// 添加收件人
        if (eMail.getCc() != null && !"".equals(eMail.getCc().trim())) {
            setCc(eMail, message);// 添加抄送人
        }
        if (eMail.getBcc() != null && !"".equals(eMail.getBcc().trim())) {
            setBcc(eMail, message);// 添加密送人
        }
        try {
            message.setSubject(eMail.getTitle());// 添加主题
            message.setContent(eMail.getContent(), "text/html;charset=utf-8");
            setText(message, eMail);// 添加邮件内容，HTML格式
            message.setSentDate(new Date());// 添加发送日期
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(eMail.getHost(), eMail.getUserName(),
                    eMail.getPwd());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (SendFailedException e) {
            // by yuxi
            // 1.在catch里拿到SendFailedException,
            // 2.校验下是否能转为SMTPAddressFailedException如果能转型，则转型
            // 3.从SMTPAddressFailedException里能拿到校验不通过的用户，从收件人地址里删除，然后重发即可多了一次发送请求而已。
            Address[] addresses = e.getValidUnsentAddresses();// 找到有效但是没有发出邮件的邮箱
            // 重新再发一次邮件
            if (addresses.length > 0) {
                sendMailWhenException(addresses, eMail);
            } else {
                e.printStackTrace();
            }
            LogUtil.error(EmailSendUtil.class,
                    eMail.getTitle() + "_" + eMail.getRecivers() + "_"
                            + "系统邮件发送错误信息：" + eMail.getErrorMsg());
            LogUtil.error(EmailSendUtil.class, e);
        }

        return true;
    }

    // 重新发送一遍除去无效的邮箱
    public static boolean sendMailWhenException(Address[] addresses,
                                                EMailVO eMail) throws Exception {
        boolean valid = validEmailConfig(eMail);// 对邮件信息进行校验
        if (!valid) {
            return false;
        }
        Properties properties = new Properties();
        properties.put("mail.smtp.host", eMail.getHost());// 存储发送邮件服务器的信息
        properties.put("mail.smtp.auth", "true");// 同时通过验证
        Session session = Session.getDefaultInstance(properties);// 根据属性新建一个邮件会话
        MimeMessage message = new MimeMessage(session);// 由邮件会话新建一个消息对象
        setFrom(eMail, message);// 添加发件人
        setReplyTo(eMail, message);// 添加回复邮箱
        // setTo(eMail, message);// 添加收件人
        message.addRecipients(Message.RecipientType.TO, addresses);
        if (eMail.getCc() != null && !"".equals(eMail.getCc().trim())) {
            setCc(eMail, message);// 添加抄送人
        }
        if (eMail.getBcc() != null && !"".equals(eMail.getBcc().trim())) {
            setBcc(eMail, message);// 添加密送人
        }
        try {
            message.setSubject(eMail.getTitle());// 添加主题
            message.setContent(eMail.getContent(), "text/html;charset=utf-8");
            setText(message, eMail);// 添加邮件内容，HTML格式
            message.setSentDate(new Date());// 添加发送日期
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(eMail.getHost(), eMail.getUserName(),
                    eMail.getPwd());
            transport.sendMessage(message, addresses);
            transport.close();
        } catch (Exception e) {
            LogUtil.error(EmailSendUtil.class,
                    eMail.getTitle() + "_" + eMail.getRecivers() + "_"
                            + "系统邮件发送错误信息：" + eMail.getErrorMsg());
            LogUtil.error(EmailSendUtil.class, e);
        }
        return true;
    }

    /**
     * 设置回复邮箱
     *
     * @param eMail
     * @param message
     * @throws MessagingException
     * @author zhaolimin
     * @date 2014-7-1 上午10:25:35
     */
    private static void setReplyTo(EMailVO eMail, MimeMessage message)
            throws MessagingException {
        List<InternetAddress> replyToList = new ArrayList<InternetAddress>();
        String[] replyToArray;
        if (eMail.getReplyTo() != null && !"".equals(eMail.getReplyTo().trim())) {
            replyToArray = eMail.getReplyTo().split(";");
            for (String replyTo : replyToArray) {
                InternetAddress replyToAdd = new InternetAddress(replyTo);// 设置收件人
                replyToList.add(replyToAdd);
            }
        } else {
            InternetAddress replyToAdd = new InternetAddress(eMail.getFrom()
                    .trim());// 设置收件人
            replyToList.add(replyToAdd);
        }

        Address[] replyTos = new Address[replyToList.size()];
        for (int i = 0; i < replyToList.size(); i++) {
            replyTos[i] = replyToList.get(i);
        }
        message.setReplyTo(replyTos);
    }

    /**
     * 对email的信息校验</br> 校验内容：
     * <p>
     * 发送主机， 发件人，用户名， 发件密码， 接收人， 邮件主题是否非空
     * </P>
     * <p>
     * 发件人，收件人，抄送人，密送人的格式是否正确
     * </p>
     * <p>
     * 附件是否存在和附件的大小是否超过了设置的大小
     * </p>
     *
     * @param eMail
     * @return
     * @throws Exception
     * @author zhaolimin
     * @date 2014-3-25 下午3:31:40
     */
    private static boolean validEmailConfig(EMailVO eMail) throws Exception {
        if (!validEmailEmpty(eMail)) {// 校验发送主机， 发件人，用户名， 发件密码， 接收人， 邮件主题是否非空
            return false;
        }
        if (!validEmailFormat(eMail)) {// 校验发件人，收件人，抄送人，密送人的邮箱格式是否正确
            return false;
        }
        if (eMail.getFile() != null && !"".equals(eMail.getFile())) {// 校验附件是否存在以及校验附件的大小是否超过了系统设置的最大值
            if (!validEmailAttachment(eMail)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验发送主机， 发件人，用户名， 发件密码， 接收人， 邮件主题是否非空
     *
     * @param eMail
     * @return
     * @author zhaolimin
     * @date 2014-3-25 下午3:47:24
     */
    private static boolean validEmailEmpty(EMailVO eMail) {
        boolean result = true;
        StringBuffer errorMsg = new StringBuffer();
        if (eMail.getHost() == null || "".equals(eMail.getHost().trim())) {
            errorMsg.append("发送主机必输\n");
            result = false;
        }
        if (eMail.getFrom() == null || "".equals(eMail.getFrom().trim())) {
            errorMsg.append("发件人必输\n");
            result = false;
        }
        if (eMail.getUserName() == null
                || "".equals(eMail.getUserName().trim())) {
            errorMsg.append("发件人用户名必输\n");
            result = false;
        }
        if (eMail.getPwd() == null || "".equals(eMail.getPwd())) {
            errorMsg.append("发件人密码必输\n");
            result = false;
        }
        if (eMail.getRecivers() == null || "".equals(eMail.getRecivers())) {
            errorMsg.append("收件人必输\n");
            result = false;
        }
        if (eMail.getTitle() == null || "".equals(eMail.getTitle())) {
            errorMsg.append("邮件主题必输");
            result = false;
        }
        if (!"".equals(errorMsg.toString())) {
            eMail.setErrorMsg(errorMsg.toString());
        }
        return result;
    }

    /**
     * 校验发件人，收件人，抄送人，密送人,回复邮箱的邮箱格式是否正确
     *
     * @param eMail
     * @return
     */
    private static boolean validEmailFormat(EMailVO eMail) {
        boolean result = true;
        StringBuffer errorMsg = new StringBuffer();
        if (!validEmailPattern(eMail.getFrom().trim())) {
            errorMsg.append("发件人邮箱格式错误\n");
            result = false;
        }
        String[] reciversArray = eMail.getRecivers().trim().split(";");// 校验收件人邮箱格式
        for (int i = 0; i < reciversArray.length; i++) {
            if (!validEmailPattern(reciversArray[i].trim())) {
                errorMsg.append("第" + (i + 1) + "个收件人邮箱格式错误\n");
                result = false;
            }
        }
        if (eMail.getCc() != null && !"".equals(eMail.getCc().trim())) {// 校验抄送人邮箱格式
            String[] ccArray = eMail.getCc().trim().split(";");
            for (int i = 0; i < ccArray.length; i++) {
                if (!validEmailPattern(ccArray[i].trim())) {
                    errorMsg.append("第" + (i + 1) + "个抄送人邮箱格式错误\n");
                    result = false;
                }
            }
        }
        if (eMail.getBcc() != null && !"".equals(eMail.getBcc())) {// 校验密送人邮箱格式
            String[] bccArray = eMail.getBcc().trim().split(";");
            for (int i = 0; i < bccArray.length; i++) {
                if (!validEmailPattern(bccArray[i].trim())) {
                    errorMsg.append("第" + (i + 1) + "个密送人邮箱格式错误\n");
                    result = false;
                }
            }
        }
        if (eMail.getReplyTo() != null && !"".equals(eMail.getReplyTo())) {
            String[] replyTo = eMail.getReplyTo().trim().split(";");
            for (int i = 0; i < replyTo.length; i++) {
                if (!validEmailPattern(replyTo[i].trim())) {
                    errorMsg.append("第" + (i + 1) + "个回复邮箱格式错误\n");
                    result = false;
                }
            }
        }
        eMail.setErrorMsg(errorMsg.toString());
        return result;
    }

    /**
     * 校验附件是否存在以及检验附件的大小是否超过了设置的大小
     *
     * @param eMail
     * @return
     * @throws Exception
     */
    private static boolean validEmailAttachment(EMailVO eMail) throws Exception {
        boolean result = true;
        StringBuffer errorMsg = new StringBuffer();
        long fileSize = 0;
        String[] attachmentArray = eMail.getFile().trim().split(";");
        for (int i = 0; i < attachmentArray.length; i++) {
            File file = new File(attachmentArray[i]);
            if (!file.exists()) {
                result = false;
                errorMsg.append("\"" + attachmentArray[i] + "\"不存在\n");
            }
            fileSize += file.length();
        }
        long attachmentlength = Long.parseLong(SysEmailConst.EMAILATTACHMENT) * 1024 * 1024;// 从系统配置中获取邮件附件的最大值转换为以字节为单位
        if (fileSize > attachmentlength) {
            eMail.setErrorMsg("附件的大小超过了系统设置的最大值\n");
            result = false;
        }
        eMail.setErrorMsg(errorMsg.toString());
        return result;
    }

    /**
     * 校验邮箱格式是否正确
     *
     * @param eMail
     * @return true-邮箱格式正确；false-邮箱格式不正确
     */
    public static boolean validEmailPattern(String eMail) {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(eMail);
        return matcher.matches();
    }

    /**
     * 设置发件人
     *
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    private static void setFrom(EMailVO eMail, MimeMessage message)
            throws UnsupportedEncodingException, MessagingException {
        String showName = eMail.getNickName();
        if (showName == null || "".equals(showName)) {
            showName = eMail.getUserName();
        }
        InternetAddress from = new InternetAddress(eMail.getFrom(),
                MimeUtility.encodeText(showName));// 设置发件人
        message.setFrom(from);
    }

    /**
     * 设置收件人
     *
     * @param eMail
     * @param message
     * @throws MessagingException
     */
    private static void setTo(EMailVO eMail, MimeMessage message)
            throws MessagingException {
        String[] toArray = eMail.getRecivers().trim().split(";");
        if (toArray.length > 0) {
            Address[] toArr = new Address[toArray.length];
            for (int i = 0; i < toArray.length; i++) {
                InternetAddress to = new InternetAddress(toArray[i]);// 设置收件人
                toArr[i] = to;
                // message.addRecipient(Message.RecipientType.TO, to);
            }
            message.addRecipients(Message.RecipientType.TO, toArr);
        }
    }

    /**
     * 设置抄送人
     *
     * @param eMail
     * @param message
     * @throws MessagingException
     */
    private static void setCc(EMailVO eMail, MimeMessage message)
            throws MessagingException {
        String[] ccArray = eMail.getCc().trim().split(";");
        if (ccArray.length > 0) {
            Address[] toArr = new Address[ccArray.length];
            for (int i = 0; i < ccArray.length; i++) {
                InternetAddress cc = new InternetAddress(ccArray[i]);// 设置收件人
                toArr[i] = cc;
                // message.addRecipient(Message.RecipientType.CC, cc);
            }
            message.addRecipients(Message.RecipientType.CC, toArr);
        }
    }

    /**
     * 设置密送人
     *
     * @param eMail
     * @param message
     * @throws MessagingException
     */
    private static void setBcc(EMailVO eMail, MimeMessage message)
            throws MessagingException {
        String[] bccArray = eMail.getBcc().trim().split(";");
        if (bccArray.length > 0) {
            Address[] toArr = new Address[bccArray.length];
            for (int i = 0; i < bccArray.length; i++) {
                InternetAddress bcc = new InternetAddress(bccArray[i]);// 设置收件人
                toArr[i] = bcc;
                // message.addRecipient(Message.RecipientType.BCC, bcc);
            }
            message.addRecipients(Message.RecipientType.BCC, toArr);
        }
    }

    /**
     * 添加邮件文本信息以及附件信息
     *
     * @param message
     * @param eMail
     * @throws Exception
     */
    private static void setText(MimeMessage message, EMailVO eMail)
            throws Exception {
        MimeMultipart allMultipart = new MimeMultipart("related"); // 添加邮件文本信息
        // 创建一个表示HTML正文的MimeBodyPart对象，并将它加入到前面创建的MimeMultipart对象中
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(eMail.getContent(), "text/html;charset=utf-8");
        allMultipart.addBodyPart(htmlBodyPart);
        if (eMail.getFile() != null && !"".equals(eMail.getFile().trim())) {// 添加附件信息
            String[] attachmentArray = eMail.getFile().trim().split(";");
            for (int i = 0; i < attachmentArray.length; i++) {
                MimeBodyPart attchPart = createAttachment(attachmentArray[i]);
                allMultipart.addBodyPart(attchPart);
            }
            message.setContent(allMultipart);
        }
    }

    /**
     * 添加附件内容和相应信息
     *
     * @param filename
     * @return
     * @throws Exception
     */
    private static MimeBodyPart createAttachment(String filename)
            throws Exception {
        // 创建保存附件的MimeBodyPart对象，并加入附件内容和相应信息
        MimeBodyPart attachPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(filename);
        attachPart.setDataHandler(new DataHandler(fds));
        attachPart.setFileName(MimeUtility.encodeText(fds.getName()));
        return attachPart;
    }

    public static void main(String[] args) {
        try {
            SysEmailConst.reloadConfig();
            EmailSendUtil.sendSysEmail("920415463@qq.com", "", "",
                    "测试系统邮件", "920415463@qq.com", "邮件内容", "",
                    0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 队列发送系统邮件-可设置回复邮箱
//     *
//     * @param to
//     *            收件人，以分号（;）分割
//     * @param cc
//     *            抄送人，以分号（;）分割
//     * @param bcc
//     *            密送人，以分号（;）分割
//     * @param replyTo
//     *            回复邮箱 ，以分号（;）分割
//     * @param title
//     *            主题
//     * @param content
//     *            内容
//     * @param file
//     *            附件，以分号（;）分割 为空时设为""
//     * @param bottom
//     *            0不加EMAILBOTTOM，1加EMAILBOTTOM
//     * @param type
//     *            分类
//     * @return
//     * @throws Exception
//     */
//    public static boolean sendEmailbyJms(String to, String cc, String bcc,
//                                         String title, String replyTo, String content, String file,
//                                         String type) {
//        boolean resultInfo = false;
//        List<Object> lsArgs_ = new ArrayList<Object>();
//        lsArgs_.add(delFilter(to));
//        lsArgs_.add(delFilter(cc));
//        lsArgs_.add(delFilter(bcc));
//        lsArgs_.add(title);
//        lsArgs_.add(delFilter(replyTo));
//        lsArgs_.add(content);
//        lsArgs_.add(file);
//        lsArgs_.add(type);
//        List<List<Object>> lsArgs = new ArrayList<List<Object>>();
//        lsArgs.add(lsArgs_);
//        JmsSuperVO jmsSuperVO = new JmsSuperVO();
//        jmsSuperVO.setType("邮件");
//        jmsSuperVO.setMethod("sendEmail");
//        jmsSuperVO.setPackclass("SysNoticeMngImpl");
//        jmsSuperVO.setLevel(8);
//        jmsSuperVO.setContent(lsArgs);
//        try {
//            resultInfo = JmsPublisher.sendQueue(jmsSuperVO);
//        } catch (Exception e) {
//            LogUtil.error(EmailSendUtil.class, e);
//        }
//        return resultInfo;
//    }

    /**
     * 过滤离职员工邮箱
     *
     * @param emails
     * @return
     */
    public static String delFilter(String emails) {
        StringBuffer str = new StringBuffer("");
        if (!StringUtils.isBlank(emails)) {
            String arr[] = emails.split(";");
            for (int i = 0; i < arr.length; i++) {
                if (!StringUtils.isBlank(arr[i])) {
                    if (!arr[i].toLowerCase().endsWith(".del")) {
                        str.append(arr[i] + ";");
                    }
                }
            }
        }
        return str.toString();
    }

    /**
     * 获得发件人的地址和姓名
     *
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    public static String getFrom(MimeMessage mimeMessage) throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String fromAddr = address[0].getAddress();
        if (fromAddr == null) {
            fromAddr = "";
        }
        return fromAddr;
    }

    /**
     * 获取发件人昵称
     *
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    public static String getFromNickName(MimeMessage mimeMessage)
            throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String personal = address[0].getPersonal();
        if (personal == null) {
            personal = "";
        }
        return personal;
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人　"cc"---抄送人地址　"bcc"---密送人地址
     *
     * @param type
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    public static String getMailAddress(String type, MimeMessage mimeMessage)
            throws Exception {
        String mailAddr = "";
        String addType = type.toUpperCase();

        InternetAddress[] address = null;
        if (addType.equals("TO") || addType.equals("CC")
                || addType.equals("BCC")) {

            if (addType.equals("TO")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.TO);
            } else if (addType.equals("CC")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.BCC);
            }

            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String emailAddr = address[i].getAddress();
                    if (emailAddr == null) {
                        emailAddr = "";
                    } else {
                        // 防止中文乱码
                        emailAddr = getChinese(emailAddr);
                    }
                    mailAddr += "," + emailAddr;
                }
                mailAddr = mailAddr.substring(1);
            }
        }
        return mailAddr;
    }

    /**
     * 获得邮件主题
     *
     * @param mimeMessage
     * @return
     * @throws MessagingException
     */
    public static String getSubject(MimeMessage mimeMessage)
            throws MessagingException {
        String subject = "";
        try {
            // 解决中文乱码问题
            subject = mimeMessage.getSubject();
            subject = getChinese(subject);
            if (subject == null) {
                subject = "";
            }
        } catch (Exception e) {
            LogUtil.error(EmailSendUtil.class, e);
        }
        return subject;
    }

    /**
     * 防止中文乱码
     *
     * @param regionStr
     * @return
     */
    public static String getChinese(String regionStr) {
        try {
            String regEx = "[\u4e00-\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(regionStr);
            if (!m.find()) {
                regionStr = new String(regionStr.getBytes("ISO8859_1"), "GBK");
                regionStr = MimeUtility.decodeText(regionStr);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return regionStr;

    }

    /**
     * 获得邮件发送日期
     *
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    public static String getSentDate(MimeMessage mimeMessage) throws Exception {
        Date sentDate = mimeMessage.getSentDate();
        String sendDateString = "";
        if (null != sentDate) {
            sendDateString = DateUtil.parseDateToString(sentDate,
                    DateUtil.C_YYYY_MM_DD_HH_MM_SS);
        }
        return sendDateString;
    }


    /**
     * 获取邮件的ID
     *
     * @param mimeMessage
     * @return
     * @throws MessagingException
     */
    public static String getMessageId(MimeMessage mimeMessage)
            throws MessagingException {
        String messageID = mimeMessage.getMessageID();
        return messageID.replaceAll("<", "").replaceAll(">", "");
    }

    /**
     * @param host     收件箱服务器地址
     * @param username 用户名
     * @param password 密码
     * @return
     * @throws Exception
     */
    public static List<EMailVO> getEmailData(String host, String username,
                                             String password) throws Exception {
        Properties props = new Properties();
        // 构造默认的session对象
        Session session = Session.getDefaultInstance(props, null);

        Store store = session.getStore("pop3");
        // 连接服务器
        store.connect(host, username, password);
        // 获取收件箱目录
        Folder folder = store.getFolder("INBOX");
        // 以读写方式打开
        folder.open(Folder.READ_WRITE);
        // 增加查询条件
        /*
         * Calendar calendar = Calendar.getInstance(); Calendar calendar1 =
		 * Calendar.getInstance(); // 当前时间十分钟以前 calendar.add(Calendar.MINUTE,
		 * (-1) * intervalTime); // 当前时间十分钟以后 calendar1.add(Calendar.MINUTE,
		 * intervalTime); Date beforeDate = calendar.getTime(); Date afterDate =
		 * calendar1.getTime(); // 构造搜索开始时间 SearchTerm comparisonTermGe = new
		 * SentDateTerm(ComparisonTerm.GE, beforeDate); // 构造搜索结束时间 SearchTerm
		 * comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, afterDate); //
		 * 构造搜索起止时间，指明是逻辑与的关系 SearchTerm comparisonAndTerm = new
		 * AndTerm(comparisonTermGe, comparisonTermLe); // 搜索符合条件的邮件 Message[]
		 * message = folder.search(comparisonAndTerm);
		 */
        Message[] message = folder.getMessages();
        if (null != message) {
            LogUtil.info(EmailSendUtil.class, message.length + "=======");
        } else {
            LogUtil.info(EmailSendUtil.class, "message为null-------------");
        }

        List<EMailVO> emailVOList = new ArrayList<EMailVO>();
        for (int i = 0; i < message.length; i++) {
            MimeMessage mimeMessage = (MimeMessage) message[i];
            EMailVO emailVO = new EMailVO();
            // 设置服务器地址
            emailVO.setHost(host);
            // 发件人姓名 默认是邮箱@以前内容
            emailVO.setUserName(getFrom(mimeMessage).substring(0,
                    getFrom(mimeMessage).indexOf("@")));
            // 昵称
            emailVO.setNickName(getFromNickName(mimeMessage));
            // 发件人
            emailVO.setFrom(getFrom(mimeMessage));
            // 发件人密码，这个无法获取
            emailVO.setPwd("");
            // 收件人
            emailVO.setRecivers(getMailAddress("to", mimeMessage));
            // 抄送人
            emailVO.setCc(getMailAddress("cc", mimeMessage));
            // 密送人
            emailVO.setBcc(getMailAddress("bcc", mimeMessage));
            // 回复邮箱
            emailVO.setReplyTo("");
            // 主题
            emailVO.setTitle(getSubject(mimeMessage));
            // 清空内容,否则每次将会累加上封邮件内容
            attachmentName = new StringBuffer();
            SingleText = new StringBuffer(); // 清空存放的纯文本
            bodyBlodString = new StringBuffer(); // 清空存放的HTML格式内容
            contentList = new ArrayList<Map<String, Object>>();
            attachmentList = new ArrayList<Map<String, Object>>();
            writePart((Part) message[i]);
            // 邮箱内容
            if (null != bodyBlodString
                    && bodyBlodString.toString().length() > 0) {
                emailVO.setContent(bodyBlodString.toString());
            } else {
                emailVO.setContent(SingleText.toString());
            }
            // 设置正文中图片
            emailVO.setContentList(contentList);
            // 设置附件
            emailVO.setAttachmentList(attachmentList);
            if (null != attachmentName
                    && attachmentName.toString().length() > 0) {
                emailVO.setAttachmentName(attachmentName.toString().substring(
                        0, attachmentName.toString().length() - 1));
            }

            // 邮件ID，是由SMTP服务器生成的唯一ID
            emailVO.setMessageid(getMessageId(mimeMessage));
            // 邮件发送时间
            emailVO.setSendDate(getSentDate(mimeMessage));
            emailVOList.add(emailVO);
            message[i].setFlag(Flags.Flag.DELETED, true);
        }
        ;
        folder.close(true);
        store.close();

        return emailVOList;
    }


    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中， 解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     *
     * @param part
     * @throws Exception
     */
    public static void writePart(Part part) throws Exception {
        // 纯文本，直接追加到纯文本内容后面
        if (part.isMimeType("text/plain")) {
            SingleText.append(getChinese((String) part.getContent()));
        }
        // html格式文件，直接追加到HTML内容后面
        else if (part.isMimeType("text/html")) {
            bodyBlodString.append(getChinese((String) part.getContent()));
        }
        // 如果是混合类型，则可能包含附件和内嵌图片，进一步解析
        else if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                writePart(mp.getBodyPart(i));
            }
        } else if (part.isMimeType("image/*")) {
            // 获取文件类型
            String contentType = part.getContentType();
            // 获取部署类型
            String disposition = part.getDisposition();
            // 如果部署类型为空，代表不是附件
            if (null == disposition) {
                // 图片类型，获取他的流，便于读取
                Object o = part.getContent();
                Map<String, Object> map = new HashMap<String, Object>();
                // 存放流
                map.put("inputStream", o);
                //
                map.put("contentType",
                        contentType.substring(0, contentType.indexOf(";")));
                map.put("fileName", part.getFileName());
                contentList.add(map);
            } else {
                // 如果是附件图片，则存到另外一个地方
                Object o = part.getContent();
                // 将获取附件名称，中文可能会乱码
                String filename = part.getFileName();
                // 对汉字进行转码
                if ((filename.startsWith("=?GB2312?B?") && filename
                        .endsWith("?="))
                        || (filename.startsWith("=?gb2312?B?") && filename
                        .endsWith("?="))) {
                    filename = getFromBASE64(filename.substring(11,
                            filename.indexOf("?=")));
                }
                // 将附件名称追加进去
                attachmentName.append(filename + ",");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("inputStream", o);
                map.put("contentType",
                        contentType.substring(0, contentType.indexOf(";")));
                map.put("fileName", filename);
                attachmentList.add(map);
            }
        } else if (part.isMimeType("application/*")) {
            // 获取文件类型
            String contentType = part.getContentType();
            // 获取部署类型
            String disposition = part.getDisposition();
            // 这中情况是针对直接转发别人的邮件，会导致附件被当成一个application,
            // 这样的话就在内层对其继续判断，处理jpg格式图片
            // 将获取附件名称，中文可能会乱码
            String filename = part.getFileName();
            logger.info("转化前：" + filename);
            if ((filename.startsWith("=?GB2312?B?") && filename.endsWith("?="))
                    || (filename.startsWith("=?gb2312?B?") && filename
                    .endsWith("?="))) {
                filename = getFromBASE64(filename.substring(11,
                        filename.indexOf("?=")));
            }
            logger.info("转化后：" + filename);
            // 如果附件，disposition不为空，则是附件,如果disposition为空，则为正文图片
            // jpng,png,jpg,gif
            if (null == disposition && (filename.endsWith(".jpng") || filename.endsWith(".jpg") || filename.endsWith(".png"))) {
                Object o = part.getContent();
                // 将附件名称追加进去
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("inputStream", o);
                map.put("contentType",
                        contentType.substring(0, contentType.indexOf(";")));
                map.put("fileName", filename);
                contentList.add(map);
            } else {
                Object o = part.getContent();

                // 将附件名称追加进去
                attachmentName.append(filename + ",");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("inputStream", o);
                map.put("contentType",
                        contentType.substring(0, contentType.indexOf(";")));
                map.put("fileName", filename);
                attachmentList.add(map);
            }
        }
    }

    /**
     * 用Base64对中文名解码
     *
     * @param s
     * @return
     */
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        try {
            byte[] b = Base64.decode(s);
            return new String(b, "GB2312");
        } catch (Exception e) {
            return "";
        }
    }

}
