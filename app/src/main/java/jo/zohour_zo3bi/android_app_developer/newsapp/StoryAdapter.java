package jo.zohour_zo3bi.android_app_developer.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context context;
    private List<Story> newsStoriesList;

    public StoryAdapter(Context context, List<Story> newsStories) {
        this.context = context;
        this.newsStoriesList = newsStories;
    }//end StoryAdapter class constructor

    // Inflating an item layout from xml and returning a view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(context, itemView);
    }//end onCreateViewHolder()

    // Populating data into the item view components through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story newsStory = newsStoriesList.get(position); // get the view item to bind data to its sub-views

        // bind data to the item sub-views
        holder.storyTitle.setText(newsStory.getStoryTitle());
        holder.storySection.setText(newsStory.getStorySection());
        holder.storyAuthor.setText(newsStory.getStoryAuthor());
        holder.storyTime.setText(newsStory.getStoryTime());

        // use the glide library to fetch the story thumbnail from its provided
        // url and bind it later to the story image view
        Glide.with(context).load(newsStory.getStoryImageUrl()).into(holder.storyImage);
    }//end onBindViewHolder()

    @Override
    public int getItemCount() {
        return newsStoriesList.size();
    }//end getItemCount()

    /**
     * Fill in the story list with the stories fetched using the loader
     *
     * @param newsStories List of the fetched news stories
     */
    public void setNewsStoriesList(List<Story> newsStories) {
        newsStoriesList = newsStories;
        this.notifyDataSetChanged();
    }//end setNewsStoriesList()

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private TextView storyTitle;
        private TextView storySection;
        private TextView storyAuthor;
        private TextView storyTime;
        private ImageView storyImage;

        public ViewHolder(Context context, @NonNull View itemView) {
            super(itemView);

            // Find the sub views for each item view
            this.mContext = context;
            this.storyTitle = itemView.findViewById(R.id.title_text_view);
            this.storySection = itemView.findViewById(R.id.section_text_view);
            this.storyAuthor = itemView.findViewById(R.id.author_name_text_view);
            this.storyTime = itemView.findViewById(R.id.date_text_view);
            this.storyImage = itemView.findViewById(R.id.item_image_view);

            // Make each item row clickable by using setOnClickListener
            itemView.setOnClickListener(this);
        }//end ViewHolder class constructor

        // Set the action to be taken when an item view is clicked
        @Override
        public void onClick(View v) {
            // Get the clicked story position id, retrieve it and create an intent
            // that directs the user to the full story body using one of its device browsers
            // the story url is used to achieve this task
            int storyItemPosition = getAbsoluteAdapterPosition();
            Story selectedStory = newsStoriesList.get(storyItemPosition);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedStory.getStoryUrl()));
            mContext.startActivity(browserIntent);
        }//end onClick()
    }//end ViewHolder
}//end StoryAdapter
