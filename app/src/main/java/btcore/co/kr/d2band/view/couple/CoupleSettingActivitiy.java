package btcore.co.kr.d2band.view.couple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.bus.CallBusEvent;
import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.databinding.ActivityCoupleSettingBinding;
import btcore.co.kr.d2band.service.BluetoothLeService;
import btcore.co.kr.d2band.user.ContactItem;
import btcore.co.kr.d2band.util.BleProtocol;
import btcore.co.kr.d2band.view.couple.dialog.CoupleDialog;
import btcore.co.kr.d2band.view.couple.presenter.CoupleSettingPresenter;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

import static btcore.co.kr.d2band.database.ServerCommand.contactArrayList;
import static btcore.co.kr.d2band.service.BluetoothLeService.STATE;

public class CoupleSettingActivitiy extends AppCompatActivity implements CoupleSetting.view {

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
    private CoupleDialog coupleDialog;
    private final int GALLERY_CODE = 1112;
    private CoupleSetting.Presenter presenter;

    int imageType = 0;
    SharedPreferences.Editor editor;
    SharedPreferences pref = null;
    String dateStart, dateForMat;
    ActivityCoupleSettingBinding coupleSettingBinding;
    MaterialDialog mMaterialDialog;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coupleSettingBinding = DataBindingUtil.setContentView(this, R.layout.activity_couple_setting);
        coupleSettingBinding.setCoupleSettingActivitiy(this);

        // 블루투스 서비스 등록
        service_init();

        // 블루투스 생성
        bleProtocol = new BleProtocol();

        // 버스 등록
        CallProvider.getInstance().register(this);
        SmsProvider.getInstance().register(this);

        pref = getSharedPreferences("D2", Activity.MODE_PRIVATE);
        editor = pref.edit();

        AutoConnection();

        // 프레젠터 등록
        presenter = new CoupleSettingPresenter(this);

