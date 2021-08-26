//package com.xmq.xbind
//
//import org.objectweb.asm.MethodVisitor
//import org.objectweb.asm.Opcodes
//import org.objectweb.asm.commons.AdviceAdapter
//
//class XBindLayoutMethodVisitor extends MethodVisitor {
//
//    XBindLayoutMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
//        super(Opcodes.ASM9, methodVisitor)
//        this.className = className
//        this.methodName = methodName
//    }
//    private String className, methodName
//    @Override
//    void visitInsn(int opcode) {
//        super.visitInsn(opcode)
////        methodVisitor.visitVarInsn(ALOAD, 0);
////        methodVisitor.visitLdcInsn(new Integer(2131427357));
////        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/xmq/bind/SecondActivity", "setContentView", "(I)V", false);
////        methodVisitor.visitVarInsn(ALOAD, 0);
////        methodVisitor.visitVarInsn(ALOAD, 0);
////        methodVisitor.visitLdcInsn(new Integer(2131231039));
////        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/xmq/bind/SecondActivity", "findViewById", "(I)Landroid/view/View;", false);
////        methodVisitor.visitTypeInsn(CHECKCAST, "android/widget/TextView");
////        methodVisitor.visitFieldInsn(PUTFIELD, "com/xmq/bind/SecondActivity", "second_text", "Landroid/widget/TextView;");
////        methodVisitor.visitInsn(RETURN);
//
//    }
//    @Override
//    void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
//        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
//        println("\tvisitMethodInsn: $opcodeAndSource, $owner, $name, $descriptor, $isInterface")
//        if (name == "onCreate" && descriptor == "(Landroid/os/Bundle;)V") {
//
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitLdcInsn(new Integer(2131427357));
//        mv.visitMethodInsn(INVOKEVIRTUAL, owner, "setContentView", "(I)V", false);
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitLdcInsn(new Integer(2131231039));
//        mv.visitMethodInsn(INVOKEVIRTUAL, "com/xmq/bind/SecondActivity", "findViewById", "(I)Landroid/view/View;", false);
//        mv.visitTypeInsn(CHECKCAST, "android/widget/TextView");
//        mv.visitFieldInsn(PUTFIELD, "com/xmq/bind/SecondActivity", "second_text", "Landroid/widget/TextView;");
//        mv.visitInsn(RETURN);
//        }
//    }
//}