package com.thesis.ClientApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thesis.ClientApp.notifications.Token;
import com.thesis.ClientApp.shareChat.ChatFragment;
import com.thesis.ClientApp.user.HomeFragment;
import com.thesis.ClientApp.user.ProfileFragment;
import com.thesis.ClientApp.search.SearchFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    private String userId;
    com.thesis.ClientApp.SharedPref sharedPref;



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new com.thesis.ClientApp.SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(nevigationSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        db.getReference("UserLogTime").child(userId).child("logintime").setValue(String.valueOf(System.currentTimeMillis()));

    }

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(userId).setValue(mToken);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener nevigationSelected =
            item -> {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();

                        break;
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.nav_group:
                        selectedFragment = new com.thesis.ClientApp.MapsFragment();
                        break;
                    case R.id.nav_chat:
                        selectedFragment = new ChatFragment();
                        break;
                    case R.id.nav_user:
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        editor.apply();
                        selectedFragment = new ProfileFragment();
                        break;
                }
                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };
}


