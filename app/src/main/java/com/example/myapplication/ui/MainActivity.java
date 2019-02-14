package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.util.Constants;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  @Inject
  DataManager dataManager;

  CompositeDisposable compositeDisposable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    compositeDisposable.add(
        dataManager
        .getPopularMovies(Constants.LANGUAGE_US_EN, 1)
        .subscribe(movies -> {
          Log.d("application", new GsonBuilder().create().toJson(movies));
        }, ex -> {
          Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
          ex.printStackTrace();
        })
    );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
  }
}
