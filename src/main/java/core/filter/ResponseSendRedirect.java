package core.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.sysconst.Constant;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 11:10
 */
public class ResponseSendRedirect {

    /**
     * 登录统一转向方法
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws IOException
     * @throws ServletException
     */
    public static void sendRedirect(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) throws IOException,
            ServletException {
        String url = httpServletRequest.getRequestURI();

        if (httpServletRequest.getHeader("x-requested-with") != null
                && httpServletRequest.getHeader("x-requested-with")
                .equalsIgnoreCase("XMLHttpRequest")) {

            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse
                    .setContentType("application/json; charset=utf-8");
            PrintWriter out = httpServletResponse.getWriter();
            out.append("{\"code\":-1,\"message\":\"登录超时，请重新登录\",\"overtime\":true,\"loginpage\":\""
                    + Constant.LOGOUTPAGE_URL + "\"}");
            out.flush();
            out.close();
            return;
        } else {
            String gopage = url;
            String linkFrom = httpServletRequest.getParameter("LinkFrom");

            if (null != gopage) {
                Map<String, String[]> param_map = (Map<String, String[]>) httpServletRequest
                        .getParameterMap();
                if (null != param_map) {
                    String split = "?";
                    for (String key : param_map.keySet()) {
                        String value = httpServletRequest.getParameter(key);
                        if (null != value && value.trim().length() > 0) {
                            if (!"token".equals(key)) {
                                gopage += split + key + "=" + value;
                                split = "&";
                            }
                        }
                    }
                }
                if (null != Constant.PRONAME && Constant.PRONAME.length() > 1
                        && gopage.startsWith("/" + Constant.PRONAME)) {
                    gopage = gopage.substring(Constant.PRONAME.length() + 1);
                }
            }

            // 设置登录来源系统-用于子系统统一登录
            String logOutPageUrl = Constant.LOGOUTPAGE_URL;
            String fromSysId = "0";
            if (null != Constant.SYSINFOID) {
                fromSysId = Constant.SYSINFOID;
            }
            logOutPageUrl = logOutPageUrl + "?fromSysId=" + fromSysId;

            if (null != linkFrom && "email".equals(linkFrom) && null != gopage
                    && gopage.length() > 3) {
                gopage = URLEncoder.encode(gopage, "GBK");
                httpServletResponse.sendRedirect(logOutPageUrl + "&gopage="
                        + gopage);
            } else {
                httpServletResponse.sendRedirect(logOutPageUrl);
            }

            return;
        }
    }

}
