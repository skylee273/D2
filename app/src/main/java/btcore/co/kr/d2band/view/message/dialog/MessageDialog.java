package btcore.co.kr.d2band.view.message.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class MessageDialog extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_message;
    private Context context;

    private TextInputEditText name;
    private TextInputEditText phone;
    private TextView state;
    private TextView cancel;
    private TextView plus;
    private String mName;
    private String mPhone;

    public MessageDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        name = findViewById(R.id.edit_name);
        phone = findViewById(R.id.edit_phone);

        state = findViewById(R.id.text_state);
        cancel = findViewById(R.id.btn_cancel);
        plus = findViewById(R.id.btn_plus);

        cancel.setOnClickListener(this);
        plus.setOnClickListener(this);

    }

    private void setText() {
        mName = name.getText().toString();
        mPhone = phone.getText().toString();
    }

    public String getmName() {
        return mName;
    }
    public String getmPhone(){
        return mPhone;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_plus:
                setText();
                if(mName != null && mPhone != null ){
                    MessageDialog.this.dismiss();
                }else{
                    state.setText("이름 또는 휴대폰 번호를 입력하세요");
                }
                break;
        }
    }
}