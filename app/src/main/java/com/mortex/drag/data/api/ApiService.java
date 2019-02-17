package com.mortex.drag.data.api;

import com.mortex.drag.data.api.model.TimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Morteza Rahimi on 17,February,2019
 */
public interface ApiService {

    @GET(ApiConstants.API_URL)
    Call<TimeResponse> getTime();
}
