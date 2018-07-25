package btcore.co.kr.d2band.view.step.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import btcore.co.kr.d2band.R;

public class StepDialog extends Dialog implements View.OnClickListener {

    private static final int LAYOUT = R.layout.dialog_step;

    private TextInputEditText goal;
    private TextView state;
    private String mGoal;

    public StepDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        goal = findViewById(R.id.edit_goal);

        state = findViewById(R.id.text_state);
        TextView cancel = findViewById(R.id.btn_cancel);
        TextView confirm = findViewById(R.id.btn_change);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

    }

    private void setText() {
        mGoal = goal.getText().toString();
    }
    public String getmGoal() {
        return mGoal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_change:
                setText();
                if(mGoal != null){
                    StepDialog.this.dismiss();
                }else{
                    state.setText("목표를 입력하세요.");
                }
                break;
        }
    }
}