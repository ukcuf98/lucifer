package core.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @Description:日志记录工具类
 * @version: v1.0.0
 * @author: Lucifer
 * @date: 2016-3-8 10:00:00
 */
@SuppressWarnings({"rawtypes"})
public class LogUtil {
    private LogUtil() {

    }

    /**
     * 获取Log4j对象
     *
     * @param clzz     用于日志文件命名，例如：core.util.LogUtil_20160101.log
     * @param logsPath 日志文件存放路径
     * @return
     */
    public static Logger getLogger(Class<?> clzz, String logsPath) {
        Logger logger = null;
        logger = Logger.getLogger(clzz);
        logger.addAppender(getAppender(clzz.getName(), logsPath));
        logger.addAppender(getConsolAppender());
        logger.setAdditivity(false);
        return logger;
    }

    /**
     * 获取Log4j对象
     *
     * @param clzz 用于日志文件命名，例如：core.util.LogUtil_20160101.log
     * @return
     */
    public static Logger getLogger(Class<?> clzz) {
        return getLogger(clzz, "logs");
    }

    private static DailyRollingFileAppender getAppender(String className, String logsPath) {
        DailyRollingFileAppender appender = null;
        try {
            String pattern = "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} - %m%n";
            Layout layout = new PatternLayout(pattern);
            appender = new DailyRollingFileAppender(layout, getPath(className, logsPath),
                    "'_'yyyyMMdd'.log'");
        } catch (IOException e) {
            new Exception(e);
        }
        return appender;
    }

    private static Appender getConsolAppender() {
        String pattern = "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} %c - %m%n";
        Layout layout = new PatternLayout(pattern);
        Appender appender = new ConsoleAppender(layout);
        return appender;
    }

    /**
     * 取得log文件的路径
     *
     * @param className 类名
     * @return
     */
    private static String getPath(String className, String logsPath) {
        StringBuffer path = new StringBuffer();
        path.append(logsPath);
        checkPathExists(path);
        path.append("/" + className);
        return path.toString();
    }

    /**
     * 检查路径是否存在，不存在时创建
     *
     * @param path
     */
    private static void checkPathExists(StringBuffer path) {
        String thePath = path.toString();
        File file = new File(thePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 删除指定天数之前的某类的日志文件
     *
     * @param clzz       类名
     * @param beforeDays 天数
     */
    public static void removeLogByDate(Class<?> clzz, int beforeDays) {
        try {
            File file = new File("logs");
            if (!file.exists()) {
                file.mkdir();
            }
            if (beforeDays > 0) {
                beforeDays = -beforeDays;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, beforeDays);
            File[] files = file.listFiles();
            String className = clzz.getName();
            for (File f : files) {
                String fileName = f.getName();
                if (fileName.startsWith(className + "_")) {
                    if (f.lastModified() <= calendar.getTime().getTime()) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(LogUtil.class, e);
        }
    }

    /**
     * 记录错误信息日志
     *
     * @param clazz 调用类
     * @param t     内容
     */
    public static void error(Class clazz, Throwable t) {
        LogUtil.error(clazz, t, "");
    }

    /**
     * 记录错误信息日志。
     *
     * @param clazz   调用类
     * @param message 内容
     */
    public static void error(Class clazz, String message) {
        Logger log = Logger.getLogger(clazz);
        log.error(message);
    }

    public static void error(Class clazz, Throwable t, String message) {
        Logger log = Logger.getLogger(clazz);
        log.error(message, t);
    }

    /**
     * 记录重要信息日志,线上的最低级别，可写极少的重要测试信息。
     *
     * @param clazz   调用类
     * @param message 内容
     */
    public static void info(Class clazz, String message) {
        Logger log = Logger.getLogger(clazz);
        log.info(message);
    }

    /**
     * 记录调试信息日志
     *
     * @param clazz   调用类
     * @param message 内容
     */
    public static void debug(Class clazz, String message) {
        Logger log = Logger.getLogger(clazz);
        log.debug(message);
    }
}
