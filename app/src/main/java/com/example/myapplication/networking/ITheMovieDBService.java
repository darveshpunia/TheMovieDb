package com.example.myapplication.networking;

import com.example.myapplication.models.Review;
import com.example.myapplication.models.ReviewsModel;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.models.VideoModel;
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

  @GET("movie/top_rated?api_key=" + apiKey)
  Flowable<TheMovieDbObject> getTopRatedMovies(@Query("language") String language, @Query("page") int page);

  @GET("movie/{movie_id}/reviews?api_key=" + apiKey)
  Flowable<ReviewsModel<Review>> getMovieReviews(@Path("movie_id") int movieId, @Query("language") String language, @Query("page") int page);

  @GET("movie/{movie_id}/videos?api_key=" + apiKey)
  Flowable<ReviewsModel<VideoModel>> getMovieVideos(@Path("movie_id") int movieId, @Query("language") String language, @Query("page") int page);
}
