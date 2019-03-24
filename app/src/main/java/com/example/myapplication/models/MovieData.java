package com.example.myapplication.models;

import org.parceler.Parcel;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Parcel @Entity
public class MovieData{
  public int vote_count;
  @PrimaryKey
  public int id;
  public boolean video;
  public float vote_average;
  public String title;
  public float popularity;
  public String poster_path;
  public String original_language;
  public String original_title;
  @Ignore
  List<Integer> genre_ids;
  public String backdrop_path;
  public boolean adult;
  public String overview;
  public String release_date;

  public int getVote_count() {
    return vote_count;
  }

  public int getId() {
    return id;
  }

  public boolean isVideo() {
    return video;
  }

  public float getVote_average() {
    return vote_average;
  }

  public String getTitle() {
    return title;
  }

  public float getPopularity() {
    return popularity;
  }

  public String getPoster_path() {
    return poster_path;
  }

  public String getOriginal_language() {
    return original_language;
  }

  public String getOriginal_title() {
    return original_title;
  }

  public List<Integer> getGenre_ids() {
    return genre_ids;
  }

  public String getBackdrop_path() {
    return backdrop_path;
  }

  public boolean isAdult() {
    return adult;
  }

  public String getOverview() {
    return overview;
  }

  public String getRelease_date() {
    return release_date;
  }
}
