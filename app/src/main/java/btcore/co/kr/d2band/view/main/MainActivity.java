package btcore.co.kr.d2band.view.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityMainBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.device.DeviceListActivity;

import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

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

    public BluetoothLeService mService = null;
    public BluetoothDevice mDevice = null;

    ActivityMainBinding mainBinding;
    BleProtocol bleProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터 바인딩 생성자
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setMainActivity(this);

        // 블루투스 서비스 시작
        service_init();

        // Time DATA
        bleProtocol = new BleProtocol();

    }

    @OnClick(R.id.btn_connect)
    public void OnConnect(View view) {
        if (!mBtAdapter.isEnabled()) {
            android.util.Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        }
    }

    public void SendCommand(byte[] data) {
        mService.writeRXCharacteristic(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "onDestroy()");
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
                    connectDialog = ProgressDialog.show(MainActivity.this, "잠시 기다려주세요", "블루투스 연결중입니다.", true, false);
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
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
                        Snackbar.make(getWindow().getDecorView().getRootView(), "연결 되었습니다.", Snackbar.LENGTH_LONG).show();
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                    }
                });
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }

            if (action.equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            Log.d(TAG, "ACTION_DATA_AVAILABLE : " + currentDateTimeString);
                        } catch (Exception e) {
                            android.util.Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "DEVICE_DOES_NOT_SUPPORT_UART : " + currentDateTimeString);
                mService.disconnect();
            }
            if (action.equals(BluetoothLeService.D2_CONNECTION_ACK)) {
                final String ACK = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(connectDialog != null) connectDialog.dismiss();
                        SendCommand(bleProtocol.getTimeInfo(bleProtocol.getDate(), bleProtocol.getWeek()));
                        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
                        startActivity(intent);
                        finish();
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
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(BluetoothLeService.D2_CONNECTION_ACK);
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
}
