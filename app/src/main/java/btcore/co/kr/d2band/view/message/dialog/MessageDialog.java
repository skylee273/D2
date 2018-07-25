package btcore.co.kr.d2band.view.message.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class MessageDialog extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_message;
    private boolean deleteFlag = false;

    public MessageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        TextView cancel = findViewById(R.id.btn_cancel);
        TextView plus = findViewById(R.id.btn_plus);
        cancel.setOnClickListener(this);
        plus.setOnClickListener(this);

    }

    private void setDeleteFlag() {
        deleteFlag = true;
    }
    public boolean getDeleteFlag(){
        return deleteFlag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_plus:
                setDeleteFlag();
                MessageDialog.this.dismiss();
                break;
        }
    }
}