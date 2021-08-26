package com.xmq.xbind;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:02
 */
public interface IXBinder<T> {

    void xbind(T activity);

    String getDelegateClassName();
}
