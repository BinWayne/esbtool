package esbtool.util;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import esbtool.model.BodyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class EsbParser {

    // 行解析 普通行解析 包括array 内部 和arry 外部
    public  static JavaFieldModel parseLine(BodyVO bodyVO) throws ClassNotFoundException {

        JavaFieldModel model = new JavaFieldModel();
        model.setName(GenerateUtil.lowerCase(bodyVO.getVariableName()));
        model.setComment(bodyVO.getRemarkName());
        if (!bodyVO.getType().equals("")) {
            model.setClazz(Class.forName("java.lang."+bodyVO.getType()));
        } else {
            model.setClazz(String.class);
        }
        return model;
    }

    public static List<JavaFieldModel> parseList(List<BodyVO> innerBodyVos,String listName) throws ClassNotFoundException {
        ArrayList<JavaFieldModel> javaFieldModels = new ArrayList<>();

        for(BodyVO bodyVO:innerBodyVos){
            JavaFieldModel javaFieldModel = parseLine(bodyVO);
            javaFieldModels.add(javaFieldModel);
        }
        return  javaFieldModels;

    }

    public static Map<String, List<JavaFieldModel>> parser(List<BodyVO> bodyVOS,String className) throws ClassNotFoundException {
        Map<String, List<JavaFieldModel>> result = new HashMap<>();
        List<JavaFieldModel> list = new ArrayList<>();
        List<JavaFieldModel> listModel;

        boolean isInArray= false;
        String startListName = "";
        ArrayList<BodyVO> innerBodys = new ArrayList<>();
        for(BodyVO bodyVO:bodyVOS){
            if(isInArray && !(bodyVO.getType().toUpperCase().equals("ARRAY"))){
                innerBodys.add(bodyVO);
                continue;
            }

            if(bodyVO.getType().toUpperCase().equals("ARRAY") ){


                if( "".equals(startListName)){
                    isInArray = true;
                    startListName = bodyVO.getVariableName();
                    continue;
                }else{
                    isInArray = false;

                    List<JavaFieldModel> javaFieldModels = parseList(innerBodys, startListName);
                    result.put(startListName,javaFieldModels);
                    startListName = "";
                    continue;
                }



            }


            JavaFieldModel model = parseLine(bodyVO);
            list.add(model);
        }

        result.put(className,list);
        return result;

    }

    public static void main(String[] args) throws ClassNotFoundException {
        BodyVO bodyVO1 = new BodyVO("SubSysNo", "子系统号", "String");
        BodyVO bodyVO2 = new BodyVO("TotRcrdNo", "总记录条数", "String");
        BodyVO bodyVO3 = new BodyVO("FrzInfArray", "冻结信息数组", "ARRAY");
        BodyVO bodyVO4 = new BodyVO("IdentTp", "件类型/种类", "String");
        BodyVO bodyVO5 = new BodyVO("IdentNo", "证件号码", "String");
        BodyVO bodyVO6 = new BodyVO("CstNm", "客户名称", "String");
        BodyVO bodyVO7 = new BodyVO("FrzInfArray", "冻结信息数组", "ARRAY");
        ArrayList<BodyVO> bodyVOArrayList = new ArrayList<BodyVO>();
        bodyVOArrayList.add(bodyVO1);
        bodyVOArrayList.add(bodyVO2);
        bodyVOArrayList.add(bodyVO3);
        bodyVOArrayList.add(bodyVO4);
        bodyVOArrayList.add(bodyVO5);
        bodyVOArrayList.add(bodyVO6);
        bodyVOArrayList.add(bodyVO7);


        Map<String, List<JavaFieldModel>> demo = parser(bodyVOArrayList, "demo");
//        JavaFieldModel javaFieldModel1 = EsbParser.parseLine(bodyVO1);
//        JavaFieldModel javaFieldModel2 = EsbParser.parseLine(bodyVO2);
//        ArrayList<JavaFieldModel> javaFieldModels = new ArrayList<>();
//        javaFieldModels.add(javaFieldModel1);
//        javaFieldModels.add(javaFieldModel2);
//        JavaGenerate javaGenerate = new JavaGenerate();
//        List<FieldSpec> fieldSpecs = javaGenerate.generateField(javaFieldModels);
//        List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
//
//        TypeSpec.Builder builderBase = TypeSpec.classBuilder("demo");
//        // 循环封装属性
//        for (FieldSpec fieldSpec : fieldSpecs) {
//            builderBase.addField(fieldSpec);
//        }
//        // 循环封装方法
//        for (MethodSpec spec : methodSpecs) {
//            builderBase.addMethod(spec);
//        }
//
//        JavaFile javaFile = JavaFile.builder("com.xupt.willscorpio.javatest", builderBase.build())
//                .build();
//
//        System.out.println(javaFile);


    }
}
