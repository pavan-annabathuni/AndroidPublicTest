package com.waycool.data.Network.NetworkModels

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import java.io.ByteArrayOutputStream

class ShareModel(var context: Context, var view: View) {
    private fun getBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    fun shareImageandText() {
        val bitmap = getBitmapFromView(view)
        val uri = getimageToShare(bitmap)
        val intent = Intent(Intent.ACTION_SEND)

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // adding text to share
        intent.putExtra(
            Intent.EXTRA_TEXT, """
     For more details Download Outgrow App from PlayStore 
     https://play.google.com/store/apps/details?id=${context.packageName}
     """.trimIndent()
        )

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")

        // setting type to image
        intent.type = "*/*"
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // calling startactivity() to share
        context.startActivity(
            Intent.createChooser(intent, "Share Via").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun shareText() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        //String sharetext = "Hi, Checkout the video on " + yv.getTitle() + " at https://www.youtube.com/watch?v=" + yv.getContentUrl() + " . For more videos Download GramworkX App from PlayStore \n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        val shareText = ""
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(sendIntent, "share"))
    }

    // Retrieving the url to share
    private fun getimageToShare(bitmap: Bitmap): Uri {
        /*File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context, "om.waycool.iwap.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;*/
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}