package core.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 15:44
 */
public class PagerVO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3550008277757626894L;

    /**
     * 记录总数
     */
    private int total;

    /**
     * 记录列表
     */
    private List<?> datas;

    /**
     * 数据json格式
     */
    private String dataJson;

    /**
     * json参数map
     */
    private Map<String, Object> jsonParamMap;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<?> getDatas() {
        return datas;
    }

    public void setDatas(List<?> datas) {
        this.datas = datas;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public void addJsonParam(String param, Object value) {
        if (null == jsonParamMap) {
            jsonParamMap = new HashMap<String, Object>();
        }
        jsonParamMap.put(param, value);
    }

    public Map<String, Object> getJsonParamMap() {
        return jsonParamMap;
    }

    public void setJsonParamMap(Map<String, Object> jsonParamMap) {
        this.jsonParamMap = jsonParamMap;
    }

}
