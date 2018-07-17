package btcore.co.kr.d2band.view.setting;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivitySettingBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.lock.LockActivity;
import btcore.co.kr.d2band.view.profile.ProfileAcitivty;
import btcore.co.kr.d2band.view.sos.SosActivity;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

public class SettingActivity extends AppCompatActivity {

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
    private BleProtocol bleProtocol;

    ActivitySettingBinding settingBinding;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        settingBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        settingBinding.setSettingActivity(this);

        // 블루투스 서비스 시작
        service_init();

        // 블루투스 데이터 생성
        bleProtocol = new BleProtocol();

        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        AutoConnection();

    }

    @OnClick(R.id.box_battery)
    public void OnBattery(View view){
       send(bleProtocol.Requset());
    }
    @OnClick(R.id.box_lock)
    public void OnLock(View view){
        Intent intent = new Intent(getApplicationContext(), LockActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.box_kakao)
    public void OnKakao(View view){
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }
    @OnClick(R.id.box_gps)
    public void OnGps(View view){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = getIntent();
        try{
            String name = intent.getExtras().getString("sos");
            if(name.equals("1")){
                intent = new Intent(getApplicationContext(), SosActivity.class);
                startActivity(intent);
                finish();
            }else{
                intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
                startActivity(intent);
                finish();
            }
        }catch (NullPointerException e){
            intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "onDestroy()");

        if (autoTimer != null) { autoTimer.cancel(); }
        try {
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
            if (action.equals(BluetoothLeService.D2_BATTERY_DATA)) {
                final String battery = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            int Bat = Integer.parseInt(battery);
                            switch (Bat){
                                case 0:
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "현재 배터리는 0% 입니다.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "현재 배터리는 25% 입니다.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 2:
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "현재 배터리는 50% 입니다.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 3:
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "현재 배터리는 75% 입니다.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 4:
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "현재 배터리는 100% 입니다.", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (Exception e) {
                            android.util.Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(BluetoothLeService.D2_BATTERY_DATA);
        return intentFilter;
    }

    public void send(byte[] data) {
        mService.writeRXCharacteristic(data);
    }

    public void AutoConnection() {
        autoTimer = new Timer();
        autoTask = new TimerTask() {
            @Override
            public void run() {
                if (!STATE){
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



}
