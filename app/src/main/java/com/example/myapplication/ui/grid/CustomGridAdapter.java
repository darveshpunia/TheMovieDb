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
    RATING(R.string._rating),
    POPULARITY(R.string._popularity);

    private int stringResId;

    public int getStringResId() {
      return stringResId;
    }

    SortOptions(int stringResId){
      this.stringResId = stringResId;
    }
  }

  public void refreshData(List<TheMovieDbObject.MovieData> movies){
    this.movies = new ArrayList<>(movies);
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
    rating.setText("("+context.getString(R.string._rating)+": " + (movies != null ? movies.get(position).getVote_average() : "-") + ")");
    popularity.setText("("+context.getString(R.string._popularity)+": " + (movies !=null ? movies.get(position).getPopularity():"-") + ")");
    grid.setOnClickListener(v -> {
      ShowMovieDetailsActivity.startActivity(context, movies.get(position));
    });
    return grid;
  }
}
