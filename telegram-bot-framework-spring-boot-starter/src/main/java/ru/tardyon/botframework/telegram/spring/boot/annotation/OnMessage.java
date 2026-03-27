package ru.tardyon.botframework.telegram.spring.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnMessage {

    String command() default "";

    String textEquals() default "";

    String textStartsWith() default "";

    boolean webAppDataPresent() default false;

    boolean paidMediaPresent() default false;

    boolean paidMediaPurchasedPresent() default false;

    boolean giftPresent() default false;

    boolean uniqueGiftPresent() default false;

    boolean giftUpgradeSentPresent() default false;

    boolean refundedPaymentPresent() default false;

    boolean successfulPaymentPresent() default false;

    boolean storyPresent() default false;

    boolean checklistPresent() default false;

    String state() default "";
}
