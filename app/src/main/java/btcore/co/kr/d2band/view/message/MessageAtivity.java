package btcore.co.kr.d2band.view.message;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityMessageBinding;
import btcore.co.kr.d2band.view.message.adapter.ReceiveAdapter;
import btcore.co.kr.d2band.view.message.dialog.EmergencyDialog;
import btcore.co.kr.d2band.view.message.dialog.MessageDialog;
import btcore.co.kr.d2band.view.message.presenter.MessagePresenter;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-27.
 */

public class MessageAtivity extends AppCompatActivity implements Message.View {

    private final String TAG = getClass().getSimpleName();
    private ReceiveAdapter receiveAdapter;

    SharedPreferences.Editor mEditor;
    SharedPreferences sharedPreferences = null;
    ActivityMessageBinding messageBinding;
    EmergencyDialog Dialog;
    MessageDialog MD;
    Message.Presenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        messageBinding.setMessageActivity(this);

        sharedPreferences = getSharedPreferences("Message", Activity.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        presenter = new MessagePresenter(this);
        presenter.saveMsg(sharedPreferences.getString("EmergencyMsg", ""));
        presenter.selectReceiver();
        receiveAdapter = new ReceiveAdapter();
    }

    @OnClick(R.id.btn_plus)
    public void OnPlus(View view) {
        if (receiveAdapter.getCount() <= 5) {
            Log.d(TAG, String.valueOf(receiveAdapter.getCount()));
            MD = new MessageDialog(this);
            MD.show();
            MD.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    presenter.plusReceiver(MD.getmName(), MD.getmPhone());
                }
            });
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "최대 5명 까지 추가 할 수 있습니다.", Snackbar.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.text_msg)
    public void OnMsg(View view) {
        Dialog = new EmergencyDialog(this);
        Dialog.show();
        Dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                presenter.changeMessage(Dialog.getText());
            }
        });
    }

    @OnClick(R.id.text_change)
    public void OnChange(View view) {
        Dialog = new EmergencyDialog(this);
        Dialog.show();
        Dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                presenter.changeMessage(Dialog.getText());
            }
        });
    }


    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void MessageUpdate(String msg) {
        messageBinding.textMsg.setText(msg);
        mEditor.putString("EmergencyMsg", msg);
        mEditor.apply();
    }

    @Override
    public void updateListView(String[] Receiver) {
        receiveAdapter.clearItem();
        try {
            for (String RecvInfo : Receiver) {
                String info[] = RecvInfo.split("#####");
                receiveAdapter.addItem(info[0], info[1]);
                messageBinding.listReceiver.setAdapter(receiveAdapter);
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
