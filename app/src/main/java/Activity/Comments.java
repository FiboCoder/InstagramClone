package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Comment;
import Model.User;

public class Comments extends AppCompatActivity {

    //Components
    private RecyclerView rvComments;
    private AppCompatEditText etComment;
    private AppCompatButton btSendComment;
    private Adapter.Comment adapter;
    private List<Comment> commentsList = new ArrayList<>();

    //Database
    private DatabaseReference reference;
    private DatabaseReference commentsRef;
    private ValueEventListener valueEventListenerCommentsRef;

    private String postId;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initAndConfigComponents();
    }

    private void initAndConfigComponents(){

        //Init and config Toolbar

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_black);

        //Recover post data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            postId = bundle.getString("feedId");

        }

        //Init and config components
        etComment = findViewById(R.id.etComment);

        //Init and config RecyclerView
        adapter = new Adapter.Comment(getApplicationContext(), commentsList);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setHasFixedSize(true);
        rvComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvComments.setAdapter(adapter);

        //Firebase configs
        reference = FirebaseConfig.getReference();

        //User configs
        user = FirebaseUser.getUserData();

        btSendComment = findViewById(R.id.btSendComment);
        btSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveComments();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void saveComments(){

        String comment = etComment.getText().toString();
        if(comment!= null && !comment.equals("")){

            Comment comment1 = new Comment();
            comment1.setPostId(postId);
            comment1.setUserId(user.getId());
            comment1.setName(user.getName());
            comment1.setUrlImage(user.getUrlImage());
            comment1.setId(user.getId());
            comment1.setComment(comment);
            if(comment1.save()){

                etComment.setText("");
                Toast.makeText(Comments.this, "Sucesso ao enviar comentário!", Toast.LENGTH_SHORT).show();
            }

        }else{

            Toast.makeText(Comments.this, "Insira um comentário antes de enviar!", Toast.LENGTH_SHORT).show();

        }
    }

    private void recoverComments(){

        commentsRef = reference.child("Comments")
                .child(postId);

        valueEventListenerCommentsRef = commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){

                    Comment comment = ds.getValue(Comment.class);
                    commentsList.add(comment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    protected void onStart() {
        recoverComments();
        super.onStart();
    }

    @Override
    protected void onStop() {
        commentsRef.removeEventListener(valueEventListenerCommentsRef);
        super.onStop();
    }
}