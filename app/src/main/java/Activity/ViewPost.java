package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.instagram.R;

import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPost extends AppCompatActivity {

    private CircleImageView civProfile;
    private AppCompatTextView tvName;
    private AppCompatImageView ivPost;
    private AppCompatTextView tvLikes, tvDescription, tvViewComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        //Toolbar config
        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Visualizar postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_black);

        initializeAndConfigComponents();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            Post post = (Post) bundle.getSerializable("post");
            User user = (User) bundle.getSerializable("selectedUser");

            //Show user data
            if(user.getUrlImage() != null && !user.getUrlImage().isEmpty()){

                Uri url = Uri.parse(user.getUrlImage());
                Glide.with(ViewPost.this).load(url).into(civProfile);

            }else{

                civProfile.setImageResource(R.drawable.avatar);
            }

            tvName.setText(user.getName());


            //Show post data
            if(post.getUrlImage() != null && !post.getUrlImage().isEmpty()){

                Uri url = Uri.parse(user.getUrlImage());
                Glide.with(ViewPost.this).load(url).into(ivPost);

            }else{

                civProfile.setImageResource(R.drawable.avatar);
            }

            tvDescription.setText(post.getDescription());
        }
    }

    private void initializeAndConfigComponents(){

        civProfile = findViewById(R.id.civProfileVP);
        tvName = findViewById(R.id.tvNameVP);
        ivPost = findViewById(R.id.ivPostVP);
        tvLikes = findViewById(R.id.tvLikesVP);
        tvDescription = findViewById(R.id.tvDescription);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}