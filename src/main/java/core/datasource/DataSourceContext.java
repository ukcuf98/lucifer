package core.datasource;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 15:37
 */
public class DataSourceContext {
    /**
     * 当前线程数据库操作对应的读数据源
     */
    private static ThreadLocal<String> read_jdbcTemplate = new ThreadLocal<String>();

    /**
     * 设定当前操作的读数据源
     */
    public static void setReadJdbcTemplate(String jdbcTemplate) {
        read_jdbcTemplate.set(jdbcTemplate);
    }

    /**
     * 获取当前操作的读数据源
     */
    public static String getReadJdbcTemplate() {
        return read_jdbcTemplate.get();
    }

    /**
     * 移除当前操作的读数据源（在读取操作完成后，一定要调用此方法）
     */
    public static void removeReadJdbcTemplate() {
        read_jdbcTemplate.remove();
    }
}
