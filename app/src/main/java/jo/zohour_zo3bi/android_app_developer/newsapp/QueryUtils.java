package jo.zohour_zo3bi.android_app_developer.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String STORY_TIME_STRING_SEPARATOR = "T"; // used to split the publication date from the publication time in hours

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }//end constructor

    /**
     * Fetch the list of news stories retrieved by the provided query url and return it
     *
     * @param stringUrl the query url
     * @return list of {@link Story}s
     */
    public static List<Story> fetchNewsStoriesList(String stringUrl) {
        // 1. Create URL object
        URL queryUrl = createUrl(stringUrl);
        // 2. Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(queryUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }//end try-catch

        // 3. Extract relevant fields from the JSON response and create a list of {@link Story}s
        return extractNewsStoriesList(jsonResponse);
    }//end fetchNewsStoriesList()

    /**
     * Create a URL object and return it
     *
     * @param stringUrl the query url in string format
     * @return url
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Error with creating URL", e);
        }//end try-catch
        return url;
    }//end createUrl()

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param queryUrl URL object
     * @return jsonResponse String of json format
     * @throws IOException Throws an IO Exception
     */
    private static String makeHttpRequest(URL queryUrl) throws IOException {
        String jsonResponse = "";

        if (queryUrl == null) {
            return jsonResponse;
        }//end if

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }//end if-else
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news stories JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }//end if
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }//end if
        }//end try-catch-finally
        return jsonResponse;
    }//end makeHttpRequest()

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }//end while
        }//end if
        return output.toString();
    }//end readFromStream()

    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Story> extractNewsStoriesList(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }//end if

        // Create an empty ArrayList that we can start adding stories to
        List<Story> newsStories = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert SAMPLE_JSON_RESPONSE String into a JSONObject
            JSONObject strObject = new JSONObject(jsonResponse);

            // Extract the JSONObject with the key called "response"
            JSONObject responseObject = strObject.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of stories.
            JSONArray results = responseObject.optJSONArray("results");

            // For each story in the results array, create an {@link Story} object
            for (int i = 0; i < results.length(); i++) {
                // Get a single story JSONObject at position i
                JSONObject storyObject = results.getJSONObject(i);

                // Extract the value for the key called "webTitle" which represents the storyTitle
                String storyTitle = storyObject.getString("webTitle");

                // Extract the value for the key called "sectionName" which represents the storySection name
                String storySection = storyObject.getString("sectionName");

                // Extract the value for the key called "webPublicationDate" which represents the storyTime
                String storyTime = storyObject.getString("webPublicationDate");

                // Extract the value for the key called "webUrl" which represents the storyUrl
                String storyUrl = storyObject.getString("webUrl");

                // For a given story, extract the JSONObject associated with the
                // key called "fields", which represents a list of all fields
                // for that story.
                JSONObject fieldsObject = storyObject.getJSONObject("fields");

                // Extract the value for the key called "thumbnail" which represents the storyThumbnail
                String storyThumbnail = fieldsObject.getString("thumbnail");

                // Extract the JSONArray associated with the key called "tags"
                JSONArray tagsArray = storyObject.getJSONArray("tags");
                String storyAuthorName;
                // Check if the tags array is not empty
                if (tagsArray.length() != 0) {
                    JSONObject tagsFirstObject = tagsArray.getJSONObject(0);
                    // Extract the value for the key called "webTitle" which represents the storyAuthorName
                    storyAuthorName = tagsFirstObject.getString("webTitle");
                } else {
                    // Set the author name to "Un Named" if the tags array is empty
                    storyAuthorName = "Un Named";
                }//end if-else

                // Use the String split method to split the publication date from the publication time in hours
                String[] splitStoryDate = storyTime.split(STORY_TIME_STRING_SEPARATOR);

                // Create a new {@Link Story} object using the data extracted
                Story newsStory = new Story(storyTitle, storySection, storyAuthorName, splitStoryDate[0], storyThumbnail, storyUrl);

                // Add the created {@Link Story} object to the stories list
                newsStories.add(newsStory);
            }//end for
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news story JSON results", e);
        }//end try-catch
        return newsStories;
    }//end extractNewsStoriesList()
}//end QueryUtils
