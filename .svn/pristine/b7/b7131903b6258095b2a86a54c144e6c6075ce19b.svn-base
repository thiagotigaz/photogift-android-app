package client.potlach.com.potlachandroid.fragment;

import android.app.Activity;
import android.app.Fragment;

import client.potlach.com.potlachandroid.PhotoGiftApplication;

/**
 * Created by thiago on 10/8/14.
 */
public abstract class BaseFragment extends Fragment {

    protected PhotoGiftApplication app;
    protected Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        this.app = (PhotoGiftApplication) activity.getApplication();
    }

    public PhotoGiftApplication getApp() {
        return app;
    }
}
