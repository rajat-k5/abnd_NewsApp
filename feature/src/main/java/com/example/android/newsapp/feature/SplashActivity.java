package com.example.android.newsapp.feature;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.List;

public class SplashActivity extends Activity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


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
            // Otherwise, display error
            // Update empty state with no connection error message
            Intent x = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(x);

            // close this activity
            finish();

        }


    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, QueryUtils.createStringUrl("relevance", "5", "all"));
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        // This method will be executed once the loading is over
        // Start your app main activity
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);

        // close this activity
        finish();
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
    }


}

