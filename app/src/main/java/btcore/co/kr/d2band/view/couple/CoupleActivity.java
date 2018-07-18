package btcore.co.kr.d2band.view.couple;

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
import btcore.co.kr.d2band.databinding.ActivityCoupleBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.user.Contact;
import btcore.co.kr.d2band.util.BleProtocol;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class CoupleActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private BluetoothAdapter mBtAdapter = null;
    private Context mContext;
    private int mState = UART_PROFILE_DISCONNECTED;
    private BluetoothLeService mService = null;
    private BleProtocol bleProtocol;
    private long startTime;
    private long endTime;
    private Timer autoTimer;
    private TimerTask autoTask;
    private Contact contact;

    SharedPreferences.Editor editor;
    SharedPreferences pref = null;
    ActivityCoupleBinding mCoupleBinding;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCoupleBinding = DataBindingUtil.setContentView(this, R.layout.activity_couple);
        mCoupleBinding.setCoupleActivity(this);

        // 블루투스 서비스 등록
        service_init();

        // 블루투스 생성
        bleProtocol = new BleProtocol();

        // 연락처 생성
        contact = new Contact();

        // 버스 등록
        CallProvider.getInstance().register(this);
        SmsProvider.getInstance().register(this);

        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        AutoConnection();

    }

    @OnClick(R.id.btn_setting)
    public void onSetting(View view){
        Intent intent = new Intent(getApplicationContext(), CoupleSettingActivitiy.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        endTime = System.currentTimeMillis();
        Snackbar.make(getWindow().getDecorView().getRootView(), "한번더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show();
        if (endTime - startTime < 2000) {
            super.onBackPressed();
            finishAffinity();
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(autoTimer != null ) { autoTimer.cancel(); }
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

    @Override
    public void onResume() {
        super.onResume();
        // 바텀바 셋
        if (!mBtAdapter.isEnabled()) {
            android.util.Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        autoTimer.schedule(autoTask, 1500, 30000);
    }

    @Subscribe
    public void FinishLoad(CallBusEvent callBusEvent) {
        boolean subFlag = false;
        try {
            String name = callBusEvent.getEventData();
            for (String temp : contact.getName()) {
                if (name.equals(temp)) {
                    subFlag = true;
                }
            }
            if (STATE && subFlag != true) {
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
            }
            if (STATE && subFlag == true) {
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
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Subscribe
    public void FinishLoad(SmsBusEvent smsBusEvent) {
        boolean subFlag = false;
        String[] sms = smsBusEvent.getEventData().split("&&&&&");
        String NameOrPhone = sms[0];
        try {
            for (String name : contact.getName()) {
                if (NameOrPhone.equals(name)) subFlag = true;
            }
            if (STATE && subFlag == true) {
                send(bleProtocol.getSubSms(smsBusEvent.getEventData()));
            }
            if (STATE && subFlag != true) {
                send(bleProtocol.getSms(smsBusEvent.getEventData()));
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    public void send(byte[] data) {
        mService.writeRXCharacteristic(data);
    }


}
