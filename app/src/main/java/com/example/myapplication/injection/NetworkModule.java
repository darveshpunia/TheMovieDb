package com.example.myapplication.injection;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.networking.ITheMovieDBService;
import com.example.myapplication.repository.MoviesRepository;
import com.example.myapplication.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
  private final MovieDbApplication application;
  public NetworkModule(MovieDbApplication application) {
    this.application = application;
  }

  @Singleton
  @Provides
  OkHttpClient provideOkHttpClient(){
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    return new OkHttpClient.Builder().addInterceptor(logging).build();
  }

  @Singleton
  @Provides
  Retrofit provideRetrofit(OkHttpClient client) {
    return new Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build();
  }

  @Singleton
  @Provides
  ITheMovieDBService provideM99Service(Retrofit retrofit) {
    return retrofit.create(ITheMovieDBService.class);
  }

  @Singleton
  @Provides
  MoviesRepository provideMovieRepo() {
    return new MoviesRepository();
  }
}
