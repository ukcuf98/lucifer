package core.service.impl;

import core.dao.SuperDao;
import core.dao.impl.SuperDaoImpl;
import core.exception.StringPrintWriter;
import core.listener.JdbcExecSQLListener;
import core.service.SuperMng;
import core.sysconst.LogConst;
import core.util.LogUtil;
import core.util.ResultInfo;
import core.util.Util;
import core.vo.PagerVO;
import core.vo.SuperVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 16:04
 */
@Service
public class SuperMngImpl<T extends SuperVO> implements SuperMng<T> {
    private SuperDao<T> superDao = null;
    private JdbcTemplate write_jdbcTemplate;
    private JdbcTemplate read_jdbcTemplate;

    public SuperMngImpl(JdbcTemplate write_jdbcTemplate,
                        JdbcTemplate read_jdbcTemplate) {
        super();
        this.write_jdbcTemplate = write_jdbcTemplate;
        this.read_jdbcTemplate = read_jdbcTemplate;
    }

    public SuperDao<T> getSuperDao() {
        if (null == superDao) {
            superDao = new SuperDaoImpl<T>(write_jdbcTemplate,
                    read_jdbcTemplate);
        }
        return superDao;
    }

    public List<T> findByList(T t) throws Exception {
        return this.getSuperDao().findByList(t);
    }

    public List<T> findByList(T t, String orderStr) throws Exception {
        return this.getSuperDao().findByList(t, orderStr);
    }

    public PagerVO findByPage(T t) throws Exception {
        return this.getSuperDao().findByPage(t);
    }

    public PagerVO findByPage(T t, String orderStr) throws Exception {
        return this.getSuperDao().findByPage(t, orderStr);
    }

    @Deprecated
    public List<Map<String, Object>> queryBySQL(String sql, Object... args)
            throws Exception {
        return this.getSuperDao().queryBySQL(sql, args);
    }

    @Deprecated
    public <O> O queryObjectBySQL(String sql, Class<O> requiredType,
                                  Object... args) throws Exception {
        return this.getSuperDao().queryObjectBySQL(sql, requiredType, args);
    }

    public int queryVOCount(T t) throws Exception {
        return this.getSuperDao().queryVOCount(t);
    }

    public int insertVO(T t) throws Exception {
        return this.getSuperDao().insertVO(t);
    }

    /**
     * 更新(根据主键更新) 建议使用update(SuperVO oldVO,SuperVO newVO)方法
     *
     * @param t
     * @return
     * @throws Exception
     */
    @Deprecated
    public Integer updateVO(T t) throws Exception {
        return this.getSuperDao().updateVO(t);
    }

    public Integer updateVO(T oldVO, T newVO) throws Exception {
        return this.getSuperDao().updateVO(oldVO, newVO);
    }

    public Integer updateVO(T t, Object[] keys) throws Exception {
        return this.getSuperDao().updateVO(t, keys);
    }

    public Integer updateVO(T t, Object[] keys, boolean isNullUpdate)
            throws Exception {
        return this.getSuperDao().updateVO(t, keys, isNullUpdate);
    }

    public void deleteVO(Class<T> clazz, int id) throws Exception {
        this.getSuperDao().deleteVO(clazz, id);
    }

    public void deleteVO(SuperVO vo, String[] keys) throws Exception {
        this.getSuperDao().deleteVO(vo, keys);
    }

