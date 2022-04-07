package Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static DatabaseReference reference;
    private static FirebaseAuth auth;
    public static StorageReference storage;

    public static DatabaseReference getReference(){

        if(reference == null){

            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }

    public static FirebaseAuth getAuth(){

        if(auth == null){

            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static StorageReference getStorage(){

        if(storage == null){

            storage = FirebaseStorage.getInstance().getReference();
        }

        return storage;
    }
}
