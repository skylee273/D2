package btcore.co.kr.d2band.view.profile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.bus.CallBusEvent;
import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.databinding.ActivityProfileBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.user.Contact;
import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.profile.dialog.PasswordChangeActivity;
import btcore.co.kr.d2band.view.profile.presenter.ProfilePresenter;
import btcore.co.kr.d2band.view.setting.SettingActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;
import static btcore.co.kr.d2band.view.main.fragment.FragmentBottomBar.currentPage;

/**
 * Created by leehaneul on 2018-02-27.
 */

public class ProfileAcitivty  extends AppCompatActivity implements Profile.View{

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int UART_PROFILE_READY = 10;
    private BluetoothAdapter mBtAdapter = null;
    private Context mContext;
    private int mState = UART_PROFILE_DISCONNECTED;
    private Timer autoTimer;
    private TimerTask autoTask;
    private BluetoothLeService mService = null;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref = null;
    private Contact contact;
    private BleProtocol bleProtocol;

    ActivityProfileBinding mProfileBinding;
    Profile.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mProfileBinding.setProfileActivity(this);


        // 블루투스 서비스 시작
        service_init();

        // 블루투스 프로토콜
        bleProtocol = new BleProtocol();

        // 연락처
        contact = new Contact();

        // 버스 등록
        CallProvider.getInstance().register(this);
        SmsProvider.getInstance().register(this);


        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        presenter = new ProfilePresenter(this);
        presenter.getUser();

        AutoConnection();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_setting)
    public void OnSetting(View view){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        currentPage = 0;
        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_logout)
    public void OnLogout(View view){
        editor.remove("AUTO_LOGIN");
        editor.commit();
        presenter.nextActivity();
    }
    @OnClick(R.id.btn_info)
    public void OnInfo(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileInfoActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_password)
    public void OnPasswordChange(View view){
        PasswordChangeActivity Dialog = new PasswordChangeActivity(this);
        Dialog.show();
    }
    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showUserInfo(String name, String id) {
        mProfileBinding.textName.setText(name);
        mProfileBinding.textEmail.setText(id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoTimer != null) { autoTimer.cancel(); }
        try {
            SmsProvider.getInstance().unregister(this);
            CallProvider.getInstance().unregister(this);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            android.util.Log.e(TAG, ignore.toString());
        }
        if (mService != null) {
            unbindService(mServiceConnection);
            mService.stopSelf();
            mService = null;
        }
    }


    public void service_init() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((BluetoothLeService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        android.util.Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }

            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                mService.disconnect();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    public void AutoConnection() {
        autoTimer = new Timer();
        autoTask = new TimerTask() {
            @Override
            public void run() {
                if (!STATE) {
                    String address = pref.getString("DEVICEADDR", "");
                    if (address.length() > 0) {
                        service_init();
                        mService.connect(address);
                    }
                }
            }
        };
        autoTimer.schedule(autoTask, 5000, 60000);
    }

    @Subscribe
    public void FinishLoad(CallBusEvent callBusEvent) {
        boolean subFlag = false;
        String callName = callBusEvent.getEventData();
        if(STATE){
            try {
                for (String temp : contact.getName()) {
                    if (callName.equals(temp)) {
                        subFlag = true;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            if (!subFlag) {
                switch (callBusEvent.getCallType()) {
                    case 0:
                        send(bleProtocol.getCallStart(callBusEvent.getEventData()));
                        break;
                    case 1:
                        send(bleProtocol.getCallEnd(callBusEvent.getEventData()));
                        break;
                    case 2:
                        send(bleProtocol.getMissedCall(callBusEvent.getEventData()));
                        break;
                }
            }else{
                switch (callBusEvent.getCallType()) {
                    case 0:
                        send(bleProtocol.getSubCallStart(callBusEvent.getEventData()));
                        break;
                    case 1:
                        send(bleProtocol.getSubCallEnd(callBusEvent.getEventData()));
                        break;
                    case 2:
                        send(bleProtocol.getSubMissedCall(callBusEvent.getEventData()));
                        break;
                }
            }
        }



    }

    @Subscribe
    public void FinishLoad(SmsBusEvent smsBusEvent) {
        boolean subFlag = false;
        String[] sms = smsBusEvent.getEventData().split("&&&&&");
        String NameOrPhone = sms[0];

        if (STATE) {
            try {
                for (String name : contact.getName()) {
                    if (NameOrPhone.equals(name)) subFlag = true;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            if (subFlag) {
                send(bleProtocol.getSubSms(smsBusEvent.getEventData()));
            } else {
                send(bleProtocol.getSms(smsBusEvent.getEventData()));
            }
        }

    }
    public void send(byte[] data) {
        mService.writeRXCharacteristic(data);
    }
}
