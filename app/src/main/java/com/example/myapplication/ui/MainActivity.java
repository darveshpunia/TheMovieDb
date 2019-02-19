package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.ui.grid.CustomGridAdapter;
import com.example.myapplication.ui.moviedetails.ShowMovieDetailsActivity;
import com.example.myapplication.util.Constants;
import com.example.myapplication.util.StringUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.example.myapplication.ui.ShowMoviesViewModel.CONNECTION_ISSUE;

public class MainActivity extends BaseActivity {

  @BindView(R.id.grid_view)
  GridView gridView;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.layout_internet_issue)
  LinearLayout layoutInternetIssue;
  @BindView(R.id.text_error_message)
  TextView textErrorMessage;

  boolean dataLoaded;

  CustomGridAdapter adapter;
  List<CustomGridAdapter.SortOptions> sortOptions;
  List<TheMovieDbObject.MovieData> movies;

  ShowMoviesViewModel showMoviesViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // bind
    ButterKnife.bind(this);
    // inject
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    sortOptions = new ArrayList<>(Arrays.asList(CustomGridAdapter.SortOptions.values()));
    showMoviesViewModel = ViewModelProviders.of(this).get(ShowMoviesViewModel.class);
    setUpViewMode();
  }

  private void setUpViewMode() {
    showMoviesViewModel.getMovieList().observe(this, items -> {
      if (items.isSuccess()) {
        progressBar.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        dataLoaded = true;
        invalidateOptionsMenu();
        adapter = new CustomGridAdapter(this, items.getData());
        adapter.sortFields(CustomGridAdapter.SortOptions.RATING, false);
        gridView.setAdapter(adapter);
        movies = new ArrayList<>(items.getData());
      } else if (items.getError().equals(CONNECTION_ISSUE)) {
        handleLayout(true, null);
      } else {
        handleLayout(true, getString(R.string._something_went_wrong_please_try_again));
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    getMovies();
  }

  void handleLayout(boolean showErrorLayout, String errorMessage){
    layoutInternetIssue.setVisibility(showErrorLayout ? View.VISIBLE : View.GONE);
    progressBar.setVisibility(!showErrorLayout ? View.VISIBLE : View.GONE);
    if (!StringUtils.nullOrEmpty(errorMessage)) {
      textErrorMessage.setText(errorMessage);
    }
  }

  @OnClick(R.id.retry_btn)
  void getMovies() {
    handleLayout(false, null);
    showMoviesViewModel.getPopularMovies(Constants.LANGUAGE_US_EN, Constants.POPULAR_MOVIES_PAGE);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (dataLoaded)
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
        .items(Stream.of(sortOptions).map(option -> getString(option.getStringResId())).toList())
        .itemsCallbackSingleChoice(0,
            (dialog, view, which, text) -> true)
        .positiveText(R.string._increasing)
        .onPositive((dialog, which) -> adapter.sortFields(sortOptions.get(dialog.getSelectedIndex()), true))
        .neutralText(R.string._decreasing)
        .onNeutral((dialog, which) -> adapter.sortFields(sortOptions.get(dialog.getSelectedIndex()), false))
        .show();
  }
}
