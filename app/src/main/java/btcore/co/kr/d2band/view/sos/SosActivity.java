package btcore.co.kr.d2band.view.sos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import btcore.co.kr.d2band.Manifest;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivitySosBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.find.FindIdActivity;
import btcore.co.kr.d2band.view.setting.SettingActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class SosActivity extends AppCompatActivity {

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
    private boolean sos = false;
    public BluetoothLeService mService = null;

    ActivitySosBinding mSosBinding;
    BleProtocol bleProtocol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSosBinding = DataBindingUtil.setContentView(this, R.layout.activity_sos);
        mSosBinding.setSosActivity(this);

        // 블루투스 서비스 시작
        service_init();

        // 블루투스 데이터 생성
        bleProtocol = new BleProtocol();


    }

    @SuppressLint("ResourceAsColor")
    @OnClick(R.id.btn_alert)
    public void OnAlert(View view){
        if(sos == true){
            mSosBinding.content.stopRippleAnimation();
            sos = false;
        }
    }
    @OnClick(R.id.btn_112)
    public void OnPolice(View view) {
        String tel = "tel:" + "112";
        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
    }

    @OnClick(R.id.btn_119)
    public void OnAmbulance(View view) {
        String tel = "tel:" + "119";
        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
    }

    @OnClick(R.id.btn_call)
    public void OnCall(View view) {
        String tel = "tel:" + "";
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
    }

    @OnClick(R.id.btn_message)
    public void OnMessage(View view) {
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.putExtra("sms_body", "");
        it.setType("vnd.android-dir/mms-sms");
        startActivity(it);
    }

    @OnClick(R.id.btn_map)
    public void OnMap(View view) {
        Uri uri = Uri.parse("geo:38.899533,-77.036476");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    @OnClick(R.id.btn_settings)
    public void OnSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_sound)
    public void OnRing(View view) {
        Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(i, 0);
    }

    @OnClick(R.id.btn_question)
    public void OnQuestion(View view) {
        AlertDialog.Builder questionAlert = new AlertDialog.Builder(SosActivity.this);
        questionAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        questionAlert.setTitle("도움말");
        questionAlert.setMessage("문의하실 사항은 아래 번호로 연락 부탁드립니다.");
        questionAlert.show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
                    } else {
                        ringtone.setStreamType(AudioManager.STREAM_ALARM);
                    }
                    ringtone.play();
                } else {
                }

                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                android.util.Log.e(TAG, "wrong request code");
                break;

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void SendCommand(byte[] data) {
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
            connection = false;
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            connection = false;
            mService = null;
        }
    };

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
            if (action.equals(BluetoothLeService.D2_BLUETOOTH_DATA)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            sos = true;
                            mSosBinding.content.startRippleAnimation();
                        } catch (Exception e) {
                            android.util.Log.e(TAG, e.toString());
                        }
                    }
                });
            }

            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                connection = false;
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

}
