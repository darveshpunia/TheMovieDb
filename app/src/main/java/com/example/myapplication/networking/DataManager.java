package com.example.myapplication.networking;

import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.repository.MoviesRepository;
import com.example.myapplication.ui.grid.CustomGridAdapter;
import com.example.myapplication.util.Constants;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataManager {
  private ITheMovieDBService tmdbService;
  private MoviesRepository moviesRepository;

  @Inject
  public DataManager(ITheMovieDBService tmdbService, MoviesRepository moviesRepository) {
    this.moviesRepository = moviesRepository;
    this.tmdbService = tmdbService;
  }

  public Flowable<TheMovieDbObject> getPopularMovies(String language, int page){
    CustomGridAdapter.SortOptions type = CustomGridAdapter.SortOptions.POPULARITY;
    if (moviesRepository.isPageLoaded(page, type)){
      TheMovieDbObject object = new TheMovieDbObject();
      object.setPage(page);
      object.setResults(moviesRepository.getMoviesByPage(page, type));
      return Flowable.just(object);
    }

    return tmdbService
        .getPopularMovies(language, page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(m -> moviesRepository.add(m, type));
  }

  public Flowable<TheMovieDbObject> getTopRatedMovies(String language, int page){
    CustomGridAdapter.SortOptions type = CustomGridAdapter.SortOptions.RATING;
    if (moviesRepository.isPageLoaded(page, type)){
      TheMovieDbObject object = new TheMovieDbObject();
      object.setPage(page);
      object.setResults(moviesRepository.getMoviesByPage(page, type));
      return Flowable.just(object);
    }

    return tmdbService
        .getTopRatedMovies(language, page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(m -> moviesRepository.add(m, type));
  }
}
