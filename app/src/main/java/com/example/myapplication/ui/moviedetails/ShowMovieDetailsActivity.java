package com.example.myapplication.ui.moviedetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.myapplication.R;
import com.example.myapplication.injection.MovieDbApplication;
import com.example.myapplication.models.MovieData;
import com.example.myapplication.models.Review;
import com.example.myapplication.models.ReviewsModel;
import com.example.myapplication.models.TheMovieDbObject;
import com.example.myapplication.models.VideoModel;
import com.example.myapplication.networking.DataManager;
import com.example.myapplication.room.MovieDao;
import com.example.myapplication.room.RoomDbRepository;
import com.example.myapplication.util.Constants;
import com.example.myapplication.util.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import static com.example.myapplication.util.Constants.LANGUAGE_US_EN;

public class ShowMovieDetailsActivity extends AppCompatActivity {

  MovieData movie;
  final static String MOVIE_DETAILS = "MOVIE_DETAILS";

  @BindView(R.id.image)
  ImageView imageView;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.app_bar_layout)
  AppBarLayout appBarLayout;
  @BindView(R.id.collapsing_toolbar)
  CollapsingToolbarLayout collapsingToolbarLayout;

  @BindView(R.id.text_over_view)
  TextView textOverview;
  @BindView(R.id.text_rating)
  TextView textRating;
  @BindView(R.id.text_title)
  TextView textTitle;
  @BindView(R.id.text_release_date)
  TextView textReleaseDate;
  @BindView(R.id.rating_bar)
  RatingBar ratingBar;
  @BindView(R.id.fab_save_data)
  FloatingActionButton fab;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.recycler_view_trailer)
  RecyclerView recyclerViewTrailers;

  @Inject
  RoomDbRepository<MovieData, MovieDao> roomDbMovieRepository;
  @Inject
  DataManager dataManager;

  CompositeDisposable compositeDisposable = new CompositeDisposable();

  public static Intent getStartActivity(Context context){
    return new Intent(context, ShowMovieDetailsActivity.class);
  }

  public static void startActivity(Context context, MovieData movieData){
    Intent i = getStartActivity(context);
    i.putExtra(MOVIE_DETAILS, Parcels.wrap(movieData));
    context.startActivity(i);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_movie_details);
    ButterKnife.bind(this);
    ((MovieDbApplication) getApplication()).getAppComponent().inject(this);
    movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_DETAILS));
    if (movie == null)
      handleUnexpectedError();
    configureToolbar();
    configureUI();
    if (Utils.isNetworkAvailable(this))
      getMovieData();
  }

  private void getMovieData() {
    MaterialDialog progress = new MaterialDialog.Builder(this)
      .title("Please wait...")
      .content("Loading Data...")
      .progress(true, 0)
      .canceledOnTouchOutside(false)
      .show();
    compositeDisposable.add(
      dataManager
        .getMovieReviews(movie.id, LANGUAGE_US_EN, 1)
        .subscribe(reviewsModel -> {
          progress.dismiss();
          if (reviewsModel.getResults().size() > 0) {
            findViewById(R.id.reviews_layout).setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new ReviewsAdapter(this, reviewsModel.getResults()));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
          }
        }, ex-> {
          progress.dismiss();
          Utils.captureException(ex);
          Toast.makeText(getApplicationContext(), R.string._something_went_wrong, Toast.LENGTH_LONG).show();
        })
    );
    MaterialDialog progress2 = new MaterialDialog.Builder(this)
      .title("Please wait...")
      .content("Loading Data...")
      .progress(true, 0)
      .canceledOnTouchOutside(false)
      .show();
    compositeDisposable.add(
      dataManager
        .getMovieVideos(movie.id, LANGUAGE_US_EN, 1)
        .subscribe(response -> {
          progress2.dismiss();
          if (response.getResults().size() > 0) {
            findViewById(R.id.trailer_layout).setVisibility(View.VISIBLE);
            recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTrailers.setAdapter(new TrailersAdapter(this, response.getResults()));
            recyclerViewTrailers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
          }
        }, ex-> {
          progress2.dismiss();
          Utils.captureException(ex);
          Toast.makeText(getApplicationContext(), R.string._something_went_wrong, Toast.LENGTH_LONG).show();
        })
    );
  }

  private void configureToolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    setUpToolBarTitle();
  }

  /*
  This function basically shows title only when the toolbar is in collapsed state.
  For more info:
  https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed
   */
  private void setUpToolBarTitle() {
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          collapsingToolbarLayout.setTitle(movie.getTitle());
          isShow = true;
        } else if(isShow) {
          collapsingToolbarLayout.setTitle(" ");
          isShow = false;
        }
      }
    });
  }

  private void configureUI() {
    Picasso.get()
        .load(Constants.TMDB_IMAGE_PATH + movie.getPoster_path())
        .placeholder(R.drawable.image_placeholder)
        .error(R.drawable.image_error)
        .into(imageView);
    textTitle.setText(movie.getTitle());
    textReleaseDate.setText("Release Date: " + movie.getRelease_date());
    textRating.setText("(" + String.valueOf(movie.getVote_average()/2) + ")");
    textOverview.setText(movie.getOverview());
    ratingBar.setRating(movie.getVote_average() / 2);
    compositeDisposable.add(
      roomDbMovieRepository
        .get(movie.getId())
        .subscribe(movie -> {
          fab.setImageResource(R.drawable.ic_favorite_black_24dp);
        }, Utils::captureException)
    );
  }

  @OnClick(R.id.fab_save_data)
  void saveDataInRoom(){
    compositeDisposable.add(
      roomDbMovieRepository
        .get(movie.getId())
        .subscribe(movie -> {
            roomDbMovieRepository.delete(movie.getId(), ()->{
              Toast.makeText(getApplicationContext(), "Movie removed from Favorites", Toast.LENGTH_LONG).show();
              fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            });
        }, Utils::captureException,
        () -> {
          roomDbMovieRepository.insert(movie, () -> {
            Toast.makeText(getApplicationContext(), "Movie added to Favorites", Toast.LENGTH_LONG).show();
            fab.setImageResource(R.drawable.ic_favorite_black_24dp);
          });
        })
    );
  }

  void handleUnexpectedError(){
    Toast.makeText(this, R.string._something_went_wrong, Toast.LENGTH_LONG).show();
    finish();
  }

  public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
      this.context = context;
      this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.title.setText(reviews.get(position).getAuthor());
      holder.subTitle.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
      return reviews.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
      TextView title;
      TextView subTitle;
      ViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        subTitle = view.findViewById(R.id.sub_title);
      }
    }
  }

  public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private Context context;
    private List<VideoModel> videos;

    public TrailersAdapter(Context context, List<VideoModel> videos) {
      this.context = context;
      this.videos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.title.setText(videos.get(position).getName());
      holder.subTitle.setVisibility(View.GONE);
      holder.itemView.setOnClickListener(__ ->{
        startVideo(videos.get(position).getKey());
      });
    }

    void startVideo(String videoId) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
      intent.putExtra("VIDEO_ID", videoId);
      startActivity(intent);
    }

    @Override
    public int getItemCount() {
      return videos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
      TextView title;
      TextView subTitle;
      ViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        subTitle = view.findViewById(R.id.sub_title);
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
    roomDbMovieRepository.clearSubscriptions();
  }

}
