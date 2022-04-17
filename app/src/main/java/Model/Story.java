package Model;

public class Story {

    private String userId;
    private String storyId;
    private long timeStart;
    private long timeEnd;
    private String imageUrl;

    public Story(String userId, String storyId, long timeStart, long timeEnd, String imageUrl) {
        this.userId = userId;
        this.storyId = storyId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.imageUrl = imageUrl;
    }

    public Story() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
