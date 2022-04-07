package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.instagram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Fragment.Feed;
import Fragment.Post;
import Fragment.Profile;
import Fragment.Search;
import Helper.FirebaseConfig;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("INSTAGRAM");
        setSupportActionBar(toolbar);

        bottomNavigationConfig();
    }

    private void bottomNavigationConfig(){

        BottomNavigationView bottomNavigationView = findViewById(R.id.bnvMain);

        //Enable navigation
        enableNavigation(bottomNavigationView);

        //Set standard fragment to "Feed"
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.vpMain, new Feed()).commit();

        //Set menu item selected to "Feed"
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    private void enableNavigation(BottomNavigationView bottomNavigationView){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){

                    case R.id.bnmHome:
                        fragmentTransaction.replace(R.id.vpMain, new Feed()).commit();
                        return true;

                    case R.id.bnmSearch:
                        fragmentTransaction.replace(R.id.vpMain, new Search()).commit();
                        return true;

                    case R.id.bnmPost:
                        fragmentTransaction.replace(R.id.vpMain, new Post()).commit();
                        return true;

                    case R.id.bnmProfile:
                        fragmentTransaction.replace(R.id.vpMain, new Profile()).commit();
                        return true;

                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:

                logoutUser();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){

        try {

            auth = FirebaseConfig.getAuth();
            auth.signOut();

        }catch (Exception e){

            e.printStackTrace();
        }
    }
}