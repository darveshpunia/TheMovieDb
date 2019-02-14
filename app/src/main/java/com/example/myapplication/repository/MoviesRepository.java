package com.example.myapplication.repository;

import android.util.Log;
import android.util.SparseArray;

import com.example.myapplication.models.TheMovieDbObject;

import java.util.ArrayList;
import java.util.List;

/*
In memory movies repository
 */
public class MoviesRepository {
  // keyed by movie id, to cache movies
  private SparseArray<TheMovieDbObject.MovieData> movies;
  // keyed by page number from tmdb, to cache data
  private SparseArray<List<Integer>> pageWithMoviesId;

  public MoviesRepository() {
    movies = new SparseArray<>();
    pageWithMoviesId = new SparseArray<>();
  }

  public void _addAll(List<TheMovieDbObject.MovieData> movieList){
    for (TheMovieDbObject.MovieData movie: movieList) {
      movies.put(movie.getId(), movie);
    }
  }

  public void add(TheMovieDbObject movieDbObject){
    int page = movieDbObject.getPage();
    List<Integer> movieIds = new ArrayList<>();
    for (TheMovieDbObject.MovieData movie: movieDbObject.getResults()) {
      movies.put(movie.getId(), movie);
      movieIds.add(movie.getId());
    }
    pageWithMoviesId.put(page, movieIds);
  }

  public boolean isPageLoaded(int page){
    return pageWithMoviesId.get(page) != null;
  }

  public List<TheMovieDbObject.MovieData> getMoviesByPage(int page){
    ArrayList<TheMovieDbObject.MovieData> movieData = new ArrayList<>();
    for(Integer movieId : pageWithMoviesId.get(page)){
      movieData.add(movies.get(movieId));
    }
    return movieData;
  }

}
