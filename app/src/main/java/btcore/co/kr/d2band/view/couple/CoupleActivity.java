package btcore.co.kr.d2band.view.couple;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityCoupleBinding;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class CoupleActivity extends AppCompatActivity {
    ActivityCoupleBinding mCoupleBinding;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCoupleBinding = DataBindingUtil.setContentView(this, R.layout.activity_couple);
        mCoupleBinding.setCoupleActivity(this);

    }

}
