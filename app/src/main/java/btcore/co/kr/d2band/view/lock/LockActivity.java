package btcore.co.kr.d2band.view.lock;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityLockBinding;
import btcore.co.kr.d2band.view.lock.adpter.LockAdapter;
import btcore.co.kr.d2band.view.setting.SettingActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

public class LockActivity extends AppCompatActivity {

    ActivityLockBinding lockBinding;
    LockAdapter lockAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        lockBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock);
        lockBinding.setLockActivity(this);

        lockAdapter = new LockAdapter(this);
        lockBinding.viewPager.setAdapter(lockAdapter);
    }

    @OnClick(R.id.btn_back)
    public void OnBack(View view){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        finish();
    }
}
