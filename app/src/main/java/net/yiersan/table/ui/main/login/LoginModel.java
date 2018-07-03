package net.yiersan.table.ui.main.login;

import com.jaydenxiao.common.basebean.BaseRespose;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.ToastUitl;

import net.yiersan.table.api.Api;
import net.yiersan.table.api.HostType;
import net.yiersan.table.bean.login.LoginResponse;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class LoginModel implements LoginContract.Model {
    @Override
    public Observable<LoginResponse> login(String username, String password) {
        return Api.getDefault(HostType.TYPE_COUNT_TEST).login(username,password).map(new Func1<BaseRespose<LoginResponse>, LoginResponse>() {

            @Override
            public LoginResponse call(BaseRespose<LoginResponse> loginResponseBaseRespose) {
                if (loginResponseBaseRespose == null) {
                    ToastUitl.showLong("接口错误");
                    return null;
                }
                LoginResponse data = loginResponseBaseRespose.data;
                if (data == null) {
                    ToastUitl.showLong(loginResponseBaseRespose.msg);
                    return null;
                }else {
                    return data;
                }
            }
        }).compose(RxSchedulers.<LoginResponse>io_main());
    }
}
