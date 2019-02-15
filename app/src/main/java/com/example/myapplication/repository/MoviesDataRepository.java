package com.example.myapplication.repository;

import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.networking.DataManager;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Flowable;

public class MoviesDataRepository {
  private DataManager dataManager;

  @Inject
  MoviesDataRepository(DataManager dataManager){
    this.dataManager = dataManager;
  }

  public Flowable<TheMovieDbObject> getPopularMovies(String language, int page){
    return dataManager.getPopularMovies(language, page);
  }

}
