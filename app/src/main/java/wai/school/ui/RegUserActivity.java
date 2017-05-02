package wai.school.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import net.tsz.afinal.view.TitleBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.Utils;
import wai.school.model.UserModel;

public class RegUserActivity extends BaseActivity {
    @Bind(R.id.code_et)
    EditText codeEt;
    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.pwd_et)
    EditText pwdEt;
    @Bind(R.id.reg_btn)
    Button regBtn;

    @Override
    public int setLayout() {
        return R.layout.activity_reg_user;
    }

    @Override
    public void initViews() {
        toolbar.setLeftClick(() -> finish());
        regBtn.setOnClickListener(view -> {
            String code = codeEt.getText().toString().trim();
            String tel = telEt.getText().toString().trim();
            String pwd = pwdEt.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                ToastShort("邀请码不能为空");
            } else {
                BmobQuery<UserModel> query = new BmobQuery<UserModel>();
                query.addWhereEqualTo("yz_code", code);
                query.findObjects(new FindListener<UserModel>() {
                    @Override
                    public void done(List<UserModel> list, BmobException e) {
                        if (e == null && list.size() > 0) {
                            if (TextUtils.isEmpty(tel)) {
                                ToastShort("手机号码不能为空");
                            } else if (!Utils.isMobileNo(tel)) {
                                ToastShort("手机号码格式不正确");
                            } else if (TextUtils.isEmpty(pwd)) {
                                ToastShort("密码不能为空");
                            } else {
                                UserModel bu = new UserModel();
                                bu.setUsername(tel);
                                bu.setPassword(pwd);
                                bu.setYz_code(getRandom() + "");
                                bu.signUp(new SaveListener<BmobUser>() {
                                    @Override
                                    public void done(BmobUser s, BmobException e) {
                                        if (e == null) {
                                            ToastShort("注册成功");
                                            Intent intent = new Intent();
                                            intent.putExtra("tel", tel);
                                            intent.putExtra("pwd", pwd);
                                            setResult(77, intent);
                                            finish();
                                        } else {
                                            ToastShort(e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            ToastShort("验证码不正确");
                        }
                    }
                });
            }
        });
    }

    int getRandom() {
        return (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
    }

    @Override
    public void initEvents() {

    }
}
