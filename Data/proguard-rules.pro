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


#Firebase
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

-keep class com.google.firebase.analytics.** { *; }
-keepnames class com.google.android.gms.common.internal.safeparcel.SafeParcelable


# Keep the classes and methods used by Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.android.gms.common.internal.safeparcel.SafeParcelable { *; }
-keep class com.google.android.gms.tasks.Task { *; }

-keepattributes Signature

# Keep the classes and methods used by Firebase Dynamic Links
-keep class com.google.firebase.dynamiclinks.** { *; }


# Keep any custom classes or methods that are accessed by name reflectively by your app
#-keepclassmembers class com.yourcompany.yourapp.YourClass {
 #  private void yourMethod(java.lang.String);}


# Google Play Services Location
-keep class com.google.android.gms.location.** { *; }
-keep interface com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.location.**

# Google Play Services Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# Google Maps Utils
-keep class com.google.maps.android.clustering.** { *; }
-keep interface com.google.maps.android.clustering.** { *; }
-keep class com.google.maps.android.heatmaps.** { *; }
-keep interface com.google.maps.android.heatmaps.** { *; }
-keep class com.google.maps.android.ui.** { *; }
-keep interface com.google.maps.android.ui.** { *; }
-dontwarn com.google.maps.android.**

# Google Maps
-keep class com.google.android.libraries.maps.** { *; }
-keep interface com.google.android.libraries.maps.** { *; }
-dontwarn com.google.android.libraries.maps.**

-keep class com.google.android.libraries.places.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.common.internal.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-keep class com.google.android.gms.auth.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-keep class com.google.android.gms.auth.api.phone.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory { *; }


#--------------------------------













