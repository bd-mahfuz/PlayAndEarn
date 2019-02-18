package com.kcirqueit.playandearn.adapter;

import android.os.Bundle;

import com.kcirqueit.playandearn.fragment.DynamicFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DynamicFragmentAdapter extends FragmentStatePagerAdapter {
    private int totalNoOfPage;

    private Bundle bundle;

    public DynamicFragmentAdapter(FragmentManager fm, int totalNoOfPage) {
        super(fm);
        this.totalNoOfPage = totalNoOfPage;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        Fragment frag = DynamicFragment.newInstance();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return totalNoOfPage;
    }

    public void addBundle(Bundle bundle){
        this.bundle = bundle;
    }

}