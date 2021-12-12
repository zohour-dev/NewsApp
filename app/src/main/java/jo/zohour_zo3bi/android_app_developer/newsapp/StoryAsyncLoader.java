package jo.zohour_zo3bi.android_app_developer.newsapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class StoryAsyncLoader extends AsyncTaskLoader<List<Story>> {

    private String url;

    public StoryAsyncLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }//end constructor

    @Nullable
    @Override
    public List<Story> loadInBackground() {
        if (url == null) {
            return null;
        }//end if
        return QueryUtils.fetchNewsStoriesList(url);
    }//end loadInBackground()

    @Override
    protected void onStartLoading() {
        forceLoad();
    }//end onStartLoading()
}//end StoryAsyncLoader
