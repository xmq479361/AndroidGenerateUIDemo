package com.xmq.xbind.util;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:52
 */
public class Constants {

    final static String NAME_XBINDER_INTERFACE = "com.xmq.xbind.IXBinder"
    final static String NAME_XBINDER_SUFFIX = "\$\$IXBinder"
    final static String GENERATE_TO_INJECT_CODE_METHOD_NAME = "loadInjectBindMap"
    final static String GENERATE_TO_INJECT_BIND_METHOD_NAME = "registerBindMap"
    final static String GENERATE_TO_INJECT_DESCRIPTION = "(Lcom/xmq/xbind/IXBinder;)V"
//    final static String XBIND_BIND_METHOD_NAME = "xbind"
//    final static String GENERATE_TO_INJECT_DESCRIPTION = "(Ljava/lang/String;Lcom/xmq/xbind/IXBinder;)V"
    final static String GENERATE_TO_LOAD_METHOD_NAME = "loadInfo"

    /**
     * The register code is generated into this class
     */
    static final String GENERATE_TO_CLASS_NAME = 'com/xmq/xbind/core/RegisteLogicCenter'
    /**
     * you know. this is the class file(or entry in jar file) name
     */
    static final String GENERATE_TO_CLASS_FILE_NAME = GENERATE_TO_CLASS_NAME + '.class'
}
