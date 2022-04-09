package Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;

public class Post implements Serializable {

    private String id;
    private String userId;
    private String description;
    private String urlImage;

    public Post() {

        DatabaseReference firebaseRef = FirebaseConfig.getReference();
        DatabaseReference postRef = firebaseRef.child("Posts");

        String postId = postRef.push().getKey();
        setId(postId);
    }

    public boolean save(DataSnapshot followersSnapshot){

        Map object = new HashMap();
        User loggedUser = FirebaseUser.getUserData();
        DatabaseReference firebaseRef = FirebaseConfig.getReference();

        //Post reference
        String idCombination = "/" + getUserId() + "/" + getId();
        object.put("/Posts" + idCombination, this);

        //Feed reference
        for(DataSnapshot ds : followersSnapshot.getChildren()){

            String followersId = ds.getKey();

            //Config object
            HashMap<String, Object> followerData = new HashMap<>();

            followerData.put("image", getUrlImage());
            followerData.put("description", getDescription());
            followerData.put("id", getId());

            followerData.put("name", loggedUser.getName());
            followerData.put("userImage", loggedUser.getUrlImage());

            String updateId = "/" + followersId + "/" + getId();
            object.put("Feed" + updateId, followerData);
        }

        firebaseRef.updateChildren(object);

        return true;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
