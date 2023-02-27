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






#-showcase view
-keep class com.github.amlcurran.showcaseview.** { *; }
-dontwarn com.github.amlcurran.showcaseview.**


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


