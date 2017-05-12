package wai.school.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
 * 订单管理列表
 */
public class OrderManagerActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.order_tab)
    TabLayout orderTab;
    @Bind(R.id.order_lv)
    ListView orderLv;
    List<OrderModel> list;

    @Override
    public int setLayout() {
        return R.layout.activity_order_manager;
    }

    @Override
    public void initViews() {
        toolbar.setLeftClick(() -> finish());
        list = new ArrayList<>();
        orderTab.addTab(orderTab.newTab().setText("我的发单"));
        orderTab.addTab(orderTab.newTab().setText("我的接单"));//顶部tab切换，下面内容进行修改
        orderTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refresh(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        adapter = new CommonAdapter<OrderModel>(this, list, R.layout.item_main) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                holder.setGliImage(R.id.user_iv, model.getUser().getImg());
                if (model.getUser().getName() == null) {
                    holder.setText(R.id.user_name_tv, "未知");
                } else {
                    holder.setText(R.id.user_name_tv, model.getUser().getName());
                }
                holder.setText(R.id.order_time_tv, model.getCreatedAt());
                holder.setText(R.id.order_title_tv, model.getTitle());
                holder.setText(R.id.price_tv, "￥" + model.getMoney());
                holder.setText(R.id.remark_tv, model.getRemark());
                holder.setText(R.id.address_tv, model.getAddress());
                switch (model.getSex()) {
                    case "1":
                        holder.setText(R.id.sex_tv, "仅限男性");
                        break;
                    case "2":
                        holder.setText(R.id.sex_tv, "仅限女性");
                        break;
                    default:
                        holder.setText(R.id.sex_tv, "男女不限");
                        break;
                }
                if (model.getUser().getSex() != null) {
                    switch (model.getUser().getSex()) {
                        case "1":
                            holder.setImageResource(R.id.sex_iv, R.mipmap.nan);
                            break;
                        default:
                            holder.setImageResource(R.id.sex_iv, R.mipmap.nv);
                            break;
                    }
                }
            }
        };
        orderLv.setAdapter(adapter);
        refresh("我的发单");
    }

    CommonAdapter<OrderModel> adapter;

    /**
     * 根据状态不同对数据进行刷新操作
     * @param val
     */
    void refresh(String val) {
        BmobQuery<OrderModel> query = new BmobQuery<>();
        query.order("-createdAt");
        query.include("user");//根据当前用户查询出对应订单并根据创建时间进行倒序排列
        UserModel userModel = new UserModel();
        userModel.setObjectId(Utils.getCache("user_id"));
        switch (val) {
            case "我的发单":
                query.addWhereEqualTo("user", userModel);
                break;
            default:
                query.addWhereEqualTo("jd_user", userModel);
                break;
        }
        query.findObjects(new FindListener<OrderModel>() {
            @Override
            public void done(List<OrderModel> models, BmobException e) {
                if (e == null) {
                    list = models;
                    adapter.refresh(list);
                }
            }
        });
    }

    @Override
    public void initEvents() {

    }
}
