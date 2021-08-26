package com.xmq.xbind.util

import com.xmq.xbind.core.XBindTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile

/**
 * @author xmqyeah* @CreateDate 2021/8/23 22:50
 */
public class ScanUtil {

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null
        // && entryName.startsWith(Constants.XBIND_PACKAGE_NAME)
    }

    static void scanJar(File jarFile, File dest) {
        if (!jarFile) return
        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            def jarEntry = enumeration.nextElement()
            def entryName = jarEntry.getName()
            if (entryName.endsWith(Constants.NAME_XBINDER_SUFFIX)) {
                InputStream inputStream = file.getInputStream(jarEntry)
                scanClass(inputStream)
                inputStream.close()
            } else if (Constants.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                XBindTransform.fileToInitClass = dest
            }
        }
        file.close()
    }

    /**
     * scan class file
     * @param class file
     */
    static void scanClass(File file) {
        scanClass(new FileInputStream(file))
    }

    static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    final static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor)
        }
//        String classNameIfBind
        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            Logger.info("visit: $name, $superName, ${interfaces}")
            if (interfaces != null) {
                interfaces.each {
                    Logger.info("visit interface: $it, $name, $superName, ${it.replaceAll("/", ".")}")
                    if (Constants.NAME_XBINDER_INTERFACE == it.replaceAll("/", ".")) {
//                        classNameIfBind = name
                        XBindTransform.registers.put(name, "")
                    }
                }
            }
        }

//        @Override
//        MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//            Logger.info("visit Method: $name, $descriptor, $signature, $classNameIfBind")
//            if (classNameIfBind && Constants.XBIND_BIND_METHOD_NAME == name
//                && "(Ljava/lang/Object;)V" != descriptor) {
//                String classDelegateTo = descriptor.substring(2, descriptor.length() - 3).replaceAll("/", ".")
//                XBindTransform.registers.put(classDelegateTo, classNameIfBind)
//                Logger.info("visit add resitser: $classDelegateTo : $classNameIfBind")
//            }
////            XBindTransform.registers.add(name)
////            Logger.info("visit add resitser: $it")
//            return super.visitMethod(access, name, descriptor, signature, exceptions)
//        }
    }
}