    @SuppressWarnings("unchecked")
    public ResultInfo deleteVOs(T t, String ids) throws Exception {
        ResultInfo info = new ResultInfo(1, "删除成功");
        if (null != ids && ids.trim().length() > 0) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                if (id.trim().length() > 0) {
                    this.getSuperDao().deleteVO((Class<T>) t.getClass(),
                            new Integer(id));
                }
            }
        }
        return info;

    }

    public T getVO(Class<T> clazz, int id) throws Exception {
        return this.getSuperDao().getVO(clazz, id);
    }

    public T getVO(T t) throws Exception {
        return this.getSuperDao().getVO(t);
    }

    public T getVO(T t, String orderby) throws Exception {
        return this.getSuperDao().getVO(t, orderby);
    }

    /**
     * 添加系统日志
     *
     * @param logLevel   日志级别
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键
     * @param mainid     日志所属对象id
     * @throws Exception
     */
    protected void log(String logLevel, String logSource, String operType,
                       String logContent, Integer operater, String operateIp,
                       Integer pkId, Integer mainid) throws Exception {
//        StackTraceElement st[] = Thread.currentThread().getStackTrace();
//        String className = null;
//        String methodName = null;
//        Integer lineNumber = 0;
//        if (null != st && st.length > 0) {
//            for (int i = 2; i < st.length; i++) {
//                if (st[i].getClassName().startsWith("work.")) {
//                    className = st[i].getClassName();
//                    methodName = st[i].getMethodName();
//                    lineNumber = st[i].getLineNumber();
//                    break;
//                }
//            }
//        }
//
//        List<Object> lsArgs_ = new ArrayList<Object>();
//        lsArgs_.add(logLevel);
//        lsArgs_.add(logSource);
//        lsArgs_.add(operType);
//        lsArgs_.add(logContent);
//        lsArgs_.add(Util.getIntegerValue(operater, -1));
//        lsArgs_.add(Util.null2String(operateIp));
//        lsArgs_.add(Util.getIntegerValue(pkId, -1));
//        lsArgs_.add(Util.getIntegerValue(mainid, -1));
//        lsArgs_.add(className);
//        lsArgs_.add(methodName);
//        lsArgs_.add(lineNumber);
//
//        List<List<Object>> lsArgs = new ArrayList<List<Object>>();
//        lsArgs.add(lsArgs_);
//        JmsSuperVO jmsSuperVO = new JmsSuperVO();
//        jmsSuperVO.setType("LOG");
//        jmsSuperVO.setMethod("insertSysLog");
//        jmsSuperVO.setPackclass("SysLogMngImpl");
//        jmsSuperVO.setLevel(1);
//        jmsSuperVO.setContent(lsArgs);
//        JmsPublisher.sendQueue(jmsSuperVO);
    }

    public void info_log(String logSource, String operType,
                         String logContent, Integer operater, String operateIp, Integer pkId)
            throws Exception {
        this.info_log(logSource, operType, logContent, operater, operateIp,
                pkId, null);
    }

    /**
     * 添加info级别的日志
     *
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键id
     * @param mainid     日志所属对象id
     * @throws Exception
     */
    protected void info_log(String logSource, String operType,
                            String logContent, Integer operater, String operateIp,
                            Integer pkId, Integer mainid) throws Exception {
        this.log(LogConst.LOGLEVEL_INFO, logSource, operType, logContent,
                operater, operateIp, pkId, mainid);
    }

    /**
     * 添加info级别的日志
     *
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键id
     * @throws Exception
     */
    protected void error_log(String logSource, String operType,
                             String logContent, Integer operater, String operateIp, Integer pkId)
            throws Exception {
        this.error_log(logSource, operType, logContent, operater, operateIp,
                pkId, null);
    }

    /**
     * 添加info级别的日志
     *
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键id
     * @param mainid     日志所属对象id
     * @throws Exception
     */
    protected void error_log(String logSource, String operType,
                             String logContent, Integer operater, String operateIp,
                             Integer pkId, Integer mainid) throws Exception {
        this.log(LogConst.LOGLEVEL_ERROR, logSource, operType, logContent,
                operater, operateIp, pkId, mainid);
        LogUtil.error(this.getClass(), "主键:" + pkId + " 内容：" + logContent);
    }

    /**
     * 添加info级别的日志
     *
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键id
     * @throws Exception
     */
    protected void debug_log(String logSource, String operType,
                             String logContent, Integer operater, String operateIp, Integer pkId)
            throws Exception {
        this.debug_log(logSource, operType, logContent, operater, operateIp,
                pkId, null);
    }

    /**
     * 添加info级别的日志
     *
     * @param logSource  日志来源
     * @param operType   操作类型
     * @param logContent 日志内容
     * @param operater   操作人
     * @param operateIp  操作ip
     * @param pkId       被操作的主键id
     * @param mainid     日志所属对象id
     * @throws Exception
     */
    protected void debug_log(String logSource, String operType,
                             String logContent, Integer operater, String operateIp,
                             Integer pkId, Integer mainid) throws Exception {
        this.log(LogConst.LOGLEVEL_DEBUG, logSource, operType, logContent,
                operater, operateIp, pkId, mainid);
        LogUtil.debug(this.getClass(), "主键:" + pkId + " 内容：" + logContent);
    }

    public void startSQLListener(JdbcExecSQLListener jdbcExecSQLListener) {
        getSuperDao().startSQLListener(jdbcExecSQLListener);
    }

    public void endSQLListener() {
        getSuperDao().endSQLListener();
    }

    public int insertVOByLog(T t, Integer operator, String operateIp)
            throws Exception {
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        int id = -1;
        try {
            this.startSQLListener(jdbcExecSQLListener);
            id = this.insertVO(t);
            this.endSQLListener();
            this.info_log(t.getTableName(), LogConst.OPERTYPE_ADD,
                    "新增表【" + t.getTableName() + "】数据成功," + t.getLogContent(),
                    operator, operateIp, id);
        } catch (Exception e) {
            this.error_log(t.getTableName(), LogConst.OPERTYPE_ADD,
                    "新增表【" + t.getTableName() + "】数据失败," + t.getLogContent()
                            + " 异常：" + StringPrintWriter.getExceptionString(e),
                    operator, operateIp, id);

            throw new Exception("新增失败"
                    + StringPrintWriter.getExceptionString(e));
        }

        return id;
    }

    public ResultInfo updateVOByLog(T oldVO, T newVO, Integer operator,
                                    String operateIp, String userDefinedLog) throws Exception {
        ResultInfo ri = new ResultInfo(1, "修改成功");
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        if (!Util.isBlank(userDefinedLog)) {
            userDefinedLog += oldVO.compareForLogContent(newVO);
        } else {
            userDefinedLog = oldVO.compareForLogContent(newVO);
        }
        try {
            this.startSQLListener(jdbcExecSQLListener);
            this.updateVO(oldVO, newVO);
            this.endSQLListener();

            this.info_log(oldVO.getTableName(), LogConst.OPERTYPE_MODIFY,
                    "比较两个新旧vo更新表【" + oldVO.getTableName() + "】数据成功,"
                            + userDefinedLog, operator, operateIp, Util
                            .getIntegerValue(oldVO.getAttributeValue(oldVO
                                    .getPKFieldName())));
        } catch (Exception e) {
            this.error_log(
                    oldVO.getTableName(),
                    LogConst.OPERTYPE_MODIFY,
                    "比较两个新旧vo更新表【" + oldVO.getTableName() + "】数据失败,"
                            + userDefinedLog + " 异常："
                            + StringPrintWriter.getExceptionString(e),
                    operator, operateIp, Util.getIntegerValue(oldVO
                            .getAttributeValue(oldVO.getPKFieldName())));
            ri.setCode(-1);
            ri.setMessage("修改失败");
        }
        return ri;
    }

    public ResultInfo updateVOByLog(T oldVO, T newVO, Integer operator,
                                    String operateIp) throws Exception {
        return this.updateVOByLog(oldVO, newVO, operator, operateIp, null);
    }

    public ResultInfo updateVOByLog(T t, Object[] keys, Integer operator,
                                    String operateIp) throws Exception {
        return this.updateVOByLog(t, keys, true, operator, operateIp);
    }

    public ResultInfo updateVOByLog(T t, Object[] keys, boolean isNullUpdate,
                                    Integer operator, String operateIp) throws Exception {
        ResultInfo ri = new ResultInfo(1, "修改成功");
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        try {
            this.startSQLListener(jdbcExecSQLListener);
            this.updateVO(t, keys, isNullUpdate);
            this.endSQLListener();
            this.info_log(t.getTableName(), LogConst.OPERTYPE_MODIFY, "根据多字段【"
                            + keys.toString() + "】更新表【" + t.getTableName()
                            + "】数据成功,sql:" + jdbcExecSQLListener.getExecSql(),
                    operator, operateIp, null);
        } catch (Exception e) {
            this.error_log(t.getTableName(), LogConst.OPERTYPE_MODIFY, "根据多字段【"
                            + keys.toString() + "】更新表【" + t.getTableName()
                            + "】数据失败,sql:" + jdbcExecSQLListener.getExecSql() + " 异常："
                            + StringPrintWriter.getExceptionString(e), operator,
                    operateIp, null);
            ri.setCode(-1);
            ri.setMessage("修改失败");
        }
        return ri;
    }

    public ResultInfo deleteVOByLog(Class<T> clazz, int id, Integer operator,
                                    String operateIp) throws Exception {
        ResultInfo ri = new ResultInfo(1, "删除成功");
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        T t = clazz.newInstance();
        try {
            this.startSQLListener(jdbcExecSQLListener);
            String logString = this.getVO(clazz, id).getLogContent();
            this.deleteVO(clazz, id);
            this.endSQLListener();
            this.info_log(t.getTableName(), LogConst.OPERTYPE_DELETE, "根据主键【"
                            + id + "】删除表【" + t.getTableName() + "】数据成功," + logString,
                    operator, operateIp, id);
        } catch (Exception e) {
            this.error_log(t.getTableName(), LogConst.OPERTYPE_DELETE, "根据主键【"
                            + id + "】删除表【" + t.getTableName() + "】数据失败,sql:"
                            + jdbcExecSQLListener.getExecSql() + " 异常："
                            + StringPrintWriter.getExceptionString(e), operator,
                    operateIp, id);
            ri.setCode(-1);
            ri.setMessage("删除失败");
        }
        return ri;
    }

    public ResultInfo deleteVOByLog(SuperVO vo, String[] keys,
                                    Integer operator, String operateIp) throws Exception {
        ResultInfo ri = new ResultInfo(1, "删除成功");
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        try {

            this.startSQLListener(jdbcExecSQLListener);
            // 记录操作日志
            this.deleteVO(vo, keys);
            this.endSQLListener();

            this.info_log(vo.getTableName(), LogConst.OPERTYPE_DELETE, "根据数组【"
                            + keys.toString() + "】删除表【" + vo.getTableName()
                            + "】数据成功,sql:" + jdbcExecSQLListener.getExecSql(),
                    operator, operateIp, Util.getIntegerValue((vo
                            .getAttributeValue(vo.getPKFieldName()))));
        } catch (Exception e) {
            this.error_log(vo.getTableName(), LogConst.OPERTYPE_DELETE, "根据数组【"
                            + keys.toString() + "】删除表【" + vo.getTableName()
                            + "】数据失败,sql:" + jdbcExecSQLListener.getExecSql() + " 异常："
                            + StringPrintWriter.getExceptionString(e), operator,
                    operateIp, Util.getIntegerValue((vo.getAttributeValue(vo
                            .getPKFieldName()))));
            ri.setCode(-1);
            ri.setMessage("删除失败");
        }
        return ri;
    }

    public ResultInfo deleteVOsByLog(T t, String ids, Integer operator,
                                     String operateIp) throws Exception {
        ResultInfo ri = new ResultInfo(1, "删除成功");
        JdbcExecSQLListener jdbcExecSQLListener = new JdbcExecSQLListener();
        try {
            this.startSQLListener(jdbcExecSQLListener);
            ri = this.deleteVOs(t, ids);
            this.endSQLListener();
            this.info_log(t.getTableName(), LogConst.OPERTYPE_DELETE, "根据多主键【"
                            + ids + "】删除表【" + t.getTableName() + "】数据成功,sql:"
                            + jdbcExecSQLListener.getExecSql(), operator, operateIp,
                    null);
        } catch (Exception e) {
            this.error_log(t.getTableName(), LogConst.OPERTYPE_DELETE, "根据多主键【"
                            + ids + "】删除表【" + t.getTableName() + "】数据失败,sql:"
                            + jdbcExecSQLListener.getExecSql() + " 异常："
                            + StringPrintWriter.getExceptionString(e), operator,
                    operateIp, null);
            ri.setCode(-1);
            ri.setMessage("删除失败");
        }
        return ri;
    }

    public int queryNextSeq(String sequenceName) throws Exception {
        return this.getSuperDao().queryNextSequence(sequenceName);
    }

    public <E> List<E> toIClasseList(List<T> list, Class<E> clazz) throws Exception {
        List<E> retList = new ArrayList<E>();

        for (SuperVO superVO : list) {
            @SuppressWarnings("unchecked")
            E ibean = (E) superVO.toIClass(clazz);
            retList.add(ibean);
        }
        return retList;
    }

}
