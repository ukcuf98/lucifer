package core.vo;

import core.annotation.UserDefined;
import core.sysconst.DataSourceConst;
import core.util.BeanHelper;
import core.util.LogUtil;
import core.util.ObjectUtils;
import core.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:32
 */
@JsonIgnoreProperties(value = {"START_OBJECT"})
public abstract class SuperVO implements Serializable {
    /**
     * 序列值
     */
    private static final long serialVersionUID = -213450775245826021L;

    /**
     * 用户自定义注解
     */
    private final Class<? extends Annotation> USERDEFINED_ANNOTATION = UserDefined.class;

    /**
     * 排除的属性
     */
    private final String SERIAL_VERSION_UID = "serialVersionUID";
    /**
     * 表别名
     */
    private String BYNAME = "jtds";

    public SuperVO() {
        super();
    }

    /**
     * 从map初始化实例
     *
     * @param map
     */
    public SuperVO(Map<String, Object> map) {
        if (null != map && map.size() > 0) {
            List<String> list = this.getAllAttributeNames();
            if (null != list && list.size() > 0) {
                for (String field : list) {
                    try {
                        this.setAttributeValue(field, map.get(field));
                    } catch (Exception e) {
                        LogUtil.debug(this.getClass(), "对象创建异常，字段【" + field
                                + "】从map初始化异常");
                    }
                }
            }
        }
    }

    /**
     * 从自定义实体类初始化实例
     *
     * @param <T>
     * @param t
     */
    @SuppressWarnings("unchecked")
    public <T> SuperVO(T t) {
        for (Field field : t.getClass().getDeclaredFields()) {
            try {
                Object value = this.getAttributeValue(field.getName());
                if (SuperVO.class.isInstance(value)) {
                    Class<? extends SuperVO> classtype = (Class<? extends SuperVO>) value.getClass();
                    Constructor<? extends SuperVO> constructor = classtype.getConstructor(BeanHelper.getProperty(t, field.getName()).getClass());
                    Object object = constructor.newInstance(BeanHelper.getProperty(t, field.getName()));
                    this.setAttributeValue(field.getName(), object);
                } else {
                    this.setAttributeValue(field.getName(), BeanHelper.getProperty(t, field.getName()));
                }
            } catch (Exception e) {
                LogUtil.debug(this.getClass(), "对象创建异常，字段【" + field
                        + "】从自定义实体初始化异常");
            }
        }

    }

    static class Cache {
        // 所有属性（不进行注解转换）缓存
        static ConcurrentMap<String, List<String>> all_field_map = new ConcurrentHashMap<String, List<String>>();
        // 所有field对象实体map
        static ConcurrentMap<String, List<Field>> all_fieldEntry_map = new ConcurrentHashMap<String, List<Field>>();
        // field对象实体（不带注解）map
        static ConcurrentMap<String, List<Field>> fieldEntry_map = new ConcurrentHashMap<String, List<Field>>();
        // 属性（不带注解）缓存
        static ConcurrentMap<String, List<String>> field_map = new ConcurrentHashMap<String, List<String>>();
        // 属性（带转换注解）缓存
        static ConcurrentMap<String, Map<String, String>> field_transition_map = new ConcurrentHashMap<String, Map<String, String>>();

        static List<String> getAllField(String class_name) {
            return all_field_map.get(class_name);
        }

        static void setAllField(String class_name, List<String> field_list) {
            all_field_map.put(class_name, field_list);
        }

        public static List<Field> getAll_fieldEntry(String class_name) {
            return all_fieldEntry_map.get(class_name);
        }

        public static void setAll_fieldEntry(String class_name,
                                             List<Field> field_list) {
            all_fieldEntry_map.put(class_name, field_list);
        }

        public static List<Field> getFieldEntry(String class_name) {
            return fieldEntry_map.get(class_name);
        }

        public static void setFieldEntry(String class_name,
                                         List<Field> field_list) {
            fieldEntry_map.put(class_name, field_list);
        }

        static List<String> getField(String class_name) {
            return field_map.get(class_name);
        }

