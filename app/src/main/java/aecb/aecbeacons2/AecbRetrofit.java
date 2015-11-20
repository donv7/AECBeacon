package aecb.aecbeacons2;

import android.app.Application;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public final class AecbRetrofit {

    public interface AecbApiService {

        @Multipart
        @POST("/location_pics.json")
        void postImage(@Part("location_pic[image]") TypedFile photo,
                            @Part("location_pic[beacon]") String beaconName,
                            Callback<Map<String, String>> callback);

        @GET("/location_pics.json")
        void getImages(Callback<List<AecbImage>> callback);

    }

    public AecbApiService getService(Application application) {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ec2-52-91-98-4.compute-1.amazonaws.com:3000")
                .setClient(new OkClient())
                .build();

        AecbApiService rmbxApiService = restAdapter.create(AecbApiService.class);

        return rmbxApiService;
    }

}