package core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:54
 */
public class PropUtil {
    private static Logger logger = LogUtil.getLogger(PropUtil.class);
    public static String ROOT_PATH; // 系统目录
    public static String CLASSPATH_FLODER = "/conf/";// classpath中资源文件的文件夹
    /**
     * 缓存key:val
     */
    public static Map<String, String> cacheMap = new HashMap<String, String>();

    /**
     * 设置系统更目录
     *
     * @param rOOT_PATH the rOOT_PATH to set
     */
    public static void setROOT_PATH(String rOOT_PATH) {
        ROOT_PATH = rOOT_PATH;
    }

    /**
     * 将环境变量转为实际的值
     *
     * @param key
     * @param oldVal
     * @return
     */
    public static String convertToRealVal(String key, String oldVal) {
        if (Util.isBlank(oldVal)) {
            return null;
        }
        String reg = "\\$\\{(.*)\\}";
        Pattern p_reg = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher m_reg = p_reg.matcher(oldVal);
        StringBuffer sb = new StringBuffer();
        while (m_reg.find()) {
            String group = m_reg.group(1);
            String replaceVal = System.getProperty(group);
            if (null == replaceVal) {
                replaceVal = cacheMap.get(group);
            }
            if (null != replaceVal) {
                m_reg.appendReplacement(sb,
                        Matcher.quoteReplacement(replaceVal));
            }
        }
        m_reg.appendTail(sb);
        String newVal = sb.toString().trim();
        cacheMap.put(key, newVal);
        return newVal;
    }

    /**
     * 读取Properties文件键值
     *
     * @param fname 文件名
     * @param key   键
     * @return
     */
    public static String getPropValue(String fname, String key) {
        return getPropValue(fname, key, false);
    }

    /**
     * 读取Properties文件键值
     *
     * @param fname      文件名
     * @param key        键
     * @param includeInv 是否读取环境变量
     * @return
     */
    public static String getPropValue(String fname, String key,
                                      boolean includeInv) {

        Properties newprop = loadProp(fname);
        if (null == newprop) {
            logger.error(fname + "not found!");
            return "";
        }

        String oldVal = newprop.getProperty(key);
        if (Util.isBlank(oldVal)) {
            return "";
        }
        if (includeInv) {
            return convertToRealVal(key, oldVal);
        } else {
            return oldVal.trim();
        }

    }

    /**
     * 从clapath中获取资源文件属性值
     *
     * @param fname
     * @param key
     * @return
     */
    public static String getPropValueFromClasspath(String fname, String key) {
        return getPropValueFromClasspath(fname, key, false);

    }

    /**
     * 从clapath中获取资源文件属性值
     *
     * @param fname      文件名
     * @param key        键
     * @param includeInv 是否读取环境变量
     * @return
     */
    public static String getPropValueFromClasspath(String fname, String key,
                                                   boolean includeInv) {

        Properties newprop = loadPropFromClasspath(fname);
        if (null == newprop) {
            logger.error(fname + "not found!");
            return null;
        }

        String oldVal = newprop.getProperty(key);
        if (Util.isBlank(oldVal)) {
            return "";
        }
        if (includeInv) {
            return convertToRealVal(key, oldVal);
        } else {
            return oldVal.trim();
        }

    }

    /**
     * 读取Properties文件
     *
     * @param fname
     * @return
     */
    public static Properties loadProp(String fname) {
        try {

            Properties prop = null;
            Hashtable htmlfileHash = new Hashtable();
            Hashtable htmlfileTime = new Hashtable();
            File f = new File(getPROP_ROOT() + fname + ".properties");
            if (!f.exists())
                return null;

            long ftime = f.lastModified();
            Long hftime = (Long) htmlfileTime.get(fname);

            if (hftime == null || hftime.longValue() != ftime) {

                prop = new Properties();
                FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(bis));

                prop.load(reader);
                reader.close();
                bis.close();
                fis.close();

                htmlfileHash.put(fname, prop);
                htmlfileTime.put(fname, new Long(ftime));
            }

            return (Properties) htmlfileHash.get(fname);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从classpath中读取Properties文件
     *
     * @param fname
     * @return
     */
    public static Properties loadPropFromClasspath(String fname) {
        try {
            Properties res = new Properties();
            InputStream in = PropUtil.class
                    .getResourceAsStream(CLASSPATH_FLODER + fname
                            + ".properties");
            res.load(in);
            in.close();
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取properties配置文件路径
     *
     * @return the PROP_ROOT
     */
    public static String getPROP_ROOT() {
        File localFile = new File(ROOT_PATH + "WEB-INF" + File.separatorChar
                + "conf");
        if (localFile.exists()) {
            return ROOT_PATH + "WEB-INF" + File.separatorChar + "conf"
                    + File.separatorChar;
        }
        return null;
    }

    /**
     * 写Properties文件值 重新配置文件注释会丢失
     *
     * @param fname 文件名
     * @param key   键
     * @return
     * @throws IOException
     */
    public static void setPropValue(String fname, String key, String value)
            throws IOException {
        String fileNamePath = getPROP_ROOT();
        if (!Util.isBlank(fileNamePath)) {
            String fileName = fname + ".properties";
            Properties prop = new Properties();
            InputStream in = null;
            OutputStream out = null;
            File filePath = new File(fileNamePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            File file = new File(fileNamePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try {
                in = new FileInputStream(file);
                prop.load(in);
                out = new FileOutputStream(file);
                prop.setProperty(key, value);
                prop.store(out, key);// 保存
                out.flush();
                out.close();
            } catch (IOException e) {
                LogUtil.error(PropUtil.class, e);
            } finally {
                if (null != in)
                    in.close();
                if (null != out)
                    out.close();
            }
        }
    }
}
