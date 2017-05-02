package wai.school.model;

import cn.bmob.v3.BmobObject;

/**
 * 想要接单人信息model
 * Created by Administrator on 2017/5/1.
 */

public class WantModel extends BmobObject {
    UserModel user;
    OrderModel order;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }
}
