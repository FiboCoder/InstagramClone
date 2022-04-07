package Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.R;

import java.io.ByteArrayOutputStream;

import Activity.Filters;
import Helper.Permission;

public class Post extends Fragment {


    private AppCompatButton btOpenGallery, btOpenCamera;
    private static final int GALLERY_SELECTION = 100;
    private static final int CAMERA_SELECTION = 200;

    private String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        btOpenGallery = view.findViewById(R.id.btOpenGallery);
        btOpenCamera = view.findViewById(R.id.btOpenCamera);

        Permission.validatePermissions(permissions, getActivity(), 1);

        btOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getContext().getPackageManager()) == null){

                    startActivityForResult(intent, GALLERY_SELECTION);
                }

            }
        });
        btOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getContext().getPackageManager()) == null){

                    startActivityForResult(intent, CAMERA_SELECTION);
                }

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK){

            Bitmap image = null;

            try {

                switch (requestCode) {

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImage);
                        break;

                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if(image != null){

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    Intent intent = new Intent(getActivity(), Filters.class);
                    intent.putExtra("selectedImage", imageData);
                    startActivity(intent);

                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }
}