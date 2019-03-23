package com.example.myapplication.models;

import org.parceler.Parcel;

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

}
