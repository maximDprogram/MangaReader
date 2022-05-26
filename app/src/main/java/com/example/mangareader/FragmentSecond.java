package com.example.mangareader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class FragmentSecond extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second,container,false);
        TabLayout tableLayout = view.findViewById(R.id.tablemain);
        ViewPager viewPager = view.findViewById(R.id.viewpager);

        tableLayout.setupWithViewPager(viewPager);

        assert getFragmentManager() != null;
        VPAdapter vpAdapter = new VPAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new Fragment1(), "Онгоинги");
        vpAdapter.addFragment(new Fragment2(), "Завершенные");
        viewPager.setAdapter(vpAdapter);
        return view;
    }
}