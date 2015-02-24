package client.potlach.com.potlachandroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.activity.GiftListActivity;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.service.UserSvc;
import client.potlach.com.potlachandroid.service.UserSvcApi;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.UserListAdapter;
import client.potlach.com.potlachandroid.widget.EndlessScrollListener;

/**
 * Created by thiago on 10/8/14.
 */
public class HomeUserFragment extends BaseFragment {
    private static final String TAG = "HomeUserFragment";

    @InjectView(R.id.topGiversListView)
    protected ListView topGiversListView;
    private UserSvcApi userSvcApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_user, container, false);
        ButterKnife.inject(this, rootView);

        initTopGiversListView();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        userSvcApi = UserSvc.getOrShowLogin(activity);
    }

    private void initTopGiversListView() {
        Long topGiversCount = 0l;
        List<User> users = null;
        try {
            topGiversCount = new CountAllUsersTask().execute().get();
            if(topGiversCount!=0)
                users = new LoadTopGiversByPageTask().execute(0).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(topGiversCount > 0){
            final UserListAdapter adapter = new UserListAdapter(getActivity(), users);
            adapter.setServerListSize(Integer.parseInt(topGiversCount.toString()));
            topGiversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    User u = adapter.getDataList().get(position);
                    if (u != null) {
                        //TODO set top giver on DataHolder and start GiftListActivity
                        DataHolder.getInstance().setSelectedUser(u);
                        Intent i = new Intent(getActivity(), GiftListActivity.class);
                        i.putExtra(GiftListActivity.SEARCH_ACTION, GiftListActivity.SearchGiftActionEnum.SEARCH_BY_USER);
                        i.putExtra(GiftListActivity.SEARCH_USER,u.getUsername());
                        startActivity(i);
                    }
                }
            });

            topGiversListView.setAdapter(adapter);
            final Long finalTopGiversCount = topGiversCount;
            topGiversListView.setOnScrollListener(new EndlessScrollListener(1, 0) {
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
            });
        }

    }

    class LoadTopGiversByPageTask extends AsyncTask<Integer,Integer,List<User>>{

        @Override
        protected List<User> doInBackground(Integer... params) {

            return userSvcApi.findByNumberOfTouchesDescAndPage(params[0]);
        }
    }

    class CountAllUsersTask extends AsyncTask<Void,Void,Long> {

        @Override
        protected Long doInBackground(Void... params) {
            return userSvcApi.countAll();
        }
    }
}
