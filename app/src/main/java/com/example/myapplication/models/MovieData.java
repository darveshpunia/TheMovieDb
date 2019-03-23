package com.example.myapplication.models;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class MovieData{
  int vote_count;
  int id;
  boolean video;
  float vote_average;
  String title;
  float popularity;
  String poster_path;
  String original_language;
  String original_title;
  List<Integer> genre_ids;
  String backdrop_path;
  boolean adult;
  String overview;
  String release_date;

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
