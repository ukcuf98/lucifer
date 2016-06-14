package core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 10:00
 */
public class BeanFactory {

    protected static ApplicationContext wac;
    private static BeanFactory me;

    private BeanFactory(ApplicationContext wac) {
        BeanFactory.wac = wac;
    }

    public static void init(ServletContext servletContext) {
        if (me == null) {
            me = new BeanFactory(
                    WebApplicationContextUtils
                            .getRequiredWebApplicationContext(servletContext));
        }
    }

    public static void init(ApplicationContext ctx) {
        if (me == null) {
            me = new BeanFactory(ctx);
        }
    }

    /**
     * 获取String Bean对象
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return wac.getBean(beanName);
    }

    /**
     * 获取jdbctemplate
     *
     * @param beanName
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(String beanName) {
        return (JdbcTemplate) wac.getBean(beanName);
    }

//    /**
//     * 获取JdbcTemplateAdmin管理员数据源
//     *
//     * @param beanName
//     * @return
//     */
//    public static JdbcTemplateAdmin getJdbcTemplateAdmin(String beanName) {
//        return (JdbcTemplateAdmin) wac.getBean(beanName);
//    }

    /**
     * 获取String Bean对象
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return wac.getBean(clazz);
    }

}
