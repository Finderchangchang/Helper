package wai.school.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.tsz.afinal.view.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.Utils;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.pwd_et)
    EditText pwdEt;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.go_reg_tv)
    TextView go_reg_tv;
    @Bind(R.id.toolbar)
    TitleBar toolbar;

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews() {
        go_reg_tv.setOnClickListener(view -> startActivityForResult(new Intent(LoginActivity.this, RegUserActivity.class), 0));
        toolbar.setLeftClick(() -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 77:
                String tel = data.getStringExtra("tel");
                String pwd = data.getStringExtra("pwd");
                telEt.setText(tel);
                pwdEt.setText(pwd);
                pwdEt.setSelection(pwd.length());
                break;
        }
    }

    @Override
    public void initEvents() {
        loginBtn.setOnClickListener(view -> {
            String tel = telEt.getText().toString().trim();
            String pwd = pwdEt.getText().toString().trim();
            if (TextUtils.isEmpty(tel)) {
                ToastShort("手机号码不能为空");
            } else if (TextUtils.isEmpty(pwd)) {
                ToastShort("密码不能为空");
            } else {
                BmobUser.loginByAccount(tel, pwd, new LogInListener<BmobUser>() {
                    @Override
                    public void done(BmobUser user, BmobException e) {
                        if (user != null) {
                            Utils.putCache("user_id", user.getObjectId());
                            Utils.putCache("user_tel", user.getUsername());
                            Utils.putCache("user_pwd", pwd);

                            if (MainActivity.admin != null) {
                                MainActivity.admin.finish();
                            }
                            Utils.IntentPost(MainActivity.class);
                            finish();
                        } else {
                            ToastShort(e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
