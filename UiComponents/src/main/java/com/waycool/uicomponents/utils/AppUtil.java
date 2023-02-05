package com.waycool.uicomponents.utils;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.waycool.uicomponents.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppUtil {

    public static void showUrlImage(Context context, String imageUrl, ImageView image) {
        Glide.with(context)
                .load("https://outgrow.blob.core.windows.net/outgrowstorage/Prod/CropLogo/Apple.svg")
                .fitCenter()
                .placeholder(R.drawable.ic_outgrow)
                .error(R.drawable.ic_outgrow)
                .into(image);
    }

    public static void showBannerImage(Context context, String imageUrl, ImageView image) {
        Picasso.get()
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.outgrow_logo_new)
                .into(image);
    }
    public static void showImageDrawable(Context context, int imageUrl, ImageView image) {
        Picasso.get().load(imageUrl).into(image);
    }
    public static void showFitPicasoo(Context context, String imageUrl, ImageView image) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .placeholder(R.drawable.outgrow_logo_new)
                .into(image);
    }

    public static void showLoadImage(Context context, ImageView image) {
        try {
            Glide.with(context).asGif().load(R.raw.loading).into(image);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void Toast(Context context,String msg){
        try {
            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setProgressVisible(Context context, View loadingScreen, ImageView loadingImage ){
        try{
            loadingScreen.setVisibility(View.VISIBLE);
            AppUtil.showLoadImage(context,loadingImage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void setProgressGone(Context context,View loadingScreen){
        try {
            loadingScreen.setVisibility(View.GONE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }
    public static String changeDateFormat(String givenDate) {
        String convertedDate = "";
        String date = givenDate;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yy");
        try {
            Date inputDate = input.parse(date);                 // parse input
            convertedDate = output.format(inputDate); //get output
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("dateCon", convertedDate);
        return convertedDate;
    }

    public static void handlerSet(Handler handler, Runnable runnable, int delayTime){
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,delayTime);
    }


}
