package esbtool.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data

public class EsbVO {

    String reqBody;

    String resBody;

    public  BodyVO converToBody(String body){
//        List<BodyVO> bodyVOS = new ArrayList<>();
        String[] s = body.split("\t");

        String type = s[2];
        if(!(type.trim().equals("ARRAY"))){
            try{
                type =type.substring(0,type.indexOf("("));
            }catch (Exception e ){
                System.out.println(type);
            }
        }
//        bodyVOS.add();

        return new BodyVO(s[0].trim(),s[1].trim(),type.trim());
    }

    public  List<BodyVO> converToBodyList(String reqBody){
        List<BodyVO> bodyVOS = new ArrayList<>();
        String[] split = reqBody.split("\n");
        Arrays.stream(split).forEach(s-> bodyVOS.add(converToBody(s)));

        return bodyVOS;
    }

    @Override
    public String toString() {
        return  reqBody +"\n" + resBody;
    }

    public static void main(String[] args) {
        String s1 = "FrzInfArray\t冻结信息数组\tARRAY\n" +
                "TotRcrdNo\t总记录条数\tint(8)\n" +
                "FrzInfArray\t冻结信息数组\tARRAY\n" +
                "IdentTp\t证件类型/种类\tString(3)\n" +
                "IdentNo\t证件号码\tString(32)\n" +
                "CstNm\t客户名称\tString(64)\n" +
                "CstAcctNo\t客户账号\tString(32)\n" +
                "PrdNo\t产品编号\tString(50)\n" +
                "FrzBegDt\t冻结开始日期\tString(8)";
//        List<BodyVO> bodyVOS =  EsbVO.converToBodyList(s1);
//        bodyVOS.stream().forEach(e-> System.out.println(e));
    }
}
