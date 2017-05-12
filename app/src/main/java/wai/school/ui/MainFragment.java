package wai.school.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import net.tsz.afinal.view.TitleBar;
import net.tsz.afinal.view.TotalListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import wai.school.R;
import wai.school.method.CommonAdapter;
import wai.school.method.CommonViewHolder;
import wai.school.method.GlideImageLoader;
import wai.school.method.Utils;
import wai.school.model.OrderModel;
import wai.school.model.UserModel;

/**
 * Created by Finder丶畅畅 on 2017/3/12 22:47
 * QQ群481606175
 */

public class MainFragment extends Fragment {
    TitleBar toolbar;
    TextView userNameTv;
    ImageView userIv;
    TextView schoolTv;
    TextView addressTv;
    TextView orderNumTv;
    TextView remarkTv;
    TotalListView pl_lv;
    private int position = 0;
    ArrayList images;

    public static MainFragment newInstance(int content) {
        MainFragment fragment = new MainFragment();
        fragment.position = content;
        return fragment;
    }

    Banner banner;
    TabLayout main_tab;
    TitleBar add_toolbar;
    EditText add_title_et;
    EditText add_address_et;
    EditText add_yj_et;
    EditText add_remark_et;
    RadioGroup sex_rg;
    RadioButton bx_rn;
    RadioButton nan_rn;
    CommonAdapter<OrderModel> adapter;
    List<OrderModel> list;
    TotalListView main_ll;
    RelativeLayout title_rl;
    SwipeRefreshLayout main_srl;
    String title = "全部订单";
    ScrollView main_sv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (position) {
            case 0://首页
                list = new ArrayList<>();
                View view = inflater.inflate(R.layout.frag_main, container, false);
                main_sv = (ScrollView) view.findViewById(R.id.main_sv);
                main_srl = (SwipeRefreshLayout) view.findViewById(R.id.main_srl);
                adapter = new CommonAdapter<OrderModel>(MainActivity.admin, list, R.layout.item_main) {
                    @Override
                    public void convert(CommonViewHolder holder, OrderModel model, int position) {
                        holder.setGliImage(R.id.user_iv, model.getUser().getImg());
                        if (model.getUser().getName() != null) {
                            holder.setText(R.id.user_name_tv, model.getUser().getName());
                        } else {
                            holder.setText(R.id.user_name_tv, "未知");
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
                main_ll = (TotalListView) view.findViewById(R.id.main_ll);
                main_ll.setAdapter(adapter);
                main_tab = (TabLayout) view.findViewById(R.id.main_tab);
                main_tab.addTab(main_tab.newTab().setText("全部订单"));
                main_tab.addTab(main_tab.newTab().setText("未接订单"));
                banner = (Banner) view.findViewById(R.id.main_title_ban);
                images = new ArrayList<>();
                banner.setImageLoader(new GlideImageLoader());
                images.add("https://img6.bdstatic.com/img/image/smallpic/2.jpg");
                images.add("https://img6.bdstatic.com/img/image/smallpic/3.jpg");
                banner.setImages(images);
                banner.start();
                main_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        title = tab.getText().toString();
                        refresh();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
                main_ll.setOnItemClickListener((parent, view1, position1, id) -> {
                    String user_id = Utils.getCache("user_id");
                    if (TextUtils.isEmpty(user_id)) {
                        Utils.IntentPost(LoginActivity.class);
                    } else {
                        Utils.IntentPost(OrderDetailActivity.class, intent -> intent.putExtra("order_id", list.get(position1)));
                    }
                });
                refresh();
                main_srl.setOnRefreshListener(() -> {
                    main_srl.setRefreshing(false);
                    refresh();
                });
                main_ll.smoothScrollToPosition(0);
                return view;
            case 1:
                view = inflater.inflate(R.layout.frag_add_order, container, false);
                add_toolbar = (TitleBar) view.findViewById(R.id.toolbar);
                add_title_et = (EditText) view.findViewById(R.id.title_et);
                add_address_et = (EditText) view.findViewById(R.id.address_et);
                add_yj_et = (EditText) view.findViewById(R.id.yj_et);
                add_remark_et = (EditText) view.findViewById(R.id.remark_et);
                sex_rg = (RadioGroup) view.findViewById(R.id.sex_rg);
                nan_rn = (RadioButton) view.findViewById(R.id.nan_rn);
                bx_rn = (RadioButton) view.findViewById(R.id.bx_rn);
                bx_rn.setChecked(true);
                add_toolbar.setRightClick(() -> {
                    String user_id = Utils.getCache("user_id");
                    if (TextUtils.isEmpty(user_id)) {
                        Utils.IntentPost(LoginActivity.class);
                    } else {
                        BmobQuery<UserModel> query = new BmobQuery<>();
                        query.addWhereEqualTo("objectId", user_id);
                        query.findObjects(new FindListener<UserModel>() {
                            @Override
                            public void done(List<UserModel> list, BmobException e) {
                                if (list != null && list.size() > 0) {
                                    model = list.get(0);
                                    if (TextUtils.isEmpty(model.getSex())) {
                                        MainActivity.admin.ToastShort("发单之前必须完善个人信息");
                                        Utils.IntentPost(UserEditActivity.class, intent -> intent.putExtra("user", model));
                                    } else {
                                        String title = add_title_et.getText().toString().trim();
                                        String address = add_address_et.getText().toString().trim();
                                        String yj = add_yj_et.getText().toString().trim();
                                        String remark = add_remark_et.getText().toString().trim();
                                        if (TextUtils.isEmpty(title)) {
                                            MainActivity.admin.ToastShort("标题不能为空");
                                        } else if (TextUtils.isEmpty(address)) {
                                            MainActivity.admin.ToastShort("送达地址不能为空");
                                        } else if (TextUtils.isEmpty(yj)) {
                                            MainActivity.admin.ToastShort("佣金不能为空");
                                        } else {
                                            OrderModel model = new OrderModel();
                                            model.setTitle(title);
                                            model.setAddress(address);
                                            model.setMoney(yj);
                                            model.setRemark(remark);
                                            switch (sex_rg.getCheckedRadioButtonId()) {
                                                case R.id.nan_rn:
                                                    model.setSex("1");
                                                    break;
                                                case R.id.nv_rn:
                                                    model.setSex("2");
                                                    break;
                                                case R.id.bx_rn:
                                                    model.setSex("0");
                                                    break;
                                            }
                                            model.setState("0");
                                            UserModel user = new UserModel();
                                            user.setObjectId(Utils.getCache("user_id"));
                                            model.setUser(user);
                                            model.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e == null) {
                                                        MainActivity.admin.ToastShort("发布成功");
                                                    } else {
                                                        MainActivity.admin.ToastShort("发布失败");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            default:
                view = inflater.inflate(R.layout.frag_user_detail, container, false);
                title_rl = (RelativeLayout) view.findViewById(R.id.title_rl);
                toolbar = (TitleBar) view.findViewById(R.id.toolbar);
                userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
                userIv = (ImageView) view.findViewById(R.id.user_iv);
                schoolTv = (TextView) view.findViewById(R.id.school_tv);
                addressTv = (TextView) view.findViewById(R.id.address_tv);
                orderNumTv = (TextView) view.findViewById(R.id.order_num_tv);//得计算
                remarkTv = (TextView) view.findViewById(R.id.remark_tv);
                pl_lv = (TotalListView) view.findViewById(R.id.pl_lv);
                order_manage_tv = (TextView) view.findViewById(R.id.order_manage_tv);
                pj_manage_tv = (TextView) view.findViewById(R.id.pj_manage_tv);
                order_manage_tv.setOnClickListener(view1 -> Utils.IntentPost(OrderManagerActivity.class));
                pj_manage_tv.setOnClickListener(view1 -> Utils.IntentPost(AskManageActivity.class));
                String user_id = Utils.getCache("user_id");
                toolbar.setRightClick(() -> {
                    if (TextUtils.isEmpty(user_id)) {
                        Utils.IntentPost(LoginActivity.class);
                    } else {
                        Utils.IntentPost(UserEditActivity.class, intent -> intent.putExtra("user", model));
                    }
                });
                title_rl.setOnClickListener(v -> {
                    if (TextUtils.isEmpty(user_id)) {
                        Utils.IntentPost(LoginActivity.class);
                    }
                });
                BmobQuery<UserModel> query = new BmobQuery<>();
                query.addWhereEqualTo("objectId", user_id);
                query.findObjects(new FindListener<UserModel>() {
                    @Override
                    public void done(List<UserModel> list, BmobException e) {
                        if (list != null && list.size() > 0) {
                            model = list.get(0);
                            userNameTv.setText(model.getName());
                            if (!TextUtils.isEmpty(model.getSchool())) {
                                schoolTv.setText(model.getSchool());
                            }
                            if (!TextUtils.isEmpty(model.getAddress())) {
                                addressTv.setText(model.getAddress());
                            }
                            if (!TextUtils.isEmpty(model.getRemark())) {
                                remarkTv.setText(model.getRemark());
                            }
                            Glide.with(MainActivity.admin)
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

                        }
                    }
                });
                toolbar.setLeftClick(() -> {
                    Utils.putCache("user_id", "");
                    Utils.putCache("user_name", "");
                    UserModel.logOut();
                    MainActivity.admin.finish();
                });
                orderModels = new ArrayList<>();
                pj_adapter = new CommonAdapter<OrderModel>(MainActivity.admin, orderModels, R.layout.item_pj) {
                    @Override
                    public void convert(CommonViewHolder holder, OrderModel model, int position) {
                        holder.setText(R.id.name_tv, model.getPingjia());
                    }
                };
                pl_lv.setAdapter(pj_adapter);
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
                            pj_adapter.refresh(orderModels);
                            orderNumTv.setText(orderModels.size() + "件");
                        }
                    }
                });
                return view;
        }
    }

    TextView order_manage_tv;
    TextView pj_manage_tv;
    List<OrderModel> orderModels;
    CommonAdapter<OrderModel> pj_adapter;
    UserModel model;

    void refresh() {
        BmobQuery<OrderModel> query = new BmobQuery<>();
        query.order("-createdAt");
        query.include("user");
        switch (title) {
            case "全部订单":
                query.findObjects(new FindListener<OrderModel>() {
                    @Override
                    public void done(List<OrderModel> models, BmobException e) {
                        if (e == null) {
                            list = models;
                            adapter.refresh(list);
                            main_sv.scrollTo(0, 0);
                        }
                    }
                });
                break;
            default:
                query.addWhereEqualTo("state", "0");
                query.findObjects(new FindListener<OrderModel>() {
                    @Override
                    public void done(List<OrderModel> models, BmobException e) {
                        if (e == null) {
                            list = models;
                            adapter.refresh(list);
                            main_sv.scrollTo(0, 0);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
