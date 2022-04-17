package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.example.instagram.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;

public class AddStoryActivity extends AppCompatActivity {

    //Components
    private AppCompatImageView ivPreAddStory;
    private AppCompatButton btnGallery, btnCamera, btnCancel, btnConfirm;
    private LinearLayoutCompat llButtons1, llButtons2;

    private Uri imageUri;
    private String imageUrl = "";
    private StorageTask storageTask;
    private StorageReference storage;

    private static final int GALLERY_SELECTION = 100;
    private static final int CAMERA_SELECTION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        initAndConfigComponents();
    }

    private void initAndConfigComponents(){

        ivPreAddStory = findViewById(R.id.ivPreAddStory);

        llButtons1 = findViewById(R.id.llButtons1);
        llButtons2 = findViewById(R.id.llButtons2);

        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnGallery = findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, GALLERY_SELECTION);

                    llButtons1.setVisibility(View.GONE);
                }
            }
        });

        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, CAMERA_SELECTION);

                    llButtons1.setVisibility(View.GONE);
                }
            }
        });

        storage = FirebaseConfig.getStorage();

    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void publishStory(){

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Postando");
        pd.show();

        if(imageUri != null){

            final StorageReference imageReference = storage.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            storageTask = imageReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){

                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        Uri downloadUri = (Uri) task.getResult();
                        imageUrl = downloadUri.toString();

                        String currentUserId = FirebaseUser.getUserID();

                        DatabaseReference reference = FirebaseConfig.getReference()
                                .child("Story")
                                .child(currentUserId);

                        String storyId = reference.push().getKey();
                        long timeEnd = System.currentTimeMillis()+86400000;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl", imageUrl);
                        hashMap.put("timeStart", ServerValue.TIMESTAMP);
                        hashMap.put("timeEnd", timeEnd);
                        hashMap.put("storyId", storyId);
                        hashMap.put("userId", currentUserId);

                        reference.child(storyId).setValue(hashMap);
                        pd.dismiss();
                        finish();
                    }else{

                        Toast.makeText(AddStoryActivity.this, "Falha!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{

            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            llButtons2.setVisibility(View.VISIBLE);

            Bitmap image = null;

            try{

                switch (requestCode){

                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }

                if(image != null){

                    ivPreAddStory.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
                    imageUri = Uri.parse(path);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            finish();
                        }
                    });

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            publishStory();
                        }
                    });
                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }
}