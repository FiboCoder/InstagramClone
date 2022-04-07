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

public class Feed extends Fragment {

    private RecyclerView rvFeed;
    private Adapter.Feed adapter;
    private List<Model.Feed> feedList = new ArrayList<>();

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

        return view;
    }

    private void initAndConfigComponents(View view){

        //Int and config Recycler View

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

    @Override
    public void onStart() {
        listFeed();
        super.onStart();
    }

    @Override
    public void onStop() {
        feedRef.removeEventListener(valueEventListenerListFeed);
        super.onStop();
    }
}