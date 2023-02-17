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
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
##---------------End: proguard configuration for OkHttp  ---------------


#Google Play Services
-keep class com.google.android.gms.measurement.** { *; }

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
-keepclasseswithmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
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
-keepclassmembers @androidx.room.* {
    *;
}

# For Kotlin code using Room annotations
-dontwarn kotlin.Unit
-keepclassmembers class * implements androidx.room.RoomDatabase {
    public static ** getDatabase(android.content.Context);
}

-keep class kotlinx.coroutines.** { *; }









