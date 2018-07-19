package btcore.co.kr.d2band.view.step;

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
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.bus.CallBusEvent;
import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.databinding.ActivityStepBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.user.Contact;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.view.profile.ProfileAcitivty;
import btcore.co.kr.d2band.view.step.dialog.StepDialog;
import btcore.co.kr.d2band.view.step.fragment.StepMonthFragment;
import btcore.co.kr.d2band.view.step.fragment.StepTodayFragment;
import btcore.co.kr.d2band.view.step.fragment.StepWeekFragment;
import btcore.co.kr.d2band.view.step.presenter.Step;
import btcore.co.kr.d2band.view.step.presenter.StepActivityPresenter;
import butterknife.OnClick;

import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivity extends AppCompatActivity implements Step.view {

    private final String TAG = getClass().getSimpleName();
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private BluetoothAdapter mBtAdapter = null;
    private int mState = UART_PROFILE_DISCONNECTED;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor;
    private BluetoothLeService mService = null;
    private long startTime;
    private long endTime;
    private ActivityStepBinding mStepBinding;
    private FragmentPagerAdapter mPagerAdapter = null;
    private BleProtocol bleProtocol;
    private Step.Presenter presenter;
    private StepDialog stepDialog;
    private Timer stepTimer;
    private TimerTask stepTask;
    private Contact contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStepBinding = DataBindingUtil.setContentView(this, R.layout.activity_step);
        mStepBinding.setStepActivity(this);

        initView();

        // 블루투스 서비스 시작
        service_init();

        // 블루투스 데이터 생성
        bleProtocol = new BleProtocol();

        // 연락처 생성
        contact = new Contact();
        // 프레젠터 생성
        presenter = new StepActivityPresenter(this);

        // 버스 등록
        CallProvider.getInstance().register(this);
        SmsProvider.getInstance().register(this);

        RequestTask();

        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        int todayGoal = pref.getInt("STEPGOAL", 8000);
        mStepBinding.textGoal.setText(String.valueOf(todayGoal));
        mStepBinding.progressBarStep.setMax(todayGoal);

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

    @OnClick(R.id.text_goal)
    public void OnGoal(View view) {
        stepDialog = new StepDialog(this);
        stepDialog.show();
        stepDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (stepDialog.getmGoal() != null && !stepDialog.getmGoal().equals("")) {
                    presenter.UpdateGoal(stepDialog.getmGoal());
                    editor.putInt("STEPGOAL", Integer.parseInt(stepDialog.getmGoal()));
                    editor.commit();
                }
            }
        });
    }

    @OnClick(R.id.btn_profile)
    public void OnProfile(View view) {
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        mPagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mStepBinding.viewPagerStep.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        mStepBinding.viewPagerStep.setCurrentItem(0);
        mStepBinding.viewPagerStep.setOffscreenPageLimit(3);
        mStepBinding.viewPagerStep.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mStepBinding.tabLayoutActivity));
        mStepBinding.tabLayoutActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mStepBinding.viewPagerStep.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void showTodayData(String distance) {
        mStepBinding.textKm.setText(distance);
        Log.d(TAG, distance);
    }

    @Override
    public void showErrorMessage(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void showGoal(String goal) {
        mStepBinding.textGoal.setText(goal);
        mStepBinding.progressBarStep.setMax(Integer.parseInt(goal));
    }

    public class pagerAdapter extends FragmentPagerAdapter {

        public static final String ARG_PAGE = "page";

        public pagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new StepTodayFragment();
                    return fragment;
                case 1:
                    fragment = new StepWeekFragment();
                    return fragment;
                case 2:
                    fragment = new StepMonthFragment();
                    return fragment;
                default:
                    break;
            }
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3; // two items only at the moment
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stepTimer != null) {
            stepTimer.cancel();
            stepTimer = null;
        }

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


        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("LocalMsg"));

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

    public void RequestTask() {
        stepTimer = new Timer();
        stepTask = new TimerTask() {
            @Override
            public void run() {
                if (STATE) {
                    send(bleProtocol.Requset());
                } else {
                    String address = pref.getString("DEVICEADDR", "");
                    if (address.length() > 0) {
                        service_init();
                        mService.connect(address);
                    }
                }
            }
        };
        stepTimer.schedule(stepTask, 1500, 5000);
    }

    public void send(byte[] data) {
        mService.writeRXCharacteristic(data);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String Kakao = intent.getStringExtra("kakaoInfo");
            Log.d(TAG, Kakao);
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

            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "DEVICE_DOES_NOT_SUPPORT_UART : " + currentDateTimeString);
                mService.disconnect();
            }
            if (action.equals(BluetoothLeService.D2_STEP_DATA)) {
                final String Step = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            mStepBinding.textStep.setText(Step);
                            mStepBinding.progressBarStep.setProgress(Integer.parseInt(Step));
                            presenter.UpdateStep(Step);
                        } catch (Exception e) {
                            android.util.Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            if (action.equals(BluetoothLeService.D2_CALORIE)) {
                final String Calorie = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            mStepBinding.textKcal.setText(Calorie);
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
        intentFilter.addAction(BluetoothLeService.D2_STEP_DATA);
        intentFilter.addAction(BluetoothLeService.D2_CALORIE);
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
