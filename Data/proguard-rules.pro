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


-keep class com.waycool.data.** { *; }

# crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# Picasso
-dontwarn com.squareup.okhttp.**

##---------------Begin: proguard configuration for Retrofit  -----------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
##---------------End: proguard configuration for Retrofit  -------------



##---------------Begin: proguard configuration for OkHttp  -------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
##---------------End: proguard configuration for OkHttp  ---------------


#navigation component
-keep class androidx.navigation.** { *; }

#datastore
-keepnames class androidx.datastore.preferences.** { *; }

#joda time
-keep class org.joda.time.** { *; }

#toast
-keep class es.dmoral.toasty.** { *; }

# Keep the WorkManager worker classes and related classes
-keepnames class androidx.work.impl.workers.* {
  <init>(...);
}

-keep class androidx.work.impl.** {
  *;
}

#life cycle
-keep class androidx.lifecycle.** {
    *;
}

#room db
-keep class androidx.room.** {
    *;
}

-keepclassmembers class androidx.room.** {
    *;
}

# For Room-generated code
-keep class *GeneratedTypeConverter {
    *;
}

# For code using Room annotations
#-keepclassmembers @androidx.room.* {
#    *;
#}


# Keep resources that are used by Google Maps
-keepclassmembers class * {
    @com.google.android.gms.maps.* <methods>;
}

# Keep the names of classes that are used in XML layouts
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}



# Keep Google Maps Utils library
-keep,allowoptimization class com.google.android.libraries.maps.** { *; }
-keep class com.google.maps.android.** { *; }
-keep interface com.google.maps.android.** { *; }
-keep class com.google.android.libraries.maps.** { *; }
-keep interface com.google.android.libraries.maps.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.android.gms.maps.**
-dontwarn org.xmlpull.v1.**
# Fix maps 3.0.0-beta crash:
-keep,allowoptimization class com.google.android.libraries.maps.** { *; }

# Fix maps 3.0.0-beta marker taps ignored:
-keep,allowoptimization class com.google.android.apps.gmm.renderer.** { *; }

# Keep Google Places library
-keep class com.google.android.libraries.places.** { *; }
-keep class com.google.android.gms.location.places.** { *; }
-keep interface com.google.android.gms.location.places.** { *; }
-keep interface com.google.android.libraries.places.** { *; }
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }


# Keep Google Play Services libraries
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.auth.api.** { *; }
-keep interface com.google.android.gms.auth.api.** { *; }
-dontwarn com.google.android.gms.auth.api.**
-keep class com.google.android.gms.common.api.** { *; }
-keep interface com.google.android.gms.common.api.** { *; }
-dontwarn com.google.android.gms.common.api.**











