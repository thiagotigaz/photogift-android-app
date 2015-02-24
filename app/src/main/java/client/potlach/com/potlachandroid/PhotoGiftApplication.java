package client.potlach.com.potlachandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import client.potlach.com.potlachandroid.activity.LoginActivity;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.oauth.PhotoGiftRestBuilder;
import client.potlach.com.potlachandroid.service.ChainSvc;
import client.potlach.com.potlachandroid.service.GiftSvc;
import client.potlach.com.potlachandroid.service.RefreshUserGiftsTouchesService;
import client.potlach.com.potlachandroid.service.UserSvc;

/**
 * Created by thiago on 10/7/14.
 */
public class PhotoGiftApplication extends Application{
    private static final String PREF_LOGGEDIN_USER = "pref_loggedin_user";
    private SharedPreferences preferences;
    private Activity currentActivity = null;

    public User getLoggedUser() {
        String userJson = preferences.getString(PREF_LOGGEDIN_USER, "");
        if (!userJson.equals(""))
            return new Gson().fromJson(userJson, User.class);
        else
            return null;
    }

    public void setLoggedUser(User loggedInUser) {
        if(loggedInUser==null){
            preferences.edit().remove(PREF_LOGGEDIN_USER).commit();
        }else{
            String userJson = new Gson().toJson(loggedInUser);
            preferences.edit().putString(PREF_LOGGEDIN_USER, userJson).commit();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.preferences = getSharedPreferences(getResources().getString(R.string.app_name),MODE_PRIVATE);
    }

    public void logout(Activity activity) {
        PhotoGiftRestBuilder.setLoggedIn(false);
        RefreshUserGiftsTouchesService.setRunning(false);
        setLoggedUser(null);
        PhotoGiftRestBuilder.logout();
        ChainSvc.reset();
        GiftSvc.reset();
        UserSvc.reset();
        activity.stopService(new Intent(activity, RefreshUserGiftsTouchesService.class));
//        stopService(new Intent(this,RefreshUserGiftsTouchesService.class));
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public boolean isObsceneEnabled(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PhotoGiftApplication.this);

        return sharedPref.getBoolean(getResources().getString(R.string.pref_key_obscene_enabled), false);
    }

    public Activity getCurrentActivity(){
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity;
    }
}
