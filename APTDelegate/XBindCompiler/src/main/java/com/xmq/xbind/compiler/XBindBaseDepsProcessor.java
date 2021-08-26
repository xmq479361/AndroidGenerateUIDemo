package com.xmq.xbind.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xmq.xbind.annotation.XBindEvent;
import com.xmq.xbind.annotation.XBindLayout;
import com.xmq.xbind.annotation.XBindView;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/20 22:33
 */
@AutoService(Processor.class)
public class XBindBaseDepsProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        System.out.println("XBindLayout init");
        mMessager = processingEnv.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "XBindLayout initialize");
        mFiler = processingEnv.getFiler();
         elementUtils = processingEnv.getElementUtils();
    }
    Elements elementUtils;
    Messager mMessager;
    Filer mFiler;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 规定需要处理的注解
        return Collections.singleton(XBindLayout.class.getCanonicalName());
    }
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
//        roundEnvironment.getE
        System.out.println("XBindLayout");
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XBindLayout.class);
        for (Element element : elements) {
            if (!(element instanceof  TypeElement)) {
                continue;
            }
            // 判断是否Class
            TypeElement typeElement = (TypeElement) element;
            System.out.println("XBindLayout typeElement: "+typeElement.getQualifiedName()+", "+
                typeElement.getAnnotation(XBindLayout.class));
            JavaFile javaFile = generateBaseActivity(typeElement);
//            JavaFile javaFile = generateBindByInject(element, typeElement);
            try {
                javaFile.writeTo(processingEnv.getFiler());
                System.out.println("XBindLayout typeElement generate ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private JavaFile generateBaseActivity( TypeElement typeElement) {
        XBindLayout xbindLayout = typeElement.getAnnotation(XBindLayout.class);
        MethodSpec.Builder onCreateMethodSpecBuilder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get("android.os","Bundle"), "savedInstanceState");
//            bindViewMethodSpecBuilder.addStatement("activity.setContentView(%d)", xbindLayout.value());
        onCreateMethodSpecBuilder.addStatement("super.onCreate(savedInstanceState)");
        onCreateMethodSpecBuilder.addStatement(String.format("setContentView(%d)", xbindLayout.value()));
        onCreateMethodSpecBuilder.addStatement(String.format("bindView()"));
        onCreateMethodSpecBuilder.addStatement(String.format("bindEvent()"));


        MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID);
        List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);
        for (Element fieldEle : allMembers) {
            XBindView xbindView = fieldEle.getAnnotation(XBindView.class);
            if (xbindView == null) continue;
            bindViewMethodSpecBuilder.addStatement(String.format("this.%s = (%s)findViewById(%d);",
                    fieldEle.getSimpleName(),
                    ClassName.get(fieldEle.asType()), xbindView.value()));
        }

        TypeSpec typeSpec = TypeSpec.classBuilder(Constants.FRONT + typeElement.getSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.get(typeElement.getSuperclass()))
                .addMethod(onCreateMethodSpecBuilder.build())
                .addMethod(bindViewMethodSpecBuilder.build())
                .build();
        JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec).build();
        return javaFile;
    }

    private JavaFile generateBindByDelegate(TypeElement typeElement) {

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(Constants.FRONT + typeElement.getSimpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


        XBindLayout xbindLayout = typeElement.getAnnotation(XBindLayout.class);
        MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(typeElement.asType()), "activity");
//            bindViewMethodSpecBuilder.addStatement("activity.setContentView(%d)", xbindLayout.value());
        bindViewMethodSpecBuilder.addStatement(String.format("activity.setContentView(%d)", xbindLayout.value()));

        List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);

        HashMap<Integer, Element> fieldTypes = new HashMap<>();
        List<Element> bindEventEles = new LinkedList<>();
        for (Element member : allMembers) {
            switch (member.getKind()){
                case METHOD:
                    XBindEvent xbindEvent = member.getAnnotation(XBindEvent.class);
                    if (xbindEvent != null) {
                        bindEventEles.add(member);
                        System.out.println("XBindLayout method: "+member.getSimpleName()
                                +", "+member.getClass().getSimpleName()+" = "+member.getKind()+" == "+member.getClass());
                    }
                    break;
                case FIELD:
                    XBindView xbindView = member.getAnnotation(XBindView.class);
                    if (xbindView != null) {
                        fieldTypes.put(xbindView.value(), member);
                        checkGenerateBindView(bindViewMethodSpecBuilder, member);
                        System.out.println("XBindLayout field: "+member.getSimpleName()
                                +", "+member.getClass().getSimpleName()+" = "+member.getKind()+" == "+member.getClass());
                    }
            }
        }
        typeSpecBuilder.addMethod(bindViewMethodSpecBuilder.build());
        System.out.println("XBindLayout methods: "+ bindEventEles.size());
        if (!bindEventEles.isEmpty()) {
            MethodSpec.Builder bindEventMethodSpecBuilder = MethodSpec.methodBuilder("bindEvent")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(typeElement.asType()), "activity");
            for (Element member : bindEventEles) {
                XBindEvent xbindEvent = member.getAnnotation(XBindEvent.class);
                if (xbindEvent != null) {
                    if (fieldTypes.containsKey(xbindEvent.value())) {
                        Element fieldEle = fieldTypes.get(xbindEvent.value());
                        bindEventMethodSpecBuilder.addStatement(String.format("activity.%s.setOnClickListener(new android.view.View.OnClickListener(){" +
                                        "\n\t@Override" +
                                        "\n\tpublic void onClick(android.view.View view){" +
                                        "\n\t\tactivity.%s(view);" +
                                        "\n\t}" +
                                        "\n});",
                                fieldEle.getSimpleName(), member.getSimpleName()));
                    }
//                    bindEventMethodSpecBuilder.addStatement(String.format("activity.%s = (%s)activity.findViewById(%d);", member.getSimpleName(),
//                            ClassName.get(member.asType()), xbindEvent.value()));
                }
//                checkGenerateBindEvent(bindEventMethodSpecBuilder, member);
                System.out.println("XBindLayout member: " + member.getSimpleName());
            }
            typeSpecBuilder.addMethod(bindEventMethodSpecBuilder.build());
        }
        JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpecBuilder.build()).build();
        return javaFile;
    }

    private void checkGenerateBindEvent(MethodSpec.Builder bindEventMethodSpecBuilder, Element member) {
        XBindEvent xbindEvent = member.getAnnotation(XBindEvent.class);
        if (xbindEvent != null) {
            bindEventMethodSpecBuilder.addStatement(String.format("activity.%s = (%s)activity.findViewById(%d);", member.getSimpleName(),
                    ClassName.get(member.asType()), xbindEvent.value()));
        }
    }
    private void checkGenerateBindView(MethodSpec.Builder bindViewMethodSpecBuilder, Element member) {
        XBindView xbindView = member.getAnnotation(XBindView.class);
        if (xbindView != null) {
            bindViewMethodSpecBuilder.addStatement(String.format("activity.%s = (%s)activity.findViewById(%d);", member.getSimpleName(),
                    ClassName.get(member.asType()), xbindView.value()));
        }
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
}
