package client.potlach.com.potlachandroid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.model.Chain;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.model.User;
import client.potlach.com.potlachandroid.oauth.PhotoGiftRestBuilder;
import client.potlach.com.potlachandroid.service.GiftSvc;
import client.potlach.com.potlachandroid.service.GiftSvcApi;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.GiftListAdapter;
import client.potlach.com.potlachandroid.widget.EndlessScrollListener;


public class GiftListActivity extends BaseActivity{

    private static final String TAG = "GiftListActivity";

    public static final String SEARCH_ACTION = "searchGiftsAction";
    public static final String SEARCH_TITLE = "searchTitle";
    public static final String SEARCH_USER = "searchUser";
    public static final String SEARCH_GIFT = "searchGiftId";

    @InjectView(R.id.giftListView)
    protected ListView giftListView;
    private GiftSvcApi giftSvc;
    private Long giftCount = 0l;
    private List<Gift> gifts = null;
    private Chain selectedChain;
    private User selectedUser;
    private Gift selectedGift;
    private String searchVar;
    private String actionBarTitle;
    private SearchGiftActionEnum searchAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_list);
        ButterKnife.inject(this);
        giftSvc = GiftSvc.getOrShowLogin(this);
        Bundle extras = getIntent().getExtras();
        if(extras!=null && extras.containsKey(SEARCH_ACTION)){
            searchAction = (SearchGiftActionEnum) extras.getSerializable(SEARCH_ACTION);
        }

        switch (searchAction){
            case SEARCH_BY_TITLE:
                searchVar = extras.getString(SEARCH_TITLE);
                actionBarTitle = searchVar;
                break;
            case SEARCH_BY_USER:
                selectedUser = DataHolder.getInstance().getSelectedUser();
                searchVar = extras.getString(SEARCH_USER);
                actionBarTitle = searchVar + "'S GIFTS";
                break;
            case SEARCH_BY_CHAIN:
                selectedChain = DataHolder.getInstance().getSelectedChain();
                actionBarTitle = selectedChain.getName();
                break;
            case SEARCH_BY_GIFT:
                if(PhotoGiftRestBuilder.isLoggedIn()){
                    Log.d("GiftListActivity", "usuario logado!");

                    selectedGift = DataHolder.getInstance().getSelectedGift();
                    actionBarTitle = selectedGift.getTitle();
                }else{
                    Log.d("GiftListActivity", "usuario nao logado!");
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                    return;
                }
                break;

        }

        setActionBarTitle(actionBarTitle.toUpperCase(), true);
        try {
            giftCount = new CountGiftsTask().execute().get();
            gifts = new LoadGiftsTask().execute(0).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initGiftListView();
        //Load chain from asyncTask, pass gifts from chain to
        //adapter.
    }


    private void loadGiftsBySearchTitle(String page) {
        try {
            if(giftCount==0){
                giftCount = new CountGiftsByTitleTask().execute(searchVar).get();
            }
            gifts = new LoadGiftsByTitleAndPage().execute(searchVar, page).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void loadGiftsByUser(Integer page) {
        //TODO refactor this code to fetch gifts and count by title
        try {
            if(giftCount==0){
                giftCount = new CountGiftsByTitleTask().execute(searchVar).get();
            }
            gifts = giftSvc.findByUserAndPageAndObscene(selectedUser.getId(), app.isObsceneEnabled(), page);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void loadGiftsByChain(Long page) {
        selectedChain.setGifts(null);
        if(selectedChain.getGifts()==null){
            try {
                if(giftCount==0)
                    giftCount = new CountGiftsByChainIdTask().execute(selectedChain.getId()).get();
                gifts = new LoadGiftsByChainIdAndPage().execute(selectedChain.getId(), page).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }else{
            gifts = selectedChain.getGifts();
            giftCount = Long.parseLong(Integer.toString(gifts.size())) ;
        }
    }

    private void initGiftListView() {
        if(giftCount > 0) {
            final GiftListAdapter adapter = new GiftListAdapter(this, gifts);
            adapter.setServerListSize(Integer.parseInt(giftCount.toString()));
            giftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                    Gift g = adapter.getDataList().get(position);

                }
            });
            giftListView.setAdapter(adapter);
            final Long finalGiftCount = giftCount;
            giftListView.setOnScrollListener(new EndlessScrollListener(1, 0) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if(totalItemsCount-1 < finalGiftCount){
//                        Log.d(TAG, "Loading more items, page: "+page+" totalItems: "+totalItemsCount);
//                        Log.d(TAG, "CurrentChainId: "+selectedChain.getId());

                        switch (searchAction){

                            case SEARCH_BY_TITLE:
                                loadGiftsBySearchTitle(Integer.toString(page));
                                break;
                            case SEARCH_BY_USER:
                                loadGiftsByUser(page);
                                break;
                            case SEARCH_BY_CHAIN:
                                loadGiftsByChain(Long.parseLong(Integer.toString(page)));
                                break;
                        }

                        if(gifts!=null){
                            adapter.getDataList().addAll(gifts);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gift_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_new_gift:
                Intent i = new Intent(this, PostGiftActivity.class);
                if(selectedChain !=null)
                    i.putExtra("chainName", selectedChain.getName());
                startActivity(i);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadGiftsByChainIdAndPage extends AsyncTask<Long,Integer,List<Gift>>{

        @Override
        protected List<Gift> doInBackground(Long... params) {
//            Log.d(TAG,params[0].toString() + "  "+params[1].toString());
            Long chainId = params[0];
            Integer page = Integer.parseInt(params[1].toString());
//            Log.d(TAG,chainId + "  "+page);
            return giftSvc.findByChainIdAndPageAndObscene(chainId,page, app.isObsceneEnabled());
        }
    }

    class LoadGiftsByTitleAndPage extends AsyncTask<String,Integer,List<Gift>>{

        @Override
        protected List<Gift> doInBackground(String... params) {
            Integer page = Integer.parseInt(params[1]);
            return giftSvc.findByTitleAndObsceneFlagAndPage(params[0], app.isObsceneEnabled(),page);
        }
    }

    class LoadGiftsTask extends AsyncTask<Integer, Void, List<Gift>>{

        @Override
        protected List<Gift> doInBackground(Integer... params) {
            int page = params[0];
            switch (searchAction){
                case SEARCH_BY_TITLE:
                    return giftSvc.findByTitleAndObsceneFlagAndPage(searchVar, app.isObsceneEnabled(), page);
                case SEARCH_BY_USER:
                    return giftSvc.findByUserAndPageAndObscene(selectedUser.getId(), app.isObsceneEnabled(), page);
                case SEARCH_BY_CHAIN:
                    return giftSvc.findByChainIdAndPageAndObscene(selectedChain.getId(), page, app.isObsceneEnabled());
                case SEARCH_BY_GIFT:
                    ArrayList<Gift> result = new ArrayList<Gift>();
                    result.add(selectedGift);
                    return result;
            }
            return null;
        }
    }

    class CountGiftsByChainIdTask extends AsyncTask<Long,Void,Long>{

        @Override
        protected Long doInBackground(Long... params) {
                return giftSvc.countByChainIdAndObscene(params[0], app.isObsceneEnabled());
        }
    }

    class CountGiftsByTitleTask extends AsyncTask<String,Void,Long>{

        @Override
        protected Long doInBackground(String... params) {
            return giftSvc.countByGiftTitleAndObscene(params[0],app.isObsceneEnabled());
        }
    }

    class CountGiftsTask extends AsyncTask<Void,Void,Long>{

        @Override
        protected Long doInBackground(Void... params) {
            switch (searchAction){
                case SEARCH_BY_TITLE:
                    return giftSvc.countByGiftTitleAndObscene(searchVar, app.isObsceneEnabled());
                case SEARCH_BY_USER:
                    return giftSvc.countByUserAndObscene(selectedUser.getId(), app.isObsceneEnabled());
                case SEARCH_BY_CHAIN:
                    return giftSvc.countByChainIdAndObscene(selectedChain.getId(), app.isObsceneEnabled());
                case SEARCH_BY_GIFT:
                    return 1l;
            }
            return null;
        }
    }

    public enum SearchGiftActionEnum{
        SEARCH_BY_TITLE, SEARCH_BY_USER, SEARCH_BY_CHAIN, SEARCH_BY_GIFT;
    }
}
