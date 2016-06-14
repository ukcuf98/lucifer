package plat.controller.pc.index;

import core.action.SuperController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/10 10:48
 */
@Controller
@RequestMapping("/index")
public class IndexController extends SuperController {

    @RequestMapping(value = "view")
    public String toIndex(ModelMap map, HttpServletRequest request) throws Exception {
        return "/index/index";
    }
}
