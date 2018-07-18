package btcore.co.kr.d2band.view.splash;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import btcore.co.kr.d2band.bus.CallProvider;
import btcore.co.kr.d2band.bus.SmsProvider;
import btcore.co.kr.d2band.view.login.LoginActivity;
import btcore.co.kr.d2band.view.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // 테드 퍼미션 라이브러리 상용
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("만약 서비스를 허용하지 않으시면 앱 이용시에 제한이 있습니다.\n\n 권한을 설정 해주세요 [설정] > [권한]")
                .setPermissions(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    /**
     * 퍼미션 리스너
     */
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }
    };


}
