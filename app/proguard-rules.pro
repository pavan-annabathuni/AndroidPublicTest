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

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep class com.waycool.iwap.MainActivity { *; }
-keep class com.waycool.iwap.splash.SplashActivity { *; }
-keep class * extends androidx.fragment.app.Fragment{}


#Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep the classes used by Firebase Performance Monitoring
-keep class com.google.firebase.perf.** { *; }
-dontwarn com.google.firebase.perf.**

-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.firebase.FirebaseApp {
    <init>(...);
}
-keep class com.google.firebase.** {
    *;
}
-keep class com.google.android.gms.auth.api.** { *; }
-keep class com.google.android.gms.auth.api.identity.** { *; }

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep class com.waycool.featurechat.**{ *;}
-keep class zendesk.** { *; }
-keepclassmembers enum zendesk.** {
    <fields>;
    **[] values();
}
-keep class zendesk.** { *; }
-keepnames class zendesk.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
