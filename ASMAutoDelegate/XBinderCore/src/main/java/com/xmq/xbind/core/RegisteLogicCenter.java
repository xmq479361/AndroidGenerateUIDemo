package com.xmq.xbind.core;

import com.xmq.xbind.IXBinder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:12
 */
public class RegisteLogicCenter {

    private volatile static boolean registerByPlugin;

    public final static synchronized void init() {
        loadInjectBindMap();
        if (registerByPlugin) {
        }
    }

    /**
     * mark already registered by arouter-auto-register plugin
     */
    private static void markRegisteredByPlugin() {
        if (!registerByPlugin) {
            registerByPlugin = true;
        }
    }

    private static void loadInjectBindMap() {
        registerByPlugin = false;
        //
//        registerBindMap(new XXXBinder());

//        Iterator<IXBinder> iterator =  ServiceLoader.load(IXBinder.class).iterator();
//        while (iterator.hasNext()) {
//            IXBinder binder = iterator.next();
//            android.util.Log.w("XBinder", "[ServiceLoader] registerBindMap: "+binder.getDelegateClassName()+" :"+ binder);
//            XBindMapCenter.bindMap.put(binder.getDelegateClassName(), binder);
//        }
    }

    private final static void registerBindMap(IXBinder binder) {
        final String className = binder.getDelegateClassName();
        android.util.Log.w("XBinder", "[Auto-Delegate] registerBindMap: "+className+" :"+ binder);
        XBindMapCenter.bindMap.put(className, binder);
    }
}
