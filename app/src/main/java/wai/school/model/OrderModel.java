package wai.school.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/5/1.
 */

public class OrderModel extends BmobObject implements Serializable {
    String title;//消息头
    String sex;//性别
    String remark;//备注
    String money;//佣金
    String address;//送达地址
    UserModel user;//发布订单人信息
    String state;//订单状态
    UserModel jd_user;//接单人信息
    String pingjia;//发单人对送件人的评价

    public UserModel getJd_user() {
        return jd_user;
    }

    public void setJd_user(UserModel jd_user) {
        this.jd_user = jd_user;
    }

    public String getState() {
        return state;
    }

    public String getPingjia() {
        return pingjia;
    }

    public void setPingjia(String pingjia) {
        this.pingjia = pingjia;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
