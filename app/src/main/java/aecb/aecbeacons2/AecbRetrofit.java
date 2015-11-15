package aecb.aecbeacons2;

import android.app.Application;
import android.content.Intent;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;




import android.content.Intent;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;


import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public final class AecbRetrofit {

    public interface RmbxApiService {

//        @FormUrlEncoded
//        @POST("/api/v2/users/auth_token/")
//        void getToken(@Field("email") String email, @Field("password") String password,
//                      Callback<List<Map<String, String>>> callback);
//
//        @POST("/api/v2/employee_work_shifts/")
//        void createEmployeeWorkShift(@Body EmployeeWorkShift ews,
//                                     Callback<List<EmployeeWorkShift>> callback);
    }

    public RmbxApiService getService(Application application) {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("tranquil-sands-1255.herokuapp.com")
                .setClient(new OkClient())
                .build();

        RmbxApiService rmbxApiService = restAdapter.create(RmbxApiService.class);

        return rmbxApiService;
    }

}