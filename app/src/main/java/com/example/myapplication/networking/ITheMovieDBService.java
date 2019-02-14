package com.example.myapplication.networking;

import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.util.Constants;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ITheMovieDBService {
  String apiKey = Constants.TMDB_API_KEY;

  @GET("movie/popular?api_key=" + apiKey)
  Flowable<TheMovieDbObject> getPopularMovies(@Query("language") String language, @Query("page") int page);
}
