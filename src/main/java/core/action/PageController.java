package core.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import core.page.SystemContext;
import core.util.JsonUtil;
import core.vo.PagerVO;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:26
 */
public class PageController {
    /**
     * 为动态表格放入数据，本方法已经弃用 建议使用：ajax获取数据方法
     *
     * @param model model对象
     * @param pv    分页对象
     * @throws Exception
     */
    @Deprecated
    protected void addTable(ModelMap model, PagerVO pv) throws Exception {
        String json = beanToJson(pv);
        model.addAttribute("pv_json", json);
    }

    /**
     * 将bean转换为json对象 要转换的bean如果为对象，对象中的属性必须有public的get方法
     *
     * @param obj 要转换的对象
     * @return
     * @throws Exception
     */
    protected String beanToJson(Object obj, String dateFormatStr)
            throws Exception {
        return JsonUtil.beanToJson(obj, dateFormatStr);

    }

    /**
     * 将bean转换为json对象 要转换的bean如果为对象，对象中的属性必须有public的get方法
     *
     * @param obj 要转换的对象
     * @return
     * @throws Exception
     */
    protected String beanToJson(Object obj) throws Exception {
        return JsonUtil.beanToJson(obj, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 初始化分页工具,本方法已经弃用，建议使用initPageCom方法代替
     *
     * @param request request对象
     * @throws IOException
     * @throws ServletException
     */
    @Deprecated
    public void initPage(HttpServletRequest request) throws IOException,
            ServletException {
        this.initPage(request, null, null);
    }

    /**
     * 初始化分页工具,本方法已经弃用，建议使用initPageCom方法代替
     *
     * @param request request对象
     * @param model   model对象
     * @throws IOException
     * @throws ServletException
     */
    @Deprecated
    public void initPage(HttpServletRequest request, ModelMap model)
            throws IOException, ServletException {
        this.initPage(request, null, model);
    }

    /**
     * 初始化分页工具,本方法已经弃用，建议使用initPageCom方法代替
     *
     * @param request  request对象
     * @param pageSize 每页条数
     * @throws IOException
     * @throws ServletException
     */
    @Deprecated
    public void initPage(HttpServletRequest request, Integer pageSize)
            throws IOException, ServletException {
        this.initPage(request, pageSize, null);
    }

    /**
     * 初始化分页工具,本方法已经弃用，建议使用initPageCom方法代替
     *
     * @param request  request对象
     * @param pageSize 每页条数
     * @param model    model对象
     * @throws IOException
     * @throws ServletException
     */
    @Deprecated
    public void initPage(HttpServletRequest request, Integer pageSize,
                         ModelMap model) throws IOException, ServletException {
        Map<String, String[]> param_map = (Map<String, String[]>) request
                .getParameterMap();
        Integer pager_offset_value = 1;
        StringBuffer url = new StringBuffer(request.getRequestURI())
                .append("?");
        StringBuffer export_params = new StringBuffer();
        if (null != param_map) {
            for (String key : param_map.keySet()) {
                if ("pager_offset".equals(key)) {
                    String po = request.getParameter("pager_offset");
                    if (null != po) {
                        try {
                            pager_offset_value = Integer.parseInt(po);
                        } catch (Exception e) {
                            pager_offset_value = 1;
                        }
                    }
                } else {
                    String value = request.getParameter(key);
                    if (null != value && value.trim().length() > 0) {
                        url.append(key).append("=").append(value).append("&");
                        if (null != model) {
                            // 用于查询条件回显
                            model.addAttribute(key, value);
                        }
                        if (!"method".equals(key)) {
                            export_params.append(key).append("=").append(value)
                                    .append("&");
                        }
                    }
                }
            }
        }
        url.append("pager_offset=");
        request.setAttribute("pagination_url", url);
        request.setAttribute("export_params", export_params);
        request.setAttribute("pagination_current_pageindex", pager_offset_value);

        SystemContext.setOffset(pager_offset_value);
        if (null != pageSize) {
            SystemContext.setPagesize(pageSize);
        } else {
            SystemContext.setPagesize(10);
        }
    }

    /**
     * 初始化分页控件
     *
     * @param request
     */
    public String initPageCom(HttpServletRequest request) throws Exception {
        return initPageCom(request, 10);
    }

    /**
     * 初始化分页控件
     *
     * @param request
     * @param pageSize
     */
    public String initPageCom(HttpServletRequest request, Integer pageSize)
            throws Exception {
        // 预存查询条件
        Map<String, String[]> param_map = (Map<String, String[]>) request
                .getParameterMap();
        StringBuffer querySb = new StringBuffer("");
        if (null != param_map) {
            for (String key : param_map.keySet()) {
                String value = request.getParameter(key);
                if (null != value && value.trim().length() > 0) {
                    if (!"method".equals(key)) {
                        querySb.append(key).append("=").append(value)
                                .append("&");
                    }
                }
            }
        }
        String queryStr = querySb.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.length() - 1);
        }

        String po = request.getParameter("pager_offset");
        Integer pager_offset_value = 1;
        if (null != po) {
            try {
                pager_offset_value = Integer.parseInt(po);
            } catch (Exception e) {
                pager_offset_value = 1;
            }
        }
        SystemContext.setOffset(pager_offset_value);
        if (null != pageSize) {
            SystemContext.setPagesize(pageSize);
        } else {
            SystemContext.setPagesize(10);
        }
        return queryStr;
    }

    /**
     * 下载excel文件
     *
     * @param res
     * @param workbook
     * @param filename
     * @throws Exception
     */
    protected void downExcel(HttpServletRequest request,
                             HttpServletResponse res, HSSFWorkbook workbook, String filename)
            throws Exception {
        filename = URLEncoder.encode(filename, "utf-8");
        filename = StringUtils.replace(filename, "+", "%20");
        ServletOutputStream os = null;
        try {
            res.reset();
            res.setContentType("application/msexcel");
            res.setHeader("Content-Disposition", "attachment;Filename="
                    + filename);
            os = res.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != os) {
                os.close();
            }
        }
    }

    /**
     * json转换list 例：this.json2List(josn, Map.class); this.json2List(josn,
     * EWfNodeBase.class);
     *
     * @param json         json字符串
     * @param elementClass 转化的类型
     * @return
     * @author zhaolimin
     * @date 2014-6-10 下午3:07:50
     */
    public List<?> jsonToList(String json, Class<?> elementClass)
            throws Exception {
        return JsonUtil.jsonToList(json, elementClass);
    }

    /**
     * 修改前后台交互，数组绑定，list长度超过256时就会报错，现修改为1024个
     *
     * @param request
     * @param binder
     * @throws Exception
     */
    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {
        binder.setAutoGrowCollectionLimit(1024);// 数字越界问题
    }
}
