package client.potlach.com.potlachandroid.util;

import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created by thiago on 11/3/14.
 */
public abstract class ImageLoadedCallback implements Callback {

    private final ProgressBar progressBar;

    public ImageLoadedCallback(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}
