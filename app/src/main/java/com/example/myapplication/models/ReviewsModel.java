package com.example.myapplication.models;

import java.util.List;

public class ReviewsModel<T> {
  int id;
  int page;
  List<T> results;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }

}
