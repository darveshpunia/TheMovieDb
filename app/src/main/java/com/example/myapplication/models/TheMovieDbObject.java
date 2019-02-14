package com.example.myapplication.models;

import java.util.List;

public class TheMovieDbObject {
  private int page;
  private int total_results;
  private int total_pages;
  private List<MovieData> results;

  public int getPage() {
    return page;
  }

  public int getTotal_results() {
    return total_results;
  }

  public int getTotal_pages() {
    return total_pages;
  }

  public List<MovieData> getResults() {
    return results;
  }

  public void setResults(List<MovieData> results) {
    this.results = results;
  }

  public void setPage(int page) {
    this.page = page;
  }

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
}
