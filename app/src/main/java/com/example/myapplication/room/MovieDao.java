package com.example.myapplication.room;

import com.example.myapplication.models.MovieData;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public abstract class MovieDao implements BaseDao<MovieData> {

  @Override
  public void delete(int id) {
    deleteSingle(id);
  }

  @Override
  public Maybe<MovieData> get(int id) {
    return getSingle(id);
  }

  @Override
  public Flowable<List<MovieData>> get() {
    return getAll();
  }

  @Override
  public void delete() {
    deleteAll();
  }

  @Query("SELECT * from MovieData")
  abstract Flowable<List<MovieData>> getAll();

  @Query("SELECT * from MovieData where id = :id")
  abstract Maybe<MovieData> getSingle(int id);

  @Query("DELETE FROM MovieData WHERE id = :id")
  abstract void deleteSingle(int id);

  @Query("DELETE FROM MovieData")
  abstract void deleteAll();

}
