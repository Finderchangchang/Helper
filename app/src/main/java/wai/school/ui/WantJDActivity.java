package wai.school.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.tsz.afinal.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.CommonAdapter;
import wai.school.method.CommonViewHolder;
import wai.school.method.Utils;
import wai.school.model.OrderModel;
import wai.school.model.UserModel;
import wai.school.model.WantModel;

/**
 * 想要接单页面
 */
public class WantJDActivity extends BaseActivity {
    String order_id;
    List<WantModel> orderModels;
    CommonAdapter<WantModel> adapter;
    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.main_lv)
    ListView mainLv;

    @Override
    public int setLayout() {
        return R.layout.activity_want_jd;
    }

    @Override
    public void initViews() {
        toolbar.setLeftClick(() -> finish());
        order_id = getIntent().getStringExtra("order_id");
        orderModels = new ArrayList<>();
        adapter = new CommonAdapter<WantModel>(this, orderModels, R.layout.item_user) {
            @Override
            public void convert(CommonViewHolder holder, WantModel model, int position) {
                holder.setGliImage(R.id.user_iv, model.getUser().getImg());
                holder.setText(R.id.name_tv, model.getUser().getName());
            }
        };
        mainLv.setAdapter(adapter);
        OrderModel model = new OrderModel();
        model.setObjectId(order_id);
        BmobQuery<WantModel> query = new BmobQuery<>();
        query.addWhereEqualTo("order", model);
        query.include("user");
        query.findObjects(new FindListener<WantModel>() {
            @Override
            public void done(List<WantModel> list, BmobException e) {
                if (e == null) {
                    orderModels = list;
                    adapter.refresh(orderModels);
                }
            }
        });
        mainLv.setOnItemClickListener((adapterView, view, i, l) -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setObjectId(order_id);
            orderModel.setState("1");
            orderModel.setJd_user(orderModels.get(i).getUser());
            orderModel.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        setResult(66);
                        finish();
                    }
                }
            });

        });
    }

    @Override
    public void initEvents() {

    }
}
