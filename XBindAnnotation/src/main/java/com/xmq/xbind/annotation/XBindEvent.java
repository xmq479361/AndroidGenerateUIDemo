package com.xmq.xbind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/20 23:04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface XBindEvent {
    int value();
    EventType type();
}
