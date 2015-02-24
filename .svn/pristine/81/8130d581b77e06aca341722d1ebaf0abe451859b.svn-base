package client.potlach.com.potlachandroid.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;

import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.exception.ExceptionHandler;

/**
 * Created by thiago on 10/9/14.
 */
public abstract class BaseActivity extends Activity implements AsyncActivity {

    protected PhotoGiftApplication app;
    protected ProgressDialog progressDialog;
    protected boolean destroyed = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        // Your mechanism is ready now.. In this activity from anywhere
        // if you get force close error it will be redirected to the CrashActivity.
        app = (PhotoGiftApplication) getApplication();
    }

    @Override
    protected void onResume() {
        app.setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        this.destroyed = true;
        super.onDestroy();
    }

    public PhotoGiftApplication getApp() {
        return app;
    }

    @Override
    public void showLoadingProgressDialog() {
        this.showProgressDialog("Loading. Please wait...");
    }

    @Override
    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (this.progressDialog != null && !this.destroyed) {
            this.progressDialog.dismiss();
        }
    }

    public void setActionBarTitle(String title, boolean back) {
        ActionBar actionBar = getActionBar();
        if(title!=null){
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan("Roboto-Bold.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update the action bar title with the TypefaceSpan instance
            actionBar.setTitle(title);
        }

        if(back){
            setBackEnabled(actionBar);
        }
    }

    public static void setBackEnabled(ActionBar actionBar){
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void clearReferences(){
        Activity currActivity = app.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            app.setCurrentActivity(null);
    }

}
