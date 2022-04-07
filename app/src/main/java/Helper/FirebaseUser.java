package Helper;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import Model.User;

public class FirebaseUser {

    public  static com.google.firebase.auth.FirebaseUser getCurrentUser(){

        FirebaseAuth user = FirebaseConfig.getAuth();

        return user.getCurrentUser();
    }

    public static void updateUserName(String name){

        try{

            //Recover logged user
            com.google.firebase.auth.FirebaseUser user = getCurrentUser();

            //Create and config object to change profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){



                    }

                }
            });

        }catch(Exception e){

            e.printStackTrace();

        }
    }

    public static void updateProfileImage(Uri url){

        try{

            //Recover logged user
            com.google.firebase.auth.FirebaseUser user = getCurrentUser();

            //Create and config object to change profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()){

                    }

                }
            });

        }catch(Exception e){

            e.printStackTrace();

        }
    }

    public static User getUserData(){

        com.google.firebase.auth.FirebaseUser firebaseUser = FirebaseUser.getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setId(firebaseUser.getUid());

        if(firebaseUser.getPhotoUrl() == null){

            user.setUrlImage("");

        }else{

            user.setUrlImage(firebaseUser.getPhotoUrl().toString());

        }

        return user;
    }

    public static String getUserID(){

        return getCurrentUser().getUid();
    }
}
