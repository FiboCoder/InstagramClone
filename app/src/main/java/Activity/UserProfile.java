package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.Grid;
import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    //Components
    private ProgressBar pbProfile;
    private CircleImageView civProfile;
    private AppCompatTextView tvPosts, tvFollowers, tvFollowing;
    private AppCompatButton btActProfile;
    private GridView gvProfile;

    //Database
    private DatabaseReference reference;
    private DatabaseReference usersRef;
    private DatabaseReference userProfileRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference followersRef;
    private DatabaseReference followingRef;
    private DatabaseReference postsUserRef;
    private ValueEventListener valueEventListenerUserProfile;

    //user
    private User selectedUser;
    private User loggedUser;
    private String loggedUserId;

    //Adapter
    private Grid adapter;

    //Posts List
    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initAndConfigComponents();

        //Database references config
        reference = FirebaseConfig.getReference();
        usersRef = reference.child("Users");
        followersRef = reference.child("Followers");
        followingRef = reference.child("Following");

        //User config
        loggedUserId = FirebaseUser.getUserID();

        recoverLoggedUserData();

        //Recover selected user and user data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            selectedUser = (User) bundle.getSerializable("selectedUser");

            //Config ref to user posts
            postsUserRef = reference
            .child("Posts")
            .child(selectedUser.getId());

            //Set selected username to toolbar title
            getSupportActionBar().setTitle(selectedUser.getName());

            //Get and set selected user image
            if(selectedUser.getUrlImage() != null && !selectedUser.getUrlImage().isEmpty()){

                Uri url = Uri.parse(selectedUser.getUrlImage());
                Glide.with(getApplicationContext()).load(url).into(civProfile);

            }else{

                civProfile.setImageResource(R.drawable.avatar);
            }
        }

        initImageLoader();
        loadPostsImages();

        gvProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Post post = posts.get(position);
                Intent intent = new Intent(getApplicationContext(), ViewPost.class);
                intent.putExtra("post", post);
                intent.putExtra("selectedUser", selectedUser);
                startActivity(intent);


            }
        });
    }

    private void initAndConfigComponents(){

        //Init and config toolbar
        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_black);

        //Init and config components
        pbProfile = findViewById(R.id.pbProfile);
        civProfile = findViewById(R.id.civProfile);
        tvPosts = findViewById(R.id.tvPostsProfile);
        tvFollowers = findViewById(R.id.tvFollowersProfile);
        tvFollowing = findViewById(R.id.tvFollowingProfile);

        //Init and config Button Action Profile
        btActProfile = findViewById(R.id.btActProfile);
        btActProfile.setText("Carregando");

        //Init and config Grid View
        gvProfile = findViewById(R.id.gvProfile);
        int gridSize = getResources().getDisplayMetrics().widthPixels;
        int imageSize = gridSize / 3;
        gvProfile.setColumnWidth(imageSize);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void initImageLoader(){

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();

        ImageLoader.getInstance().init(configuration);
        
    }

    public void loadPostsImages(){

        posts = new ArrayList<>();

        postsUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> urlImages = new ArrayList<>();
                for(DataSnapshot ds : snapshot.getChildren()){

                    Post post = ds.getValue(Post.class);

                    posts.add(post);
                    urlImages.add(post.getUrlImage());

                }

                adapter = new Grid(getApplicationContext(), R.layout.gv_image,urlImages);
                gvProfile.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recoverLoggedUserData(){

        loggedUserRef = usersRef.child(loggedUserId);

        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                loggedUser = snapshot.getValue(User.class);

                followUserVerification();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void followUserVerification(){

        DatabaseReference followerRef = followersRef
                .child(selectedUser.getId())
                .child(loggedUserId);

        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    changeButtonConfig(true);

                }else{

                    changeButtonConfig(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void changeButtonConfig(boolean followUser){

        if(followUser){

            btActProfile.setText("Seguindo");
        }else{

            btActProfile.setText("Seguir");

            btActProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveFollowerAndFollowing(loggedUser, selectedUser);
                }
            });
        }

    }

    private void saveFollowerAndFollowing(User lUser, User pUser){

        //Config user data to save a follower
        HashMap<String, Object> loggedUserData = new HashMap<>();
        loggedUserData.put("name", lUser.getName());
        loggedUserData.put("urlImage", lUser.getUrlImage());

        DatabaseReference followerRef = followersRef
                .child(pUser.getId())
                .child(lUser.getId());

        followerRef.setValue(loggedUserData);

        //Config user data to save a following
        HashMap<String, Object> selectedUserData = new HashMap<>();
        selectedUserData.put("name", pUser.getName());
        selectedUserData.put("urlImage", pUser.getUrlImage());

        DatabaseReference followingRef2 = followingRef
                .child(lUser.getId())
                .child(pUser.getId());

        followingRef2.setValue(selectedUserData);

        btActProfile.setText("Seguindo");
        btActProfile.setOnClickListener(null);


        //Change following numb to logged user
        int following = lUser.getFollowing() + 1;

        HashMap<String, Object> followingData = new HashMap<>();
        followingData.put("following", following);

        DatabaseReference followingUser = usersRef
                .child(lUser.getId());

        followingUser.updateChildren(followingData);

        //Change followers numb to selected user
        int followers = pUser.getFollowers() + 1;

        HashMap<String, Object> followersData = new HashMap<>();
        followersData.put("followers", followers);

        DatabaseReference followersUser = usersRef
                .child(pUser.getId());

        followersUser.updateChildren(followersData);


    }


    private void recoverProfileData(){

        userProfileRef = usersRef.child(selectedUser.getId());
        valueEventListenerUserProfile = userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                String followers = String.valueOf(user.getFollowers());
                String following = String.valueOf(user.getFollowing());
                String posts = String.valueOf(user.getPosts());

                tvFollowers.setText(followers);
                tvFollowing.setText(following);
                tvPosts.setText(posts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        recoverLoggedUserData();
        recoverProfileData();
        initImageLoader();
        loadPostsImages();
        super.onStart();
    }

    @Override
    protected void onStop() {
        userProfileRef.removeEventListener(valueEventListenerUserProfile);
        super.onStop();
    }
}