package tw.teddysoft.ezdoc.annotation;


import org.apiguardian.api.API;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(
        status = API.Status.EXPERIMENTAL,
        since = "1.0"
)
public @interface Usage {
    String value();
    Class targetClass();
    Class definitionClass() default Void.class;
}
