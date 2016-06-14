package core.service;

import java.util.List;
import java.util.Map;

import core.listener.JdbcExecSQLListener;
import core.util.ResultInfo;
import core.vo.PagerVO;
import core.vo.SuperVO;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 16:03
 */
public interface SuperMng<T extends SuperVO> {

    /**
     * 查找列表
     *
     * @param t
     * @return
     * @throws Exception
     */
    public List<T> findByList(T t) throws Exception;

    /**
     * 查找列表
     *
     * @param t
     * @param orderStr
     * @return
     * @throws Exception
     */
    public List<T> findByList(T t, String orderStr) throws Exception;

    /**
     * 分页查找
     *
     * @param t
     * @return
     */
    public PagerVO findByPage(T t) throws Exception;

    /**
     * 分页查找
     *
     * @param t
     * @return
     */
    public PagerVO findByPage(T t, String orderStr) throws Exception;

    /**
     * 根据sql查询数据 不建议使用，如果需要执行sql,请在dao层调用
     *
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    @Deprecated
    public List<Map<String, Object>> queryBySQL(String sql, Object... args)
            throws Exception;

    /**
     * 根据sql查询一个值 不建议使用，如果需要执行sql,请在dao层调用
     *
     * @param sql          sql语句
     * @param requiredType 值类型
     * @param args         参数
     * @return
     * @throws Exception
     */
    @Deprecated
    public <O> O queryObjectBySQL(String sql, Class<O> requiredType,
                                  Object... args) throws Exception;

    /**
     * 查询记录数
     *
     * @param t
     * @return
     * @throws Exception
     */
    public int queryVOCount(T t) throws Exception;

    /**
     * 新增
     *
     * @param t
     * @return
     */
    public int insertVO(T t) throws Exception;

    /**
     * 新增-自动记录操作日志
     *
     * @param t
     * @param operator
     * @param operateIp
     * @return
     * @throws Exception
     */
    public int insertVOByLog(T t, Integer operator, String operateIp)
            throws Exception;

    /**
     * 更新(根据主键更新) 建议使用update(SuperVO oldVO,SuperVO newVO)方法
     *
     * @param t
     * @return
     * @throws Exception
     */
    @Deprecated
    public Integer updateVO(T t) throws Exception;

    /**
     * 比对旧对象与新对象,更新对象数据 比较原来的方法效率更高
     *
     * @param oldVO 旧对象
     * @param newVO 新对象
     * @return
     * @throws Exception
     */
    public Integer updateVO(T oldVO, T newVO) throws Exception;

    /**
     * 比对旧对象与新对象,更新对象数据 比较原来的方法效率更高-自动记录操作日志
     *
     * @param oldVO
     * @param newVO
     * @param operator
     * @param operateIp
     * @return
     * @throws Exception
     */
    public ResultInfo updateVOByLog(T oldVO, T newVO, Integer operator,
                                    String operateIp) throws Exception;

    /**
     * 更新,where条件后多条件
     *
     * @param t
     * @param keys
     * @return
     * @throws Exception
     */
    public Integer updateVO(T t, Object[] keys) throws Exception;

    /**
     * 更新,where条件后多条件-自动记录操作日志
     *
     * @param t
     * @param keys
     * @param operator
     * @param operateIp
     * @return
     * @throws Exception
     */
    public ResultInfo updateVOByLog(T t, Object[] keys, Integer operator,
                                    String operateIp) throws Exception;

    /**
     * 更新,where条件后多条件,参数控制null值是否更新
     *
     * @param t
     * @param keys
     * @param isNullUpdate true（默认）:null更新 false:null不更新
     * @return
     * @throws Exception
     */
    public Integer updateVO(T t, Object[] keys, boolean isNullUpdate)
            throws Exception;

    /**
     * 更新,where条件后多条件,参数控制null值是否更新-字段记录操作日志
     *
     * @param t
     * @param keys
     * @param isNullUpdate true（默认）:null更新 false:null不更新
     * @param operator
     * @param operateIp
     * @return
     * @throws Exception
     */
    public ResultInfo updateVOByLog(T t, Object[] keys, boolean isNullUpdate,
                                    Integer operator, String operateIp) throws Exception;

    /**
     * 删除
     *
     * @param id
     */
    public void deleteVO(Class<T> clazz, int id) throws Exception;

    /**
     * 删除-自动记录操作日志
     *
     * @param clazz
     * @param id
     * @param operator
     * @param operateIp
     * @throws Exception
     */
    public ResultInfo deleteVOByLog(Class<T> clazz, int id, Integer operator,
                                    String operateIp) throws Exception;

    /**
     * 删除
     *
     * @param vo
     * @param keys
     * @throws Exception
     */
    public void deleteVO(SuperVO vo, String[] keys) throws Exception;

    /**
     * 删除-自动记录操作日志
     *
     * @param vo
     * @param keys
     * @param operator
     * @param operateIp
     * @throws Exception
     */
    public ResultInfo deleteVOByLog(SuperVO vo, String[] keys,
                                    Integer operator, String operateIp) throws Exception;

    /**
     * 批量删除vo
     *
     * @param t
     * @param ids
     * @return
     * @throws Exception
     */
    public ResultInfo deleteVOs(T t, String ids) throws Exception;

    /**
     * 批量删除vo-字段记录操作日志
     *
     * @param t
     * @param ids
     * @param operator
     * @param operateIp
     * @return
     * @throws Exception
     */
    public ResultInfo deleteVOsByLog(T t, String ids, Integer operator,
                                     String operateIp) throws Exception;

    /**
     * 根据主键获取对象
     *
     * @param id
     * @return
     */
    public T getVO(Class<T> clazz, int id) throws Exception;

    /**
     * 根据查询对象查询
     *
     * @param t
     * @return
     * @throws Exception
     */
    public T getVO(T t) throws Exception;

    /**
     * 根据查询对象、排序sql查询
     *
     * @param t
     * @param orderby
     * @return
     * @throws Exception
     */
    public T getVO(T t, String orderby) throws Exception;

    /**
     * 开始执行sql监听
     *
     * @param jdbcExecSQLListener
     */
    public void startSQLListener(JdbcExecSQLListener jdbcExecSQLListener);

    /**
     * 结束sql监听
     */
    public void endSQLListener();

    /**
     * 更新记录日志 可自己添加除该类字段变化的 日志内容
     *
     * @param oldVO
     * @param newVO
     * @param operator
     * @param operateIp
     * @param userDefinedLog
     * @return
     * @throws Exception
     * @decription
     * @author shangwenyu
     * @time 2015-1-14 下午3:08:52
     */
    public ResultInfo updateVOByLog(T oldVO, T newVO, Integer operator,
                                    String operateIp, String userDefinedLog) throws Exception;

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
    public void info_log(String logSource, String operType, String logContent,
                         Integer operater, String operateIp, Integer pkId) throws Exception;

    /**
     * 查询序列值
     *
     * @param sequenceName 序列名称
     * @return
     * @throws Exception
     */
    public int queryNextSeq(String sequenceName) throws Exception;

    /**
     * 转化为自定义实体list
     *
     * @param list  需转化list
     * @param clazz 自定义实体类型
     * @return
     * @throws Exception
     */
    public <E> List<E> toIClasseList(List<T> list, Class<E> clazz)
            throws Exception;
}
