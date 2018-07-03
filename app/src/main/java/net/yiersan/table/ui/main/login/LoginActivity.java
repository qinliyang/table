package net.yiersan.table.ui.main.login;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;

import net.yiersan.table.R;
import net.yiersan.table.bean.login.LoginResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPersenter,LoginModel> implements LoginContract.View {
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.text_view)
    TextView textView;
    @Bind(R.id.activity_login)
    LinearLayout activityLogin;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {
            mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {

    }

    @Override
    public void onSuccess(LoginResponse loginResponse) {

    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {
    }


    @OnClick(R.id.login)
    public void onViewClicked() {
        String s = username.getText().toString();
        String s1 = password.getText().toString();
        mPresenter.loginRequest(s,s1);
        Log.d("logger","username--->" + username);
        LogUtils.logd("username--->" + username);
        LogUtils.logd("password--->" + password);

    }
}
