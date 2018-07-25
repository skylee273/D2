package btcore.co.kr.d2band.view.couple.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class CoupleDialog extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_couple;

    private TextInputEditText name;
    private TextView state;
    private String mName;

    public CoupleDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        name = findViewById(R.id.edit_name);

        state = findViewById(R.id.text_state);
        TextView cancel = findViewById(R.id.btn_cancel);
        TextView confirm = findViewById(R.id.btn_change);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

    }

    private void setText() {
        mName = name.getText().toString();
    }
    public String getmName() {
        return mName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_change:
                setText();
                if(mName != null){
                    CoupleDialog.this.dismiss();
                }else{
                    state.setText("별명을 입력하세요.");
                }
                break;
        }
    }
}