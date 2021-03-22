package dgut.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: Service
 * @author: Steven
 * @time: 2021/3/8 15:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

    String name() default "";

    String group() default "";

    String version() default "";
}
