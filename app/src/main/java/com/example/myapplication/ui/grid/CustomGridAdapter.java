package com.example.myapplication.ui.grid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.moviedetails.ShowMovieDetailsActivity;
import com.example.myapplication.util.Constants;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

public class CustomGridAdapter extends BaseAdapter {

  List<TheMovieDbObject.MovieData> movies;
  Context context;

  public CustomGridAdapter(Context context, List<TheMovieDbObject.MovieData> data) {
    movies = new ArrayList<>(data);
    this.context = context;
  }

  public enum SortOptions {
    RATING(R.string._rating,
        (m1, m2) -> Float.compare(m1.getVote_average(), m2.getVote_average())),

    POPULARITY(R.string._popularity,
        (m1, m2) -> Float.compare(m1.getPopularity(), m2.getPopularity()));

    private int stringResId;
    private Comparator<TheMovieDbObject.MovieData> comparator;

    public int getStringResId() {
      return stringResId;
    }

    public Comparator<TheMovieDbObject.MovieData> getComparator() {
      return comparator;
    }

    SortOptions(int stringResId, Comparator<TheMovieDbObject.MovieData> comparator){
      this.stringResId = stringResId;
      this.comparator = comparator;
    }
  }

  public void sortFields(SortOptions option, boolean ascending){
    Collections.sort(movies, ascending ? option.getComparator() : (x, y) -> -option.getComparator().compare(x, y));
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return movies.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View grid;
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (convertView == null) {
      grid = new View(context);
      grid = inflater.inflate(R.layout.grid_item, null);
    } else {
      grid = (View) convertView;
    }
    ImageView imageView = grid.findViewById(R.id.image);
    Picasso.get().load(Constants.TMDB_IMAGE_PATH + movies.get(position).getBackdrop_path()).into(imageView);
    TextView title = grid.findViewById(R.id.text_title);
    TextView rating = grid.findViewById(R.id.text_rating);
    TextView popularity = grid.findViewById(R.id.text_popularity);
    title.setText(movies.get(position).getTitle());
    rating.setText("("+context.getString(R.string._rating)+": " + movies.get(position).getVote_average() + ")");
    popularity.setText("("+context.getString(R.string._popularity)+": " + movies.get(position).getPopularity() + ")");
    grid.setOnClickListener(v -> {
      ShowMovieDetailsActivity.startActivity(context, movies.get(position));
    });
    return grid;
  }
}
