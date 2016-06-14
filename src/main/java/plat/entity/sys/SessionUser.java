package plat.entity.sys;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description:登录用户
 * @version: v1.0.0
 * @author: Lucifer
 * @date: 2016-3-8 10:00:00
 */
public class SessionUser implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3679298153759703651L;
    /**
     * 当前用户id
     */
    private Integer id;
    /**
     * 登录id
     */
    private String loginid;
    /**
     * 登录密码
     */
    private String loginpwd;
    /**
     * 登录类型
     */
    private Integer logintype;
    /**
     * 人员姓名
     */
    private String lastname;
    /**
     * 部门id
     */
    private Integer departmentid;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 分部id
     */
    private Integer subcompanyid;
    /**
     * 分部名称
     */
    private String subCompanyName;
    /**
     * 安全级别
     */
    private Integer seclevel;
    /**
     * 最后登录时间
     */
    private String lastlogindate;
    /**
     * 最后登录IP
     */
    private String lastloginip;
    /**
     * 最后登录的服务器ip
     */
    private String lastloginServerip;
    /**
     * 是否短信提醒 0：否；1：是
     */
    private Integer isMessage;
    /**
     * 办公电话
     */
    private String officephone;
    /**
     * 个人手机
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * sessionid
     */
    private String sessionid;
    /**
     * 是否手机登录
     */
    private boolean mobilelogin;
    /**
     * 单点登录token
     */
    private String token;
    /**
     * 是否管理员，true:是管理员
     */
    private Boolean managerUserFlag = false;
    /**
     * 直接上级id
     */
    private Integer managerid;
    /**
     * 直接上级姓名
     */
    private String managername;
    /**
     * 岗位名称
     */
    private String jobtitlename;
    /**
     * 0表示手机APP登录,1表示手机wap登录,null表示电脑登录
     **/
    private Integer iswaplogin;

    /**
     * coolie中的记录多选项卡用户上次操作习惯
     */
    private Map<String, String> multitabcom_cookie_map;

    public SessionUser() {
        super();
    }

    public SessionUser(int userId, String loginIp, String loginTime,
                       String sessionId, boolean mobileLogin, String serverIp,
                       String lastName, Integer subCompanyId, Integer departMentId,
                       String officephone, Integer secLevel, String email) {
        super();
        this.id = userId;
        this.lastloginip = loginIp;
        this.lastlogindate = loginTime;
        this.sessionid = sessionId;
        this.mobilelogin = mobileLogin;
        this.lastloginServerip = serverIp;
        this.lastname = lastName;
        this.subcompanyid = subCompanyId;
        this.departmentid = departMentId;
        this.officephone = officephone;
        this.seclevel = secLevel;
        this.email = email;
    }


    public Integer getIswaplogin() {
        return iswaplogin;
    }

    public void setIswaplogin(Integer iswaplogin) {
        this.iswaplogin = iswaplogin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getLoginpwd() {
        return loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd;
    }

    public Integer getLogintype() {
        return logintype;
    }

    public void setLogintype(Integer logintype) {
        this.logintype = logintype;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(Integer departmentid) {
        this.departmentid = departmentid;
    }

    public Integer getSubcompanyid() {
        return subcompanyid;
    }

    public void setSubcompanyid(Integer subcompanyid) {
        this.subcompanyid = subcompanyid;
    }

    public Integer getSeclevel() {
        return seclevel;
    }

    public void setSeclevel(Integer seclevel) {
        this.seclevel = seclevel;
    }

    public String getLastlogindate() {
        return lastlogindate;
    }

    public void setLastlogindate(String lastlogindate) {
        this.lastlogindate = lastlogindate;
    }

    public String getLastloginip() {
        return lastloginip;
    }

    public void setLastloginip(String lastloginip) {
        this.lastloginip = lastloginip;
    }

    public String getLastloginServerip() {
        return lastloginServerip;
    }

    public void setLastloginServerip(String lastloginServerip) {
        this.lastloginServerip = lastloginServerip;
    }

    public Integer getIsMessage() {
        return isMessage;
    }

    public void setIsMessage(Integer isMessage) {
        this.isMessage = isMessage;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getOfficephone() {
        return officephone;
    }

    public void setOfficephone(String officephone) {
        this.officephone = officephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSubCompanyName() {
        return subCompanyName;
    }

    public void setSubCompanyName(String subCompanyName) {
        this.subCompanyName = subCompanyName;
    }

    public void setMobilelogin(boolean mobilelogin) {
        this.mobilelogin = mobilelogin;
    }

    public Boolean getMobilelogin() {
        return mobilelogin;
    }

    public void setMobilelogin(Boolean mobilelogin) {
        this.mobilelogin = mobilelogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getManagerUserFlag() {
        return managerUserFlag;
    }

    public void setManagerUserFlag(Boolean managerUserFlag) {
        this.managerUserFlag = managerUserFlag;
    }

    public Map<String, String> getMultitabcom_cookie_map() {
        return multitabcom_cookie_map;
    }

    public void setMultitabcom_cookie_map(
            Map<String, String> multitabcom_cookie_map) {
        this.multitabcom_cookie_map = multitabcom_cookie_map;
    }

    public Integer getManagerid() {
        return managerid;
    }

    public void setManagerid(Integer managerid) {
        this.managerid = managerid;
    }

    public String getManagername() {
        return managername;
    }

    public void setManagername(String managername) {
        this.managername = managername;
    }

    public String getJobtitlename() {
        return jobtitlename;
    }

    public void setJobtitlename(String jobtitlename) {
        this.jobtitlename = jobtitlename;
    }

}