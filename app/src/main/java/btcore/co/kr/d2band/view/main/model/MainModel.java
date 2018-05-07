package btcore.co.kr.d2band.view.main.model;

import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class MainModel {

    BluetoothAdapter mBtAdapter = null;


    public boolean checkBleState() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isEnable(){
        if(!mBtAdapter.isEnabled()){
            return false;
        }else{
            return true;
        }
    }


}
