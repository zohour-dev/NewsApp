package jo.zohour_zo3bi.android_app_developer.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    private static final String REQUEST_URL = "https://content.guardianapis.com/search";
    private StoryAdapter storyAdapter;
    private List<Story> newsStories = new ArrayList<>();

    private TextView emptyStateTextView;
    private ProgressBar progressBar;

    private TextView sectionNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyStateTextView = findViewById(R.id.empty_state_text_view);
        progressBar = findViewById(R.id.progress_bar);
        sectionNameTextView = findViewById(R.id.section_name_text_view);

        // Check if there is an internet connection!
        if (!checkInternetConnection()) {
            emptyStateTextView.setText(R.string.no_internet);
            emptyStateTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            sectionNameTextView.setVisibility(View.GONE);
            return;
        }//end if

        // Defining a recycler view and setting a linear manager for it
        RecyclerView storyRecyclerView = findViewById(R.id.news_recycler_view);
        LinearLayoutManager manger = new LinearLayoutManager(this);
        storyRecyclerView.setLayoutManager(manger);

        // Attaching an adapter to the recycler view for the purpose of binding data
        storyAdapter = new StoryAdapter(this, newsStories);
        storyRecyclerView.setAdapter(storyAdapter);

        // Initializing a loader for the purpose of fetching news data
        LoaderManager.getInstance(this).initLoader(0, null, this);
    }//end onCreate()

    @NonNull
    @Override
    public Loader<List<Story>> onCreateLoader(int id, @Nullable Bundle args) {

        // Get the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        // this is for the "section" field
        String section = sharedPref.getString(getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));

        // this is for the "order-by" field
        String orderBy = sharedPref.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=json`
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor"); // for showing the story writer name
        uriBuilder.appendQueryParameter("section", section); // story section
        uriBuilder.appendQueryParameter("from-date", "2021-01-01"); // the date from which stories are retrieved
        uriBuilder.appendQueryParameter("show-fields", "thumbnail"); // the story thumbnail
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "e5a0dc68-eb8a-4d66-8b49-ee8c08ed6bce"); // personal api-key

        // Create a new Async loader and passing the created query url
        return new StoryAsyncLoader(this, uriBuilder.toString());
    }//end onCreateLoader()

    @Override
    public void onLoadFinished(@NonNull Loader<List<Story>> loader, List<Story> data) {
        // Check if the list of data retrieved is empty or null
        // and make the empty state text view visible
        if (data == null || data.isEmpty()) {
            emptyStateTextView.setText(R.string.empty_state);
            emptyStateTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            sectionNameTextView.setVisibility(View.GONE);
            return;
        }//end if
        // Make the empty state text view and progress bar non visible and pass
        // the retrieved data list to the adapter to be bind into the recycler view
        emptyStateTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        storyAdapter.setNewsStoriesList(data);

        // Setting the section name heading text view value
        String sectionName = data.get(0).getStorySection();
        sectionNameTextView.setText(sectionName);
    }//end onLoadFinished()

    @Override
    public void onLoaderReset(@NonNull Loader<List<Story>> loader) {
        // Loader reset, so we can clear out our existing data.
        storyAdapter.setNewsStoriesList(null);
    }//end onLoaderReset()

    /**
     * Check if there is an internet connection or not
     */
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connectionStatus = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null && cm.getActiveNetwork() != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null) {
                // connected to the internet
                connectionStatus = true;
            }//end if
        } else {
            if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                // connected to the internet
                connectionStatus = true;
            }//end if
        }//end if-else
        return connectionStatus;
    }//end checkInternetConnection()

    // Creating an option menu with one item inside "Settings"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }//end onCreateOptionsMenu()

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings_menu_item) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(settingsIntent);
            return true;
        }//end if
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected()
}//end MainActivity
