package core.interceptor;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.util.LogUtil;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 17:10
 */
public class XSSInjectionInterceptor {
    private static XSSInjectionInterceptor xssInjectionInterceptor = null;

    private XSSInjectionInterceptor() {
    }

    public static XSSInjectionInterceptor getInstance() {
        if (null == xssInjectionInterceptor) {
            xssInjectionInterceptor = new XSSInjectionInterceptor();
        }
        return xssInjectionInterceptor;
    }

    public Boolean XSSInterceptor(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) throws Exception {
        Map<String, String[]> names = httpServletRequest.getParameterMap();
        Set<String> keys = names.keySet();
        boolean flagMethod = true;
        for (String key : keys) {
            flagMethod = XSSInjectionInterceptor.checkDangerousCharacter(null, key);
            if (!flagMethod) {
                LogUtil.info(this.getClass(),
                        "成功拦截xss注入。。。。。。。。。。拦截条件：方法名：" + key);
                return false;
            }
            String[] values = names.get(key);
            if (key.equals("method")) {
                //add by zhaosen
                for (String value : values) {
                    flagMethod = XSSInjectionInterceptor.checkDangerousCharacter("method", value);
                    if (!flagMethod) {
                        LogUtil.info(this.getClass(),
                                "成功拦截xss注入。。。。。。。。。。拦截条件：名成：" + key + ";参数：" + value);
                        return false;
                    }
                }
                if (values.length == 1 && values[0].equals("")) {
                    LogUtil.info(this.getClass(),
                            "成功拦截xss注入。。。。。。。。。。拦截条件：方法名method为空");
                    return false;
                }
            }
            //add by zhaosen
            if (key.equals("mid")) {
                for (String value : values) {
                    flagMethod = XSSInjectionInterceptor.checkDangerousCharacter("mid", value);
                    if (!flagMethod) {
                        LogUtil.info(this.getClass(),
                                "成功拦截xss注入。。。。。。。。。。拦截条件：名成：" + key + ";参数：" + value);
                        return false;
                    }
                }
            }
            for (String value : values) {
                flagMethod = XSSInjectionInterceptor.checkDangerousCharacter(null, value);
                if (!flagMethod) {
                    LogUtil.info(this.getClass(),
                            "成功拦截xss注入。。。。。。。。。。拦截条件：名成：" + key + ";参数：" + value);
                    return false;
                }
            }
        }
        String requestUrl = httpServletRequest.getRequestURI();
        boolean flagUrl = XSSInjectionInterceptor.checkDangerousCharacter(null, requestUrl);
        return flagUrl;
    }

    public static boolean checkDangerousCharacter(String key, String value) {
        boolean flg = true;
        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("(.*?)<[\\s]*?(.*?)script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", Pattern.CASE_INSENSITIVE);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        //Avoid http
//         scriptPattern = Pattern.compile("http://(.*?)",Pattern.CASE_INSENSITIVE);
//         flg = scriptPattern.matcher(value).matches();
//         if(flg)
//        	 return false;
        // Avoid anything in a src='...' type of e-xpression
//         scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//         scriptPattern = Pattern.compile("(.*?)<(.*?)src(.*?)=(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//         flg = scriptPattern.matcher(value).matches();
//         if(flg)
//        	 return false;

        // Remove any lonesome <iframe ...> tag
        scriptPattern = Pattern.compile("().*?<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Remove any lonesome <frame ...> tag
        scriptPattern = Pattern.compile("().*?<frame(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Avoid eval(...) e-xpressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Avoid e-xpression(...) e-xpressions
        scriptPattern = Pattern.compile("e-xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Avoid vbscript:... e-xpressions
        scriptPattern = Pattern.compile("(.*?)javascript:(.*?)", Pattern.CASE_INSENSITIVE);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Avoid vbscript:... e-xpressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        // Avoid onload= e-xpressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        flg = scriptPattern.matcher(value).matches();
        if (flg)
            return false;
        //add by zhaosen
        if ("method".equals(key) || "id".equals(key)) {
            scriptPattern = Pattern.compile("(.*?)<(.*?)>(.*?)");
            Pattern scriptPattern1 = Pattern.compile("(.*?)&lt(.*?)&gt(.*?)");
            Pattern scriptPattern2 = Pattern.compile("(.*?)%(.*?)");
            Pattern scriptPattern3 = Pattern.compile("(.*?)http://(.*?)");
            flg = scriptPattern.matcher(value).matches();
            Boolean flg1 = scriptPattern1.matcher(value).matches();
            Boolean flg2 = scriptPattern2.matcher(value).matches();
            Boolean flg3 = scriptPattern3.matcher(value).matches();
            if (flg || flg1 || flg2 || flg3) {
                return false;
            }
        }
        if ("mid".equals(key)) {
            scriptPattern = Pattern.compile("\\d{0,}");
            flg = scriptPattern.matcher((CharSequence) value).matches();
            if (!flg) {
                return false;
            }
        }
        return true;
    }
}
