package com.example.mangareader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    ViewFlipper flipper;

    Animation animFlipInForward;
    Animation animFlipOutForward;
    Animation animFlipInBackward;
    Animation animFlipOutBackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView lottieAnimationView = findViewById(R.id.animationView2);
        LottieAnimationView lottieAnimationView1 = findViewById(R.id.animationView1);
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) lottieAnimationView1.getLayoutParams();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) lottieAnimationView.getLayoutParams();
        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (85 * scale + 0.5f);
        int pixels1 = (int) (40 * scale + 0.5f);
        params.setMargins(0,pixels,0,0);
        params1.setMargins(0,0,0,pixels1);


        flipper = (ViewFlipper) findViewById(R.id.viewflipper);

        animFlipInForward = AnimationUtils.loadAnimation(this, R.anim.flipin1);
        animFlipOutForward = AnimationUtils.loadAnimation(this, R.anim.flipout1);
        animFlipInBackward = AnimationUtils.loadAnimation(this,
                R.anim.flipin_reverse1);
        animFlipOutBackward = AnimationUtils.loadAnimation(this,
                R.anim.flipout_reverse1);

        TabLayout tableLayout = findViewById(R.id.tablemain);
        ViewPager viewPager = findViewById(R.id.viewpager);

        tableLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new Fragment1(), "Онгоинги");
        vpAdapter.addFragment(new Fragment2(), "Завершенные");
        viewPager.setAdapter(vpAdapter);
    }

    public void onBackPressed() {
        finish();
    }


    private void SwipeLeft() {
        flipper.setInAnimation(animFlipInBackward);
        flipper.setOutAnimation(animFlipOutBackward);
        flipper.showPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        int lockswipe = 1;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {


            float sensitvity = 50;
            if (((e1.getY() - e2.getY()) > sensitvity) && lockswipe == 1) {
                SwipeLeft();
                lockswipe = 0;
            }

            return true;
        }
    };

    GestureDetector gestureDetector = new GestureDetector(getBaseContext(),
            simpleOnGestureListener);
}