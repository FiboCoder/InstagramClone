package Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import Activity.EditProfile;
import Adapter.Grid;
import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends Fragment {


    //Components
    private ProgressBar pbProfile;
    private CircleImageView civProfile;
    private AppCompatTextView tvPosts, tvFollowers, tvFollowing;
    private AppCompatButton btActProfile;
    private GridView gvProfile;

    //Firebase
    private DatabaseReference reference;
    private DatabaseReference usersRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference postsRef;
    private ValueEventListener valueEventListenerLoggedUser;

    //User utils
    private User loggedUser;
    private String loggedUserId;

    //Adapter
    private Grid adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initAndConfigComponents(view);
        initializeImageLoader();
        recoverProfileImage();
        loadPostsImages();

        return view;
    }

    private void initAndConfigComponents(View view){

        //Initialize components
        pbProfile = view.findViewById(R.id.pbProfile);
        civProfile = view.findViewById(R.id.civProfile);
        tvPosts = view.findViewById(R.id.tvPostsProfile);
        tvFollowers = view.findViewById(R.id.tvFollowersProfile);
        tvFollowing = view.findViewById(R.id.tvFollowingProfile);

        //Init and config Button Action Profile
        btActProfile = view.findViewById(R.id.btActProfile);
        btActProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });

        //Database references config
        reference = FirebaseConfig.getReference();
        usersRef = reference.child("Users");

        loggedUserId = FirebaseUser.getUserID();

        loggedUser = FirebaseUser.getUserData();

        //Initialize and config Grid View
        gvProfile = view.findViewById(R.id.gvProfile);
        int gridSize = getResources().getDisplayMetrics().widthPixels;
        int imageSize = gridSize / 3;
        gvProfile.setColumnWidth(imageSize);

    }

    public void initializeImageLoader(){

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();

        ImageLoader.getInstance().init(configuration);

    }

    public void recoverLoggedUserData(){


        loggedUserRef = usersRef.child(loggedUserId);

        valueEventListenerLoggedUser = loggedUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                tvPosts.setText(String.valueOf(user.getPosts()));
                tvFollowers.setText(String.valueOf(user.getFollowers()));
                tvFollowing.setText(String.valueOf(user.getFollowing()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recoverProfileImage(){

        //Recover logged user profile image
        loggedUser = FirebaseUser.getUserData();
        if(loggedUser.getUrlImage() != null && !loggedUser.getUrlImage().isEmpty()){

            Uri url = Uri.parse(loggedUser.getUrlImage());
            Glide.with(getContext()).load(url).into(civProfile);
        }else{

            civProfile.setImageResource(R.drawable.avatar);

        }

    }

    public void loadPostsImages(){

        postsRef = reference
                .child("Posts")
                .child(loggedUserId);

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> urlImage = new ArrayList<>();
                for(DataSnapshot ds : snapshot.getChildren()) {

                    Post post = ds.getValue(Post.class);

                    urlImage.add(post.getUrlImage());
                }
                adapter = new Grid(getContext(), R.layout.gv_image, urlImage);
                gvProfile.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        recoverLoggedUserData();
        recoverProfileImage();
        super.onStart();
    }

    @Override
    public void onResume() {
        recoverLoggedUserData();
        recoverProfileImage();
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recoverLoggedUserData();
        recoverProfileImage();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        loggedUserRef.removeEventListener(valueEventListenerLoggedUser);
        super.onStop();
    }

}