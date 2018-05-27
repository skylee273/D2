package btcore.co.kr.d2band.view.find;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityFindBinding;
import btcore.co.kr.d2band.view.find.fragment.FragmentId;
import btcore.co.kr.d2band.view.find.fragment.FragmentPw;
import btcore.co.kr.d2band.view.find.presenter.Find;
import btcore.co.kr.d2band.view.find.presenter.FindPresenter;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FindIdActivity extends AppCompatActivity implements Find.View {

    ActivityFindBinding mBinding;

    private final String TAG = getClass().getSimpleName();
    FragmentPagerAdapter mPagerAdapter = null;
    private int currentPage = 0;
    Find.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_find);
        mBinding.setFind(this);

        presenter = new FindPresenter(this);

        initView();
    }

    private void initView() {
        mPagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mBinding.viewFind.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        mBinding.viewFind.setCurrentItem(0);
        mBinding.viewFind.setOffscreenPageLimit(2);
        mBinding.viewFind.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabFind));
        mBinding.tabFind.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewFind.setCurrentItem(tab.getPosition());
                currentPage = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @OnClick(R.id.btn_cancel)
    public void onBack(View view) {
        try {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm(View view) {
        if (currentPage == 0) {
            FragmentId fragmentId1 = (FragmentId) findFragmentByPosition(currentPage);
            String name = fragmentId1.getName();
            String phone = fragmentId1.getPhone();
            presenter.initFindId(name, phone);
            presenter.callIdDialog();
        } else {
            FragmentPw fragmentPw = (FragmentPw) findFragmentByPosition(currentPage);
            String name = fragmentPw.getName();
            String phone = fragmentPw.getPhone();
            presenter.initFindPw(name, phone);
            presenter.callPwDialog();
        }


    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mBinding.viewFind.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void showErrorMessage(String message) {
        Log.d(TAG, message);
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startDialog(String title, String context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(FindIdActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setTitle(title);
        alert.setMessage("고객님의 아이디는 " + context + " 입니다.");
        alert.show();

    }

    @Override
    public void startPwDialog(String msg, String pw) {
        AlertDialog.Builder alert = new AlertDialog.Builder(FindIdActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setTitle("임시 비밀번호");
        alert.setMessage(msg + pw + " 입니다.");
        alert.show();

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
                    fragment = new FragmentId();
                    return fragment;
                case 1:
                    fragment = new FragmentPw();
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
            return 2; // two items only at the moment
        }
    }


}
