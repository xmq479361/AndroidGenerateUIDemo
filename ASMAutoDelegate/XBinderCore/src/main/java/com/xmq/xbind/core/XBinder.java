package com.xmq.xbind.core;

import android.util.Log;

import com.xmq.xbind.IXBinder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:04
 */
public class XBinder {
    final static Map<String, IXBinder> binderMaps = new LinkedHashMap<>();

    volatile static boolean isInitialized = false;
    volatile static boolean isInitialing = false;
    public static void init() {
        RegisteLogicCenter.init();
        isInitialized = true;
    }

    final static void checkInit() {
        if (!isInitialized) {
            throw new IllegalStateException("You must invoke XBind.init() before");
        }

    }

    public static synchronized <T> void bind(T object) {
        checkInit();
        final String className = object.getClass().getName();
        Log.w("XBinder", "bind: "+ className+" = "+Arrays.toString(XBindMapCenter.bindMap.keySet().toArray()));
        if (XBindMapCenter.bindMap.containsKey(className)) {
            IXBinder<T> ixBinder = XBindMapCenter.bindMap.get(className);
            if (ixBinder != null) {
                ixBinder.xbind(object);
                return;
            }
        }
        throw new RuntimeException("Can't found your bind source");
    }

}
