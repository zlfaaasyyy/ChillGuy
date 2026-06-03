package com.example.chillguy.network;

import com.example.chillguy.model.DeezerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search")
    Call<DeezerResponse> searchTracks(@Query("q") String query, @Query("limit") int limit);
}