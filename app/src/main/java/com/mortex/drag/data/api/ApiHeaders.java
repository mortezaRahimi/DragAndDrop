package com.mortex.drag.data.api;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
final class ApiHeaders implements Interceptor {

    private final Application app;

    @Inject
    public ApiHeaders(Application app) {
        this.app = app;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().addHeader("Accept", "application/json")
//                .addHeader("app", "gifto")
                .build();
        if (isNetworkAvailable()) {
            int maxAge = 1; // read from cache for 1 minute
            chain.request().newBuilder().addHeader("Cache-Control", "public, max-age=" + maxAge);
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            chain.request().newBuilder().addHeader("Cache-Control",
                    "public, only-if-cached, max-stale=" + maxStale);
        }
        return chain.proceed(request);
    }

    /**
     * Is network available boolean.
     *
     * @return a boolean indicating if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
