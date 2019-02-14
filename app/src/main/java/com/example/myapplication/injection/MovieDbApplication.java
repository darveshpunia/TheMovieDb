package com.example.myapplication.injection;

import android.app.Application;

public class MovieDbApplication extends Application {
  private AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public AppComponent getAppComponent() {
    if (appComponent == null) {
      appComponent = DaggerAppComponent.builder()
          .networkModule(new NetworkModule((MovieDbApplication) this.getApplicationContext()))
          .build();
    }
    return appComponent;
  }
}
