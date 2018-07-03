package net.yiersan.table.ui.main.login;

import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.commonutils.ToastUitl;

import net.yiersan.table.bean.login.LoginResponse;

public class LoginPersenter extends LoginContract.Presenter {
    @Override
    public void loginRequest(String username, String password) {
        mRxManage.add(mModel.login(username,password).subscribe(new RxSubscriber<LoginResponse>(mContext) {
            @Override
            protected void _onNext(LoginResponse loginResponse) {
                if (loginResponse != null){
                    ToastUitl.showLong("success");
                }
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }
}
