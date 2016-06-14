package core.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UserDefined {

    /**
     * 转换来源
     *
     * @return
     */
    public String transition_source() default "";

    /**
     * 转换函数 example:"函数名"
     *
     * @return
     */
    public String transition_function() default "";

    /**
     * 通过固定列表进行转换 example:"0,'停用',1,'启用'"
     *
     * @return
     */
    public String transition_list() default "";

    /**
     * 查询表的别名
     *
     * @return
     */
    public String table_alias() default "";

    /**
     * 数据库列名
     *
     * @return
     */
    public String db_fieldName() default "";
}
