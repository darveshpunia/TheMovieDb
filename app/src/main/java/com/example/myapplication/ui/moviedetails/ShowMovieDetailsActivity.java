package com.example.myapplication.ui.moviedetails;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.TheMovieDbObject;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

public class ShowMovieDetailsActivity extends AppCompatActivity {

  TheMovieDbObject.MovieData movie;
  final static String MOVIE_DETAILS = "MOVIE_DETAILS";

  public static Intent getStartActivity(Context context){
    return new Intent(context, ShowMovieDetailsActivity.class);
  }

  public static void startActivity(Context context, TheMovieDbObject.MovieData movieData){
    Intent i = getStartActivity(context);
    i.putExtra(MOVIE_DETAILS, Parcels.wrap(movieData));
    context.startActivity(i);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_movie_details);
    ButterKnife.bind(this);
    movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_DETAILS));
    if (movie == null)
      handleUnexpectedError();
    configureUI();
  }

  private void configureUI() {
    Log.d("darvesh", new GsonBuilder().create().toJson(movie));
  }

  void handleUnexpectedError(){
    Toast.makeText(this, R.string._something_went_wrong, Toast.LENGTH_LONG).show();
    finish();
  }

}
