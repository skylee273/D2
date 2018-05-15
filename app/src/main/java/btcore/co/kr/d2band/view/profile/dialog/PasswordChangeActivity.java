package btcore.co.kr.d2band.view.profile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class PasswordChangeActivity extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_change_pw;
    private Context context;

    private TextInputEditText newPassword;

    private TextView cancel;
    private TextView change;

    public PasswordChangeActivity(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        newPassword = findViewById(R.id.edit_newpassword);

        cancel = findViewById(R.id.btn_cancel);
        change = findViewById(R.id.btn_change);

        cancel.setOnClickListener(this);
        change.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_change:
                try {
                    if(newPassword.length() > 0){
                    }
                }catch (NullPointerException e){
                    cancel();
                }
                break;
        }
    }
}
