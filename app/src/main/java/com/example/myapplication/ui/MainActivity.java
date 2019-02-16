package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.ui.grid.CustomGridAdapter;
import com.example.myapplication.ui.moviedetails.ShowMovieDetailsActivity;
import com.example.myapplication.util.Constants;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.gridView)
  GridView gridView;

  CustomGridAdapter adapter;
  List<CustomGridAdapter.SortOptions> sortOptions;
  List<TheMovieDbObject.MovieData> movies;

  ShowMoviesViewModel showMoviesViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    sortOptions = new ArrayList<>(Arrays.asList(CustomGridAdapter.SortOptions.values()));
    showMoviesViewModel = ViewModelProviders.of(this).get(ShowMoviesViewModel.class);
    showMoviesViewModel.getMovieList().observe(this, items -> {
      adapter = new CustomGridAdapter(this, items);
      adapter.sortFields(CustomGridAdapter.SortOptions.RATING, false);
      gridView.setAdapter(adapter);
      movies = new ArrayList<>(items);
    });
    gridView.setOnItemClickListener((parent, view, position, id) -> {
      ShowMovieDetailsActivity.startActivity(MainActivity.this, movies.get(position));
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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sort_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_sort:
        showSortDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showSortDialog() {
    new MaterialDialog.Builder(this)
        .title(R.string._select_sort_option)
        .items(Stream.of(sortOptions).map(CustomGridAdapter.SortOptions::getType).toList())
        .itemsCallbackSingleChoice(0,
            (dialog, view, which, text) -> true)
        .positiveText(R.string._increasing)
        .onPositive((dialog, which) -> adapter.sortFields(sortOptions.get(dialog.getSelectedIndex()), true))
        .neutralText(R.string._decreasing)
        .onNeutral((dialog, which) -> adapter.sortFields(sortOptions.get(dialog.getSelectedIndex()), false))
        .show();
  }
}
