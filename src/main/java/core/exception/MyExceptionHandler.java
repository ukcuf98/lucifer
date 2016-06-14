package core.exception;

import core.action.SuperController;
import core.sysconst.Constant;
import core.util.JsonUtil;
import core.util.LogUtil;
import core.util.ResultInfo;
import core.util.Util;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import plat.entity.sys.SessionUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:44
 */
public class MyExceptionHandler implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof MaxUploadSizeExceededException) {
            // 文件大小超过限制
            try {
                ResultInfo ri = new ResultInfo(-1, "文件大小超过20M");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JsonUtil.beanToJson(ri));
                return new ModelAndView();
            } catch (Exception e) {
                LogUtil.error(ex.getClass(), e);
            }
        } else {
            LogUtil.error(ex.getClass(), ex);
            StringPrintWriter strintPrintWriter = new StringPrintWriter();
            ex.printStackTrace(strintPrintWriter);

            try {
                // 发送错误邮件
                this.sendEmail(request, response, handler, ex);
            } catch (Exception e) {
                LogUtil.error(this.getClass(), e);
            }
            if (request.getHeader("x-requested-with") != null
                    && request.getHeader("x-requested-with").equalsIgnoreCase(
                    "XMLHttpRequest")) {
                // 如果ajax请求
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("错误，系统发生异常！");
                    return new ModelAndView();
                } catch (Exception e) {
                    LogUtil.error(ex.getClass(), e);
                }
            } else {
                request.setAttribute("error_msg", strintPrintWriter.getString());
                ModelAndView modelAndView = new ModelAndView("msg/error");
                Class<?> clz = Constant.class;
                String name = clz.getSimpleName();
                try {
                    modelAndView.addObject(name,
                            BeansWrapper.getDefaultInstance().getStaticModels()
                                    .get(clz.getName()));
                } catch (TemplateModelException e) {
                    LogUtil.error(this.getClass(), e.getMessage());
                }
                return modelAndView;
            }
        }
        return null;
    }

    /**
     * 发送错误邮件
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @author zhaolimin
     * @date 2014-5-19 下午2:43:06
     */
    private void sendEmail(HttpServletRequest request,
                           HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    /**
     * 发送邮件内容
     *
     * @param request
     * @param ex
     * @return
     * @author zhaolimin
     * @date 2014-5-19 下午2:43:25
     */
    private String emailMsg(HttpServletRequest request, Exception ex) {
        SessionUser user = (SessionUser) request.getSession(true).getAttribute(
                SuperController.SESSION_USERNAME);// 当前登录人
        StringBuffer content = new StringBuffer();
        String url = request.getServerName() + ":" + request.getServerPort()
                + "(" + Util.getServerIP() + ") 后台 error:<br/>\n";
        if (user != null) {
            String loginUser = "当前登录人为：" + user.getLastname() + "--登录名为("
                    + user.getLoginid() + ")--id为(" + user.getId() + ")<br/>\n";
            content.append(loginUser);// 当前登录人
            content.append(url);// 请求的url
        }
        StringBuffer msg = new StringBuffer();
        msg.append(ex.toString() + "<br/>\n");
        for (StackTraceElement stackTrace : ex.getStackTrace()) {
            msg.append(stackTrace.toString() + "\n");
        }
        content.append("请求的IP：" + Util.getClientIPAll(request) + "<br/>\n");// 请求的路径
        content.append("请求的路径：" + request.getServletPath() + "<br/>\n");// 请求的路径
        content.append("请求的来源：" + request.getHeader("referer") + "<br/>\n");// 请求的来源
        content.append("请求的方法：" + request.getParameter("method") + "<br/>\n");// 请求的方法
        content.append("请求的方式：" + request.getMethod() + "<br/>\n");// 请求的方法
        StringBuffer para = new StringBuffer();// 请求的参数
        if ("get".equalsIgnoreCase(request.getMethod())) {
            para.append(request.getQueryString());
        } else {
            Enumeration<String> enumeration = request.getParameterNames();
            int i = 0;
            while (enumeration.hasMoreElements()) {
                String paramName = enumeration.nextElement();
                String paramValue = request.getParameter(paramName);
                if (!"".equals(paramValue) && paramValue != null
                        && !"method".equals(paramName)) {
                    para.append(paramName + "=" + paramValue + ";");
                    i++;
                }
                if (i % 3 == 0) {// 3个参数换行
                    para.append("<br/>\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                }
            }
        }
        content.append("请求的参数：" + para + "<br/>\n");
        content.append(msg);
        return content.toString();
    }

}
