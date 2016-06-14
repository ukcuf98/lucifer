package core.interceptor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import plat.entity.sys.SessionUser;
import core.action.SuperController;
import core.sysconst.SysEmailConst;
import core.util.EmailSendUtil;
import core.util.LogUtil;
import core.util.Util;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 16:59
 */
public class SQLInjectionInterceptor {
    /**
     * sql存在拦截（存在就拦截）
     */
    private final String[] sql_checks = new String[]{"exec", "execute",
            "insert", "delete", "update", "truncate", "drop", "create",
            "grant", "declare", "script", "iframe", "frame"};
    /**
     * sql匹配拦截（完全匹配才拦截）
     */
    private final String[] sql_equal_checks = new String[]{"%", "%25", "_"};
    /**
     * 不拦截的参数
     */
    private String[] remove_params = new String[]{"method", "ran", "SYNCXML",
            "gopage"};
    private static SQLInjectionInterceptor sqlInjectionInterceptor = null;

    private SQLInjectionInterceptor() {
    }

    public static SQLInjectionInterceptor getInstance() {
        if (null == sqlInjectionInterceptor) {
            sqlInjectionInterceptor = new SQLInjectionInterceptor();
        }
        return sqlInjectionInterceptor;
    }

    /**
     * sql注入拦截器
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws Exception
     */
    public Boolean SQLInterceptor(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) throws Exception {
        Map<String, String[]> param_map = (Map<String, String[]>) httpServletRequest
                .getParameterMap();
        Boolean result = isPassCheck(httpServletRequest, param_map);
        return result;
    }

    /**
     * 判断参数是否通过验证
     *
     * @param httpServletRequest
     * @param param_map
     * @return
     */
    private Boolean isPassCheck(HttpServletRequest httpServletRequest,
                                Map<String, String[]> param_map) throws Exception {
        SessionUser user = null;
        user = (SessionUser) ((httpServletRequest.getSession(true)).getAttribute(
                SuperController.SESSION_USERNAME));
        for (String key : param_map.keySet()) {
            if (!binarySearch(remove_params, key)) {
                Object value = new String(httpServletRequest.getParameter(key));
                if (null != value && value instanceof String
                        && value.toString().trim().length() > 0) {
                    if (binarySearch(sql_equal_checks, value)) {
                        return false;
                    }
                    for (String check : sql_checks) {
                        Pattern p_Str;
                        Matcher m_Str;
                        p_Str = Pattern.compile(check + " ", Pattern.CASE_INSENSITIVE);
                        m_Str = p_Str.matcher(value.toString());
                        while (m_Str.find()) {
                            LogUtil.info(this.getClass(),
                                    "成功被sql注入拦截了............请求地址：" +
                                            httpServletRequest.getRequestURI() + " 拦截条件：" + check + "空格"
                                            + " 参数名：" + key + " 参数值：" + value);
                            EmailSendUtil.sendSysEmail(SysEmailConst.MONITOEMAIL, "", "",
                                    "sql注入拦截_" + Util.getServerIP(),
                                    "成功被sql注入拦截了................当前登录人：" + (user == null ? "" : (user.getLastname() + user.getLoginid() + user.getId())) + "  请求地址：" +
                                            httpServletRequest.getRequestURI() + "  拦截条件：" + check
                                            + " 参数名：" + key + " 参数值：" + value, "");
                            return false;
                        }
                        p_Str = Pattern.compile(check + "%20", Pattern.CASE_INSENSITIVE);
                        m_Str = p_Str.matcher(value.toString());
                        while (m_Str.find()) {
                            LogUtil.info(this.getClass(),
                                    "成功被sql注入拦截了............请求地址：" +
                                            httpServletRequest.getRequestURI() + " 拦截条件：" + check
                                            + " 参数名：" + key + " 参数值：" + value);
                            EmailSendUtil.sendSysEmail(SysEmailConst.MONITOEMAIL, "", "",
                                    "sql注入拦截_" + Util.getServerIP(),
                                    "成功被sql注入拦截了............当前登录人：" + (user == null ? "" : (user.getLastname() + user.getLoginid() + user.getId())) + " 请求地址：" +
                                            httpServletRequest.getRequestURI() + " 拦截条件：" + check + "%20"
                                            + " 参数名：" + key + " 参数值：" + value, "");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 查询字符串是否在字符串数组中
     *
     * @param arrays
     * @param key
     * @return
     */
    private boolean binarySearch(String[] arrays, Object key) {
        boolean result = false;
        if (null == key) {
            return result;
        }
        if (null != arrays) {
            for (String str : arrays) {
                if (str.equals(key.toString().trim())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
