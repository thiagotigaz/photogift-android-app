package client.potlach.com.potlachandroid.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.model.Chain;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.widget.GenericAdapter;

public class ChainListAdapter extends GenericAdapter<Chain> {
    private Context ctx;
    private ChainViewHolder holder;
    private Activity activity;
    private Picasso pic;

    public ChainListAdapter(Activity activity, List<Chain> objects) {
        super(activity, objects);
        ctx = activity.getBaseContext();
        this.activity = activity;
        this.pic = Picasso.with(activity);
    }

    @Override
    public View getDataRow(int position, View convertView, ViewGroup parent) {

        Log.d(ChainListAdapter.class.getName(), "GetView Holder");
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            row = inflater.inflate(R.layout.single_row_chain, parent, false);
            holder = new ChainViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ChainViewHolder) row.getTag();
        }

        configRow(row);

        Chain chain = getItem(position);
        Gift gift = chain.getFeaturedGift();
        holder.getChainTitleTextView().setText(chain.getName());
        holder.getGiftsCountTextView().setText(chain.getGiftsCount().toString());
        holder.getFollowersCountTextView().setText(chain.getFollowersCount().toString());
        if(!((PhotoGiftApplication)activity.getApplication()).isObsceneEnabled() && gift.isObscene()){
            holder.getProgressBar().setVisibility(View.GONE);
            pic.load(R.drawable.ic_warning_holder).into(holder.getGiftThumb());
        }
        else
            pic.load(gift.getS3Path()).into(holder.getGiftThumb(), new ImageLoadedCallback(holder.getProgressBar()) {

                @Override
                public void onSuccess() {
                    if(getProgressBar()!=null)
                        getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        return row;
    }

    private void configRow(View row) {
        LinearLayout rootLayout = (LinearLayout) row;
        rootLayout.setFocusable(false);
        rootLayout.findViewById(R.id.chainTitleTextView).setFocusable(false);
        rootLayout.findViewById(R.id.chainImageView).setFocusable(false);
        rootLayout.findViewById(R.id.giftsCountTextView).setFocusable(false);
    }

    @Override
    public View getFooterView(int position, View convertView, ViewGroup parent) {
        return super.getFooterView(position, convertView, parent);
    }

    public Chain getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    class ChainViewHolder {
         @InjectView(R.id.chainTitleTextView)
         protected TextView chainTitleTextView;

         @InjectView(R.id.chainFollowersCountTextView)
         protected TextView followersCountTextView;

         @InjectView(R.id.giftsCountTextView)
         protected TextView giftsCountTextView;

         @InjectView(R.id.chainImageView)
         protected ImageView giftThumb;

         @InjectView(R.id.chainImageProgressBar)
         protected ProgressBar progressBar;

         public ChainViewHolder(View row) {
             ButterKnife.inject(this, row);
         }

         public TextView getChainTitleTextView() {
             return chainTitleTextView;
         }

         public void setChainTitleTextView(TextView chainTitleTextView) {
             this.chainTitleTextView = chainTitleTextView;
         }

         public TextView getFollowersCountTextView() {
             return followersCountTextView;
         }

         public void setFollowersCountTextView(TextView followersCountTextView) {
             this.followersCountTextView = followersCountTextView;
         }

         public ImageView getGiftThumb() {
             return giftThumb;
         }

         public void setGiftThumb(ImageButton giftThumb) {
             this.giftThumb = giftThumb;
         }

         public TextView getGiftsCountTextView() {
             return giftsCountTextView;
         }

         public void setGiftsCountTextView(TextView giftsCountTextView) {
             this.giftsCountTextView = giftsCountTextView;
         }

         public ProgressBar getProgressBar() {
             return progressBar;
         }

         public void setProgressBar(ProgressBar progressBar) {
         this.progressBar = progressBar;
    }
 }

}
