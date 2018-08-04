package com.example.android.newsapp.feature;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.net.Uri;
import android.app.LoaderManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {


    SwipeRefreshLayout swipe;
    private int LOADER_ID = 0;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private DrawerLayout mDrawerLayout;
    private String mSect = "all";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View loadingIndicator = findViewById(R.id.loading_indicator);


        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Handle navigation view item clicks here.
                        if (menuItem.getItemId() == R.id.nav_all) {
                            mSect = "all";
                            LOADER_ID = 0;
                        } else if (menuItem.getItemId() == R.id.nav_world) {
                            mSect = "world";
                            LOADER_ID = 1;
                        } else if (menuItem.getItemId() == R.id.nav_tech) {
                            LOADER_ID = 2;
                            mSect = "technology";
                        } else if (menuItem.getItemId() == R.id.nav_politics) {
                            LOADER_ID = 3;
                            mSect = "politics";
                        } else if (menuItem.getItemId() == R.id.nav_business) {
                            LOADER_ID = 6;
                            mSect = "business";
                        } else if (menuItem.getItemId() == R.id.nav_society) {
                            LOADER_ID = 5;
                            mSect = "society";
                        } else if (menuItem.getItemId() == R.id.nav_lifestyle) {
                            LOADER_ID = 7;
                            mSect = "lifeandstyle";
                        } else if (menuItem.getItemId() == R.id.nav_film) {
                            LOADER_ID = 8;
                            mSect = "film";
                        } else if (menuItem.getItemId() == R.id.nav_opinion) {
                            LOADER_ID = 9;
                            mSect = "commentisfree";
                        } else if (menuItem.getItemId() == R.id.nav_sport) {
                            LOADER_ID = 10;
                            mSect = "sport";
                        }
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        loadingIndicator.setVisibility(View.VISIBLE);
                        mEmptyStateTextView.setVisibility(View.INVISIBLE);

                        // Add code here to update the UI based on the item selected
                        mAdapter.clear();
                        mAdapter.notifyDataSetChanged();
                        onRefresh();


                        return true;
                    }
                });

        swipe = findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(this);

        ListView listView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);


        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = mAdapter.getItem(i);
                String url = news.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {

            loadingIndicator.setVisibility(View.GONE);
            // Otherwise, display error
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }


    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String pageCount = sharedPrefs.getString(
                getString(R.string.settings_page_key),
                getString(R.string.settings_page_default)
        );
        return new NewsLoader(this, QueryUtils.createStringUrl(orderBy, pageCount, mSect));
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        swipe.setRefreshing(false);
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Set empty state text to display "No News found."
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_news);
        } else {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        if (data != null && !data.isEmpty()) {
            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();
            mAdapter.setNotifyOnChange(true);
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        mEmptyStateTextView.setVisibility(View.GONE);
    }

}
