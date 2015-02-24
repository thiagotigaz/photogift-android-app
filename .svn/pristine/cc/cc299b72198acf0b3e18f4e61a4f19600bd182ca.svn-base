package client.potlach.com.potlachandroid.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.StringUtils;

import java.util.Locale;

import butterknife.ButterKnife;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.fragment.HomeChainFragment;
import client.potlach.com.potlachandroid.fragment.HomeGiftFragment;
import client.potlach.com.potlachandroid.fragment.HomeUserFragment;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.HomeViewPager;

public class HomeActivity extends BaseActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    HomeViewPager mViewPager;


    protected EditText searchEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("error")){
            String error = getIntent().getExtras().getString("error");
            Log.d("PhotoGift", error);
        }
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (HomeViewPager) findViewById(R.id.pager_home);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchEditText = (EditText) searchItem.getActionView().findViewById(R.id.searchGiftEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // hide virtual keyboard
                InputMethodManager imm =
                        (InputMethodManager) HomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                Toast.makeText(
                        HomeActivity.this,
                        "Search text: " + searchEditText.getText(),
                        Toast.LENGTH_SHORT).show();
                searchItem.collapseActionView();
                String searchTitle = searchEditText.getText().toString().trim();
                if(!StringUtils.isEmptyString(searchTitle)){
                    DataHolder.getInstance().setSelectedChain(null);
                    Intent i = new Intent(HomeActivity.this,GiftListActivity.class);
                    i.putExtra(GiftListActivity.SEARCH_ACTION, GiftListActivity.SearchGiftActionEnum.SEARCH_BY_TITLE);
                    i.putExtra(GiftListActivity.SEARCH_TITLE, searchTitle);
                    startActivity(i);
                }
                searchEditText.setText("");
                return true;
            }
        });
        final MenuItem logoutItem = menu.findItem(R.id.action_logout);
        logoutItem.setTitle("Logout  ("+ app.getLoggedUser().getUsername()+")");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;
        switch (id){
            case R.id.action_new_gift:
                i = new Intent(this, PostGiftActivity.class);
                startActivity(i);
                return true;
            case R.id.action_logout:
                app.logout(HomeActivity.this);
                return true;
            case R.id.action_settings:
                i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.action_search:
                item.expandActionView();
                searchEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return new HomeChainFragment();
                case 1:
                    return new HomeGiftFragment();
                case 2:
                    return new HomeUserFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_giftchain).toUpperCase(l);
                case 1:
                    return getString(R.string.title_fragment_gift).toUpperCase(l);
                case 2:
                    return getString(R.string.title_fragment_user).toUpperCase(l);
            }
            return null;
        }
    }

}
