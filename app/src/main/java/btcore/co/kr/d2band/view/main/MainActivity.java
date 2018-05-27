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
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import btcore.co.kr.d2band.view.find.fragment.FragmentId;
import btcore.co.kr.d2band.view.find.fragment.FragmentPw;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.main.presenter.MainPresenter;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Main.View {

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;

    private static boolean connection = false;

    private BluetoothAdapter mBtAdapter = null;
    private Context mContext;

    private int mState = UART_PROFILE_DISCONNECTED;

    public BluetoothLeService mService = null;
    public BluetoothDevice mDevice = null;

    ActivityMainBinding mainBinding;
    Main.Presenter presenter;
    BleProtocol bleProtocol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터 바인딩 생성자
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setMainActivity(this);

        // 컨테스트 받기
        mContext = this;

        // 프레젠터 생성자
        presenter = new MainPresenter(this);

        // 블루투스 상태 체크
        presenter.checkBluetooth();

        // 블루투스 서비스 시작
        service_init();

        // Time DATA
        bleProtocol = new BleProtocol();

    }

    @OnClick(R.id.btn_connect)
    public void OnConnect(View view) {
       presenter.isEnabled();
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startRequsetSelectDevice() {
        if (connection == false) {
            //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
            Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        } else {
            if (mDevice != null) {
                mService.disconnect();
            }
        }
    }

    @Override
    public void startMainActivity() {
        SendCommand(bleProtocol.getTimeInfo(bleProtocol.getDate(), bleProtocol.getWeek()));
        Intent intent = new Intent(getApplicationContext(), StepActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startRequestEnableBt() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    //((TextView) findViewById(R.id.tag_name)).setText(mDevice.getName() );
                    try {
                        mService.connect(deviceAddress);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "해당 기기가 없습니다.", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "블루투스를 활성화 했습니다.", Snackbar.LENGTH_LONG).show();
                } else {
                    android.util.Log.d(TAG, "BT not enabled");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "블루투스를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
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

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                connection = false;
                runOnUiThread(new Runnable() {
                    public void run() {
                        mState = UART_PROFILE_CONNECTED;
                        presenter.isNext();
                    }
                });
            }

            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                connection = false;
                runOnUiThread(new Runnable() {
                    public void run() {
                        android.util.Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                    }
                });
            }


            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                connection = true;
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

                        } catch (Exception e) {
                            android.util.Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                connection = false;
                //showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
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
        return intentFilter;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((BluetoothLeService.LocalBinder) rawBinder).getService();
            connection = false;
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            connection = false;
            //       mService.disconnect(mDevice);
            mService = null;
        }
    };

    public void service_init() {
        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

}
