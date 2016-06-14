package plat.controller.pc.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zwq on 2016/3/8.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "gopage")
    public String goTestPage(ModelMap map) {
        System.out.println("测试成功");
        map.put("name", "名称");
        return "/test/test";
    }


}