        static void setField(String class_name, List<String> field_list) {
            field_map.put(class_name, field_list);
        }

        static Map<String, String> getTransitionField(String class_name) {
            return field_transition_map.get(class_name);
        }

        static void setTransitionField(String class_name,
                                       Map<String, String> field_map) {
            field_transition_map.put(class_name, field_map);
        }
    }

    @JsonIgnore
    public String getBYNAME() {
        return BYNAME;
    }

    @JsonIgnore
    public void setBYNAME(String bYNAME) {
        BYNAME = bYNAME;
    }

    /**
     * 获取主键字段名 例如：“id”
     *
     * @return
     */
    public abstract String getPKFieldName();

    /**
     * 获取父级主键字段名 例如：“pid”
     *
     * @return
     */
    @JsonIgnore
    public abstract String getParentPKFieldName();

    /**
     * 获取配置的数据库表名
     *
     * @return
     */
    @JsonIgnore
    public abstract String getTableName();

    /**
     * 获取主键序列名
     *
     * @return
     */
    @JsonIgnore
    public abstract String getSequence();

    /**
     * 附加的sqlwhere条件
     */
    private String additionalWhereSQL = null;
    /**
     * 附加的sqlwhere条件参数列表
     */
    private List<Object> additionalWhereParams = null;
    /**
     * 附加的sqlwhere条件参数map，例：参数名：参数值
     */
    private HashMap<String, Object> additionalWhereParamMap = null;
    /**
     * 附加的join条件sql语句，该语句优先于所有where条件
     */
    private String joinSQL = null;

    /**
     * 获取附加的join条件sql语句
     *
     * @return
     */
    @JsonIgnore
    public String getJoinSQL() {
        return joinSQL;
    }

    /**
     * 设置附加的join条件sql语句
     *
     * @param joinSQL
     */
    @JsonIgnore
    public void setJoinSQL(String joinSQL) {
        this.joinSQL = joinSQL;
    }

    /**
     * 获取附加的sqlwhere条件
     *
     * @return
     */
    @JsonIgnore
    public String getAdditionalWhereSQL() {
        return additionalWhereSQL;
    }

    /**
     * 设置附加的sqlwhere条件
     *
     * @param additionalWhereSQL
     */
    @JsonIgnore
    public void setAdditionalWhereSQL(String additionalWhereSQL) {
        this.additionalWhereSQL = additionalWhereSQL;
    }

    /**
     * 添加附加的sqlwhere条件参数值
     *
     * @param param
     */
    @JsonIgnore
    public void addAdditionalWhereParams(Object param) {
        if (null == additionalWhereParams) {
            additionalWhereParams = new ArrayList<Object>();
        }
        additionalWhereParams.add(param);
    }

    /**
     * 添加附加的sqlwhere条件参数值,参数为数组
     *
     * @param param
     */
    @JsonIgnore
    public void addAdditionalWhereParams(Object[] param) {
        if (null == additionalWhereParams) {
            additionalWhereParams = new ArrayList<Object>();
        }
        if (null != param) {
            for (Object p : param) {
                additionalWhereParams.add(p);
            }
        }
    }

    @JsonIgnore
    public HashMap<String, Object> getAdditionalWhereParamMap() {
        return additionalWhereParamMap;
    }

    /**
     * 通过参数名参数值的方式、添加附加的参数
     *
     * @param paramName
     * @param paramValue
     */
    @JsonIgnore
    public void addAdditionalWhereParamMap(String paramName, Object paramValue) {
        if (null == additionalWhereParamMap) {
            additionalWhereParamMap = new HashMap<String, Object>();
        }
        additionalWhereParamMap.put(paramName, paramValue);
    }

    /**
     * 获取附加的sqlwhere条件参数值
     *
     * @return
     */
    @JsonIgnore
    public List<Object> getAdditionalWhereParams() {
        return additionalWhereParams;
    }

