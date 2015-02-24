package client.potlach.com.potlachandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.service.UserSvc;
import client.potlach.com.potlachandroid.service.UserSvcApi;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.UserListAdapter;
import client.potlach.com.potlachandroid.widget.EndlessScrollListener;

public class LikeTouchesUserActivity extends BaseActivity {

    @InjectView(R.id.likeTouchesUsersListView)
    protected ListView giftLikeUsersListView;
    private UserSvcApi userSvcApi;
    private Gift selectedGift;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_touches_user);
        ButterKnife.inject(this);
        selectedGift = DataHolder.getInstance().getSelectedGift();
        setActionBarTitle(selectedGift.getTitle() +"'S LIKES",true);
        userSvcApi = UserSvc.getOrShowLogin(this);
        initLikeTouchesUsersListView();
    }

    private void initLikeTouchesUsersListView() {
        Long likeTouchesUsersCount = 0l;
        List<User> users = null;
        try {
                users = new LoadTopGiversByPageTask().execute(selectedGift.getId()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final UserListAdapter adapter = new UserListAdapter(this, users);
        adapter.setServerListSize(users.size());
        giftLikeUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    User u = adapter.getDataList().get(position);
                    if (u != null) {
                        //TODO set top giver on DataHolder and start GiftListActivity
                        DataHolder.getInstance().setSelectedUser(u);
                        Intent i = new Intent(LikeTouchesUserActivity.this, GiftListActivity.class);
                        i.putExtra(GiftListActivity.SEARCH_ACTION, GiftListActivity.SearchGiftActionEnum.SEARCH_BY_USER);
                        i.putExtra(GiftListActivity.SEARCH_USER,u.getUsername());
                        startActivity(i);
                    }
                }
            });

        giftLikeUsersListView.setAdapter(adapter);
        /*  final Long finalTopGiversCount = likeTouchesUsersCount;
        giftLikeUsersListView.setOnScrollListener(new EndlessScrollListener(1, 0) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if(totalItemsCount-1< finalTopGiversCount){
                        try {
                            List<User> result = new LoadTopGiversByPageTask().execute(page).get();
                            adapter.getDataList().addAll(result);
                            adapter.notifyDataSetChanged();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_like_touches_user, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadTopGiversByPageTask extends AsyncTask<Long,Void,List<User>> {

        @Override
        protected List<User> doInBackground(Long... params) {

            return userSvcApi.findByLikeTouchGiftId(params[0]);
        }
    }
}
