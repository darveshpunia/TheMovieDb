package com.example.myapplication.injection;

import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.ShowMoviesViewModel;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class})
public interface AppComponent {
  void inject(MainActivity mainActivity);
  void inject(ShowMoviesViewModel showMoviesViewModel);
}