    /**
     * 获取所有属性名（通过注解进行排除）
     *
     * @return
     */
    @JsonIgnore
    public List<String> getAttributeNames() {
        if (null == Cache.getField(this.getClass().getName())) {
            Field[] f = this.getClass().getDeclaredFields();
            List<String> fields = new ArrayList<String>();
            for (int i = 0; i < f.length; i++) {
                if (!f[i].isAnnotationPresent(USERDEFINED_ANNOTATION)
                        && !SERIAL_VERSION_UID.equalsIgnoreCase(f[i].getName())) {
                    fields.add(f[i].getName());
                }
            }
            if (!this.getClass().getSuperclass().equals(SuperVO.class)) {
                try {
                    SuperVO superVo = (SuperVO) this.getClass().getSuperclass()
                            .newInstance();
                    fields.addAll(superVo.getAttributeNames());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            Cache.setField(this.getClass().getName(), fields);
        }
        return Cache.getField(this.getClass().getName());

    }

    /**
     * 获取所有属性名
     *
     * @return
     */
    @JsonIgnore
    public List<String> getAllAttributeNames() {
        if (null == Cache.getAllField(this.getClass().getName())) {
            Field[] f = this.getClass().getDeclaredFields();
            List<String> fields = new ArrayList<String>();
            for (int i = 0; i < f.length; i++) {
                fields.add(f[i].getName());
            }
            if (!this.getClass().getSuperclass().equals(SuperVO.class)) {
                try {
                    SuperVO superVo = (SuperVO) this.getClass().getSuperclass()
                            .newInstance();
                    fields.addAll(superVo.getAllAttributeNames());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            Cache.setAllField(this.getClass().getName(), fields);
        }
        return Cache.getAllField(this.getClass().getName());

    }

    /**
     * 获取所有属性名（通过注解进行排除）
     *
     * @return map<字段名：数据库转换函数>
     */
    @JsonIgnore
    public Map<String, String> getTransitionAttributeNames() {
        if (null == Cache.getTransitionField(this.getClass().getName())) {
            Field[] f = this.getClass().getDeclaredFields();
            Map<String, String> field_map = new HashMap<String, String>();
            for (int i = 0; i < f.length; i++) {
                if (f[i].isAnnotationPresent(USERDEFINED_ANNOTATION)) {
                    UserDefined ud = (UserDefined) f[i]
                            .getAnnotation(USERDEFINED_ANNOTATION);
                    String transition_source = ud.transition_source();
                    String transition_function = ud.transition_function();
                    String transition_list = ud.transition_list();
                    if (!"".equals(transition_source)) {
                        if (!"".equals(transition_list)) {
                            field_map.put(
                                    f[i].getName(),
                                    analayDecode(transition_source,
                                            transition_list));
                        } else if (!"".equals(transition_function)) {
                            field_map.put(f[i].getName(), transition_function
                                    + "(" + transition_source + ")");
                        }

                    }
                }
            }
            if (!this.getClass().getSuperclass().equals(SuperVO.class)) {
                try {
                    SuperVO superVo = (SuperVO) this.getClass().getSuperclass()
                            .newInstance();
                    field_map.putAll(superVo.getTransitionAttributeNames());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            Cache.setTransitionField(this.getClass().getName(), field_map);
        }
        return Cache.getTransitionField(this.getClass().getName());

    }

    /**
     * 解析decode函数
     *
     * @param transition_source
     * @param transition_list
     * @return
     */
    @JsonIgnore
    private String analayDecode(String transition_source, String transition_list) {
        StringBuffer decode_fun = new StringBuffer("");
        switch (DataSourceConst.DATABASE_TYPE) {
            case DataSourceConst.DATABASE_ORACLE:
                decode_fun.append("DECODE(").append(transition_source).append(",")
                        .append(transition_list).append(")");
                break;
            case DataSourceConst.DATABASE_MYSQL:
                decode_fun.append("case ").append(transition_source).append(" ");
                String[] arrs = transition_list.split(",");
                if (arrs.length < 3) {
                    return transition_source;
                }
                if (arrs.length % 2 == 0) {
                    // 偶数
                    for (int i = 0; i < arrs.length - 1; ) {
                        decode_fun.append(" when ").append(arrs[i])
                                .append(" then ").append(arrs[i + 1]);
                        i = i + 2;
                    }
                } else {
                    // 奇数
                    for (int i = 0; i < arrs.length - 2; ) {
                        decode_fun.append(" when ").append(arrs[i])
                                .append(" then ").append(arrs[i + 1]);
                        i = i + 2;
                    }
                    decode_fun.append(" else ").append(arrs[arrs.length - 1]);
                }
                decode_fun.append(" end");
                break;
        }
        return decode_fun.toString();
    }

    /**
     * 获取所有属性字符串
     *
     * @return
     */
    @JsonIgnore
    public String getAllAttributeNamesByStr() {
        return this.getAllAttributeNamesByStr(true, true);
    }

    /**
     * 获取所有属性字符串
     *
     * @param isContainByName     是否包含表别名
     * @param isContainTransition 是否包含函数转换的属性
     * @return
     */
    @JsonIgnore
    protected String getAllAttributeNamesByStr(boolean isContainByName,
                                               boolean isContainTransition) {
        StringBuffer sb = new StringBuffer();
        List<String> field_list = this.getAttributeNames();
        for (int i = 0; i < field_list.size(); i++) {
            sb.append(BYNAME).append(".").append(field_list.get(i));
            if (i != field_list.size() - 1) {
                sb.append(",");
            }
        }
        if (isContainTransition) {
            Map<String, String> field_map = this.getTransitionAttributeNames();
            if (field_map.size() > 0) {
                sb.append(",");
                int i = 1;
                for (String key : field_map.keySet()) {
                    sb.append(field_map.get(key)).append(" ").append(key);
                    if (i < field_map.size()) {
                        sb.append(",");
                    }
                    i++;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取属性的值
     *
     * @param name
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public Object getAttributeValue(String name) throws Exception {
        if (Util.isBlank(name)) {
            return null;
        }
        Object value = BeanHelper.getProperty(this, name);
        if (null != value && value instanceof String) {
            return value.toString().trim();
        }
        return value;
    }

    /**
     * 设置属性的值
     *
     * @param name
     * @param value
     * @throws Exception
     */
    @JsonIgnore
    public void setAttributeValue(String name, Object value) throws Exception {
        BeanHelper.setProperty(this, name, value);
    }

    /**
     * 获取所有属性值字符串
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getAllAttributeValueByStr() throws Exception {
        List<String> field_list = this.getAttributeNames();
        StringBuffer value_str = new StringBuffer(" ");
        for (int i = 0; i < field_list.size(); i++) {
            if (getAttributeValue(field_list.get(i)) instanceof String) {
                value_str.append("'");
            }
            value_str.append(getAttributeValue(field_list.get(i)));
            if (getAttributeValue(field_list.get(i)) instanceof String) {
                value_str.append("'");
            }
            if (i < field_list.size() - 1) {
                value_str.append(",");
            }
        }
        return value_str.toString();
    }

    /**
     * 获取所有属性与值的字符串
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getAllAttributeNameAndValueByStr(boolean isNullUpdate)
            throws Exception {
        List<String> field_list = this.getAttributeNames();
        StringBuffer value_str = new StringBuffer(" ");
        for (int i = 0; i < field_list.size(); i++) {
            if (!isNullUpdate && null == getAttributeValue(field_list.get(i))) {
                continue;
            }
            value_str.append(field_list.get(i)).append("=");
            if (getAttributeValue(field_list.get(i)) instanceof String) {
                value_str.append("'");
            }
            value_str.append(getAttributeValue(field_list.get(i)));
            if (getAttributeValue(field_list.get(i)) instanceof String) {
                value_str.append("'");
            }
            if (i < field_list.size() - 1) {
                value_str.append(",");
            }
        }
        return value_str.toString();
    }

    /**
     * 获取where语句用到的sql
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getWhereSQL() throws Exception {
        return this.getWhereSQL(false, true);
    }

    /**
     * 获取where语句用到的sql
     *
     * @param isNullUpdate null值的字段是否匹配
     * @param enableLike   字符串是否采用like查询
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getWhereSQL(boolean isNullUpdate, boolean enableLike)
            throws Exception {
        List<Field> field_list = getAllAttributeFieldEntry();
        StringBuffer value_str = new StringBuffer();
        Object val = null;
        Field f = null;
        UserDefined ud = null;
        String alias = null;
        String db_fieldName = null;
        for (int i = 0; i < field_list.size(); i++) {
            f = field_list.get(i);
            db_fieldName = f.getName();
            val = getAttributeValue(f.getName());
            if (!isNullUpdate
                    && (null == val || val.toString().trim().length() == 0)) {
                continue;
            }
            if (value_str.length() > 0) {
                value_str.append(" and ");
            } else {
                value_str.append(" ");
            }
            if (f.isAnnotationPresent(USERDEFINED_ANNOTATION)) {
                ud = (UserDefined) f.getAnnotation(USERDEFINED_ANNOTATION);
                alias = ud.table_alias();
                if (null != alias) {
                    value_str.append(alias).append(".");
                }
                if (null != ud.db_fieldName()) {
                    db_fieldName = ud.db_fieldName();
                }
            }
            value_str.append(db_fieldName);

            if (enableLike && val instanceof String) {
                value_str.append(" like '%").append(val).append("%' ");
            } else if (val instanceof String) {
                value_str.append(" ='").append(val).append("' ");
            } else {
                value_str.append(" =").append(val).append(" ");
            }
        }
        return value_str.toString();
    }

    /**
     * 获取insert的sql语句
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getInsertSQL() throws Exception {
        StringBuffer sql = new StringBuffer("insert into ")
                .append(getTableName()).append("(")
                .append(getAllAttributeNamesByStr(false, false))
                .append(") values(").append(getAllAttributeValueByStr())
                .append(")");
        return sql.toString();
    }

    /**
     * 获取update的sql语句
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getUpdateSQL() throws Exception {
        return getUpdateSQL(true);
    }

    /**
     * 获取update的sql语句
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getUpdateSQL(boolean isNullUpdate) throws Exception {
        StringBuffer sql = new StringBuffer(" update ").append(getTableName())
                .append(" set ")
                .append(getAllAttributeNameAndValueByStr(isNullUpdate))
                .append(" where ").append(getPKFieldName()).append("=")
                .append(getAttributeValue(getPKFieldName()));
        return sql.toString();
    }

    /**
     * 获取delete的sql语句
     *
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public String getDeleteSQL() throws Exception {
        StringBuffer sql = new StringBuffer(" delete from  ")
                .append(getTableName()).append(" where ")
                .append(getPKFieldName()).append("=")
                .append(getAttributeValue(getPKFieldName()));
        return sql.toString();
    }

    /**
     * 对对象进行整体转码
     *
     * @throws Exception
     */
    @JsonIgnore
    public void decode() throws Exception {
        this.decode("utf-8");
    }

    /**
     * 对对象进行整体转码
     *
     * @param charset
     * @throws Exception
     */
    @JsonIgnore
    public void decode(String charset) throws Exception {
        List<String> list = this.getAllAttributeNames();
        if (null != list && list.size() > 0) {
            Object value;
            for (String field : list) {
                value = this.getAttributeValue(field);
                if (null != value && value instanceof String) {
                    try {
                        value = URLDecoder.decode(value.toString(), charset);
                        this.setAttributeValue(field, value);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * 当前对象与新对象进行比较，返回差异字段的日志信息
     *
     * @param newVO
     * @return
     * @throws Exception
     */
    @JsonIgnore
    public List<EntityDifferenceVO> compareForLog(SuperVO newVO)
            throws Exception {
//        List<EntityDifferenceVO> edVOList = new ArrayList<EntityDifferenceVO>();
//        List<Field> filedList = this.getAllAttributeFieldEntry();
//        for (Field field : filedList) {
//            Object oldValue = this.getAttributeValue(field.getName());
//            Object newValue = newVO.getAttributeValue(field.getName());
//            if ((oldValue != null && !oldValue.equals(newValue))
//                    || (newValue != null && !newValue.equals(oldValue))) {
//                FieldMeta meta = field.getAnnotation(FieldMeta.class);
//                if (meta != null) {
//                    SortableField sf = new SortableField(meta, field);
//                    EntityDifferenceVO edVO = new EntityDifferenceVO();
//                    edVO.setFieldDesc(sf.getMeta().columDesc());
//                    edVO.setOldVal(oldValue + "");
//                    edVO.setNewVal(newValue + "");
//                    edVO.setDesc(sf.getMeta().columDesc() + ":由‘" + oldValue
//                            + "’变为‘" + newValue + "’");
//                    edVOList.add(edVO);
//                }
//            }
//        }
//        return edVOList;
        return null;
    }

    /**
     * @return
     * @throws Exception
     * @decription获取当前对象的日志内容
     */
    @JsonIgnore
    public String getLogContent() throws Exception {
//        List<Field> filedList = this.getAllAttributeFieldEntry();
//        StringBuffer logcontent = new StringBuffer();
//        for (Field field : filedList) {
//            Object oldValue = this.getAttributeValue(field.getName());
//            if (oldValue != null) {
//                FieldMeta meta = field.getAnnotation(FieldMeta.class);
//                if (meta != null) {
//                    SortableField sf = new SortableField(meta, field);
//                    logcontent
//                            .append(sf.getMeta().columDesc() + ":" + oldValue)
//                            .append("，");
//                }
//            }
//        }
//        return logcontent.toString();
        return "";
    }

    /**
     * 当前对象与新对象进行比较，返回差异字段的日志信息
     *
     * @param newVO
     * @return
     * @throws Exception
     * @decription
     * @author shangwenyu
     * @time 2015-1-14 上午10:01:40
     */
    @JsonIgnore
    public String compareForLogContent(SuperVO newVO) throws Exception {
//        List<EntityDifferenceVO> list = this.compareForLog(newVO);
//        StringBuffer logcontent = new StringBuffer();
//
//        for (EntityDifferenceVO entityDifferenceVO : list) {
//            logcontent.append(entityDifferenceVO.getDesc()).append("，");
//        }
//        return logcontent.toString();
        return "";
    }

    /**
     * 获取所有属性（通过注解进行排除）
     *
     * @return
     */
    @JsonIgnore
    public List<Field> getAttributeFieldEntry() {
        if (null == Cache.getFieldEntry(this.getClass().getName())) {
            List<Field> fields = new ArrayList<Field>();
            Field[] f = this.getClass().getDeclaredFields();
            for (int i = 0; i < f.length; i++) {
                if (!f[i].isAnnotationPresent(USERDEFINED_ANNOTATION)
                        && !SERIAL_VERSION_UID.equalsIgnoreCase(f[i].getName())) {
                    fields.add(f[i]);
                }
            }
            if (!this.getClass().getSuperclass().equals(SuperVO.class)) {
                try {
                    SuperVO superVo = (SuperVO) this.getClass().getSuperclass()
                            .newInstance();
                    fields.addAll(superVo.getAttributeFieldEntry());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            Cache.setFieldEntry(this.getClass().getName(), fields);
        }
        return Cache.getFieldEntry(this.getClass().getName());
    }

    /**
     * 获取所有属性（通过注解进行排除）
     *
     * @return
     */
    @JsonIgnore
    public List<Field> getAllAttributeFieldEntry() {
        if (null == Cache.getAll_fieldEntry(this.getClass().getName())) {
            Field[] f = this.getClass().getDeclaredFields();
            Cache.setAll_fieldEntry(this.getClass().getName(), Arrays.asList(f));
        }
        return Cache.getAll_fieldEntry(this.getClass().getName());
    }

    /**
     * 去除所有值的前后英文空格
     *
     * @throws Exception
     */
    @JsonIgnore
    public void trim() throws Exception {
        List<String> list = this.getAllAttributeNames();
        if (null != list && list.size() > 0) {
            Object value;
            for (String field : list) {
                value = this.getAttributeValue(field);
                if (null != value && value instanceof String) {
                    try {
                        value = value.toString().trim().replaceAll(" ", "");
                        this.setAttributeValue(field, value);
                    } catch (Exception e) {
                    }

                }
            }
        }
    }

    /**
     * 克隆对象生成一个新对象
     */
    @Override
    public SuperVO clone() {
        try {
            SuperVO newVO = this.getClass().newInstance();
            List<String> field_list = this.getAttributeNames();
            if (null != field_list && field_list.size() > 0) {
                for (String field : field_list) {
                    newVO.setAttributeValue(field,
                            this.getAttributeValue(field));
                }
            }
            return newVO;
        } catch (Exception e) {
            LogUtil.error(this.getClass(), e);
            return null;
        }
    }

    /**
     * 与新实例比较，得到发生改变的属性名称，以英文逗号“，”分隔
     *
     * @param newVO
     * @return
     * @throws Exception
     */
    public String findUpdateFields(SuperVO newVO) throws Exception {
        String fields = "";
        List<Field> filedList = this.getAttributeFieldEntry();
        for (Field field : filedList) {
            Object oldValue = this.getAttributeValue(field.getName());
            Object newValue = newVO.getAttributeValue(field.getName());
            if ("".equals(oldValue))
                oldValue = null;
            if ("".equals(newValue))
                newValue = null;

            if (!ObjectUtils.nullSafeEquals(oldValue, newValue)) {
                fields += "," + field.getName();
            }
        }
        if (fields.length() > 0) {
            fields = fields.substring(1);
        } else {
            fields = "-none-";
        }
        return fields.toLowerCase();
    }

    /**
     * 与新实例比较，得到执行的update语句
     *
     * @param newVO 新数据集
     * @return
     * @throws Exception
     */
    public String findUpdateSqls(SuperVO newVO) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(newVO.getTableName());
        List<Field> filedList = this.getAttributeFieldEntry();
        for (Field field : filedList) {
            Object oldValue = this.getAttributeValue(field.getName());
            Object newValue = newVO.getAttributeValue(field.getName());
            if ((null != oldValue && !oldValue.equals(newValue))
                    || (null != newValue && !newValue.equals(oldValue))) {
                sql.append(" set ").append(field.getName()).append(" = '")
                        .append(newValue).append("', ");
            }
        }
        sql.append(" where ").append(newVO.getPKFieldName()).append(" = ")
                .append(newVO.getAttributeValue(newVO.getPKFieldName()));
        return sql.toString().toLowerCase();
    }

    /**
     * 获取insert语句
     *
     * @param pkid
     * @return
     * @throws Exception
     */
    public String findInsertSqls(int pkid) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(this.getTableName());
        List<Field> filedList = this.getAttributeFieldEntry();
        StringBuffer fields = new StringBuffer("(").append(
                this.getPKFieldName()).append(",");
        StringBuffer values = new StringBuffer(" values(").append(pkid).append(
                ",");
        for (Field field : filedList) {
            if (field.getName().equalsIgnoreCase(this.getPKFieldName())) {
                continue;
            }
            Object value = this.getAttributeValue(field.getName());
            fields.append(field.getName()).append(",");
            values.append("'").append(value).append("',");
        }
        sql.append(fields).append(")").append(values).append(");");
        return sql.toString().toLowerCase();
    }

    /**
     * 转换为map
     *
     * @return
     */
    public Map<String, Object> toMap() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> list = this.getAllAttributeNames();
        if (null != list && list.size() > 0) {
            for (String field : list) {
                map.put(field, this.getAttributeValue(field));
            }
        }
        return map;
    }

    /**
     * 转换为自定义实体类
     *
     * @return
     */
    public Object toIClass(Class<?> elementClass) throws Exception {
        Object ret = elementClass.newInstance();
        Field[] f = elementClass.getDeclaredFields();

        for (Field field : f) {
            Object value = BeanHelper.getProperty(this, field.getName());
            if (SuperVO.class.isInstance(value)) {
                Class<? extends Object> cc = value.getClass();
                String typename = cc.getName();
                typename = typename.replaceFirst("E", "I");
                Class<?> clazz = Class.forName(typename);
                BeanHelper.setProperty(ret, field.getName(), ((SuperVO) value).toIClass(clazz));
            } else {
                BeanHelper.setProperty(ret, field.getName(), value);
            }
        }
        return ret;
    }
}
