package com.chenglong.annotations;


import com.chenglong.enums.DataType;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/20   该注解将默认非空判断
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Valid {
    DataType value() default DataType.STRING;
}