        presenter.updateSaveView();
    }

    @OnClick(R.id.text_me)
    public void onMe(View view) {
        coupleDialog = new CoupleDialog(this);
        coupleDialog.show();
        coupleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (coupleDialog.getmName() != null && !coupleDialog.getmName().equals("")) {
                    //presenter.UpdateGoal(stepDialog.getmGoal());
                    editor.putString("COUPLE_NAME_ME", coupleDialog.getmName());
                    editor.commit();
                    presenter.updateNickName(coupleDialog.getmName(), 0);
                }
            }
        });
    }

    @OnClick(R.id.text_couple)
    public void onCouple(View view) {
        coupleDialog = new CoupleDialog(this);
        coupleDialog.show();
        coupleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (coupleDialog.getmName() != null && !coupleDialog.getmName().equals("")) {
                    //presenter.UpdateGoal(stepDialog.getmGoal());
                    editor.putString("COUPLE_NAME_COUPLE", coupleDialog.getmName());
                    editor.commit();
                    presenter.updateNickName(coupleDialog.getmName(), 1);
                }
            }
        });
    }


    @OnClick(R.id.image_calendar)
    public void onCalendar(View view) {

        final Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(CoupleSettingActivitiy.this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                dateStart = String.format("%04d년 %02d월 %02d일", year, month + 1, date);
                dateForMat = String.format("%04d-%02d-%02d", year, month + 1, date);
                // presenter
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editor.putString("COUPLE_DATE", dateStart);
                editor.putString("COUPLE_DATE_FORMAT", dateForMat);
                editor.commit();
                presenter.updateCalendar(dateStart);
            }
        });
    }

    @OnClick(R.id.btn_image)
    public void onImage(View view) {
        selectGallery();
        imageType = 0;
    }
    @OnClick(R.id.image_me)
    public void onImageMe(View view) {
        selectGallery();
        imageType = 1;
    }
    @OnClick(R.id.image_couple)
    public void onImageCouple(View view) {
        selectGallery();
        imageType = 2;
    }
    @OnClick(R.id.btn_back)
    public void onBack(View view){
        settingSave();
    }
    @OnClick(R.id.btn_confirm)
    public void onConfirm(View view){
        presenter.isNextActivity();
    }
    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData(), imageType); //갤러리에서 가져오기
                    break;
                default:
                    break;
            }
        }
    }

    private void sendSavePicture(String path, int type){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
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
    private void sendPicture(Uri imgUri, int type) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        switch (type){
            case 0:
                editor.putString("IMAGEPATH_IMAGE",imagePath);
                editor.commit();
                presenter.updateImage(bitmap, type);
                break;
            case 1:
                editor.putString("IMAGEPATH_MY",imagePath);
                editor.commit();
                presenter.updateImage(bitmap, type);
                break;
            case 2:
                editor.putString("IMAGEPATH_COUPLE",imagePath);
                editor.commit();
                presenter.updateImage(bitmap, type);
                break;
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    @Override
    public void onBackPressed() {
        settingSave();
    }

    private void settingSave(){
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("커플설정")
                .setMessage("설정내용을 저장 하지 않으면 모든 데이터는 초기화 됩니다. 데이터를 초기화 하시겠습니까?")
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.remove("IMAGEPATH_IMAGE");
                        editor.remove("IMAGEPATH_MY");
                        editor.remove("IMAGEPATH_COUPLE");
                        editor.remove("COUPLE_NAME_ME");
                        editor.remove("COUPLE_NAME_COUPLE");
                        editor.remove("COUPLE_DATE");
                        editor.remove("COUPLE_DATE_FORMAT");
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), CoupleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoTimer != null) {
            autoTimer.cancel();
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

            //*********************//
            assert action != null;
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
        TimerTask autoTask = new TimerTask() {
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
                for (ContactItem aContactArrayList : contactArrayList) {
                    String name = aContactArrayList.getName();
                    if (callName.equals(name)) {
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
                for (ContactItem aContactArrayList : contactArrayList) {
                    String name = aContactArrayList.getName();
                    if (NameOrPhone.equals(name)) {
                        subFlag = true;
                    }
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

    @Override
    public void showErrorMessage(String message) {
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showImageBitmap(Bitmap bitmap, int type) {
        switch (type){
            case 0:
                Glide.with(this).load(bitmap).into(coupleSettingBinding.btnImage);
                coupleSettingBinding.imagePlus.setVisibility(View.INVISIBLE);
                coupleSettingBinding.textAdd.setVisibility(View.INVISIBLE);
                break;
            case 1:
                Glide.with(this).load(bitmap).apply(new RequestOptions().circleCrop()).into(coupleSettingBinding.imageMe);
                break;
            case 2:
                Glide.with(this).load(bitmap).apply(new RequestOptions().circleCrop()).into(coupleSettingBinding.imageCouple);
                break;
        }
    }

    @Override
    public void showNickName(String name, int type) {
        switch (type){
            case 0:
                coupleSettingBinding.textMe.setText(name);
                break;
            case 1:
                coupleSettingBinding.textCouple.setText(name);
                break;
        }
    }

    @Override
    public void showCalendar(String date) {
        coupleSettingBinding.textDateStart.setText(date);
    }

    @Override
    public void nextActivity() {
        Snackbar.make(getWindow().getDecorView().getRootView(), "저장되었습니다.", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), CoupleActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSaveView() {
        String image = pref.getString("IMAGEPATH_IMAGE",null);
        String imageMy = pref.getString("IMAGEPATH_MY", null);
        String imageCouple = pref.getString("IMAGEPATH_COUPLE",null);
        String myName = pref.getString("COUPLE_NAME_ME",null);
        String coupleName = pref.getString("COUPLE_NAME_COUPLE",null);
        String coupleDate = pref.getString("COUPLE_DATE", null);

        if(image != null ) { sendSavePicture(image, 0);}
        if(imageMy != null) { sendSavePicture(imageMy, 1);}
        if(imageCouple != null) { sendSavePicture(imageCouple, 2);}
        if(myName != null) { presenter.updateNickName(myName, 0);}
        if(coupleName != null) { presenter.updateNickName(coupleName, 1);}
        if(coupleDate != null) { presenter.updateCalendar(coupleDate);}
    }
}
