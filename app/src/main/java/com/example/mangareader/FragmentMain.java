package com.example.mangareader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;


public class FragmentMain extends Fragment {
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.animationView2);
        LottieAnimationView lottieAnimationView1 = view.findViewById(R.id.animationView1);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.main));
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) lottieAnimationView1.getLayoutParams();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) lottieAnimationView.getLayoutParams();
        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (85 * scale + 0.5f);
        int pixels1 = (int) (40 * scale + 0.5f);
        params.setMargins(0,pixels,0,0);
        params1.setMargins(0,0,0,pixels1);
        return view;
    }
}