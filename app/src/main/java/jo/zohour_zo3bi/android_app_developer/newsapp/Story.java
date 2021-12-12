package jo.zohour_zo3bi.android_app_developer.newsapp;

public class Story {
    private String storyTitle;
    private String storySection;
    private String storyAuthor;
    private String storyTime;
    private String storyImageUrl;
    private String storyUrl;

    public Story(String storyTitle, String storySection, String storyAuthor, String storyTime, String storyImageUrl, String storyUrl) {
        this.storyTitle = storyTitle;
        this.storySection = storySection;
        this.storyAuthor = storyAuthor;
        this.storyTime = storyTime;
        this.storyUrl = storyUrl;
        this.storyImageUrl = storyImageUrl;
    }//end constructor

    public String getStoryTitle() {
        return storyTitle;
    }//end getStoryTitle()

    public String getStorySection() {
        return storySection;
    }//end getStorySection()

    public String getStoryAuthor() {
        return storyAuthor;
    }//end getStoryAuthor()

    public String getStoryTime() {
        return storyTime;
    }//end getStoryTime()

    public String getStoryImageUrl() {
        return storyImageUrl;
    }//end getStoryImageResourceId()

    public String getStoryUrl() {
        return storyUrl;
    }//end getStoryUrl()
}//end NewsStory
