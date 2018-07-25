package btcore.co.kr.d2band.view.find.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.FragmentIdBinding;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FragmentId extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private static final String ARG_COLUMN_COUNT = "colum-count";
     String name, phone;
    public FragmentIdBinding mBinding;

    public  FragmentId (){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup contanier, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_id, contanier, false);
        mBinding.setFindId(this);

        return mBinding.getRoot();
    }


    public String getPhone(){

        phone = mBinding.editPhone.getText().toString();
        return phone;
    }
    public String getName(){
        name = mBinding.editName.getText().toString();
        return name;
    }


}
