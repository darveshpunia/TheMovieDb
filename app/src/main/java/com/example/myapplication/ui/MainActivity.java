package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.ui.grid.CustomGridAdapter;
import com.example.myapplication.util.Constants;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.gridView)
  GridView gridView;

  CustomGridAdapter adapter;

  ShowMoviesViewModel showMoviesViewModel;
  CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    showMoviesViewModel = ViewModelProviders.of(this).get(ShowMoviesViewModel.class);
    showMoviesViewModel.getMovieList().observe(this, items -> {
      adapter = new CustomGridAdapter(this, items);
      adapter.sortFields(CustomGridAdapter.SortOptions.RATING, false);
      gridView.setAdapter(adapter);
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    showMoviesViewModel.getPopularMovies(Constants.LANGUAGE_US_EN, 1);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
  }
}
