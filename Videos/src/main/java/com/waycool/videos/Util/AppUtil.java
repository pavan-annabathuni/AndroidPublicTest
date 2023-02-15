package com.waycool.videos.Util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.waycool.videos.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtil {

    public static void showFitPicasoo(Context context, String imageUrl, ImageView image) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .placeholder(R.drawable.outgrow_logo_new)
                .into(image);
    }

    public static void setProgressVisible(Context context, View loadingScreen, ImageView loadingImage) {
        try {
            loadingScreen.setVisibility(View.VISIBLE);
            AppUtil.showLoadImage(context, loadingImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setProgressGone(Context context, View loadingScreen) {
        try {
            loadingScreen.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLoadImage(Context context, ImageView image) {
        try {
            Glide.with(context).asGif().load(R.raw.loading).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String changeDateFormat(String givenDate) {
        String convertedDate = "";
        String date = givenDate;
        if (date == null)
            return "";
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
        try {
            Date inputDate = input.parse(date);                 // parse input
            convertedDate = output.format(inputDate); //get output
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("dateCon", convertedDate);
        return convertedDate;
    }
}
