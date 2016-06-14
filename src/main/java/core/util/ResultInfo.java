package core.util;

import java.io.Serializable;
import java.net.URLEncoder;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/8 16:18
 */
public class ResultInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 512681557748919471L;
    /**
     * 结果状态值 默认0，-1：失败；1：成功； 1:成功，-1：失败
     */
    private int code = 0;
    /**
     * 对状态值的描述
     */
    private String message;
    /**
     * 数据
     */
    private Object data;
    /**
     * 异步验证-成功信息
     */
    private String ok;
    /**
     * 异步验证-失败信息
     */
    private String error;
    /**
     * 执行的sql
     */
    private String sql;

    public ResultInfo(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设定字符集
     *
     * @param code
     * @param message
     * @param charset
     */
    public ResultInfo(int code, String message, String charset) {
        this.code = code;
        try {
            this.message = URLEncoder.encode(message, charset);
        } catch (Exception e) {
            this.message = message;
        }
    }

    public ResultInfo() {
        this.code = 0;
        this.message = "";
    }

    public ResultInfo(Integer resultCode) {
        this.code = resultCode;
    }

    public ResultInfo(Integer resultCode, Object data) {
        this.code = resultCode;
        this.data = data;
    }

    public void setSuccess(String message, Object data) {
        this.code = 1;
        this.message = message;
        this.data = data;
    }
}
