package btcore.co.kr.d2band.view.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.database.SEVER;
import btcore.co.kr.d2band.databinding.ActivityMainBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.device.DeviceListActivity;

import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private BluetoothAdapter mBtAdapter = null;
    private int mState = UART_PROFILE_DISCONNECTED;
    private ProgressDialog connectDialog;
    private Timer task;
    TimerTask mnTask;
    public BluetoothLeService mService = null;
    public BluetoothDevice mDevice = null;
    public SharedPreferences pref = null;
    public SharedPreferences.Editor editor;
    private long startTime;
    private long endTime;

    ActivityMainBinding mainBinding;
    BleProtocol bleProtocol;
    String gpsEnabled;
    Intent intent;
    SEVER sever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터 바인딩 생성자
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setMainActivity(this);

        // 블루투스 서비스 시작
        service_init();

        bleProtocol = new BleProtocol();

        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        chkGpsService();
        // 서버 생성
        sever = new SEVER();
        sever.SELECT_STEP();

    }

    @OnClick(R.id.image_auto)
    public void OnAuto(View view){
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            String address = pref.getString("DEVICEADDR", "");
            if(address.length() > 0){
                connectDialog = ProgressDialog.show(MainActivity.this, "잠시 기다려주세요", "블루투스 연결 및 시간 동기화 중입니다.", true, false);
                mService.connect(address);
            }else{
                Snackbar.make(getWindow().getDecorView().getRootView(), "현재 저장된 기기가 없습니다.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    @OnClick(R.id.btn_connect)
    public void OnConnect(View view) {
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        }
    }

    public void send(byte[] data) {
        mService.writeRXCharacteristic(data);
    }

    @Override
    public void onBackPressed() {
        if(connectDialog!= null) connectDialog.dismiss();
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
        if(task != null) task.cancel();
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

    @Override
    protected void onStop() {
        android.util.Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        android.util.Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        android.util.Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    connectDialog = ProgressDialog.show(MainActivity.this, "잠시 기다려주세요", "블루투스 연결 및 시간 동기화 중입니다.", true, false);
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    if (deviceAddress != null) {
                        editor.putString("DEVICEADDR", deviceAddress);
                        editor.commit();
                    }
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    try {
                        mService.connect(deviceAddress);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "해당기기가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "블루투스를 활성화 했습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    android.util.Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "블루투스를 활성화 해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                android.util.Log.e(TAG, "wrong request code");
                break;
        }
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        TimeInfo();
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(connectDialog != null){ connectDialog.dismiss(); }
                        Snackbar.make(getWindow().getDecorView().getRootView(), "연결에 실패했습니다. 다시 연결해주세요", Snackbar.LENGTH_LONG).show();
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }

            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "DEVICE_DOES_NOT_SUPPORT_UART : " + currentDateTimeString);
                mService.disconnect();
            }
            if (action.equals(BluetoothLeService.D2_TIME_ACK)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(connectDialog != null){ connectDialog.dismiss(); }
                        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    };

    public void TimeInfo() {
        task = new Timer();
        mnTask = new TimerTask() {
            @Override
            public void run() {
                if(STATE){
                    send(bleProtocol.getTimeInfo(bleProtocol.getDate(),bleProtocol.getWeek()));
                }
            }
        };
        task.schedule(mnTask,1500, 5000);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(BluetoothLeService.D2_TIME_ACK);
        return intentFilter;
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
    private boolean chkGpsService() {

        gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            //gps가 사용가능한 상태가 아니면
            new AlertDialog.Builder(this).setMessage("계속하려면 Google 위치 서비스를 사용하는 기기 위치 기능을 사용 설정하세요.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();

        }
        return false;
    }

}
