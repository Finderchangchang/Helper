package wai.school.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import net.tsz.afinal.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import me.iwf.photopicker.PhotoPicker;
import wai.school.BaseActivity;
import wai.school.R;
import wai.school.method.Utils;
import wai.school.model.UserModel;

/**
 * 编辑个人信息
 */
public class UserEditActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    TitleBar toolbar;
    @Bind(R.id.img_iv)
    ImageView imgIv;
    @Bind(R.id.img_ll)
    LinearLayout imgLl;
    @Bind(R.id.name_et)
    EditText nameEt;
    @Bind(R.id.nan_rb)
    RadioButton nanRb;
    @Bind(R.id.nv_rb)
    RadioButton nvRb;
    @Bind(R.id.sex_rg)
    RadioGroup sexRg;
    @Bind(R.id.school_et)
    EditText schoolEt;
    @Bind(R.id.address_et)
    EditText addressEt;
    @Bind(R.id.remark_et)
    EditText remarkEt;
    UserModel userModel;

    @Override
    public int setLayout() {
        return R.layout.activity_user_edit;
    }

    @Override
    public void initViews() {
        nanRb.setChecked(true);
        userModel = (UserModel) getIntent().getSerializableExtra("user");
        BmobQuery<UserModel> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", Utils.getCache("user_id"));
        imgLl.setOnClickListener(view -> {//选择头像操作
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(false)
                    .start(this, PhotoPicker.REQUEST_CODE);
        });
        query.findObjects(new FindListener<UserModel>() {
            @Override
            public void done(List<UserModel> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    UserModel model = list.get(0);//查询出当前用户的数据并在页面进行显示
                    nameEt.setText(model.getName());
                    Glide.with(UserEditActivity.this)
                            .load(model.getImg()).error(R.mipmap.user)
                            .into(imgIv);
                    switch (model.getSex()) {
                        case "2":
                            nvRb.setChecked(true);
                            break;
                        default:
                            nanRb.setChecked(true);
                            break;
                    }
                    schoolEt.setText(model.getSchool());
                    addressEt.setText(model.getAddress());
                    remarkEt.setText(model.getRemark());
                }
            }
        });
        toolbar.setLeftClick(() -> finish());
        toolbar.setRightClick(() -> {//执行保存操作
            String name = nameEt.getText().toString().trim();
            String school = schoolEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();
            String remark = remarkEt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastShort("昵称不能为空");
            } else if (TextUtils.isEmpty(school)) {
                ToastShort("大学不能为空");
            } else if (TextUtils.isEmpty(address)) {
                ToastShort("宿舍楼不能为空");
            } else {
                userModel.setName(name);
                userModel.setSchool(school);
                userModel.setAddress(address);
                userModel.setRemark(remark);
                switch (sexRg.getCheckedRadioButtonId()) {
                    case R.id.nan_rb:
                        userModel.setSex("1");
                        break;
                    default:
                        userModel.setSex("2");
                        break;
                }
                BmobUser user = BmobUser.getCurrentUser();
                if (user == null) {//修改当前用户信息(先检测是否处于登录状态，登录了执行save，未登录在重新登录一下)
                    BmobUser.loginByAccount(Utils.getCache("user_tel"), Utils.getCache("user_pwd"), new LogInListener<Object>() {
                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                save();
                            } else {
                                ToastShort("请检查网络连接");
                            }
                        }
                    });
                } else {
                    save();
                }
            }
        });
    }

    /**
     * 修改保存用户信息功能
     */
    void save() {
        userModel.update(userModel.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastShort("保存成功");
                    finish();
                    if (MainActivity.admin != null) {
                        MainActivity.admin.finish();
                    }
                    Utils.IntentPost(MainActivity.class);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos.size() > 0) {
                    final String[] filePaths = new String[1];//选择或者拍的头像信息，并上传到bmob服务器上
                    filePaths[0] = photos.get(0);
                    imgIv.setImageBitmap(Utils.getBitmapByFile(photos.get(0)));
                    BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                            if (urls.size() == 1) {
                                userModel.setImg(urls.get(0));
                            }
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            ToastShort("上传失败，请重试");
                        }

                        @Override
                        public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                            //1、curIndex--表示当前第几个文件正在上传
                            //2、curPercent--表示当前上传文件的进度值（百分比）
                            //3、total--表示总的上传文件数
                            //4、totalPercent--表示总的上传进度（百分比）
                        }
                    });
                }
            }
        }
    }

    @Override
    public void initEvents() {

    }
}
