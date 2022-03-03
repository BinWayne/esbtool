package esbtool.service;

import com.squareup.javapoet.*;
import esbtool.model.BodyVO;
import esbtool.util.EsbParser;
import esbtool.util.GenerateUtil;
import esbtool.util.JavaFieldModel;
import esbtool.util.JavaGenerate;
import org.springframework.stereotype.Component;

import java.util.*;

import static javax.lang.model.element.Modifier.PUBLIC;

@Component
public class GenerateService {

    public String parseBody(List<BodyVO> bodyVOList) throws ClassNotFoundException {

        ArrayList<JavaFieldModel> javaFieldModels = new ArrayList<>();
        for (BodyVO bodyVO:bodyVOList){
            JavaFieldModel javaFieldModel = EsbParser.parseLine(bodyVO);
            javaFieldModels.add(javaFieldModel);
        }
        JavaGenerate javaGenerate = new JavaGenerate();
        List<FieldSpec> fieldSpecs = javaGenerate.generateField(javaFieldModels);
        List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
        TypeSpec.Builder builderBase = TypeSpec.classBuilder("demo");
        // 循环封装属性
        for (FieldSpec fieldSpec : fieldSpecs) {
            builderBase.addField(fieldSpec);
        }
        // 循环封装方法
        for (MethodSpec spec : methodSpecs) {
            builderBase.addMethod(spec);
        }

        JavaFile javaFile = JavaFile.builder("com.ncbank.openbank", builderBase.build())
                .build();

        return javaFile.toString();
    }


    public String generate(Map<String, List<JavaFieldModel>> demo){
        Map<String, String> map = new HashMap<>();

        Set<Map.Entry<String, List<JavaFieldModel>>> entries = demo.entrySet();

        JavaGenerate javaGenerate = new JavaGenerate();
        for (Map.Entry<String, List<JavaFieldModel>> entry : entries) {
            // 最后生成base 主类模板
            if (!entry.getKey().equals("demo")) {
                List<FieldSpec> fieldSpecs = javaGenerate.generateField(entry.getValue());
                List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
                MethodSpec methodSpec = javaGenerate.generateToString(fieldSpecs, entry.getKey());

                TypeSpec.Builder builder = TypeSpec.classBuilder(entry.getKey());
                // 循环封装属性
                for (FieldSpec fieldSpec : fieldSpecs) {
                    builder.addField(fieldSpec);
                }

                // 循环封装方法
                for (MethodSpec spec : methodSpecs) {
                    builder.addMethod(spec);
                }
                TypeSpec build = builder.addMethod(methodSpec).build();
                //生成一个Java文件
                JavaFile javaFile = JavaFile.builder("com.xupt.willscorpio.javatest", build)
                        .build();

                System.out.println(javaFile);
                map.put(entry.getKey(), javaFile.packageName);
            }
        }

        //--------------补充上列表属性------------
        // 生成主类模板
        List<JavaFieldModel> list = demo.get("demo");
        List<FieldSpec> fieldSpecs = javaGenerate.generateField(list);
        List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
        TypeSpec.Builder builderBase = TypeSpec.classBuilder("demo");
        // 循环封装属性
        for (FieldSpec fieldSpec : fieldSpecs) {
            builderBase.addField(fieldSpec);
        }
        // 循环封装方法
        for (MethodSpec spec : methodSpecs) {
            builderBase.addMethod(spec);
        }
        //---补充列表的 getter setter
        // 先构造一个list，然后构造一个泛型的类型
        ClassName className = ClassName.get("java.util", "List");
        Set<Map.Entry<String, String>> entries1 = map.entrySet();
        MethodSpec methodSpec;
        for (Map.Entry<String, String> stringStringEntry : entries1) {
            ClassName fanxing = ClassName.get(stringStringEntry.getValue(), stringStringEntry.getKey());
            // 泛型组装
            TypeName listOfHoverboards = ParameterizedTypeName.get(className, fanxing);
            builderBase
                    .addField(FieldSpec.builder(listOfHoverboards, stringStringEntry.getKey()).build());

            String name = GenerateUtil.upper(stringStringEntry.getKey());
            methodSpec = MethodSpec.methodBuilder("get" + name)
                    .addModifiers(PUBLIC)
                    .returns(listOfHoverboards)
                    .addStatement("return " + stringStringEntry.getKey())
                    .build();
            builderBase.addMethod(methodSpec);

            methodSpec = MethodSpec.methodBuilder("set" + name)
                    .addModifiers(PUBLIC)
                    .addParameter(listOfHoverboards, name)
                    .addStatement("this." + name + "=" + name)
                    .build();
            builderBase.addMethod(methodSpec);

        }

        //生成一个Java文件
        JavaFile javaFile = JavaFile.builder("com.xupt.willscorpio.javatest", builderBase.build())
                .build();

        System.out.println(javaFile);
        return javaFile.toString();
    }
}
