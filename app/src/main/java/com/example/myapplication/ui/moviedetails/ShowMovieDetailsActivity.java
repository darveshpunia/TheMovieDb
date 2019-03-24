package com.example.myapplication.ui.moviedetails;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.MovieData;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.room.MovieDao;
import com.example.myapplication.room.RoomDbRepository;
import com.example.myapplication.util.Constants;
import com.example.myapplication.util.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import javax.inject.Inject;

public class ShowMovieDetailsActivity extends AppCompatActivity {

  MovieData movie;
  final static String MOVIE_DETAILS = "MOVIE_DETAILS";

  @BindView(R.id.image)
  ImageView imageView;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.app_bar_layout)
  AppBarLayout appBarLayout;
  @BindView(R.id.collapsing_toolbar)
  CollapsingToolbarLayout collapsingToolbarLayout;

  @BindView(R.id.text_over_view)
  TextView textOverview;
  @BindView(R.id.text_rating)
  TextView textRating;
  @BindView(R.id.text_title)
  TextView textTitle;
  @BindView(R.id.text_release_date)
  TextView textReleaseDate;
  @BindView(R.id.rating_bar)
  RatingBar ratingBar;
  @BindView(R.id.fab_save_data)
  FloatingActionButton fab;

  @Inject
  RoomDbRepository<MovieData, MovieDao> roomDbMovieRepository;

  CompositeDisposable compositeDisposable = new CompositeDisposable();

  public static Intent getStartActivity(Context context){
    return new Intent(context, ShowMovieDetailsActivity.class);
  }

  public static void startActivity(Context context, MovieData movieData){
    Intent i = getStartActivity(context);
    i.putExtra(MOVIE_DETAILS, Parcels.wrap(movieData));
    context.startActivity(i);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_movie_details);
    ButterKnife.bind(this);
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_DETAILS));
    if (movie == null)
      handleUnexpectedError();
    configureToolbar();
    configureUI();
  }

  private void configureToolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    setUpToolBarTitle();
  }

  /*
  This function basically shows title only when the toolbar is in collapsed state.
  For more info:
  https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed
   */
  private void setUpToolBarTitle() {
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          collapsingToolbarLayout.setTitle(movie.getTitle());
          isShow = true;
        } else if(isShow) {
          collapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });
  }

  private void configureUI() {
    Picasso.get()
        .load(Constants.TMDB_IMAGE_PATH + movie.getPoster_path())
        .placeholder(R.drawable.image_placeholder)
        .error(R.drawable.image_error)
        .into(imageView);
    textTitle.setText(movie.getTitle());
    textReleaseDate.setText("Release Date: " + movie.getRelease_date());
    textRating.setText("(" + String.valueOf(movie.getVote_average()/2) + ")");
    textOverview.setText(movie.getOverview());
    ratingBar.setRating(movie.getVote_average() / 2);
    compositeDisposable.add(
      roomDbMovieRepository
        .get(movie.getId())
        .subscribe(movie -> {
          fab.setImageResource(R.drawable.ic_favorite_black_24dp);
        }, Utils::captureException)
    );
  }

  @OnClick(R.id.fab_save_data)
  void saveDataInRoom(){
    compositeDisposable.add(
      roomDbMovieRepository
        .get(movie.getId())
        .subscribe(movie -> {
            roomDbMovieRepository.delete(movie.getId(), ()->{
              Toast.makeText(getApplicationContext(), "Movie removed from Favorites", Toast.LENGTH_LONG).show();
              fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            });
        }, Utils::captureException,
        () -> {
          roomDbMovieRepository.insert(movie, () -> {
            Toast.makeText(getApplicationContext(), "Movie added to Favorites", Toast.LENGTH_LONG).show();
            fab.setImageResource(R.drawable.ic_favorite_black_24dp);
          });
        })
    );
  }

  void handleUnexpectedError(){
    Toast.makeText(this, R.string._something_went_wrong, Toast.LENGTH_LONG).show();
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
    roomDbMovieRepository.clearSubscriptions();
  }
}
