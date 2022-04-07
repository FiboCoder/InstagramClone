package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.User;

public class Register extends AppCompatActivity {

    private AppCompatEditText etName, etEmail, etPass;
    private ProgressBar pbRegister;

    private User user;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();

    }

    public void register(View view){

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(!name.isEmpty()){

            if(!email.isEmpty()){

                if(!pass.isEmpty()){

                    user = new User();
                    user.setName(name);
                    user.setNameLowerCase(name);
                    user.setEmail(email);
                    user.setPass(pass);
                    registerUser(user);

                }else{

                    Toast.makeText(Register.this, "Pro favor, digite a senha!", Toast.LENGTH_SHORT).show();

                }

            }else{

                Toast.makeText(Register.this, "Pro favor, digite o email!", Toast.LENGTH_SHORT).show();


            }

        }else{

            Toast.makeText(Register.this, "Pro favor, digite o nome!", Toast.LENGTH_SHORT).show();


        }

    }

    public void registerUser(User user){

        pbRegister.setVisibility(View.VISIBLE);

        auth = FirebaseConfig.getAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPass()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    try{

                        pbRegister.setVisibility(View.GONE);

                        //Save user data on Real Time Database
                        String userID = task.getResult().getUser().getUid();
                        user.setId(userID);
                        user.save();

                        //Save data on Firebase Profile
                        FirebaseUser.updateUserName(user.getName());


                        Toast.makeText(Register.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }catch (Exception e){

                        e.printStackTrace();

                    }

                }else{

                    pbRegister.setVisibility(View.GONE);

                    String exception = "";

                    try {

                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e){

                        exception = "Digite uma senha mais fortes!";
                    }catch (FirebaseAuthInvalidCredentialsException e){

                        exception = "Digite uma e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){

                        exception = "Já existe uma conta cadastrada com esse e-mail!";
                    }catch (Exception e){

                        exception = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(Register.this, exception, Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void loginScreen(View view){

        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();

    }

    public void initializeComponents(){

        //Components config
        etName = findViewById(R.id.etNameR);
        etEmail = findViewById(R.id.etEmailR);
        etPass = findViewById(R.id.etPassR);
        pbRegister = findViewById(R.id.pbRegister);

        etName.requestFocus();

        pbRegister.setVisibility(View.GONE);
    }
}