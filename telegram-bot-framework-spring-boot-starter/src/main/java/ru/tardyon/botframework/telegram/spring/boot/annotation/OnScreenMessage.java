package ru.tardyon.botframework.telegram.spring.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnScreenMessage {

    String screen();

    String command() default "";

    String textEquals() default "";

    String textStartsWith() default "";

    String state() default "";
}
