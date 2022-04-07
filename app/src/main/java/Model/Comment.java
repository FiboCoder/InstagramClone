package Model;

import com.google.firebase.database.DatabaseReference;

import Helper.FirebaseConfig;

public class Comment {

    private String id;
    private String postId;
    private String userId;
    private String urlImage;
    private String name;
    private String comment;

    public Comment() {
    }

    public boolean save(){

        DatabaseReference commentsRef = FirebaseConfig.getReference()
                .child("Comments")
                .child(getPostId());
        String key = commentsRef.push().getKey();
        setId(key);
        commentsRef.child(getId()).setValue(this);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
