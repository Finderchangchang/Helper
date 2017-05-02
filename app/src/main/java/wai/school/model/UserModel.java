package wai.school.model;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/5/1.
 */

public class UserModel extends BmobUser implements Serializable {
    String sex;
    String school;//所属学校
    String remark;//个人签名
    String name;//真实姓名
    String img;//头像url
    String address;//收货地址
    String yz_code;//邀请码

    public String getYz_code() {
        return yz_code;
    }

    public void setYz_code(String yz_code) {
        this.yz_code = yz_code;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
