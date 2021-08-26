package com.xmq.xbind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * class XBindxxxProvider implementation IXBinder {
 *      @Override
 *      public void loadInfo(Ma
 * }
 * @author xmqyeah
 * @CreateDate 2021/8/20 22:53
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface XBindView {
    int value();
}
