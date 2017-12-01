package com.zhp.facedetect.net;

import com.being.base.http.retrofit.calladapter.BaseCall;
import com.zhp.facedetect.net.response.FaceidentifyResponse;
import com.zhp.facedetect.net.response.NewPersonResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by zhangpeng on 2017/11/30.
 */

public interface Apis {

    @Headers("Content-Type:text/json")
    @POST(Command.faceidentify)
    BaseCall<FaceidentifyResponse> faceIdentify(@Body RequestBody body);


    @POST(Command.newperson)
    BaseCall<NewPersonResponse> newperson(@Body RequestBody body);
}
