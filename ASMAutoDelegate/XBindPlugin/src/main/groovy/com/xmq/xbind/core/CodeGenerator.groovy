package com.xmq.xbind.core

import com.sun.xml.bind.v2.runtime.reflect.opt.Const
import com.xmq.xbind.util.Constants
import com.xmq.xbind.util.Logger
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.lang.reflect.Method
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry;


/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:51
 */
 class CodeGenerator {


     static void insertInitCodeTo() {
         Logger.info("insertInitCodeTo: ")
//         if (registerSetting != null && !registerSetting.classList.isEmpty()) {
         CodeGenerator processor = new CodeGenerator()
         File file = XBindTransform.fileToInitClass
         if (file.getName().endsWith('.jar'))
             processor.insertInitCodeIntoJarFile(file)
//         }
     }

/**
      * generate code into jar file
      * @param jarFile the jar file which contains LogisticsCenter.class
      * @return
      */
     private File insertInitCodeIntoJarFile(File jarFile) {
         if (jarFile) {
             def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
             if (optJar.exists())
                 optJar.delete()
             Logger.info("insertInitCodeIntoJarFile: $jarFile")
             def file = new JarFile(jarFile)
             Enumeration enumeration = file.entries()
             JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

             while (enumeration.hasMoreElements()) {
                 JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                 String entryName = jarEntry.getName()
                 ZipEntry zipEntry = new ZipEntry(entryName)
                 InputStream inputStream = file.getInputStream(jarEntry)
                 jarOutputStream.putNextEntry(zipEntry)
                 Logger.i('Insert entryName >> ' + entryName)
                 if (Constants.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                     Logger.e('Insert init code to class >> ' + entryName)
                     def bytes = insertLoadInfoWhenInit(inputStream)
                     jarOutputStream.write(bytes)
                 } else {
                     jarOutputStream.write(IOUtils.toByteArray(inputStream))
//                     jarOutputStream.write(IOUtils.toByteArray(inputStream))
                 }
                 inputStream.close()
                 jarOutputStream.closeEntry()
             }
             jarOutputStream.close()
             file.close()

             if (jarFile.exists()) {
                 jarFile.delete()
             }
             optJar.renameTo(jarFile)
         }
         return jarFile
     }

     byte[] insertLoadInfoWhenInit(InputStream inputStream) {
        ClassReader classReader = new ClassReader(inputStream)
         ClassWriter classWriter = new ClassWriter(classReader, 0)
         ClassVisitor classVisitor = new InsertCodeClassVisitor(classWriter);
         classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
         return classWriter.toByteArray()
     }

     private final class InsertCodeClassVisitor extends ClassVisitor {

         InsertCodeClassVisitor(ClassVisitor classVisitor) {
             super(Opcodes.ASM9, classVisitor)
         }

         @Override
         void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
             super.visit(version, access, name, signature, superName, interfaces)
         }

         @Override
         MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
             MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
             if (name == Constants.GENERATE_TO_INJECT_CODE_METHOD_NAME) {
                 mv = new InsertCodeMethodVisitor(api, mv)
             }
             return mv
         }
     }
     private final class InsertCodeMethodVisitor extends MethodVisitor {

         InsertCodeMethodVisitor(int api, MethodVisitor methodVisitor) {
             super(api, methodVisitor)
         }

         @Override
         void visitInsn(int opcode) {
             if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                 XBindTransform.registers.each {key, it->
                     Logger.e('Insert init code to class >> ' + key)
//                     mv.visitLdcInsn(key)
                     mv.visitTypeInsn(Opcodes.NEW, key)
                     mv.visitInsn(Opcodes.DUP)
                     mv.visitMethodInsn(Opcodes.INVOKESPECIAL, key, "<init>", "()V", false)

                     Logger.e('Insert init code to class >> ' + Constants.GENERATE_TO_INJECT_DESCRIPTION);
                     mv.visitMethodInsn(Opcodes.INVOKESTATIC, Constants.GENERATE_TO_CLASS_NAME,
                            Constants.GENERATE_TO_INJECT_BIND_METHOD_NAME,
                             Constants.GENERATE_TO_INJECT_DESCRIPTION, false)
//                     Constants.GENERATE_TO_INJECT_BIND_METHOD_NAME, "(Ljava/lang/String;)V", false)
                     /**
                      methodVisitor.visitInsn(ICONST_0);
                      methodVisitor.visitFieldInsn(PUTSTATIC, "com/xmq/xbind/core/RegisteLogicCenter", "registerByPlugin", "Z");
                      methodVisitor.visitLdcInsn("");
                      methodVisitor.visitTypeInsn(NEW, "com/xmq/xbind/core/RegisteLogicCenter$XXXBinder");
                      methodVisitor.visitInsn(DUP);
                      methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/xmq/xbind/core/RegisteLogicCenter$XXXBinder", "<init>", "()V", false);
                      methodVisitor.visitMethodInsn(INVOKESTATIC, "com/xmq/xbind/core/RegisteLogicCenter", "registerBindMap", "(Ljava/lang/String;Lcom/xmq/xbind/IXBinder;)V", false);

                      */
                 }
             }
             super.visitInsn(opcode)
         }
         @Override
         void visitMaxs(int maxStack, int maxLocals) {
             super.visitMaxs(maxStack + 4, maxLocals)
         }
     }
}
