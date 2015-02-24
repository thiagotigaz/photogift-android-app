package client.potlach.com.potlachandroid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.activity.LikeTouchesUserActivity;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.service.GiftSvc;
import client.potlach.com.potlachandroid.service.GiftSvcApi;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.widget.GenericAdapter;

public class GiftListAdapter extends GenericAdapter<Gift> {
    private static final String TAG = "GiftListAdapter";
    private static final int GIFT_LIKE_TOUCH = 1;
    private static final int GIFT_UNLIKE_TOUCH = 2;
    private static final int GIFT_OBSCENE_TOUCH = 3;
    private static final int GIFT_REVERT_OBSCENE_TOUCH = 4;
    private Context ctx;
    private GiftViewHolder holder;
    private Activity activity;
    private Picasso pic;
    private GiftSvcApi giftSvc;

    public GiftListAdapter(Activity activity, List<Gift> gifts) {
        super(activity, gifts);
        ctx = activity.getBaseContext();
        this.activity = activity;
        giftSvc = GiftSvc.getOrShowLogin(activity);
//        this.pic = PicassoUtil.getPicassoClient(activity);
        this.pic = Picasso.with(activity);
//        pic.setDebugging(true);
//        pic.setIndicatorsEnabled(true);
//        pic.setLoggingEnabled(true);
    }

    @Override
    public View getDataRow(int position, View convertView, ViewGroup parent) {
//        Log.d(GiftListAdapter.class.getName(), "GetView Holder");
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            row = inflater.inflate(R.layout.single_row_gift, parent, false);
            holder = new GiftViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (GiftViewHolder) row.getTag();
        }

        Gift gift = getItem(position);
        configureGiftDoubleClick(holder.getGiftThumb(), gift);
        configureObsceneTouch(holder.getGiftObsceneTouchsCountTextView(),gift);
        configureLikeTouch(holder.getGiftLikeTouchsCountTextView(), gift);
        holder.getGiftTitleTextView().setText(gift.getTitle());
        holder.getGiftDescriptionTextView().setText(gift.getDescription());
        holder.getGiftLikeTouchsCountTextView().setText(gift.getLikeTouches().toString());
        Drawable img;
        img = activity.getResources().getDrawable(gift.isLikeTouched()? R.drawable.ic_heart_red:R.drawable.ic_heart);
        img.setBounds(0,0,120,120);
        holder.getGiftLikeTouchsCountTextView().setCompoundDrawables(img,null,null,null);

        holder.getGiftObsceneTouchsCountTextView().setText(gift.getObsceneTouches().toString());
        img = activity.getResources().getDrawable(gift.isObsceneTouched()?R.drawable.ic_warning_orange:R.drawable.ic_warning);
        img.setBounds(0,0,120,120);
        holder.getGiftObsceneTouchsCountTextView().setCompoundDrawables(img,null,null,null);

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

