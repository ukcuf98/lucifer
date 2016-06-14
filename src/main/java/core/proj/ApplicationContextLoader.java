package core.proj;

import core.sysconst.Constant;
import core.util.BeanFactory;
import core.util.LogUtil;
import core.util.PropUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 10:06
 */
public class ApplicationContextLoader extends HttpServlet {
    private static final long serialVersionUID = 8937450357131799601L;

    public void init(ServletConfig servletConfig) throws ServletException {
        try {
            ServletContext localServletContext = servletConfig
                    .getServletContext();

            BeanFactory.init(localServletContext);

            String rootPath = localServletContext.getRealPath("/");
            if (!rootPath.endsWith(String.valueOf(File.separatorChar)))
                rootPath += File.separatorChar;
            PropUtil.setROOT_PATH(rootPath);
            // 工程自己的启动类
            projectInit(localServletContext);

            // 启动定时任务
//            TaskManager.doTask(localServletContext
//                    .getRealPath("/WEB-INF/conf/schedule.xml"));
        } catch (Exception e) {
            LogUtil.error(this.getClass(), e);
        }

    }

    protected void projectInit(ServletContext localServletContext)
            throws ServletException {
        try {
            Constant.PROPATH = localServletContext.getContextPath();
            Constant.reloadConfigAll();
        } catch (Exception e) {
            LogUtil.error(this.getClass(), e);
        }
    }

    public void destroy() {
        super.destroy();
    }
}
