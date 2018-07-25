package btcore.co.kr.d2band.view.message.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class EmergencyDialog extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_emergency;

    private TextInputEditText newPassword;
    private TextView state;
    private String changeContext;

    public EmergencyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        newPassword = findViewById(R.id.edit_msg);

        state = findViewById(R.id.text_state);
        TextView cancel = findViewById(R.id.btn_cancel);
        TextView change = findViewById(R.id.btn_change);

        cancel.setOnClickListener(this);
        change.setOnClickListener(this);

    }

    private void setText() {
        changeContext = newPassword.getText().toString();
    }

    public String getText() {
        return changeContext;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_change:
                setText();
                if(changeContext != null){
                    EmergencyDialog.this.dismiss();
                }else{
                    state.setText("변경하실 내용을 입력하세요.");
                }
                break;
        }
    }
}