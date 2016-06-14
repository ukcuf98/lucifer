package core.action;

import core.util.Util;
import plat.entity.sys.SessionUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:28
 */
public class SuperController {
    /**
     * session中的用户对象名
     */
    public static final String SESSION_USERNAME = "platform_sessionuser";
    /**
     * session中的登录次数
     */
    public static final String SESSION_LOGINTIMES = "platform_logintimes";
    /**
     * 登录-验证码
     */
    public static final String SESSION_LOGIN_VALIDATECODE = "validateCode";
    /**
     * cookie中的记录多选项卡用户上次操作习惯
     */
    public static final String MULTITABCOM_COOKIE_NAME = "mtmc";
    /**
     * cookie中的记录上次登录成功的用户id
     */
    public static final String LASTLOGINID_COOKIE_NAME = "lastlgid";

    /**
     * 获取权限验证提示信息
     *
     * @param request
     * @return
     * @throws Exception
     */
    protected String getRightAuthMsg(HttpServletRequest request)
            throws Exception {
        String msg = (String) request.getAttribute("rightauth_msg");
        if (null == msg) {
            return "没有权限";
        } else {
            return msg;
        }
    }

    /**
     * 跳转到错误页面
     *
     * @param msg
     * @return
     */
    protected String toNoright(HttpServletRequest request, String msg) {
        request.setAttribute("rightauth_msg", msg);
        return "/msg/noright";
    }

    /**
     * 获取当前登录用户
     *
     * @param request request对象
     * @return
     */
    protected SessionUser getUserFromSession(HttpServletRequest request) {
        SessionUser user = (SessionUser) request.getSession(true).getAttribute(
                SESSION_USERNAME);// 当前登录人
        return user;
    }

    /**
     * 从session中获取属性值
     *
     * @param request
     * @param attrName
     * @return
     */
    protected Object getAttrFromSession(HttpServletRequest request,
                                        String attrName) {
        return request.getSession(true).getAttribute(attrName);
    }

    /**
     * 设置值到session
     *
     * @param request
     * @param objName
     * @param objVal
     */
    protected void setObjectToSession(HttpServletRequest request,
                                      String objName, Object objVal) {
        request.getSession(true).setAttribute(objName, objVal);
    }

    /**
     * 从session中删除属性值
     *
     * @param request
     * @param attrName
     */
    protected void removeAttrFromSession(HttpServletRequest request,
                                         String attrName) {
        Object obj = this.getAttrFromSession(request, attrName);
        if (null != obj) {
            request.getSession(true).removeAttribute(attrName);
        }
    }

    /**
     * 生成formToken并放入session中
     *
     * @param request
     */
    protected void creatFormToken(HttpServletRequest request) {
        request.getSession(false).setAttribute("formToken",
                Util.get32RandomUID());
    }

    /**
     * 验证formToken
     *
     * @param request
     * @return 若request和session中的 formToken一致则返回true；否则返回false
     */
    protected boolean validatFormToken(HttpServletRequest request) {
        String serverToken = (String) request.getSession(false).getAttribute(
                "formToken");
        if (serverToken == null) {
            return false;
        }
        String clinetToken = request.getParameter("formToken");
        if (clinetToken == null) {
            return false;
        }
        if (!serverToken.equals(clinetToken)) {
            return false;
        }
        return true;
    }

    /**
     * 获取域名
     *
     * @param request
     * @return
     */
    protected String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = "";
        basePath = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + path + "/";
        return basePath;
    }
}
