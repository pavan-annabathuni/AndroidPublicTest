# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-----uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#------glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#-----pixplicity
-keep class com.pixplicity.sharp.** { *; }

#-----picasso
-keep class com.squareup.picasso.** { *; }

#------squareup.okhttp3
-keep class okhttp3.** { *; }

#----transition-----
# Keep the classes used by the Navigation library
-keep class android.arch.navigation.** { *; }

# Keep the classes used by the KTX extensions for Navigation
-keep class androidx.navigation.fragment.** { *; }
-keep class androidx.navigation.ui.** { *; }

# Keep the Parcelable implementations used by the Navigation library
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

#-----MPAndroidChart
# Keep the classes used by MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Keep the interface used by MPAndroidChart
-keep interface com.github.mikephil.charting.interfaces.datasets.IDataSet { *; }

# Keep the classes used by MPAndroidChart for highlighting values
-keep class com.github.mikephil.charting.highlight.** { *; }

# Keep the classes used by MPAndroidChart for rendering the chart
-keep class com.github.mikephil.charting.renderer.** { *; }

# Keep the classes used by MPAndroidChart for interactions with the chart
-keep class com.github.mikephil.charting.listener.** { *; }

#-----flexbox
-keep class com.google.android.flexbox.** { *; }

#-----coil
# Keep the classes used by Coil
-keep class coil.** { *; }

# Keep the classes used by OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep the classes used by Coroutines
-keep class kotlinx.coroutines.** { *; }

#------coil svg
# Keep the classes used by Coil SVG
-keep class coil.svg.** { *; }
-keep class com.caverock.androidsvg.** { *; }

#-----circular image view
-keep class de.hdodenhof.circleimageview.** { *; }


# -----SpeedView
-keep class com.github.anastr.speedviewlib.** { *; }
-keepclassmembers class com.github.anastr.speedviewlib.** { *; }


