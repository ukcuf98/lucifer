package core.sysconst;

import core.util.PropUtil;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:42
 */
public class DataSourceConst {
    /**
     * 写数据源
     */
    public static final String JDBCTEMPLATEWRITE = "jdbcTemplateWrite";
    /**
     * 读数据源
     */
    public static final String JDBCTEMPLATEREAD = "jdbcTemplateRead";
    /**
     * 数据库类型
     */
    public static int DATABASE_TYPE = 0;
    /**
     * 数据库类型-oracle
     */
    public static final int DATABASE_ORACLE = 0;
    /**
     * 数据库类型-mysql
     */
    public static final int DATABASE_MYSQL = 1;
    /**
     * 数据库类型-其他
     */
    public static final int DATABASE_OTHER = -1;

    /**
     * 重新加载配置
     *
     * @throws Exception
     */
    public static void reloadConfig() throws Exception {
        String driverClasses = PropUtil.getPropValueFromClasspath("datasource",
                "DriverClasses");
        if (null == driverClasses) {
            DATABASE_TYPE = DATABASE_ORACLE;
        }
        if (-1 != driverClasses.indexOf("oracle")) {
            DATABASE_TYPE = DATABASE_ORACLE;
        } else if (-1 != driverClasses.indexOf("mysql")) {
            DATABASE_TYPE = DATABASE_MYSQL;
        } else {
            DATABASE_TYPE = DATABASE_OTHER;
        }
    }
}
