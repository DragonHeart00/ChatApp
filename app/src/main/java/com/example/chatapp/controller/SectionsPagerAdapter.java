package com.example.chatapp.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.fragment.ChatsFragment;
import com.example.chatapp.fragment.FriendsFragment;
import com.example.chatapp.fragment.RequestsFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> listFragment = new ArrayList<>();
    private final List<String> listTitle = new ArrayList<>();


    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
            }

    @Override
    public int getCount() {
        return listTitle.size();
    }

    public CharSequence getPageTitle(int position){
        return super.getPageTitle(position);
    }

    //add fragment
    public void addFragment(Fragment fragment, String title){
        listFragment.add(fragment);
        listTitle.add(title);

    }
}
