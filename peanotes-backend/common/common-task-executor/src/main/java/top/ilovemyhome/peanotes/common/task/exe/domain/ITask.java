package top.ilovemyhome.peanotes.common.task.exe.domain;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ITask {

    String value();

    String init() default "";

    String destroy() default "";

}
