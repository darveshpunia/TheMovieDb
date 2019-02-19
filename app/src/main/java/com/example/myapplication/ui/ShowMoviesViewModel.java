package com.example.myapplication.ui;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.models.ApiResponse;
import com.example.myapplication.repository.MoviesDataRepository;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

public class ShowMoviesViewModel extends AndroidViewModel {
  @Inject
  MoviesDataRepository moviesDataRepository;
  CompositeDisposable compositeDisposable = new CompositeDisposable();
  Context context;
  private MutableLiveData<ApiResponse<List<TheMovieDbObject.MovieData>>> movieList = new MutableLiveData<>();
  ApiResponse<List<TheMovieDbObject.MovieData>> dataState;

  public static String CONNECTION_ISSUE = "CONNECTION_ISSUE";

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
          setDataState(true, movies.getResults(), null);
          movieList.setValue(dataState);
        }, throwable -> {
          if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException || throwable instanceof ConnectException) {
            setDataState(false, null, CONNECTION_ISSUE);
          } else
            setDataState(false, null, null);
          movieList.setValue(dataState);
          throwable.printStackTrace();
        })
    );
  }

  void setDataState(boolean success, List<TheMovieDbObject.MovieData> data, String error){
    dataState = new ApiResponse<>();
    dataState.setSuccess(success);
    dataState.setData(data);
    dataState.setError(error);
  }

  public LiveData<ApiResponse<List<TheMovieDbObject.MovieData>>> getMovieList(){
    return movieList;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if(compositeDisposable != null)
      compositeDisposable.clear();
  }
}
