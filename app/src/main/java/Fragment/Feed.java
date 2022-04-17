package Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Story;
import de.hdodenhof.circleimageview.CircleImageView;

public class Feed extends Fragment {

    private RecyclerView rvFeed;
    private Adapter.Feed adapter;
    private List<Model.Feed> feedList = new ArrayList<>();

    //Story
    private RecyclerView rvStories;
    private List<Story> storyList = new ArrayList<>();
    private List<String> followingList = new ArrayList<>();
    private Adapter.Story storyAdapter;

    //Database
    private DatabaseReference reference;
    private DatabaseReference feedRef;
    private ValueEventListener valueEventListenerListFeed;

    private String loggedUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initAndConfigComponents(view);
        listFeed();
        recoverFollowingList();
        listStories();

        return view;
    }

    private void initAndConfigComponents(View view){

        //Int and config Recycler View to Stories
        storyAdapter = new Adapter.Story(storyList, getContext());
        rvStories = view.findViewById(R.id.rvStories);
        rvStories.setHasFixedSize(true);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvStories.setAdapter(storyAdapter);


        //Int and config Recycler View to Feed

        adapter = new Adapter.Feed(view.getContext(), feedList);
        rvFeed = view.findViewById(R.id.rvFeed);
        rvFeed.setHasFixedSize(true);
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeed.setAdapter(adapter);

        //User configs
        loggedUserId = FirebaseUser.getUserID();

        //Config database references
        reference = FirebaseConfig.getReference();
        feedRef = reference.child("Feed").child(loggedUserId);


    }

    private void listFeed(){

        valueEventListenerListFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                feedList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){

                    feedList.add(ds.getValue(Model.Feed.class));

                }

                Collections.reverse(feedList);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recoverFollowingList(){

        DatabaseReference followingRef = FirebaseConfig.getReference()
                .child("Following")
                .child(loggedUserId);

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                followingList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    String userId = ds.getKey();
                    followingList.add(userId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listStories(){

        String currentUserId = FirebaseUser.getUserID();

        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long timeCurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story(currentUserId, "", 0, 0, ""));
                for(String id : followingList){

                    int countStory = 0;
                    Story story = null;

                    for(DataSnapshot ds : snapshot.child(id).getChildren()){

                        story = ds.getValue(Story.class);
                        if(timeCurrent > story.getTimeStart() ){

                            countStory++;
                        }
                    }

                    if(countStory > 0){

                        storyList.add(story);
                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        listFeed();
        recoverFollowingList();
        listStories();
        super.onStart();
    }

    @Override
    public void onResume() {
        listFeed();
        recoverFollowingList();
        listStories();
        super.onResume();
    }

    @Override
    public void onStop() {
        feedRef.removeEventListener(valueEventListenerListFeed);
        super.onStop();
    }
}