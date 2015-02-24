package client.potlach.com.potlachandroid.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.activity.BaseActivity;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.service.GiftSvc;
import client.potlach.com.potlachandroid.service.GiftSvcApi;
import client.potlach.com.potlachandroid.util.GiftListAdapter;
import client.potlach.com.potlachandroid.widget.EndlessScrollListener;

/**
 * Created by thiago on 10/8/14.
 */
public class HomeGiftFragment extends BaseFragment {
    private static final String TAG = "HomeGiftFragment";

    @InjectView(R.id.userGiftsListView)
    protected ListView userGiftsListView;
    private Long giftCount = 0l;
    private List<Gift> gifts = null;
    private GiftSvcApi giftSvc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_gift, container, false);
        ButterKnife.inject(this, rootView);
        giftSvc = GiftSvc.getOrShowLogin(activity);

        loadGiftsByUser(0);
        initUserGiftsListView();
        return rootView;
    }

    private void initUserGiftsListView() {
        if(giftCount > 0) {
            final GiftListAdapter adapter = new GiftListAdapter(activity, gifts);
            adapter.setServerListSize(Integer.parseInt(giftCount.toString()));
            userGiftsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                    Gift g = adapter.getDataList().get(position);

                }
            });
            userGiftsListView.setAdapter(adapter);
            final Long finalGiftCount = giftCount;
            userGiftsListView.setOnScrollListener(new EndlessScrollListener(1, 0) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if(totalItemsCount-1 < finalGiftCount){
//                        Log.d(TAG, "Loading more items, page: "+page+" totalItems: "+totalItemsCount);
//                        Log.d(TAG, "CurrentChainId: "+selectedChain.getId());

                        loadGiftsByUser(page);

                        if(gifts!=null){
                            adapter.getDataList().addAll(gifts);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void loadGiftsByUser(Integer page) {
        try {
            if(giftCount==0){
                giftCount = new CountGiftsTask().execute().get();
            }
            gifts = new LoadGiftsTask().execute(page).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class CountGiftsTask extends AsyncTask<Void,Void,Long> {

        @Override
        protected Long doInBackground(Void... params) {
            return giftSvc.countByUserAndObscene(app.getLoggedUser().getId(), app.isObsceneEnabled());
        }
    }

    class LoadGiftsTask extends AsyncTask<Integer, Void, List<Gift>>{

        @Override
        protected List<Gift> doInBackground(Integer... params) {
            int page = params[0];
            return giftSvc.findByUserAndPageAndObscene(app.getLoggedUser().getId(), app.isObsceneEnabled(), page);
        }
    }
}
