package com.waycool.newsandarticles.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.waycool.newsandarticles.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static void shareItem(Context context, String url, ImageView imageView, String content) {

        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("*/*");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.putExtra(Intent.EXTRA_STREAM, getImageUri(context, getBitmapFromView(imageView)));
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, content);
                try {
                    context.startActivity(Intent.createChooser(i, "Share via..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            // does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File fileName = new File(inContext.getExternalCacheDir(),"newspic.jpg");

        try {
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(inContext,inContext.getApplicationContext().getPackageName() + ".provider",fileName);
//        return Uri.parse(path);
    }

    public static String changeDateFormat(String givenDate) {
        String convertedDate = "";
        String date = givenDate;
        if(date==null)
            return "";
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
}
