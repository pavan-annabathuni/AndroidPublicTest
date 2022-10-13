package com.waycool.data.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.waycool.data.Network.NetworkModels.UserDetailsDTO;

import java.lang.reflect.Type;


public class SharedPreferenceUtility {

    private static SharedPreferences sharedPreferences;


    public static SharedPreferences getSharedPreferenceInstance(Context context) {
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(
                    "user_preferences",
                    Context.MODE_PRIVATE);

        return sharedPreferences;
    }

    //* SAVING USER ID & TOKEN *//
    public static void saveUserLoginDetails(Context context, String userID, String phone, String name,
                                            String lat, String longi, String village, String state, String taluk,
                                            String district, String image,
                                            String fcm_token, String device_id) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("executive_id", userID);
        sharedPreferencesEditor.putString("executive_mobile", phone);
        sharedPreferencesEditor.putString("executive_name", name);
        sharedPreferencesEditor.putString("executive_latitude", lat);
        sharedPreferencesEditor.putString("executive_longitude", longi);
        sharedPreferencesEditor.putString("executive_village", village);
        sharedPreferencesEditor.putString("executive_state", state);
        sharedPreferencesEditor.putString("executive_taluk", taluk);
        sharedPreferencesEditor.putString("executive_district", district);
        sharedPreferencesEditor.putString("executive_image", image);
        sharedPreferencesEditor.putString("fcm_token", fcm_token);
        sharedPreferencesEditor.putString("device_id", device_id);
        sharedPreferencesEditor.commit();
    }

    public static void setUnregisteredMobile(Context context, String mobilenumber) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        // sharedPreferencesEditor.putString(context.getString(R.string.unregisteredphone), mobilenumber);
        sharedPreferencesEditor.commit();
    }

    public static boolean getFirsttimelogin(Context context) {
        return getSharedPreferenceInstance(context).getBoolean("", false);
    }

    public static void setFirsttimelogin(Context context, boolean mobilenumber) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putBoolean("", mobilenumber);
        sharedPreferencesEditor.commit();
    }

    public static String getUnregisteredMobile(Context context) {
        return getSharedPreferenceInstance(context).getString("", "");
    }


    public static void setLanguageId(Context context, String languageID) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("language_id", languageID);
        sharedPreferencesEditor.commit();
    }

    public static void setBannerList(Context context, String jsonlist) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("banner_list", jsonlist);
        sharedPreferencesEditor.commit();
    }

    public static String getBannerList(Context context) {
        return getSharedPreferenceInstance(context).getString("banner_list", "");
    }

    public static void setAndroidId(Context context, String id) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("android_id", id);
        sharedPreferencesEditor.commit();
    }

    public static String getAndroidId(Context context) {
        return getSharedPreferenceInstance(context).getString("android_id", "");
    }

    public static String getLanguageId(Context context) {
        return getSharedPreferenceInstance(context).getString("language_id", "");
    }

    public static void setSoilGuidelines(Context context, boolean isshown) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putBoolean("soil_guide", isshown);
        sharedPreferencesEditor.commit();
    }

    public static void setBenchMarkerToken(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("benchmarker_token", token);
        sharedPreferencesEditor.commit();
    }


    public static String getBenchMarkerToken(Context context) {
        return getSharedPreferenceInstance(context).getString("benchmarker_token", "");
    }

    public static void savePreferencesForLogout(Context context) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.remove("executive_id");
        sharedPreferencesEditor.remove("user_type");
        sharedPreferencesEditor.remove("executive_mobile");
        sharedPreferencesEditor.remove("executive_name");
        sharedPreferencesEditor.remove("executive_latitude");
        sharedPreferencesEditor.remove("executive_longitude");
        sharedPreferencesEditor.remove("executive_village");
        sharedPreferencesEditor.remove("executive_state");
        sharedPreferencesEditor.remove("executive_taluk");
        sharedPreferencesEditor.remove("executive_district");
        sharedPreferencesEditor.remove("executive_image");
        sharedPreferencesEditor.remove("fcm_token");
        sharedPreferencesEditor.remove("device_id");
        sharedPreferencesEditor.remove("benchmarker_token");
        // sharedPreferencesEditor.remove("language_id");
        sharedPreferencesEditor.remove("");
        /*sharedPreferencesEditor.clear();*/
        sharedPreferencesEditor.commit();
    }


    public static boolean getSoilGuidelines(Context context) {
        return getSharedPreferenceInstance(context).getBoolean("soil_guide", false);
    }

    public static String getUserId(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_id", "");
    }

    public static int getIntUserId(Context context) {
        return getSharedPreferenceInstance(context).getInt("executive_id", -1);
    }

    public static String getLatitude(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_latitude", "");
    }

    public static String getLongitude(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_longitude", "");
    }

    public static String getFCM_Token(Context context) {
        return getSharedPreferenceInstance(context).getString("fcm_token", "");
    }

    public static String getDevice_id(Context context) {
        return getSharedPreferenceInstance(context).getString("device_id", "");
    }

    public static String getUserName(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_name", "");
    }

    public static String getUserMobile(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_mobile", "");
    }

    public static String getUserImage(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_image", "");
    }

    public static String getCity(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_village", "");
    }

    public static String getTaluk(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_taluk", "");
    }

    public static String getDistrict(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_district", "");
    }

    public static String getState(Context context) {
        return getSharedPreferenceInstance(context).getString("executive_state", "");
    }

    public static void setUserType(Context context, String usertype) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();

        sharedPreferencesEditor.putString("user_type", usertype);
        sharedPreferencesEditor.commit();
    }

    public static String getUserType(Context context) {
        return getSharedPreferenceInstance(context).getString("user_type", "");
    }


    public static void setLanguageSelection(Context context, String mobilenumber) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("logout", mobilenumber);
        sharedPreferencesEditor.commit();
    }

    public static String getLanguageSelection(Context context) {
        return getSharedPreferenceInstance(context).getString("logout", "0");
    }

    public static void setFcmToken(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("fcmToken", token);
        sharedPreferencesEditor.commit();
    }

    public static String getFcmToken(Context context) {
        return getSharedPreferenceInstance(context).getString("fcmToken", "0");
    }

    public static void seUserToken(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("userToken", token);
        sharedPreferencesEditor.commit();
    }

    public static String getUserToken(Context context) {
        return getSharedPreferenceInstance(context).getString("userToken", "");
    }

    public static void setUserFirst(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("newUser", token);
        sharedPreferencesEditor.commit();
    }

    public static String getUserFirst(Context context) {
        return getSharedPreferenceInstance(context).getString("newUser", "0");
    }

    public static void setLogin(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("userLogin", token);
        sharedPreferencesEditor.commit();
    }

    public static String getLogin(Context context) {
        return getSharedPreferenceInstance(context).getString("userLogin", "0");
    }

    public static void setLanguage(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("userLanguage", token);
        sharedPreferencesEditor.commit();
    }

    public static String getLanguage(Context context) {
        return getSharedPreferenceInstance(context).getString("userLanguage", "");
    }

    public static void setMobileModel(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("mobileModel", token);
        sharedPreferencesEditor.commit();
    }

    public static String getMobileModel(Context context) {
        return getSharedPreferenceInstance(context).getString("mobileModel", "");
    }

    public static void setMobileMan(Context context, String token) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("mobileMan", token);
        sharedPreferencesEditor.commit();
    }

    public static String getMobileMan(Context context) {
        return getSharedPreferenceInstance(context).getString("mobileMan", "");
    }

    public static void setMobileNumber(Context context, String number) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("mobileNumber", number);
        sharedPreferencesEditor.commit();
    }

    public static String getMobileNumber(Context context) {
        return getSharedPreferenceInstance(context).getString("mobileNumber", "");
    }

    public static void setUserDetais(Context context, UserDetailsDTO userDetails) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance(context).edit();
        sharedPreferencesEditor.putString("userDetails", convertUserDetailsToString(userDetails));
        sharedPreferencesEditor.commit();
    }

    public static UserDetailsDTO getUserDetails(Context context) {
        return convertStringToUserDetails(getSharedPreferenceInstance(context).getString("userDetails", null));
    }

    public static String convertUserDetailsToString(UserDetailsDTO deviceDataRange) {
        Gson gson = new Gson();
        return gson.toJson(deviceDataRange);
    }

    public static UserDetailsDTO convertStringToUserDetails(String s) {
        Type listType = new TypeToken<UserDetailsDTO>() {
        }.getType();
        return new Gson().fromJson(s, listType);
    }

}
