package com.example.tann.androidchatapp;

/**
 * Created by Tann on 10/31/2018.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                RequestsFragment requestsFragment=new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragmen chatsFragmen=new ChatsFragmen();
                return chatsFragmen;
            case 2:
                FriendsFragment friendsFragment= new FriendsFragment();
                return friendsFragment;
                default:return null;
        }
    }

    @Override
    public int getCount() {
        //3 bao gồm ChatsFragmen,FriendFragmen,RequestsFragmen
        return 3;

    }

    public CharSequence getPageTitle(int Position){
        switch (Position)
        {
            case 0:
                return "ReQuests";
            case 1:
                return "Groups";
            case 2:
                return "Friends";
                default:return null;
        }
    }
}

