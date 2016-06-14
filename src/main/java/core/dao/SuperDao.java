package core.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.listener.JdbcExecSQLListener;
import core.vo.PagerVO;
import core.vo.SuperVO;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 15:17
 */
public interface SuperDao<T extends SuperVO> {

    /**
     * 新增
     *
     * @param t
     * @return
     */
    public int insertVO(T t) throws Exception;

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
     * 比对旧对象与新对象,更新对象数据
     * 比较原来的方法效率更高
     *
     * @param oldVO 旧对象
     * @param newVO 新对象
     * @return
     * @throws Exception
     */
    public Integer updateVO(T oldVO, T newVO) throws Exception;

    /**
     * 更新,where条件后多条件，null也值更新
     *
     * @param t
     * @param keys 依据字段
     * @return
     * @throws Exception
     */
    public Integer updateVO(T t, Object[] keys) throws Exception;

    /**
     * 更新,where条件后多条件,参数控制null值是否更新
     *
     * @param t
     * @param keys         依据字段
     * @param isNullUpdate
     * @return
     * @throws Exception
     */
    public Integer updateVO(T t, Object[] keys, boolean isNullUpdate)
            throws Exception;

    /**
     * 删除
     *
     * @param id
     */
    public void deleteVO(Class<T> clazz, int id) throws Exception;

    /**
     * 删除
     *
     * @param vo
     * @param keys
     * @throws Exception
     */
    public void deleteVO(SuperVO vo, String[] keys) throws Exception;

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
     * 执行sql查询返回list，参数为map类型
     *
     * @param sql
     * @param paramMap 参数名，参数值
     * @param clazz
     * @return
     * @throws Exception
     */
    public List<T> findByList(String sql, HashMap<String, Object> paramMap,
                              Class<T> clazz) throws Exception;

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
     * 分页查找，参数map
     *
     * @param sql
     * @param paramMap 参数名：参数值
     * @return
     * @throws Exception
     */
    public PagerVO findByPageParamMap(String sql,
                                      HashMap<String, Object> paramMap) throws Exception;

    /**
     * 分页查找，参数map
     *
     * @param clazz
     * @param sql
     * @param paramMap
     * @return
     * @throws Exception
     */
    public PagerVO findByPageParamMap(Class<? extends SuperVO> clazz,
                                      String sql, HashMap<String, Object> paramMap) throws Exception;

    /**
     * 查找为map
     *
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public Map<String, Object> findByMap(String sql, Object[] args)
            throws Exception;

    /**
     * 查询记录数
     *
     * @param t
     * @return
     * @throws Exception
     */
    public int queryVOCount(T t) throws Exception;

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryBySQL(String sql, Object... args)
            throws Exception;

    /**
     * 根据sql查询一个值
     *
     * @param sql          sql语句
     * @param requiredType 值类型
     * @param args         参数
     * @return
     * @throws Exception
     */
    public <O> O queryObjectBySQL(String sql, Class<O> requiredType,
                                  Object... args) throws Exception;

    /**
     * 根据序列名称查询下一个序列值
     *
     * @param sequenceName
     * @return
     * @throws Exception
     */
    public int queryNextSequence(String sequenceName) throws Exception;

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
     * 根据sql查询一个值
     *
     * @param sql
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryByParamMap(String sql,
                                                     HashMap<String, Object> paramMap) throws Exception;

    /**
     * 根据sql查询一个值
     *
     * @param sql
     * @param paramMap
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryForMapByParamMap(String sql,
                                                     Map<String, Object> paramMap) throws Exception;

}
