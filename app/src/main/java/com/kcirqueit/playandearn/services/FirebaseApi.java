package com.kcirqueit.playandearn.services;

import com.kcirqueit.playandearn.model.Quiz;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FirebaseApi {

    @FormUrlEncoded
    @POST("send")
    Call<ResponseBody> sendRequest(
            @Field("topic") String topic,
            @Field("title") String title,
            @Field("body") String body
    );


}
