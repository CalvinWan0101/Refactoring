package tw.teddysoft.ezdoc.annotation;


import org.apiguardian.api.API;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(
        status = API.Status.EXPERIMENTAL,
        since = "1.0"
)
public @interface Definition {

    String brief();
    String narrative() default "";
    Class targetClass();
    Class[] usages() default {};
}
