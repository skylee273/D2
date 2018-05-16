package btcore.co.kr.d2band.view.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivitySettingBinding;
import btcore.co.kr.d2band.view.lock.LockActivity;
import btcore.co.kr.d2band.view.profile.ProfileAcitivty;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding settingBinding;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        settingBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        settingBinding.setSettingActivity(this);
    }

    @OnClick(R.id.box_lock)
    public void OnLock(View view){
        Intent intent = new Intent(getApplicationContext(), LockActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.box_kakao)
    public void OnKakao(View view){
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }
    @OnClick(R.id.box_gps)
    public void OnGps(View view){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), ProfileAcitivty.class);
        startActivity(intent);
        finish();
    }


}
