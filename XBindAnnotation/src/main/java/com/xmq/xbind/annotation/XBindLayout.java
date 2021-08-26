package com.xmq.xbind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/20 22:27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface XBindLayout {
    int value();
}
