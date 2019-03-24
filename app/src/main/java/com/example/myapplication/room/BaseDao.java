package com.example.myapplication.room;

import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

import static androidx.room.OnConflictStrategy.REPLACE;

interface BaseDao<T> {
  @Insert(onConflict = REPLACE)
  void insert (T data);
  @Insert(onConflict = REPLACE)
  void insert(List<T> data);
  @Update(onConflict = REPLACE)
  void update (T data);
  @Delete
  void delete (T data);
  @Delete
  void delete (List<T> data);
  // override these according to need
  void delete (int id);
  Flowable<List<T>> get ();
  Maybe<T> get (int id);
  void delete();
}
