package esbtool.controller;


import esbtool.model.BodyVO;
import esbtool.model.EsbVO;
import esbtool.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CallController {

    @Autowired
    private GenerateService generateService;

    @GetMapping("/index")
    public String showUserList(Model model) {
        model.addAttribute("users", "userRepository.findAll()");
        return "index";
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "esbadd";
    }

    @RequestMapping("/add")
    public String add(EsbVO esbVO,Model model) {
       // System.out.println(esbVO.toString());
//        String[] res = esbVO.toString().split("\n");
//        Arrays.stream(res).forEach(s-> System.out.println("--"+s));

        List<BodyVO> reqBody = esbVO.converToBodyList(esbVO.getReqBody());
        List<BodyVO> resBody = esbVO.converToBodyList(esbVO.getResBody());
        String result = null;
        try {
             result = generateService.parseBody(reqBody);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        model.addAttribute("result",result.trim());

        return "javabean";
    }

    public static void generateFile(EsbVO esbVO){

    }

    public static void main(String[] args) {

    }
}
