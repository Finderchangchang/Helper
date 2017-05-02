package wai.school.model;

import cn.bmob.v3.BmobObject;

/**
 * 评论订单model
 * Created by Administrator on 2017/5/1.
 */

public class AskModel extends BmobObject {
    UserModel user;
    OrderModel order;
    String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
