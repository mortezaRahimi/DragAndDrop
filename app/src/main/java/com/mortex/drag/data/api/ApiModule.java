package com.mortex.drag.data.api;
import android.app.Application;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.internal.JavaNetCookieJar;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class ApiModule {

  static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);

  /**
   * Create ok http client ok http client.
   *
   * @param app the app
   * @return the ok http client
   */

  static OkHttpClient createOkHttpClient(Application app, ApiHeaders headers) {
    File cacheDir = new File(app.getCacheDir(), "http");
    Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

    CookieManager cookieManager = new CookieManager();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

    OkHttpClient client = new OkHttpClient.Builder()
        .cookieJar(new JavaNetCookieJar(cookieManager))
        .addInterceptor(headers)
        .writeTimeout(45,SECONDS)
        .readTimeout(45,SECONDS)
        .connectTimeout(45,SECONDS)
        .cache(cache)
        .build();

    return client;
  }

  @Provides
  @Singleton
  Retrofit provideMainRestAdapter(OkHttpClient client) {
    return new Retrofit.Builder()
        .client(client)
        .baseUrl(ApiConstants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  @Provides
  @Singleton
  ApiService provideAuthenticationService(Retrofit restAdapter) {
    return restAdapter.create(ApiService.class);
  }

  @Provides
  @Singleton
  OkHttpClient provideOkHttpClient(Application app, ApiHeaders headers) {
    return createOkHttpClient(app,headers);
  }
}
