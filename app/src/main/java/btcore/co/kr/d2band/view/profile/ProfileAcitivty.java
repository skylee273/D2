package btcore.co.kr.d2band.view.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityProfileBinding;
import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.profile.presenter.ProfilePresenter;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-27.
 */

public class ProfileAcitivty  extends AppCompatActivity implements Profile.View{
    ActivityProfileBinding mProfileBinding;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref = null;

    Profile.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mProfileBinding.setProfileActivity(this);



        presenter = new ProfilePresenter(this);
        presenter.getUser();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_logout)
    public void OnLogout(View view){
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.remove("AUTO_LOGIN");
        editor.commit();
        presenter.nextActivity();
    }
    @OnClick(R.id.btn_info)
    public void OnInfo(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileInfoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showUserInfo(String name, String id) {
        mProfileBinding.textName.setText(name);
        mProfileBinding.textEmail.setText(id);
    }
}
