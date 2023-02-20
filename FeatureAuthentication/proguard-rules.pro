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

# Google Places
-keep class com.google.android.gms.maps.model.** { *; }
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.location.places.** { *; }
-keep class com.google.android.libraries.places.** { *; }

-keep interface com.google.android.gms.maps.internal.IProjectionDelegate { *; }
-keep interface com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate { *; }
-keep interface com.google.android.gms.maps.internal.IMapFragmentDelegate { *; }
-keep interface com.google.android.gms.maps.internal.IMapViewDelegate { *; }
-keep interface com.google.android.gms.maps.internal.IUiSettingsDelegate { *; }

-dontwarn com.google.android.gms.maps.**
-dontwarn com.google.android.gms.location.places.**
-dontwarn com.google.android.libraries.places.**
