package btcore.co.kr.d2band.view.find.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.FragmentPwBinding;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FragmentPw extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private static final String ARG_COLUMN_COUNT = "colum-count";
    String pw, phone, id;
    public FragmentPwBinding mBinding;

    public  FragmentPw (){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup contanier, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pw, contanier, false);
        mBinding.setFindPw(this);

        return mBinding.getRoot();
    }


    public String getID(){
        this.id = mBinding.editId.getText().toString();
        return id;
    }

    public String getPhone(){
        this.phone = mBinding.editPhone.getText().toString();
        return phone;
    }
    public String getName(){
        this.pw = mBinding.editName.getText().toString();
        return pw;
    }




}
