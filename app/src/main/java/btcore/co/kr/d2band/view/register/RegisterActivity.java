package btcore.co.kr.d2band.view.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import btcore.co.kr.d2band.databinding.ActivityRegisterBinding;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.view.register.dialog.CutsomDaumDialog;
import btcore.co.kr.d2band.view.register.presenter.Register;
import btcore.co.kr.d2band.view.register.presenter.RegisterPresenter;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class RegisterActivity extends AppCompatActivity implements Register.View {

    ActivityRegisterBinding mbinding;
    private final static String TAG = "RegisterActivity";
    private String gender;
    private CutsomDaumDialog cutsomDaumDialog;
    Register.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mbinding.setActivity(this);
        presenter = new RegisterPresenter(this);

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_back_register)
    public void onRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_register)
    public void onRegisterCall(View view) {

        presenter.initUserData(mbinding.editId.getText().toString(), mbinding.editPw.getText().toString(), mbinding.editPwconfirm.getText().toString()
                , mbinding.editName.getText().toString(), mbinding.editBirthday.getText().toString(), gender, mbinding.editHeight.getText().toString(),
                mbinding.editWeight.getText().toString(), mbinding.editPhone.getText().toString(), mbinding.textLocation.getText().toString());
        presenter.callSignup();
    }

    @OnClick(R.id.btn_location)
    public void onLocation(View view) {
        getAddressCallMethod();
    }

    @OnClick(R.id.btn_man)
    public void onMan(View view) {
        mbinding.btnWomen.setBackgroundResource(R.drawable.btn_uncheck_women);
        mbinding.btnMan.setBackgroundResource(R.drawable.btn_man_check);
        gender = "m";
    }
    @OnClick(R.id.btn_women)
    public void onWomen(View view) {
        mbinding.btnWomen.setBackgroundResource(R.drawable.btn_check_women);
        mbinding.btnMan.setBackgroundResource(R.drawable.btn_man_uncheck);
        gender = "w";
    }


    public void getAddressCallMethod() {
        cutsomDaumDialog = new CutsomDaumDialog(this);
        cutsomDaumDialog.setCancelable(true);
        cutsomDaumDialog.setTitle("주소검색");
        cutsomDaumDialog.show();

        cutsomDaumDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mbinding.textLocation.setText(cutsomDaumDialog.getAddressStr());
            }
        });
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void showErrorMessage(String message) {
        Log.d(TAG, message);
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void startMainActivity() {
        Log.i(TAG, "회원가입 성공");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
