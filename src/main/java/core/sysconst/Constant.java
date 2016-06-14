package core.sysconst;

import java.io.IOException;
import java.util.Arrays;

import core.util.DateUtil;
import core.util.PropUtil;
import core.util.Util;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:31
 */
public class Constant {
    /**
     * js服务器地址
     */
    public static String JSPATH;
    /**
     * 域名地址
     */
    public static String PROPATH;
    /**
     * 旧域名地址
     */
    public static String OLDPROPATH;
    /**
     * 平台地址-跳转到登录退出页面时使用
     */
    public static String PLATFORM_PATH;
    /**
     * 工程名
     */
    public static String PRONAME;
    /**
     * 系统id
     */
    public static String SYSINFOID;
    /**
     * 系统名称
     */
    public static String SYSINFONAME = "";
    /**
     * 是否cookie验证 0不验证 1验证
     */
    public static String COOKIEHECK;
    /**
     * js参数值
     */
    public static String JSCOUNT = "";
    /**
     * 不需要校验session的具体页面
     */
    public static String[] NOTCHECK_SESSION_URL = null;
    /**
     * 登录页面的url地址
     */
    public static String LOGINPAGE_URL = "";
    /**
     * 退出页面的url地址
     */
    public static String LOGOUTPAGE_URL = "";
    /**
     * 是否删除session
     */
    public static String deleteSession;

    /**
     * 服务器IP
     */
    public static String SERVER_IP;

    /**
     * 登录状态-true：允许登录，false:禁止登录
     */
    public static String LOGINSTATUS;
    /**
     * 子类的类路径
     */
    public static String childClassPath = null;

    /**
     * 重置全部常量
     *
     * @throws Exception
     */
    public static void reloadConfigAll() throws Exception {
//        MemConst.reloadConfig();
//        LoginConst.reloadConfig();
        Constant.reloadConfig();
        DataSourceConst.reloadConfig();
//        FTPConst.reloadConfig();
        SysEmailConst.reloadConfig();
//        FastDFSConst.reloadConfig();
    }

    /**
     * 工程重置常量
     *
     * @throws Exception
     */
    public void projectReloadConfigAll() throws Exception {

    }

    /**
     * 重新加载常量
     *
     * @throws IOException
     */
    public static void reloadConfig() throws Exception {

        // js常量相关
        JSPATH = PropUtil.getPropValue("sysconst", "jspath");

        PRONAME = PropUtil.getPropValue("sysconst", "proname");

        try {
            if (!PROPATH.endsWith("/")) {
                PROPATH = PROPATH + "/";
            }
        } catch (Exception e) {
            PROPATH = "/";
        }

        OLDPROPATH = PropUtil.getPropValue("sysconst", "oldpropath");

        if (!OLDPROPATH.endsWith("/")) {
            OLDPROPATH = OLDPROPATH + "/";
        }

        PLATFORM_PATH = PropUtil.getPropValue("sysconst", "platform_path");

        if (!PLATFORM_PATH.endsWith("/")) {
            PLATFORM_PATH = PLATFORM_PATH + "/";
        }

        SYSINFOID = PropUtil.getPropValue("sysconst", "sysinfoid");

        SYSINFONAME = PropUtil.getPropValue("sysconst", "sysinfoname");

        COOKIEHECK = PropUtil.getPropValue("sysconst", "cookie_check");

        if (Util.isBlank(JSCOUNT)) {
            JSCOUNT = PropUtil.getPropValue("sysstore", "jscount");
            if (Util.isBlank(JSCOUNT)) {
                reloadJSCOUNT();
            }
        } else {
            reloadJSCOUNT();
        }

        // 登录页面的url地址
        LOGINPAGE_URL = PLATFORM_PATH + "login/loginAct.do?method=login";
        // 退出页面的url地址
        LOGOUTPAGE_URL = PLATFORM_PATH + "login/logout.jsp";

        // 登录拦截验证相关
        String NOTCHECK_SESSION_URL_STR = PropUtil.getPropValue("sysconst",
                "notcheck_session_url");
        if (null != NOTCHECK_SESSION_URL_STR) {
            NOTCHECK_SESSION_URL = NOTCHECK_SESSION_URL_STR.split("\\|");
            Arrays.sort(NOTCHECK_SESSION_URL);
        }

        SERVER_IP = Util.getServerIP();
        deleteSession = PropUtil.getPropValue("sysconst", "deleteSession");
        LOGINSTATUS = PropUtil.getPropValue("sysconst", "loginStatus");
    }

    /**
     * 重置jscount值
     *
     * @throws Exception
     */
    public static void reloadJSCOUNT() throws Exception {
        JSCOUNT = DateUtil.getCurrentDateStr(DateUtil.C_YYYY_MM_DD_HH_MM_SS);
        PropUtil.setPropValue("sysstore", "jscount", JSCOUNT);
    }
}
