package Model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import Helper.FirebaseConfig;

public class PostsLikes {

    public Feed feed;
    public User user;
    public int qttLikes = 0;

    public PostsLikes() {

    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();


        HashMap<String, Object> userData = new HashMap();
        userData.put("name", user.getName());
        userData.put("urlImage", user.getUrlImage());
        DatabaseReference postsLikesRef = reference.child("Posts-Likes")
                .child(feed.getId())
                .child(user.getId());

        postsLikesRef.setValue(userData);

        updateQuantity(1);
    }

    public void removeLike(){

        DatabaseReference reference = FirebaseConfig.getReference();

        DatabaseReference postsLikesRef = reference.child("Posts-Likes")
                .child(feed.getId())
                .child(user.getId());
        postsLikesRef.removeValue();

        updateQuantity(-1);

    }

    public void updateQuantity(int value){

        DatabaseReference reference = FirebaseConfig.getReference();

        DatabaseReference postsLikesRef = reference.child("Posts-Likes")
                .child(feed.getId())
                .child("qttLikes");
        setQttLikes(qttLikes + value);

        postsLikesRef.setValue(getQttLikes());

    }

    public int getQttLikes() {
        return qttLikes;
    }

    public void setQttLikes(int qttLikes) {
        this.qttLikes = qttLikes;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
