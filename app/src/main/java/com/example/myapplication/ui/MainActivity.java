package com.example.myapplication.ui;

import androidx.annotation.Nullable;
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
import com.annimon.stream.function.Function;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.MovieData;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.room.MovieDao;
import com.example.myapplication.room.RoomDbRepository;
import com.example.myapplication.ui.grid.CustomGridAdapter;
import com.example.myapplication.ui.moviedetails.ShowMovieDetailsActivity;
import com.example.myapplication.util.Constants;
import com.example.myapplication.util.StringUtils;
import com.example.myapplication.util.Utils;
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
  List<MovieData> movies, favMovies, allMovies;
  List<Integer> favIds;
  CustomGridAdapter.SortOptions filterSelected;
  ShowMoviesViewModel showMoviesViewModel;
  CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  RoomDbRepository<MovieData, MovieDao> roomDbMovieRepository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // bind
    ButterKnife.bind(this);
    // inject
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    filterSelected = CustomGridAdapter.SortOptions.POPULARITY;
    sortOptions = new ArrayList<>(Arrays.asList(CustomGridAdapter.SortOptions.values()));
    showMoviesViewModel = ViewModelProviders.of(this).get(ShowMoviesViewModel.class);
    //setUpFavoritesView();
    setUpViewModel();
    getMovies();
  }


  private void setUpFavoritesView() {
    compositeDisposable.add(
      roomDbMovieRepository
        .getAll()
        .subscribe(movieList -> {
          favMovies = new ArrayList<>(movieList);
          favIds = new ArrayList<>(Stream.of(favMovies).map(MovieData::getId).toList());
          movies = new ArrayList<>(favMovies);
          if (allMovies != null) {
            movies.addAll(Stream.of(allMovies).filter(m -> favIds.indexOf(m.getId()) == -1).toList());
          }
          if (adapter == null) {
            adapter = new CustomGridAdapter(this, movies, favIds);
            gridView.setAdapter(adapter);
            gridView.setVisibility(View.VISIBLE);
          } else
            adapter.refreshData(movies, favIds);
        })
    );
  }

  private void setUpViewModel() {
    setUpFavoritesView();
    showMoviesViewModel.getMovieList().observe(this, items -> {
      if (items != null) {
        if (items.isSuccess()) {
          compositeDisposable.clear();
          allMovies = new ArrayList<>(items.getData());
          setUpFavoritesView();
          progressBar.setVisibility(View.GONE);
          dataLoaded = true;
          invalidateOptionsMenu();
        } else if (items.getError().equals(CONNECTION_ISSUE)) {
          handleLayout(true, null);
        } else {
          handleLayout(true, getString(R.string._something_went_wrong_please_try_again));
        }
      } else
        handleLayout(true, getString(R.string._something_went_wrong_please_try_again));
    });
  }

  void handleLayout(boolean showErrorLayout, String errorMessage){
    if (showErrorLayout) {
      new MaterialDialog.Builder(this)
        .title(R.string._error)
        .content(StringUtils.nullOrEmpty(errorMessage) ? getString(R.string.no_internet): errorMessage)
        .positiveText(R.string._try_again)
        .negativeText(R.string._use_offline)
        .onPositive((__, ___) -> getMovies())
        .onNegative((dialog1, ___) -> {
          dialog1.dismiss();
          progressBar.setVisibility(View.GONE);
        })
        .canceledOnTouchOutside(false)
        .show();
    }
  }

  @OnClick(R.id.retry_btn)
  void getMovies() {
    handleLayout(false, null);
    getMostPopularMovies();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
    roomDbMovieRepository.clearSubscriptions();
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
        .itemsCallbackSingleChoice(sortOptions.indexOf(filterSelected), (dialog, view, which, text) -> true)
        .positiveText(getString(R.string._ok))
        .onPositive((dialog, which) -> getData(sortOptions.get(dialog.getSelectedIndex())))
        .neutralText(getString(R.string._cancel))
        .onNeutral((dialog, which) -> dialog.dismiss())
        .show();
  }

  void getMostPopularMovies(){
    showMoviesViewModel.getPopularMovies(Constants.LANGUAGE_US_EN, Constants.POPULAR_MOVIES_PAGE);
  }

  void getTopRatedMovies(){
    progressBar.setVisibility(View.VISIBLE);
    if (favMovies.size() == 0)
      gridView.setVisibility(View.GONE);
    showMoviesViewModel.getTopRatedMovies(Constants.LANGUAGE_US_EN, Constants.POPULAR_MOVIES_PAGE);
  }

  void getData(CustomGridAdapter.SortOptions option){
    switch (option){
      case RATING:
        getTopRatedMovies();
        filterSelected = CustomGridAdapter.SortOptions.RATING;
        break;
      case POPULARITY:
        getMostPopularMovies();
        filterSelected = CustomGridAdapter.SortOptions.POPULARITY;
        break;
    }
  }

}
