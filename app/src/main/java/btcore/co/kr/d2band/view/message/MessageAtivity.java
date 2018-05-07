package btcore.co.kr.d2band.view.message;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.ActivityMessageBinding;

/**
 * Created by leehaneul on 2018-02-27.
 */

public class MessageAtivity extends AppCompatActivity {
    ActivityMessageBinding messageBinding;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        messageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        messageBinding.setMessageActivity(this);

    }

}
