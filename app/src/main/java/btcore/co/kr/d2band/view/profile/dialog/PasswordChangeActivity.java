package btcore.co.kr.d2band.view.profile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.view.profile.Password;
import btcore.co.kr.d2band.view.profile.presenter.PasswordPresenter;

public class PasswordChangeActivity extends Dialog implements View.OnClickListener, Password.View {

    private static final int LAYOUT = R.layout.dialog_change_pw;
    private Context context;

    private TextInputEditText newPassword;
    private TextView state;
    private TextView cancel;
    private TextView change;

    PasswordPresenter presenter;

    public PasswordChangeActivity(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        newPassword = findViewById(R.id.edit_newpassword);

        state = findViewById(R.id.text_state);
        cancel = findViewById(R.id.btn_cancel);
        change = findViewById(R.id.btn_change);

        cancel.setOnClickListener(this);
        change.setOnClickListener(this);

        presenter = new PasswordPresenter(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_change:
                try {
                    presenter.changePassword(newPassword.getText().toString());
                }catch (NullPointerException e){
                    showErrorMessage("비밀번호를 입력해주세요.");
                }
                break;
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        state.setText(msg);
    }

    @Override
    public void Update(String msg) {
        cancel();
    }
}
