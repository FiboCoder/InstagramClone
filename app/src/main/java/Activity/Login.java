package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

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

import Helper.FirebaseConfig;
import Model.User;

public class Login extends AppCompatActivity {

    private AppCompatEditText etEmail, etPass;
    private ProgressBar pbLogin;

    private User user;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verifyLoggedUser();
        initializeComponents();

    }

    public void login(View view){

        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(!email.isEmpty()){

            if(!pass.isEmpty()){

                user = new User();
                user.setEmail(email);
                user.setPass(pass);
                loginUser(user);

            }else{

                Toast.makeText(Login.this, "Por favor, digite a senha!", Toast.LENGTH_SHORT).show();

            }

        }else{

            Toast.makeText(Login.this, "Por favor, digite o e-mail!", Toast.LENGTH_SHORT).show();

        }

    }

    public void verifyLoggedUser(){

        auth = FirebaseConfig.getAuth();
        if(auth.getCurrentUser() != null){

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void loginUser(User user){

        pbLogin.setVisibility(View.VISIBLE);

        auth = FirebaseConfig.getAuth();
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPass()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    pbLogin.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {

                    pbLogin.setVisibility(View.GONE);

                    Toast.makeText(Login.this, "Erro ao fazer login!", Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void registerScreen(View view){

        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();

    }

    public void initializeComponents(){

        //Components config
        etEmail = findViewById(R.id.etEmailL);
        etPass = findViewById(R.id.etPassL);
        pbLogin = findViewById(R.id.pbLogin);

        etEmail.requestFocus();

        pbLogin.setVisibility(View.GONE);
    }
}