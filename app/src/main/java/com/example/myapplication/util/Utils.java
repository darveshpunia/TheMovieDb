package com.example.myapplication.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.GsonBuilder;

public class Utils {
  public static void logPrettyJson(Object jsonData){
    Log.d("darvesh", new GsonBuilder().setPrettyPrinting().create().toJson(jsonData));
  }

  public static Intent getPlayStoreIntent(String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    return intent;
  }

  public static void captureException(Throwable ex){
    ex.printStackTrace();
  }
}
