package api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)    // 设置注解的作用范围，注解在类上
@Retention(RetentionPolicy.RUNTIME) // 设置注解的作用时间，运行时生效
public @interface ServiceName {

    String value() default ""; // 调用接口的具体实现类型
}
