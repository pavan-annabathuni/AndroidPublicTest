//
// Created by Pavan A on 28/04/22.
//

#include <jni.h>
#include <string>

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_waycool_core_utils_AppConfig_getInternalAPIKey(JNIEnv *env, jclass clazz) {
//    std::string api_key = "CGE233FB8653421";
//    return env->NewStringUTF(api_key.c_str());
//}


extern "C"
JNIEXPORT jstring
Java_com_waycool_core_utils_AppSecrets_getBASEURLDebug(JNIEnv *env, jobject clazz) {
    std::string base_url = "https://adminqa.outgrowdigital.com/";
//    std::string base_url = "https://admindev.outgrowdigital.com/";
    return env->NewStringUTF(base_url.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getApiKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "hln20-zym7q6jkcsowdx3ie9bv4tfr1pug8a5";
    return env->NewStringUTF(base_url.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getWeatherBaseUrl(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://api.openweathermap.org/";
    return env->NewStringUTF(base_url.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getWeatherApiKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "9ff1d72c826bc07dd12b5194e9927dbd";
    return env->NewStringUTF(base_url.c_str());
}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getMsgApiKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "339466As1e7SnWEJ5f3e52efP1";
//    return env->NewStringUTF(base_url.c_str());
//}
//
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getMsgTemplateKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "5f71ae22d69188233e3d50e8";
//    return env->NewStringUTF(base_url.c_str());
//}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getSprayingBASEURL(JNIEnv *env, jclass clazz) {
//    std::string base_url = "http://3.109.59.109/";
//    return env->NewStringUTF(base_url.c_str());
//}

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getWeatherPremiumApiKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "cde1ce6c4f81301a4d517f1dd0565c90";
//    return env->NewStringUTF(base_url.c_str());
//}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getPremiumWeatherBaseUrl(JNIEnv *env, jclass clazz) {
//    std::string base_url = "https://api.darksky.net/";
//    return env->NewStringUTF(base_url.c_str());
//}

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getSprayingApiKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "CC233FB865537";
//    return env->NewStringUTF(base_url.c_str());
//}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getDLTKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "1007161217962634901";
//    return env->NewStringUTF(base_url.c_str());
//}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getMapsKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "AIzaSyAbYkho0xs5_muW3_tobx43l02p1LsF5vI";
//    return env->NewStringUTF(base_url.c_str());
//}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_gramworkx_gramworkx_utils_AppConfig_getYTKey(JNIEnv *env, jclass clazz) {
//    std::string base_url = "AIzaSyB-6QNre3Dz_Kz-he8zY4ex2kw7PVGHkNg";
//    return env->NewStringUTF(base_url.c_str());
//}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getOTPBaseURL(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://api.msg91.com/";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getOTPKey(JNIEnv *env, jobject thiz) {

    std::string base_url = "329266A6qC0z6ZNgBD5ebfbe1cP1";
    return env->NewStringUTF(base_url.c_str());

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getOTPTemplateId(JNIEnv *env, jobject thiz) {
    std::string base_url = "6335616bd6fc05351b5194a3";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getYoutubeKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "AIzaSyB-6QNre3Dz_Kz-he8zY4ex2kw7PVGHkNg";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getChatAppId(JNIEnv *env, jobject thiz) {
    std::string base_url = "73015859e3bdae57c168235eb6c96f25c46e747c24bb5e8";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getAccountKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "dt55P5snqpfyOrXfNqz56lwrup8amDdz";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getMapsKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "AIzaSyAbYkho0xs5_muW3_tobx43l02p1LsF5vI";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getGeoBaseUrl(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://maps.googleapis.com/";
    return env->NewStringUTF(base_url.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_waycool_core_utils_AppSecrets_getChatChannelKey(JNIEnv *env, jobject thiz) {
    std::string base_url = "eyJzZXR0aW5nc191cmwiOiJodHRwczovL3dheWNvb2xpbmRpYS56ZW5kZXNrLmNvbS9tb2JpbGVfc2RrX2FwaS9zZXR0aW5ncy8wMUdIS0E5Njc2MkFKOUVENzRWWUVRSlA5WC5qc29uIn0=";
    return env->NewStringUTF(base_url.c_str());
}