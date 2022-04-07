package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import Helper.FirebaseConfig;
import Helper.Permission;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView civProfile;
    private AppCompatTextView tvChangeProfileImage;
    private AppCompatEditText etName, etEmail;
    private AppCompatButton btSaveChanges;

    private User loggedUser;

    private static final int GALLERY_SELECTION = 200;

    private StorageReference storage;

    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        loggedUser = Helper.FirebaseUser.getUserData();
        storage = FirebaseConfig.getStorage();

        userID = Helper.FirebaseUser.getUserID();

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_black);

        initializeComponents();

        Permission.validatePermissions(permissions, this, 1);

        FirebaseUser profile = Helper.FirebaseUser.getCurrentUser();
        etName.setText(profile.getDisplayName());
        etEmail.setText(profile.getEmail());

        Uri url = profile.getPhotoUrl();
        if(url != null){

            Glide.with(EditProfile.this)
                    .load(url)
                    .into(civProfile);
        }else{

            civProfile.setImageResource(R.drawable.avatar);
        }

        btSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedName = etName.getText().toString();

                Helper.FirebaseUser.updateUserName(updatedName);

                loggedUser.setName(updatedName);
                loggedUser.setNameLowerCase(updatedName);
                loggedUser.update();

            }
        });

        tvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) == null){

                    startActivityForResult(intent, GALLERY_SELECTION);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap image = null;

            try{

                switch (requestCode){

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }

                if(image != null){

                    civProfile.setImageBitmap(image);

                    //Recover image data to Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70,baos);
                    byte[] imageData = baos.toByteArray();

                    //Save image in Firebase
                    StorageReference imageRef = storage
                            .child("Images")
                            .child("Profile")
                            .child(userID + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditProfile.this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri url = task.getResult();
                                    updateProfileImage(url);

                                    Toast.makeText(EditProfile.this, "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }

    private void updateProfileImage(Uri url){

        //Update image in Firebase profile
        Helper.FirebaseUser.updateProfileImage(url);

        //Update image in Real Time Database
        loggedUser.setUrlImage(url.toString());
        loggedUser.update();

        Toast.makeText(EditProfile.this, "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();


    }

    private void initializeComponents(){

        civProfile = findViewById(R.id.civEditProfile);

        tvChangeProfileImage = findViewById(R.id.tvChangeProfileImage);

        etName = findViewById(R.id.etNameEditProfile);
        etEmail = findViewById(R.id.etEmailEditProfile);

        btSaveChanges = findViewById(R.id.btSaveChanges);

        etEmail.setFocusable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int resultPermission : grantResults){

            if(resultPermission == PackageManager.PERMISSION_DENIED){

                alertNeededPermissions();
            }
        }
    }

    public void alertNeededPermissions(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o Instagram é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.pink1));
    }
}