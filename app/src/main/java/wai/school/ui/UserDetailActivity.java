package wai.school.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.tsz.afinal.view.TitleBar;
import net.tsz.afinal.view.TotalListView;

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
 * 用户详情
 */
public class UserDetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.user_iv)
    ImageView userIv;
    @Bind(R.id.title_rl)
    RelativeLayout titleRl;
    @Bind(R.id.school_tv)
    TextView schoolTv;
    @Bind(R.id.address_tv)
    TextView addressTv;
    @Bind(R.id.order_num_tv)
    TextView orderNumTv;
    @Bind(R.id.remark_tv)
    TextView remarkTv;
    @Bind(R.id.pl_lv)
    TotalListView plLv;
    @Bind(R.id.xy_ll)
    LinearLayout xy_ll;
    UserModel model;

    @Override
    public int setLayout() {
        return R.layout.activity_user_detail;
    }

    @Override
    public void initViews() {
        toolbar.setLeftClick(() -> finish());
        model = (UserModel) getIntent().getSerializableExtra("user");
        /**-----用户基本信息显示-------*/
        if (!TextUtils.isEmpty(model.getName())) {
            userNameTv.setText(model.getName());
        }
        if (!TextUtils.isEmpty(model.getSchool())) {
            schoolTv.setText(model.getSchool());
        }
        if (!TextUtils.isEmpty(model.getAddress())) {
            addressTv.setText(model.getAddress());
        }
        if (!TextUtils.isEmpty(model.getRemark())) {
            remarkTv.setText(model.getRemark());
        }
        Glide.with(this)
                .load(model.getImg())
                .error(R.mipmap.user)
                .into(userIv);
        switch (model.getSex()) {
            case "1":
                Drawable drawable = getResources().getDrawable(R.mipmap.nan);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                userNameTv.setCompoundDrawables(drawable, null, null, null);
                break;
            default:
                drawable = getResources().getDrawable(R.mipmap.nv);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                userNameTv.setCompoundDrawables(drawable, null, null, null);
                break;
        }
        orderModels = new ArrayList<>();
        pj_adapter = new CommonAdapter<OrderModel>(this, orderModels, R.layout.item_pj) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                holder.setText(R.id.name_tv," "+ model.getPingjia());
            }
        };
        plLv.setAdapter(pj_adapter);
        BmobQuery<OrderModel> bmobQuery = new BmobQuery<>();
        UserModel model = new UserModel();
        model.setObjectId(Utils.getCache("user_id"));
        bmobQuery.addWhereEqualTo("jd_user", model);//当前账户
        bmobQuery.addWhereEqualTo("state", "3");//状态为用户确认订单
        bmobQuery.findObjects(new FindListener<OrderModel>() {
            @Override
            public void done(List<OrderModel> list, BmobException e) {
                if (e == null) {
                    orderModels = list;
                    pj_adapter.refresh(orderModels);//刷新订单列表，并显示数据
                    orderNumTv.setText(orderModels.size() + "件");
                }
            }
        });
    }

    List<OrderModel> orderModels;
    CommonAdapter<OrderModel> pj_adapter;

    @Override
    public void initEvents() {

    }
}
