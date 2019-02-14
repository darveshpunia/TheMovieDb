package com.example.myapplication.networking;

import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.repository.MoviesRepository;
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
    if (moviesRepository.isPageLoaded(page)){
      TheMovieDbObject object = new TheMovieDbObject();
      object.setPage(page);
      object.setResults(moviesRepository.getMoviesByPage(page));
      return Flowable.just(object);
    }

    return tmdbService
        .getPopularMovies(language, page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(moviesRepository::add);
  }
}
