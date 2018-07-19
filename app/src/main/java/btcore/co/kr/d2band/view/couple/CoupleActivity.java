package btcore.co.kr.d2band.view.couple;

import android.annotation.SuppressLint;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import btcore.co.kr.d2band.view.couple.presenter.CouplePresenter;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class CoupleActivity extends AppCompatActivity implements Couple.View {

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
    private Couple.Presenter presenter;

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

        // 프레젠터 생성
        presenter = new CouplePresenter(this);

        presenter.updateSaveView();

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


    private void sendSavePicture(String path, int type){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Bitmap bitmap = BitmapFactory.decodeFile(path);//경로를 통해 비트맵으로 전환
        switch (type){
            case 0:
                presenter.updateImage(bitmap, type);
                break;
            case 1:
                presenter.updateImage(bitmap, type);
                break;
            case 2:
                presenter.updateImage(bitmap, type);
                break;
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showSaveView() {
        String image = pref.getString("IMAGEPATH_IMAGE",null);
        String imageMy = pref.getString("IMAGEPATH_MY", null);
        String imageCouple = pref.getString("IMAGEPATH_COUPLE",null);
        String myName = pref.getString("COUPLE_NAME_ME",null);
        String coupleName = pref.getString("COUPLE_NAME_COUPLE",null);
        String coupleDate = pref.getString("COUPLE_DATE_FORMAT", null);

        if(image != null) { sendSavePicture(image, 0);}
        if(imageMy != null) { sendSavePicture(imageMy, 1);}
        if(imageCouple != null) { sendSavePicture(imageCouple, 2);}
        if(myName != null) { presenter.updateNickName(myName, 0);}
        if(coupleName != null) { presenter.updateNickName(coupleName, 1);}
        if(coupleDate != null) { presenter.updateCalendar(coupleDate);}

    }

    @Override
    public void showImageBitmap(Bitmap bitmap, int type) {
        switch (type){
            case 0:
               Glide.with(this).load(bitmap).into(mCoupleBinding.btnImage);
                break;
            case 1:
                Glide.with(this).load(bitmap).apply(new RequestOptions().circleCrop()).into(mCoupleBinding.imageMe);
                break;
            case 2:
                Glide.with(this).load(bitmap).apply(new RequestOptions().circleCrop()).into(mCoupleBinding.imageCouple);
                break;
        }
    }

    @Override
    public void showNickName(String name, int type) {
        switch (type){
            case 0:
                mCoupleBinding.textMe.setText(name);
                break;
            case 1:
                mCoupleBinding.textCouple.setText(name);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showCalendar(String date, String koreaDate, String goalDay, String dDay) {
        mCoupleBinding.textCoupleDate.setText(date + "일");
        mCoupleBinding.textKorea.setText(koreaDate);
        mCoupleBinding.textAnniversary.setText(goalDay + "일");
        mCoupleBinding.textMod.setText(dDay + "일 남음");
        mCoupleBinding.progressBarDate.setMax(Integer.parseInt(goalDay));
        mCoupleBinding.progressBarDate.setProgress(Integer.parseInt(goalDay) - Integer.parseInt(dDay));
    }

}
