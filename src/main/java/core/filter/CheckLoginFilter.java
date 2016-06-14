package core.filter;

import core.action.SuperController;
import core.sysconst.Constant;
import plat.entity.sys.SessionUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 9:56
 */
public class CheckLoginFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        boolean result = true;
        SessionUser user = (SessionUser) httpServletRequest.getSession(true)
                .getAttribute(SuperController.SESSION_USERNAME);
        String url = httpServletRequest.getRequestURI();
        String ismobile = request.getParameter("isMobile");

        // 验证该url是否需要验证
        if (null == user) {
            if (null != Constant.NOTCHECK_SESSION_URL
                    && Arrays.binarySearch(Constant.NOTCHECK_SESSION_URL, url) >= 0) {
                result = true;
            } else {
                result = false;
            }
        }
//
//        // token验证
//        if (!Util.isBlank(httpServletRequest.getParameter("token"))
//                && !Util.isBlank(httpServletRequest.getParameter("loginid"))) {
//            IWSHrmLoginAuth iHrmLoginAuth=BeanFactory.getBean(IWSHrmLoginAuth.class);
//            SessionUser sessionUser = iHrmLoginAuth.checkToken(
//                    httpServletRequest.getParameter("loginid"),
//                    httpServletRequest.getParameter("token"),
//                    ismobile,
//                    Util.getClientIP(httpServletRequest),
//                    Util.getClientIPAll(httpServletRequest));
//            if (null != sessionUser) {
//                httpServletRequest.getSession(true).setAttribute(
//                        SuperAct.SESSION_USERNAME, sessionUser);
//                CookieUtil.setLoginCookie(sessionUser, httpServletRequest,
//                        httpServletResponse);
//                result = true;
//            } else if (null != user) {
//                result = true;
//            } else {
//                result = false;
//            }
//        }

        if (result) {
            chain.doFilter(request, response);
        } else {
            ResponseSendRedirect.sendRedirect(httpServletRequest,
                    (HttpServletResponse) response);
        }
        return;
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}
