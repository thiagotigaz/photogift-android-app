package client.potlach.com.potlachandroid.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.model.Chain;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.widget.GenericAdapter;

public class UserListAdapter extends GenericAdapter<User> {
    private Context ctx;
    private UserViewHolder holder;
    private Activity activity;

    public UserListAdapter(Activity activity, List<User> objects) {
        super(activity, objects);
        ctx = activity.getBaseContext();
        this.activity = activity;
    }

    @Override
    public View getDataRow(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            row = inflater.inflate(R.layout.single_row_user, parent, false);
            holder = new UserViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (UserViewHolder) row.getTag();
        }

        User user = getItem(position);
        holder.getUserNameTextView().setText(user.getUsername());
        holder.getUserLikeTouchesCountTextView().setText(user.getNumberOfTouches().toString());
        holder.getUserGiftsCountTextView().setText(user.getNumberOfGifts().toString());

        return row;
    }

    @Override
    public View getFooterView(int position, View convertView, ViewGroup parent) {
        return super.getFooterView(position, convertView, parent);
    }

    public User getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    class UserViewHolder{

        @InjectView(R.id.userNameTextView)
        protected TextView userNameTextView;
        @InjectView(R.id.userLikeTouchesCountTextView)
        protected TextView userLikeTouchesCountTextView;
        @InjectView(R.id.userGiftsCountTextView)
        protected TextView userGiftsCountTextView;

        UserViewHolder(View row) {
            ButterKnife.inject(this,row);
        }

        public TextView getUserNameTextView() {
            return userNameTextView;
        }

        public void setUserNameTextView(TextView userNameTextView) {
            this.userNameTextView = userNameTextView;
        }

        public TextView getUserLikeTouchesCountTextView() {
            return userLikeTouchesCountTextView;
        }

        public void setUserLikeTouchesCountTextView(TextView userLikeTouchesCountTextView) {
            this.userLikeTouchesCountTextView = userLikeTouchesCountTextView;
        }

        public TextView getUserGiftsCountTextView() {
            return userGiftsCountTextView;
        }

        public void setUserGiftsCountTextView(TextView userGiftsCountTextView) {
            this.userGiftsCountTextView = userGiftsCountTextView;
        }
    }

}
