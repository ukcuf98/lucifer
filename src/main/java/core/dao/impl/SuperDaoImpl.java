package core.dao.impl;

import core.dao.SuperDao;
import core.listener.JdbcExecSQLListener;
import core.vo.PagerVO;
import core.vo.SuperVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("superDao")
public class SuperDaoImpl<T extends SuperVO> extends JdbcTemplatePageDaoSupport
        implements SuperDao<T> {
    public SuperDaoImpl(JdbcTemplate write_jdbcTemplate,
                        JdbcTemplate read_jdbcTemplate) {
        super(write_jdbcTemplate, read_jdbcTemplate);
    }

    public void deleteVO(Class<T> clazz, int id) throws Exception {
        super.delete(clazz, id);

    }

    public void deleteVO(SuperVO vo, String[] keys) throws Exception {
        super.delete(vo, keys);
    }

    public PagerVO findByPage(T t) throws Exception {
        return this.findByPage(t, null);
    }

    ;

    public PagerVO findByPage(T t, String orderStr) throws Exception {
        return super.queryByPage(t.getClass(), t, orderStr);

    }

    ;

    public PagerVO findByPageParamMap(String sql,
                                      HashMap<String, Object> paramMap) throws Exception {
        return super.queryByPageParamMap(sql, paramMap);
    }

    public PagerVO findByPageParamMap(Class<? extends SuperVO> clazz,
                                      String sql, HashMap<String, Object> paramMap) throws Exception {
        return super.queryByPageParamMap(clazz, sql, paramMap);
    }

    public List<T> findByList(T t) throws Exception {
        return this.findByList(t, null);
    }

    ;

    @SuppressWarnings("unchecked")
    public List<T> findByList(T t, String orderStr) throws Exception {
        return (List<T>) super.query(t.getClass(), t, orderStr);
    }

    ;

    public List<T> findByList(String sql, HashMap<String, Object> paramMap,
                              Class<T> clazz) throws Exception {
        return (List<T>) super.queryForListByParamMap(sql, paramMap, clazz);
    }

    public Map<String, Object> findByMap(String sql, Object[] args)
            throws Exception {
        return super.queryForMap(sql, args);
    }

    public T getVO(Class<T> clazz, int id) throws Exception {
        return (T) super.get(clazz, id);
    }

    public T getVO(T t) throws Exception {
        return (T) super.get(t);
    }

    public T getVO(T t, String orderby) throws Exception {
        return (T) super.get(t, orderby);
    }

    public int insertVO(T t) throws Exception {
        return super.insert(t);
    }

    ;

    /**
     * 更新(根据主键更新) 建议使用update(SuperVO oldVO,SuperVO newVO)方法
     *
     * @param t
     * @return
     * @throws Exception
     */
    @Deprecated
    public Integer updateVO(T t) throws Exception {
        return super.update(t);
    }

    ;

    public Integer updateVO(T oldVO, T newVO) throws Exception {
        return super.update(oldVO, newVO);
    }

    public Integer updateVO(T t, Object[] keys) throws Exception {
        return super.update(t, keys, true);
    }

    ;

    public Integer updateVO(T t, Object[] keys, boolean isNullUpdate)
            throws Exception {
        return super.update(t, keys, isNullUpdate);
    }

    ;

    public int queryVOCount(T t) throws Exception {
        return super.queryCount(t);
    }

    ;

    public List<Map<String, Object>> queryBySQL(String sql, Object... args)
            throws Exception {
        return super.query(sql, args);
    }

    @Override
    public <O> O queryObjectBySQL(String sql, Class<O> requiredType,
                                  Object... args) throws Exception {
        return super.queryObjectBySQL(sql, requiredType, args);
    }

    public int queryNextSequence(String sequenceName) throws Exception {
        return super.queryNextSeq(sequenceName);
    }

    public void startSQLListener(JdbcExecSQLListener jdbcExecSQLListener) {
        super.setSqlListenerThread(jdbcExecSQLListener);
    }

    public void endSQLListener() {
        super.removeSqlListenerThread();
    }

    @Override
    public List<Map<String, Object>> queryByParamMap(String sql,
                                                     HashMap<String, Object> paramMap) throws Exception {
        return super.queryByParamMap(sql, paramMap);
    }

    @Override
    public Map<String, Object> queryForMapByParamMap(String sql,
                                                     Map<String, Object> paramMap) throws Exception {
        return super.queryForMapByParamMap(sql, paramMap);
    }
}
