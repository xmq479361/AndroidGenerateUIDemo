//package com.xmq.xbind
//
//import org.objectweb.asm.AnnotationVisitor
//import org.objectweb.asm.ClassVisitor
//import org.objectweb.asm.FieldVisitor
//import org.objectweb.asm.MethodVisitor
//import org.objectweb.asm.Opcodes
//import org.objectweb.asm.TypePath
//
//class XBindClassVisitor extends ClassVisitor {
//
//    XBindClassVisitor(ClassVisitor classVisitor) {
//        super(Opcodes.ASM9, classVisitor)
//    }
//
//    @Override
//    AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
//        println("visitTypeAnnotation: $typeRef, ${typePath} => $descriptor")
//        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
//    }
//
//    @Override
//    void visitNestMember(String nestMember) {
//        println("visitNestMember: $nestMember")
//        super.visitNestMember(nestMember)
//    }
//
//    @Override
//    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
//        println("visitAnnotation: $descriptor")
//        if ("Lxbind/annotation/XBindLayout;".equals(descriptor)) {
//            isXBindRegsiter = true
//        }
//        return super.visitAnnotation(descriptor, visible)
//    }
//    boolean isXBindRegsiter
//    String className
//    String srcSuperName
//    @Override
//    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        println("visit: $version, $access, $name, $signature, $superName, $interfaces")
//        className = name
//        srcSuperName = superName
//        super.visit(version, access, name, signature, superName, interfaces)
//    }
//
//    @Override
//    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
//        println("visitField: $access, $name, $descriptor, $signature, $value")
//        return super.visitField(access, name, descriptor, signature, value)
//    }
//
//    @Override
//    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//        println("==>visitMethod: $access, $name, $descriptor, $signature, $exceptions")
//        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
//        if (isXBindRegsiter && name == "onCreate" && descriptor == "(Landroid/os/Bundle;)V") {
//            return new XBindLayoutMethodVisitor(mv, className, name)
//        }
//        return mv
//    }
//
//    @Override
//    void visitSource(String source, String debug) {
//        println("visitSource: $source, $debug")
//        super.visitSource(source, debug)
//    }
//}