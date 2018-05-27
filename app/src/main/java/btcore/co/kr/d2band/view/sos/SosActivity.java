package btcore.co.kr.d2band.view.sos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import btcore.co.kr.d2band.Manifest;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivitySosBinding;
import btcore.co.kr.d2band.view.find.FindIdActivity;
import btcore.co.kr.d2band.view.setting.SettingActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class SosActivity  extends AppCompatActivity{

    private final  String TAG = getClass().getSimpleName();
    ActivitySosBinding mSosBinding;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSosBinding = DataBindingUtil.setContentView(this, R.layout.activity_sos);
        mSosBinding.setSosActivity(this);

    }

    @OnClick(R.id.btn_112)
    public void OnPolice(View view){
        String tel = "tel:" + "112";
        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
    }
    @OnClick(R.id.btn_119)
    public void OnAmbulance(View view){
        String tel = "tel:" + "119";
        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
    }
    @OnClick(R.id.btn_call)
    public void OnCall(View view){
        String tel = "tel:" + "";
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
    }
    @OnClick(R.id.btn_message)
    public void OnMessage(View view){
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.putExtra("sms_body", "");
        it.setType("vnd.android-dir/mms-sms");
        startActivity(it);
    }
    @OnClick(R.id.btn_map)
    public void OnMap(View view){
        Uri uri = Uri.parse("geo:38.899533,-77.036476");
        Intent it = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(it);
    }
    @OnClick(R.id.btn_settings)
    public void OnSettings(View view){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.btn_sound)
    public void OnRing(View view){
        Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(i,0);
    }

    @OnClick(R.id.btn_question)
    public void OnQuestion(View view){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 0:
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED){
                    Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
                    } else {
                        ringtone.setStreamType(AudioManager.STREAM_ALARM);
                    }
                    ringtone.play();
                }else{
                }

                break;
        }
    }
}
