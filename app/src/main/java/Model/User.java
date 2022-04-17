package Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Helper.FirebaseConfig;

public class User implements Serializable {

    private String id;
    private String name;
    private String nameLowerCase;
    private String email;
    private String pass;
    private String urlImage = "";
    private int followers = 0;
    private int following = 0;
    private int posts = 0;

    public User() {
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference userRef = reference.child("Users");
                userRef.child(getId())
                .setValue(this);
    }

    public void saveFollowers(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference followersRef = reference
                .child("Followers")
                .child(getId())
                .child(getId());
        followersRef.setValue(getName());

        HashMap<String, Object> loggedUserData = new HashMap<>();
        loggedUserData.put("name", getName());
        loggedUserData.put("urlImage", getUrlImage());
        followersRef.setValue(loggedUserData);
    }

    public void updateUserAndFollowersNode(){

        DatabaseReference reference = FirebaseConfig.getReference();

        Map object = new HashMap();
        object.put("/Users/" + getId() + "/name", getName());
        object.put("/Users/" + getId() + "/urlImage", getUrlImage());
        object.put("/Users/" + getId() + "/nameLowerCase", getNameLowerCase());

        reference.updateChildren(object);

        DatabaseReference followerRef = reference
                .child("Followers")
                .child(getId())
                .child(getId());

        HashMap<String, Object> userObject = new HashMap<>();
        userObject.put("urlImage", getUrlImage());

        followerRef.updateChildren(userObject);

    }

    public void updatePostsQuantity(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference userRef = reference.child("Users").child(getId());

        HashMap<String, Object> data = new HashMap<>();
        data.put("posts", getPosts());
        userRef.updateChildren(data);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public void setNameLowerCase(String nameLowerCase) {
        this.nameLowerCase = nameLowerCase.toLowerCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
