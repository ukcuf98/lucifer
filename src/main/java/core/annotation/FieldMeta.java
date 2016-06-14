package core.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface FieldMeta {
    String columDesc();
}
