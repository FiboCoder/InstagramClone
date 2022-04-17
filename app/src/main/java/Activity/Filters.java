package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.Post;
import Model.User;

public class Filters extends AppCompatActivity {

    //Components
    private AppCompatImageView ivSelectedImage;
    private Bitmap image;
    private AppCompatEditText etDescription;
    private ProgressDialog pd;

    //Database
    private DatabaseReference reference;
    private DatabaseReference usersRef;
    private DatabaseReference loggedUserRef;
    private ValueEventListener valueEventListenerLoggedUser;
    private DataSnapshot followersSnapshot;

    //User
    private User loggedUser;
    private String loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initAndConfigComponents();

        recoverPostData();

        //Recover image
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            byte[] imageData = bundle.getByteArray("selectedImage");
            image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            ivSelectedImage.setImageBitmap(image);

        }
    }

    private void initAndConfigComponents(){

        //Initialize and config toolbar
        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_black);

        //Init components
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        etDescription = findViewById(R.id.etDescriptionFilters);

        //Database references config
        reference = FirebaseConfig.getReference();

        //User ID config
        loggedUserId = FirebaseUser.getUserID();

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();
    }

    public void recoverPostData() {

        openDialogLoading("Carregando dados, aguarde...");

        usersRef = reference.child("Users");
        loggedUserRef = usersRef.child(loggedUserId);

        valueEventListenerLoggedUser = loggedUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                loggedUser = snapshot.getValue(User.class);

                DatabaseReference followersRef = reference
                        .child("Followers")
                        .child(loggedUserId);
                followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followersSnapshot = snapshot;
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openDialogLoading(String title){

        pd = new ProgressDialog(this);
        pd.setMessage(title);
        pd.show();


    }

    private void publishPost() {

        openDialogLoading("Salvando postagem...");
        Post post = new Post();
        post.setUserId(loggedUserId);
        post.setDescription(etDescription.getText().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference storageRef = FirebaseConfig.getStorage();
        StorageReference imageRef = storageRef
                .child("Images")
                .child("Posts")
                .child(post.getId() + ".jpeg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Filters.this, "Erro ao salvar a imagem, tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        //Recover local image
                        Uri url = task.getResult();
                        post.setUrlImage(url.toString());

                        //Update post quantity
                        int qttPosts = loggedUser.getPosts() + 1;
                        loggedUser.setPosts(qttPosts);
                        loggedUser.updatePostsQuantity();

                        //Save post
                        if (post.save(followersSnapshot)) {

                            Toast.makeText(Filters.this, "Sucesso ao salvar postagem!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            finish();

                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_filters, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.publishPost:
                publishPost();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
