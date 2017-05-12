package wai.school.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.MainAdapter;
import wai.school.method.Utils;
import wai.school.model.UserModel;

public class MainActivity extends BaseActivity {

    private MainAdapter mAdapter;
    @Bind(R.id.tab_pager)
    ViewPager tabPager;
    @Bind(R.id.bottom_tab)
    TabLayout bottomTab;//底部切换菜单
    public static MainActivity admin;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        mAdapter = new MainAdapter(getSupportFragmentManager());
        tabPager.setAdapter(mAdapter);
        tabPager.setOffscreenPageLimit(3);//预加载出3个fragment页面
        bottomTab.setupWithViewPager(tabPager);
        admin = this;
    }

    @Override
    public void initEvents() {

    }
}
