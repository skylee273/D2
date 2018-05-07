package btcore.co.kr.d2band.view.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import btcore.co.kr.d2band.view.main.MainActivity;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityLoginBinding;
import btcore.co.kr.d2band.view.find.FindIdActivity;
import btcore.co.kr.d2band.view.login.presenter.LoginPresenter;
import btcore.co.kr.d2band.view.register.RegisterActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-01-08.
 */

public class LoginActivity extends AppCompatActivity implements Login.View {

    private final String TAG = "LoginActivity";
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref = null;
    private boolean isAuto;
    ActivityLoginBinding mBinding;
    Login.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setLogin(this);


        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        presenter = new LoginPresenter(this);
        isAuto = pref.getBoolean("AUTO_LOGIN",false);

        if(isAuto) AutoLogin();
        else { mBinding.btnAutologin.setBackgroundResource(R.drawable.icon_uncheck); }

    }

    private void AutoLogin(){
        String id = pref.getString("ID","");
        String pw = pref.getString("PW","");
        progressDialog = ProgressDialog.show(LoginActivity.this, "로그인 중 입니다.",null, true, true);
        presenter.initUserData(id, pw);
        presenter.callLogin();
    }
    @OnClick(R.id.btn_login)
    public void onLogin(View view){
        progressDialog = ProgressDialog.show(LoginActivity.this, "로그인 중 입니다.",null, true, true);
        presenter.initUserData(mBinding.editId.getText().toString(), mBinding.editPw.getText().toString());
        presenter.callLogin();
    }
    @OnClick(R.id.btn_autologin)
    public void onAutoLogin(View view){
        // SharedPreference 에 저장된 데이터 가져오김.
        if(isAuto){
            mBinding.btnAutologin.setBackgroundResource(R.drawable.icon_check);
            isAuto = false;
        }else{
            mBinding.btnAutologin.setBackgroundResource(R.drawable.icon_uncheck);
            isAuto = true;
        }
    }
    @OnClick(R.id.text_idfind)
    public void onFindId(View view){
        try{
            Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
    @OnClick(R.id.text_pwfind)
    public void onFindPw(View view){
        try{
            Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
    @OnClick(R.id.text_register)
    public void onRegister(View view){
        try{
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();

        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void showErrorMessage(String msg) {
        Log.d(TAG, msg);
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void startMainActivity() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        if(!isAuto){
            editor.putBoolean("AUTO_LOGIN",true);
            editor.putString("ID",mBinding.editId.getText().toString());
            editor.putString("PW",mBinding.editPw.getText().toString());
            editor.commit();
        }else{
            editor.remove("AUTO_LOGIN");
            editor.commit();

        }

        presenter.UserSet();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
