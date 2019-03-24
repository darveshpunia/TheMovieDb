package com.example.myapplication.room;

import com.example.myapplication.models.MovieData;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MovieData.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
  public static final String DATABASE_NAME = "db-1";
  public abstract MovieDao movieDao();
}