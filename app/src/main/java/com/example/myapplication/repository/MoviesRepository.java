package com.example.myapplication.repository;

import android.util.Log;
import android.util.SparseArray;

import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.ui.grid.CustomGridAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
In memory movies repository
 */
public class MoviesRepository {

  // keyed by movie id, to cache movies
  private SparseArray<TheMovieDbObject.MovieData> movies;
  // keyed by type of sort option, to cache data
  private HashMap<CustomGridAdapter.SortOptions, SparseArray<List<Integer>>> pageWithMoviesId;

  public MoviesRepository() {
    movies = new SparseArray<>();
    pageWithMoviesId = new HashMap<>();
  }

  public void _addAll(List<TheMovieDbObject.MovieData> movieList){
    for (TheMovieDbObject.MovieData movie: movieList) {
      movies.put(movie.getId(), movie);
    }
  }

  public void add(TheMovieDbObject movieDbObject, CustomGridAdapter.SortOptions option){
    int page = movieDbObject.getPage();
    List<Integer> movieIds = new ArrayList<>();
    for (TheMovieDbObject.MovieData movie: movieDbObject.getResults()) {
      movies.put(movie.getId(), movie);
      movieIds.add(movie.getId());
    }
    SparseArray<List<Integer>> array = new SparseArray<>();
    array.put(page, movieIds);
    pageWithMoviesId.put(option, array);
  }

  public boolean isPageLoaded(int page, CustomGridAdapter.SortOptions option){
    return pageWithMoviesId.get(option) != null && pageWithMoviesId.get(option).get(page) != null;
  }

  public List<TheMovieDbObject.MovieData> getMoviesByPage(int page, CustomGridAdapter.SortOptions options){
    ArrayList<TheMovieDbObject.MovieData> movieData = new ArrayList<>();
    for(Integer movieId : pageWithMoviesId.get(options).get(page)){
      movieData.add(movies.get(movieId));
    }
    return movieData;
  }

}
