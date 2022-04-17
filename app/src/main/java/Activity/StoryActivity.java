package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Story;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private int counter = 0;
    private long pressTime = 0L;
    private long limit = 500L;

    private StoriesProgressView storiesProgressView;

    private AppCompatImageView ivStory;
    private CircleImageView civProfileStory;
    private AppCompatTextView tvStoryUsername;

    private List<String> images;
    private List<String> storyIds;
    private String userId;

    private LinearLayoutCompat llViews;
    private AppCompatTextView tvSeenNumber;
    private AppCompatImageView ivDeleteStory;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        iniAndConfigComponents();
    }

    private void iniAndConfigComponents(){

        storiesProgressView = findViewById(R.id.timeStoryBar);
        ivStory = findViewById(R.id.ivStory);
        civProfileStory = findViewById(R.id.civProfileStory);
        tvStoryUsername = findViewById(R.id.tvStoryUsername);
        llViews = findViewById(R.id.llViews);
        tvSeenNumber = findViewById(R.id.tvSeenNumber);
        ivDeleteStory = findViewById(R.id.ivDeleteStory);

        userId = getIntent().getStringExtra("userId");

        if(userId.equals(FirebaseUser.getUserID())){

            llViews.setVisibility(View.VISIBLE);
            ivDeleteStory.setVisibility(View.VISIBLE);

        }

        getStories(userId);
        userInfo(userId);

        View reverse = findViewById(R.id.vReverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.vSkip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        ivDeleteStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseConfig.getReference()
                        .child("Story")
                        .child(userId)
                        .child(storyIds.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(StoryActivity.this, "Story apagado!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onNext() {

        Glide.with(getApplicationContext()).load(images.get(++counter)).into(ivStory);
        addView(storyIds.get(counter));
        seeNumber(storyIds.get(counter));
    }

    @Override
    public void onPrev() {

        if((counter - 1) < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(ivStory);
        seeNumber(storyIds.get(counter));
    }

    @Override
    public void onComplete() {

        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userId){

        images = new ArrayList<>();
        storyIds = new ArrayList<>();
        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Story")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                images.clear();
                storyIds.clear();
                for(DataSnapshot ds : snapshot.getChildren()){

                    Story story = ds.getValue(Story.class);

                    long timeCurrent = System.currentTimeMillis();
                    if(timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()){

                        images.add(story.getImageUrl());
                        storyIds.add(story.getStoryId());
                    }
                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext()).load(images.get(counter)).into(ivStory);

                addView(storyIds.get(counter));
                seeNumber(storyIds.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userId){

        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Users")
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getUrlImage()).into(civProfileStory);
                tvStoryUsername.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyId){

        if(!userId.equals(FirebaseUser.getUserID())){

            DatabaseReference reference = FirebaseConfig.getReference()
                    .child("Story")
                    .child(userId)
                    .child(storyId)
                    .child("views")
                    .child(FirebaseUser.getUserID());
            reference.setValue(true);
        }

    }

    private void seeNumber(String storyId){

        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Story")
                .child(userId)
                .child(storyId)
                .child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tvSeenNumber.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}