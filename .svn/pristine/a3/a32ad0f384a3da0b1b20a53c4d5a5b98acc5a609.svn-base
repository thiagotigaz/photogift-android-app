package client.potlach.com.potlachandroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import client.potlach.com.potlachandroid.model.Chain;
import client.potlach.com.potlachandroid.service.ChainSvc;
import client.potlach.com.potlachandroid.service.ChainSvcApi;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.ChainListAdapter;
import client.potlach.com.potlachandroid.widget.EndlessScrollListener;

/**
 * Created by thiago on 10/8/14.
 */
public class HomeChainFragment extends BaseFragment {

    private static final String TAG = "HomeChainFragment";

    @InjectView(R.id.chainListView)
    protected ListView chainListView;
    private ChainSvcApi chainSvc;
    private Chain chain;

    // ***************************************
    // Fragment methods
    // ***************************************

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_chain, container, false);
        ButterKnife.inject(this, rootView);

        initChainListView();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chainSvc = ChainSvc.getOrShowLogin(activity);
    }

    // ***************************************
    // Custom methods
    // ***************************************

    private void initChainListView() {
        Long chainCount = 0l;
        List<Chain> chains = null;
        try {
            chainCount = new CountAllChainTask().execute().get();
            if(chainCount!=0)
                chains = new LoadChainByPageTask().execute(0).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(chainCount > 0){
            Log.d(TAG,"chains: "+chains);
            final ChainListAdapter adapter = new ChainListAdapter(getActivity(), chains);
            adapter.setServerListSize(Integer.parseInt(chainCount.toString()));
            chainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                    Log.d("PhotoGift", "listview clicked");

                    Chain c = adapter.getDataList().get(position);
                    if (c != null) {
                        DataHolder.getInstance().setSelectedChain(c);
                        Intent i = new Intent(getActivity(), GiftListActivity.class);
                        i.putExtra(GiftListActivity.SEARCH_ACTION, GiftListActivity.SearchGiftActionEnum.SEARCH_BY_CHAIN);
                        startActivity(i);
                    }
                }
            });

            chainListView.setAdapter(adapter);
            final Long finalChainCount = chainCount;
            chainListView.setOnScrollListener(new EndlessScrollListener(1, 0) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if(totalItemsCount-1< finalChainCount){
                        Log.d(TAG, "Loading more items, page: "+page+" totalItems: "+totalItemsCount);

                        try {
                            List<Chain> result = new LoadChainByPageTask().execute(page).get();
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

    @Override
    public void onResume() {
        initChainListView();
        super.onResume();
    }

    class LoadChainByPageTask extends AsyncTask<Integer,Integer,List<Chain>>{

        @Override
        protected List<Chain> doInBackground(Integer... params) {
            Integer page = params[0];

            return chainSvc.findByPage(page);
        }
    }

    class CountAllChainTask extends AsyncTask<Void,Void,Long>{

        @Override
        protected Long doInBackground(Void... params) {
            return chainSvc.countAll();
        }
    }

}
