package core.exception;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.Writer;

/**
 * @Description:freemarker报错异常捕获处理
 * @version: v1.0.0
 * @author: Lucifer
 * @date: 2016-3-8 10:00:00
 */
public class FreemarkerExceptionHandler implements TemplateExceptionHandler {


    /**
     * 发送错误邮件
     *
     * @param templateException
     * @param environment
     * @param writer
     * @throws Exception
     * @author zhaolimin
     * @date 2014-5-19 下午2:43:46
     */
    private void sendEmail(TemplateException templateException,
                           Environment environment, Writer writer) throws Exception {

    }

    public void handleTemplateException(TemplateException e, Environment environment, Writer writer) throws TemplateException {

    }
}
