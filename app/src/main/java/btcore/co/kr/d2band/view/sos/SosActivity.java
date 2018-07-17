package btcore.co.kr.d2band.view.sos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.Manifest;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.bus.CallBusEvent;
import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.databinding.ActivitySosBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.service.GPSTracker;
import btcore.co.kr.d2band.user.Contact;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.setting.SettingActivity;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

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
    private BluetoothAdapter mBtAdapter = null;
    private Context mContext;
    private int mState = UART_PROFILE_DISCONNECTED;
    private boolean sos = false;
    private BluetoothLeService mService = null;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor;
    private ActivitySosBinding mSosBinding;
    private BleProtocol bleProtocol;
    private long startTime;
    private long endTime;
    private Timer sosTimer, autoTimer;
    private TimerTask sosTask, autoTask;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location TODO;
    private Contact contact;

    // GPSTracker class
    GPSTracker gps = null;
    public Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSosBinding = DataBindingUtil.setContentView(this, R.layout.activity_sos);
        mSosBinding.setSosActivity(this);

        // 블루투스 서비스 시작
        service_init();

        // 블루투스 데이터 생성
        bleProtocol = new BleProtocol();

        // 버스 등록
        CallProvider.getInstance().register(this);
        SmsProvider.getInstance().register(this);

        // 연락처
        contact = new Contact();
        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        try {
            Intent intent = getIntent();
            if (intent.getExtras().getInt("emergency") == 4) {
                mSosBinding.content.startRippleAnimation();
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }

                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what==RENEW_GPS){
                            makeNewGpsService();
                        }
                        if(msg.what==SEND_PRINT){
                            logPrint((String)msg.obj);
                        }
                    }
                };
                if(gps == null) {
                    gps = new GPSTracker(SosActivity.this,mHandler);
                }else{
                    gps.Update();
                }
                sosTask();
                sos = true;
            }
        } catch (NullPointerException e) {
            Log.d(TAG, e.toString());
        }

        AutoConnection();

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

    @SuppressLint("ResourceAsColor")
    @OnClick(R.id.btn_alert)
    public void OnAlert(View view) {
        if (sos == true) {
            mSosBinding.content.stopRippleAnimation();
            sos = false;
            try {
                Contact contact = new Contact();
                for (String sms : contact.getPhone()) {
                    sendSMS(sms, "저는 괜찮아 졌습니다. 감사합니다.");
                }
            } catch (NullPointerException e) {
                Log.d(TAG, e.toString());
            }
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
        intent.putExtra("sos","1");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "onDestroy()");

        if (autoTimer != null) {
            autoTimer.cancel();
            autoTimer = null;
        }
        if (sosTimer != null) {
            sosTimer.cancel();
            sosTimer = null;
        }

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

    public void send(byte[] data) {
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

    public void sosTask() {
        sosTimer = new Timer();
        sosTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Contact contact = new Contact();
                    String strSMS = pref.getString("EmergencyMsg", "저는 위급 상항입니다. 찾아주시기 바랍니다.");
                    String strSMS2 = "위치 정보가 없습니다.";
                    // check if GPS enabled
                    if(gps.canGetLocation()){
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        strSMS2 = "https://www.geoplaner.com/?z=10;m=5;p=" + latitude + "," + longitude + "WP01-A;;";
                        Log.d("SOS", getTimeStr() + " " + "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
                    }
                    for (String sms : contact.getPhone()) {
                        sendSMS(sms, strSMS);
                        sendSMS(sms, strSMS2);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, e.toString());
                }
            }
        };
        sosTimer.schedule(sosTask, 3000);
    }


    public void sendSMS(String smsNumber, String smsText){
        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, null, null);
    }

    public void makeNewGpsService(){
        if(gps == null) {
            gps = new GPSTracker(SosActivity.this,mHandler);
        }else{
            gps.Update();
        }

    }
    public void logPrint(String str){
        Log.d("Main", getTimeStr() + " " + str);
    }
    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd HH:mm:ss");
        return sdfNow.format(date);
    }

}
