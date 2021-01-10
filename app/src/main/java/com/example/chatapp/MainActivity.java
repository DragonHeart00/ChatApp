package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chatapp.activities.SettingsActivity;
import com.example.chatapp.activities.StartActivity;
import com.example.chatapp.activities.UsersActivity;
import com.example.chatapp.controller.SectionsPagerAdapter;
import com.example.chatapp.fragment.ChatsFragment;
import com.example.chatapp.fragment.FriendsFragment;
import com.example.chatapp.fragment.RequestsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Solo Chat");


        //tabLayout
        mTabLayout = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.main_tab_pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mSectionsPagerAdapter.addFragment(new RequestsFragment(), "Request");
        mSectionsPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        mSectionsPagerAdapter.addFragment(new FriendsFragment(), "Friends");

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);



        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_person_add_24);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_chat_24);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_people_24);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.main_logout_btn){
             FirebaseAuth.getInstance().signOut();
             sendToStart();
         }


         if (item.getItemId() == R.id.settings_btn){
             Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
             startActivity(settingsIntent);
         }


        if (item.getItemId() == R.id.all_users_btn){
            Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }


        return true;
    }



}