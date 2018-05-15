package btcore.co.kr.d2band.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityProfileinfoBinding;
import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.view.profile.presenter.InfoPresenter;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

public class ProfileInfoActivity extends AppCompatActivity implements Info.View{

    ActivityProfileinfoBinding mInfoBinding;
    Info.Presenter presenter;
    User user;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_profileinfo);
        mInfoBinding.setProfileInfoActivity(this);

        user = new User();
        presenter = new InfoPresenter(this);
        presenter.setUser(user.getName(), user.getBirthday(), user.getGender(), user.getHeight(), user.getWeight(), user.getPhone(), user.getAddress());
    }

    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startMainActivity() {

    }

    @Override
    public void showUserInfo(String name, String birthday, String gender, String height, String weight, String phone, String addr) {
        mInfoBinding.textInfoName.setText(name);
        mInfoBinding.textInfoBirthday.setText(birthday);
        mInfoBinding.textInfoGender.setText(gender);
        mInfoBinding.textInfoHeight.setText(height);
        mInfoBinding.textInfoWeight.setText(weight);
        //mInfoBinding.textInfoPhone.setText(name);
        mInfoBinding.textInfoAddr.setText(addr);

    }
}