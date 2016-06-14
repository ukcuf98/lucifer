package core.proj;

import core.sysconst.Constant;
import core.util.EmailSendUtil;
import core.util.LogUtil;

import javax.servlet.ServletContext;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 10:04
 */
public class ProjectApplicationContextLoader extends ApplicationContextLoader {
    /**
     *
     */
    private static final long serialVersionUID = 5015752607567008246L;

    @Override
    protected void projectInit(ServletContext localServletContext) {
        try {
            Constant.PROPATH = localServletContext.getContextPath();
            Constant.reloadConfigAll();
//            // 更新在线人员
//            MemClient.set(
//                    MemKeyUtil.onlineUserStatisPrefix + Util.getServerIP(),
//                    new LinkedHashMap<Integer, SessionUser>());
//
//            List<String> serverIpsList = (List<String>) MemClient
//                    .get(MemKeyUtil.serverIpsList);
//            serverIpsList = serverIpsList == null ? new ArrayList<String>()
//                    : serverIpsList;
//            if (serverIpsList.indexOf(Util.getServerIP()) < 0) {
//                serverIpsList.add(Util.getServerIP());
//                MemClient.set(MemKeyUtil.serverIpsList, serverIpsList);
//            }
//            // 启动项目邮件提醒，接收人为系统监控邮箱配置
//            DictSysParamMng dictSysParamMng = DictSysParamMngImpl.getInstance();
//            // 监控人
//            String to = dictSysParamMng
//                    .getValueByCode(SysParamConst.SYSEMAILCONF_MONITOEMAIL);
//            // 是否发送重启邮件
//            String restartSendEmail = dictSysParamMng
//                    .getValueByCode(SysParamConst.RESTART_SENDEMAIL);
//            String serverIP = Util.getServerIP();
//            File directory = new File("");// 设定为当前文件夹
//            if (null != restartSendEmail
//                    && restartSendEmail.equalsIgnoreCase("true")) {
//                String title = "应用重启_" + ProjectOAConst.PROPATH + "_"
//                        + directory.getAbsolutePath() + "_" + serverIP;
//                EmailSendUtil.sendSysEmail(to, "", "", title, title, "");
//            }
            //发邮件
//            EmailSendUtil.sendSysEmail("920415463@qq.com","", "",
//                    "测试系统邮件", "920415463@qq.com", "邮件内容","",
//                    0);
        } catch (Exception e) {
            LogUtil.error(this.getClass(), e);
        }
    }
}
