package com.xing.api.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.moshi.Json;
import com.xing.api.annotations.CompanionForJson;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

public class MyClass extends AbstractProcessor{
    private Elements elements;
    private Filer filer;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        filer = processingEnv.getFiler();
        types = processingEnvironment.getTypeUtils();
    }

    @SuppressWarnings("SSBasedInspection")
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(CompanionForJson.class)) {
            String packageName = elements.getPackageOf(element) + ".generated";
            String className = "FieldsCompanion_" + element.getSimpleName().toString();
            try {
                JavaFileObject jfo = filer.createSourceFile(packageName + "." + className);
                Writer writer = jfo.openWriter();
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                      .superclass(ClassName.get("com.xing.api.companion","Companion"))
                      .addModifiers(Modifier.PUBLIC);
                List<? extends Element> enclosedElements = element.getEnclosedElements();
                for (int i = 0; i < enclosedElements.size(); i++) {

                    Element el = enclosedElements.get(i);
                    Json jsonAnnotation = el.getAnnotation(Json.class);
                    if (jsonAnnotation != null) {
                        String fieldName = jsonAnnotation.name();
                        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("request" + fieldName);
                        methodBuilder
                              .addModifiers(Modifier.PUBLIC)
                              .returns(ClassName.get(packageName, className))
                              .addStatement("// %s" + ((ExecutableElement) el).getReturnType())
                              .addStatement("addField(\""+fieldName+"\")")
                              .addStatement("return this");

                        classBuilder.addMethod(methodBuilder.build());
                    }

                }
//                for (int i = 0; i < jsonFields.size(); i++) {
//                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("request" + jsonFields.get(i));
//                    methodBuilder
//                          .addModifiers(Modifier.PUBLIC)
//                          .returns(ClassName.get(packageName, className))
//                          .addParameter()
//                }
                JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build()).build();
                javaFile.writeTo(writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return true;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(CompanionForJson.class.getCanonicalName()));
    }
}
