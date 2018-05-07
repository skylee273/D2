package btcore.co.kr.d2band.view.step;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import btcore.co.kr.d2band.databinding.ActivityStepBinding;
import btcore.co.kr.d2band.view.find.fragment.FragmentId;
import btcore.co.kr.d2band.view.find.fragment.FragmentPw;
import btcore.co.kr.d2band.view.main.MainActivity;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.view.profile.ProfileAcitivty;
import btcore.co.kr.d2band.view.step.fragment.StepMonthFragment;
import btcore.co.kr.d2band.view.step.fragment.StepTodayFragment;
import btcore.co.kr.d2band.view.step.fragment.StepWeekFragment;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivity extends AppCompatActivity {

    ActivityStepBinding mStepBinding;
    FragmentPagerAdapter mPagerAdapter = null;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mStepBinding = DataBindingUtil.setContentView(this, R.layout.activity_step);
        mStepBinding.setStepActivity(this);

        initView();
    }

    @OnClick(R.id.btn_profile)
    public void OnProfile(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }
    private void initView(){


        mPagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mStepBinding.viewPagerStep.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        mStepBinding.viewPagerStep.setCurrentItem(0);
        mStepBinding.viewPagerStep.setOffscreenPageLimit(3);
        mStepBinding.viewPagerStep.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mStepBinding.tabLayoutActivity));
        mStepBinding.tabLayoutActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mStepBinding.viewPagerStep.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public class pagerAdapter extends FragmentPagerAdapter {

        public static final String ARG_PAGE = "page";

        public pagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new StepTodayFragment();
                    return fragment;
                case 1:
                    fragment = new StepWeekFragment();
                    return fragment;
                case 2:
                    fragment = new StepMonthFragment();
                    return fragment;
                default:
                    break;
            }
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3; // two items only at the moment
        }
    }

}