    private void configureGiftDoubleClick(ImageView giftThumb, final Gift gift) {
        giftThumb.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                final ImageView img = (ImageView) v;
                final ImageView heart = (ImageView) ((RelativeLayout)v.getParent()).findViewById(R.id.likeHeartImageView);

                heart.setImageDrawable(activity.getResources().getDrawable(gift.isLikeTouched()?R.drawable.ic_heart_unlike:R.drawable.ic_heart_like));
                heart.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(activity, R.anim.gift_click);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        heart.setVisibility(View.GONE);
                        try {
                            Long action = Long.parseLong(Integer.toString(gift.isLikeTouched()?GIFT_UNLIKE_TOUCH:GIFT_LIKE_TOUCH));
                            Gift result = new ManageTouchesTask().execute(action,gift.getId()).get();
                            int position = dataList.indexOf(gift);
                            dataList.add(position ,result);
                            dataList.remove(gift);

                            notifyDataSetChanged();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animation);
            }
        });
    }

    private void configureObsceneTouch(final TextView obsceneTouchTextView, final Gift gift){
        obsceneTouchTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    //Warning click, set/unset obscene
                    if(event.getRawX() <= (obsceneTouchTextView.getLeft() + obsceneTouchTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())-20) {
                        // your action here
                        handleObsceneTouch(gift);
                        return true;
                    }else{
                        // Counter click, open show users activity.
                        showObsceneTouchUsers(gift);
                    }
                }
                return false;
            }
        });
    }

    private void configureLikeTouch(final TextView giftLikeTouchsCountTextView, final Gift gift){
        giftLikeTouchsCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder.getInstance().setSelectedGift(gift);
                activity.startActivity(new Intent(activity, LikeTouchesUserActivity.class));
            }
        });

    }

    private void showObsceneTouchUsers(Gift gift) {
        //TODO show users that marked this gift as obscene
    }

    private void handleObsceneTouch(final Gift gift) {
        new AlertDialog.Builder(activity)
                .setTitle(gift.getTitle())
                .setMessage(activity.getResources().getString(R.string.confirm_dialog_obscene))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        try{
                            Long action = Long.parseLong(Integer.toString(gift.isObscene()?GIFT_REVERT_OBSCENE_TOUCH:GIFT_OBSCENE_TOUCH));
                            Log.d(TAG,"Action: "+action);
                            Gift result = new ManageTouchesTask().execute(action,gift.getId()).get();
                            int position = dataList.indexOf(gift);
                            dataList.add(position ,result);
                            dataList.remove(gift);
                            notifyDataSetChanged();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
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

    public Gift getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    class ManageTouchesTask extends AsyncTask<Long,Void,Gift>{

        private int action = 0;

        @Override
        protected Gift doInBackground(Long... params) {
            this.action = Integer.parseInt(Long.toString(params[0]));
            switch (action){
                case GIFT_LIKE_TOUCH:
                    return giftSvc.likeTouch(params[1]);
                case GIFT_UNLIKE_TOUCH:
                    return giftSvc.unlikeTouch(params[1]);
                case GIFT_OBSCENE_TOUCH:
                    return giftSvc.obsceneTouch(params[1]);
                case GIFT_REVERT_OBSCENE_TOUCH:
                    return giftSvc.revertObsceneTouch(params[1]);
                default:
                    return null;
            }
        }
    }

    class GiftViewHolder {
        @InjectView(R.id.giftTitleTextView)
        protected TextView giftTitleTextView;

        @InjectView(R.id.giftLikeTouchsCountTextView)
        protected TextView giftLikeTouchsCountTextView;

        @InjectView(R.id.giftObsceneTouchsCountTextView)
        protected TextView giftObsceneTouchsCountTextView;

        @InjectView(R.id.giftDescriptionTextView)
        protected TextView giftDescriptionTextView;

        @InjectView(R.id.giftImageView)
        protected ImageView giftThumb;

        @InjectView(R.id.giftImageProgressBar)
        protected ProgressBar progressBar;


        public GiftViewHolder(View row) {
            ButterKnife.inject(this, row);
        }

        public TextView getGiftTitleTextView() {
            return giftTitleTextView;
        }

        public void setGiftTitleTextView(TextView giftTitleTextView) {
            this.giftTitleTextView = giftTitleTextView;
        }

        public TextView getGiftLikeTouchsCountTextView() {
            return giftLikeTouchsCountTextView;
        }

        public void setGiftLikeTouchsCountTextView(TextView giftLikeTouchsCountTextView) {
            this.giftLikeTouchsCountTextView = giftLikeTouchsCountTextView;
        }

        public TextView getGiftObsceneTouchsCountTextView() {
            return giftObsceneTouchsCountTextView;
        }

        public void setGiftObsceneTouchsCountTextView(TextView giftObsceneTouchsCountTextView) {
            this.giftObsceneTouchsCountTextView = giftObsceneTouchsCountTextView;
        }

        public ImageView getGiftThumb() {
            return giftThumb;
        }

        public void setGiftThumb(ImageView giftThumb) {
            this.giftThumb = giftThumb;
        }

        public TextView getGiftDescriptionTextView() {
            return giftDescriptionTextView;
        }

        public void setGiftDescriptionTextView(TextView giftDescriptionTextView) {
            this.giftDescriptionTextView = giftDescriptionTextView;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }
    }

}