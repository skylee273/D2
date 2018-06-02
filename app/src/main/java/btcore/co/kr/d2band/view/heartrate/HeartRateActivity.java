package btcore.co.kr.d2band.view.heartrate;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.util.Date;

import btcore.co.kr.d2band.bus.CallBusEvent;
import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.databinding.ActivityHeartrateBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.heartrate.presenter.HeartRate;
import btcore.co.kr.d2band.view.heartrate.presenter.HeartRatePresenter;
import btcore.co.kr.d2band.R;

/**
 * Created by leehaneul on 2018-01-17.
 */

/**
 * 블루투스 프로그래밍을 옵저버 패턴으로 구현후 옵저버 패턴에서 데이터를 받았을 경우를 가정하에 코딩.
 */
public class HeartRateActivity extends AppCompatActivity implements HeartRate.View {

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private BluetoothAdapter mBtAdapter = null;
    private int mState = UART_PROFILE_DISCONNECTED;
    public BluetoothLeService mService = null;

    // 멤버 변수
    ActivityHeartrateBinding mBinding;
    HeartRate.Presenter presenter;
    BleProtocol bleProtocol;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_heartrate);
        mBinding.setHeartActivity(this);

        presenter = new HeartRatePresenter(this);

        // 블루투스 서비스 시작
        service_init();

        // 블루투스 데이터 생성
        bleProtocol = new BleProtocol();

    }
    @Override
    public void showHeartData(String heart, String avgHeart, String maxHeart, String minHeart, String currentState, String error) {
        mBinding.textBpm.setText(heart);
        mBinding.textAvgbpm.setText(avgHeart + " BPM");
        mBinding.textMaxbpm.setText(maxHeart + " BPM");
        mBinding.textMinbpm.setText(minHeart + " BPM");
        mBinding.textStatecurrent.setText(currentState);
        mBinding.textErrorbpm.setText(error + "%");
    }

    @Override
    public void showErrorMessage(String message) {
        Log.e(TAG,message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "onDestroy()");
        SmsProvider.getInstance().unregister(this);
        CallProvider.getInstance().unregister(this);
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

    @Subscribe
    public void FinishLoad(CallBusEvent callBusEvent){
        switch (callBusEvent.getCallType()){
            case 0:
                sendMsg(bleProtocol.getCallStart(callBusEvent.getEventData()));
                break;
            case 1:
                sendMsg(bleProtocol.getCallEnd(callBusEvent.getEventData()));
                break;
            case 2:
                sendMsg(bleProtocol.getMissedCall(callBusEvent.getEventData()));
                break;
        }
    }
    @Subscribe
    public void FinishLoad(SmsBusEvent smsBusEvent){
        sendMsg(bleProtocol.getSms(smsBusEvent.getEventData()));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    public void sendMsg(byte[] data) {
        mService.writeRXCharacteristic(data);
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

            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "재연결 되었습니다.", Snackbar.LENGTH_LONG).show();
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
            if (action.equals(BluetoothLeService.D2_HEART_DATA)) {
                final String Heart = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            presenter.UpdateHeart(Heart);
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

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
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(BluetoothLeService.D2_HEART_DATA);
        return intentFilter;
    }


}
