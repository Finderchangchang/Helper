package wai.school.ui;

import android.os.Bundle;
import android.widget.ListView;

import net.tsz.afinal.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.CommonAdapter;
import wai.school.method.CommonViewHolder;
import wai.school.method.Utils;
import wai.school.model.OrderModel;
import wai.school.model.UserModel;

/**
 * 评分管理
 */
public class AskManageActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.main_lv)
    ListView mainLv;
    List<OrderModel> orderModels;
    CommonAdapter<OrderModel> pj_adapter;

    @Override
    public int setLayout() {
        return R.layout.activity_ask_manage;
    }

    @Override
    public void initViews() {
        toolbar.setLeftClick(() -> finish());
    }

    @Override
    public void initEvents() {
        orderModels = new ArrayList<>();
        pj_adapter = new CommonAdapter<OrderModel>(MainActivity.admin, orderModels, R.layout.item_pj) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                holder.setGliImage(R.id.user_iv, model.getUser().getImg());
                if (model.getUser().getName() == null) {
                    holder.setText(R.id.name_tv, "未知");
                } else {
                    holder.setText(R.id.name_tv, model.getUser().getName());
                }
                if (model.getPingjia() == null) {
                    holder.setText(R.id.content_tv, "未知");
                } else {
                    holder.setText(R.id.content_tv, model.getPingjia());
                }
            }
        };
        mainLv.setAdapter(pj_adapter);
        BmobQuery<OrderModel> bmobQuery = new BmobQuery<>();
        UserModel model = new UserModel();
        model.setObjectId(Utils.getCache("user_id"));
        bmobQuery.include("jd_user");
        bmobQuery.addWhereEqualTo("jd_user", model);//当前账户
        bmobQuery.addWhereEqualTo("state", "3");//状态为用户确认订单
        bmobQuery.findObjects(new FindListener<OrderModel>() {
            @Override
            public void done(List<OrderModel> list, BmobException e) {
                if (e == null) {
                    orderModels = list;
                    pj_adapter.refresh(orderModels);
                }
            }
        });
    }
}
