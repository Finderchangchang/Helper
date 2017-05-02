package wai.school.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.CommonAdapter;
import wai.school.method.CommonViewHolder;
import wai.school.method.Utils;
import wai.school.model.AskModel;
import wai.school.model.OrderModel;
import wai.school.model.UserModel;
import wai.school.model.WantModel;

/**
 * 订单详情页面
 */
public class OrderDetailActivity extends BaseActivity {
    OrderModel order;
    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.user_iv)
    ImageView userIv;
    @Bind(R.id.sex_iv)
    ImageView sexIv;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.order_time_tv)
    TextView orderTimeTv;
    @Bind(R.id.order_title_tv)
    TextView orderTitleTv;
    @Bind(R.id.price_tv)
    TextView priceTv;
    @Bind(R.id.remark_tv)
    TextView remarkTv;
    @Bind(R.id.address_tv)
    TextView addressTv;
    @Bind(R.id.sex_tv)
    TextView sexTv;
    @Bind(R.id.state1_tv)
    TextView state1Tv;
    @Bind(R.id.state2_tv)
    TextView state2Tv;
    @Bind(R.id.state3_tv)
    TextView state3Tv;
    @Bind(R.id.ask_lv)
    TotalListView askLv;
    @Bind(R.id.choice_person_btn)
    Button choicePersonBtn;
    List<AskModel> askModels = new ArrayList<>();
    List<WantModel> wantModels = new ArrayList<>();
    CommonAdapter<AskModel> adapter;
    UserModel now_user;
    @Bind(R.id.ask_ll)
    LinearLayout askLl;
    @Bind(R.id.ask_et)
    EditText askEt;
    @Bind(R.id.send_ask_btn)
    Button sendAskBtn;

    @Override
    public int setLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initViews() {
        adapter = new CommonAdapter<AskModel>(this, askModels, R.layout.item_ask) {
            @Override
            public void convert(CommonViewHolder holder, AskModel askModel, int position) {
                holder.setGliImage(R.id.user_iv, askModel.getUser().getImg());
                holder.setText(R.id.name_tv, askModel.getUser().getName());
                holder.setText(R.id.content_tv, askModel.getContent());
            }
        };
        askLv.setAdapter(adapter);
        order = (OrderModel) getIntent().getSerializableExtra("order_id");
        askLv.setOnItemClickListener((adapterView, view, i, l) -> {
            Utils.IntentPost(UserDetailActivity.class, intent -> intent.putExtra("user", askModels.get(i).getUser()));
        });
        refreshUI();
        refreshAsks();
    }

    String user_id;

    void refreshUI() {
        now_user = order.getUser();
        user_id = Utils.getCache("user_id");
        choicePersonBtn.setOnClickListener(view -> {
            switch (choicePersonBtn.getText().toString()) {
                case "想接此单":
                    BmobQuery<WantModel> query = new BmobQuery<WantModel>();
                    UserModel userModel = new UserModel();
                    userModel.setObjectId(user_id);
                    query.addWhereEqualTo("user", userModel);
                    query.addWhereEqualTo("order", order);
                    query.findObjects(new FindListener<WantModel>() {
                        @Override
                        public void done(List<WantModel> list, BmobException e) {
                            if (e == null && list.size() > 0) {
                                ToastShort("不能重复申请");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("确定要接此单吗？");
                                builder.setNegativeButton("确定", (dialogInterface, i) -> {
                                    WantModel wantModel = new WantModel();
                                    wantModel.setOrder(order);
                                    UserModel user = new UserModel();
                                    user.setObjectId(Utils.getCache("user_id"));
                                    wantModel.setUser(user);
                                    wantModel.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {//提交成功，刷新一下头像列表
                                                ToastShort("提交成功。");
                                            }
                                        }
                                    });
                                });
                                builder.setPositiveButton("取消", null);
                                builder.show();
                            }
                        }
                    });

                    break;
                case "等待送达":
                    break;
                case "配送完成":
                    if (("1").equals(order.getState())) {
                        order.setState("2");
                        order.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    totalRefresh();
                                    ToastShort("配送完成");
                                }
                            }
                        });
                    } else {
                        ToastShort("您已配送完成");
                    }
                    break;
                case "评价送单人":
                    if (("3").equals(order.getState())) {
                        ToastShort("您已评价此单");
                    } else {
                        EditText et = new EditText(this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("确定对送件员进行评价");
                        builder.setView(et, 20, 20, 20, 20);
                        builder.setNegativeButton("确定", (dialogInterface, i) -> {
                            order.setPingjia(et.getText().toString().trim());
                            order.setState("3");
                            order.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        totalRefresh();
                                        ToastShort("评价成功");
                                    }
                                }
                            });
                        });
                        builder.setPositiveButton("取消", null);
                        builder.show();
                    }

                    break;
                default://选择接单人--跳转到新页面选择完以后刷新状态
                    Intent intent = new Intent(OrderDetailActivity.this, WantJDActivity.class);
                    intent.putExtra("order_id", order.getObjectId());
                    startActivityForResult(intent, 9);
                    break;
            }
        });
        sendAskBtn.setOnClickListener(view -> {
            AskModel askModel = new AskModel();
            askModel.setOrder(order);
            UserModel userModel = new UserModel();
            userModel.setObjectId(Utils.getCache("user_id"));
            askModel.setUser(userModel);
            askModel.setContent(askEt.getText().toString().trim());
            askModel.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastShort("发送成功");
                        refreshAsks();
                    }
                }
            });
        });
        Glide.with(this)
                .load(order.getUser().getImg())
                .error(R.mipmap.user)
                .into(userIv);
        switch (order.getSex()) {
            case "1":
                sexIv.setImageResource(R.mipmap.nan);
                break;
            default:
                sexIv.setImageResource(R.mipmap.nv);
                break;
        }
        userNameTv.setText(order.getUser().getName());
        orderTimeTv.setText(order.getCreatedAt());
        orderTitleTv.setText(order.getTitle());
        priceTv.setText("￥" + order.getMoney());
        remarkTv.setText(order.getRemark());
        addressTv.setText(order.getAddress());
        switch (order.getSex()) {
            case "1":
                sexTv.setText("仅限男性");
                break;
            case "2":
                sexTv.setText("仅限女性");
                break;
            default:
                sexTv.setText("男女不限");
                break;
        }
        refreshState(order.getState());

    }

    void refreshState(String state) {
        switch (state) {//订单状态
            case "0":
                state1Tv.setVisibility(View.VISIBLE);
                state2Tv.setVisibility(View.GONE);
                state3Tv.setVisibility(View.GONE);
                if (user_id.equals(order.getUser().getObjectId())) {
                    choicePersonBtn.setText("选择接单人");
                } else {
                    choicePersonBtn.setText("想接此单");
                }
                break;
            case "1":
                state1Tv.setVisibility(View.VISIBLE);
                state2Tv.setVisibility(View.VISIBLE);
                state3Tv.setVisibility(View.GONE);
                if (user_id.equals(order.getUser().getObjectId())) {
                    choicePersonBtn.setText("等待送达");
                } else if (user_id.equals(order.getJd_user().getObjectId())) {
                    choicePersonBtn.setText("配送完成");
                } else {
                    choicePersonBtn.setText("其他人已接单");
                }
                break;
            default:
                state1Tv.setVisibility(View.VISIBLE);
                state2Tv.setVisibility(View.VISIBLE);
                state3Tv.setVisibility(View.VISIBLE);
                if (user_id.equals(order.getUser().getObjectId())) {
                    choicePersonBtn.setText("评价送单人");
                } else {
                    choicePersonBtn.setText("配送完成");
                }

                break;
        }
    }

    /**
     * 刷新一下回复列表
     */
    void refreshAsks() {
        askEt.setText("");
        BmobQuery<AskModel> query = new BmobQuery<>();
        query.addWhereEqualTo("order", order);
        query.order("-createdAt");
        query.include("user");
        query.findObjects(new FindListener<AskModel>() {
            @Override
            public void done(List<AskModel> list, BmobException e) {
                if (e == null) {
                    askModels = list;
                    adapter.refresh(askModels);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 66) {
            totalRefresh();
        }
    }

    /**
     * 整体数据刷新
     */
    void totalRefresh() {
        BmobQuery<OrderModel> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", order.getObjectId());
        query.include("user");
        query.findObjects(new FindListener<OrderModel>() {
            @Override
            public void done(List<OrderModel> list, BmobException e) {
                if (e == null) {
                    order = list.get(0);
                    refreshUI();
                }
            }
        });
    }

    @Override
    public void initEvents() {
        toolbar.setLeftClick(() -> finish());
    }
}
