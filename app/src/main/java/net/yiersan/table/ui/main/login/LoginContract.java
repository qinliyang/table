package net.yiersan.table.ui.main.login;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;
import com.jaydenxiao.common.basebean.BaseRespose;

import net.yiersan.table.bean.login.LoginResponse;

import rx.Observable;

public interface LoginContract {

        interface Model extends BaseModel {
            Observable<LoginResponse> login(String username,String password);
        }

        interface View extends BaseView {
            void onSuccess(LoginResponse loginResponse);
        }
        abstract static class Presenter extends BasePresenter<View, Model> {
            public abstract void loginRequest(String username,String password);
        }
}
