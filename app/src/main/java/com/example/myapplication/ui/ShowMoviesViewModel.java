package com.example.myapplication.ui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.repository.MoviesDataRepository;
import com.example.myapplication.repository.MoviesRepository;
import com.example.myapplication.util.Constants;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

public class ShowMoviesViewModel extends AndroidViewModel {
  @Inject
  MoviesDataRepository moviesDataRepository;
  CompositeDisposable compositeDisposable = new CompositeDisposable();
  Context context;
  private MutableLiveData<List<TheMovieDbObject.MovieData>> movieList = new MutableLiveData<>();

  public ShowMoviesViewModel(@NonNull Application application) {
    super(application);
    MovieDbApplication context = (MovieDbApplication) application;
    context.getAppComponent().inject(this);
    this.context = context;
  }

  public void getPopularMovies(String language, int page){
    compositeDisposable.add(
        moviesDataRepository
        .getPopularMovies(language, page)
        .subscribe(movies -> {
          movieList.setValue(movies.getResults());
        }, ex -> {
          Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
          ex.printStackTrace();
        })
    );
  }

  public LiveData<List<TheMovieDbObject.MovieData>> getMovieList(){
    return movieList;
  }
}
