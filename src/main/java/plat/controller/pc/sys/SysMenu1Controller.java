package plat.controller.pc.sys;

import core.action.SuperController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import plat.entity.sys.ESysMenu1;
import plat.service.SysMenu1Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 16:30
 */
@Controller
@RequestMapping(value = "/sys/menu1")
public class SysMenu1Controller extends SuperController {
    @Autowired
    private SysMenu1Service sysMenu1Service;

    @RequestMapping(value = "toList")
    public String toList(ModelMap map, HttpServletRequest request) throws Exception {
        ESysMenu1 vo = new ESysMenu1();
        vo.setName("测试2");
        vo.setCreator(1);
        vo.setCreatetime(new Date());
        vo.setUpdater(1);
        vo.setUpdatetime(new Date());
        int newid = sysMenu1Service.insertVO(vo);
        map.put("newid", newid);
        ESysMenu1 con_vo = new ESysMenu1();
        con_vo.setDelflag(0);
        List<ESysMenu1> list = sysMenu1Service.findByList(con_vo);
        map.put("list", list);
        return "/sys/sysMenu1";
    }
}
