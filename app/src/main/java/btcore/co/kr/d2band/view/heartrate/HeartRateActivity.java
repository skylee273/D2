package btcore.co.kr.d2band.view.heartrate;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import btcore.co.kr.d2band.databinding.ActivityHeartrateBinding;
import btcore.co.kr.d2band.view.heartrate.presenter.HeartRate;
import btcore.co.kr.d2band.view.heartrate.presenter.HeartRatePresenter;
import btcore.co.kr.d2band.R;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-01-17.
 */

/**
 * 블루투스 프로그래밍을 옵저버 패턴으로 구현후 옵저버 패턴에서 데이터를 받았을 경우를 가정하에 코딩.
 */
public class HeartRateActivity extends AppCompatActivity implements HeartRate.View {

    final String TAG = "HeartRateActivity";
    // 멤버 변수
    ActivityHeartrateBinding mBinding;
    HeartRate.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_heartrate);
        mBinding.setHeartActivity(this);

        presenter = new HeartRatePresenter(this);


        /**
         * 배열로된 블루투스 데이터 초기화
         */
        // presenter.initBleData();
        // presenter.callBpm

    }

    @Override
    public void showHeartData(String[] heartRate) {

    }

    @Override
    public void showErrorMessage(String message) {
        Log.e(TAG,message);
    }
}
