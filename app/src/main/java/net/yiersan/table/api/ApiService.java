package net.yiersan.table.api;

import com.jaydenxiao.common.basebean.BaseRespose;
import net.yiersan.table.bean.login.LoginResponse;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**

 */
public interface ApiService {

    @FormUrlEncoded
    @POST("api.php?m=users&a=login")
    Observable<BaseRespose<LoginResponse>> login(@Field("phone") String username, @Field("password") String password);



}
