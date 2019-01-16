package cn.hayye.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvColumn {
    /**
     * 所在列对应的；列名
     */
    public String name() default "";
}
