package btcore.co.kr.d2band.view.main.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import btcore.co.kr.d2band.BR;
import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.FragmentBottomBinding;
import btcore.co.kr.d2band.view.couple.CoupleActivity;
import btcore.co.kr.d2band.view.heartrate.HeartRateActivity;
import btcore.co.kr.d2band.view.main.Bottom;
import btcore.co.kr.d2band.view.main.presenter.BottomPresenter;
import btcore.co.kr.d2band.view.message.MessageAtivity;
import btcore.co.kr.d2band.view.sos.SosActivity;
import btcore.co.kr.d2band.view.step.StepActivity;
import butterknife.OnClick;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class FragmentBottomBar extends Fragment implements Bottom.View {

    private final String TAG = getClass().getSimpleName();
    private static int currentPage = 0;
    FragmentBottomBinding mBinding;
    Bottom.Presenter presenter;
    public FragmentBottomBar() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        presenter = new BottomPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contanier, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom, contanier, false);
        mBinding.setBottomBar(this);
        presenter.chekCurrentPage(currentPage);
        return mBinding.getRoot();
    }

    @OnClick(R.id.btn_activity)
    public void OnAcivitiy(View view){
        if(currentPage != 0) {
            currentPage = 0;
            presenter.chekCurrentPage(currentPage);
            presenter.startActivity(currentPage);
        }
    }
    @OnClick(R.id.btn_heartrate)
    public void OnHeartRate(View view){
        if(currentPage != 1){
            currentPage = 1;
            presenter.chekCurrentPage(currentPage);
            presenter.startActivity(currentPage);
        }
    }
    @OnClick(R.id.btn_alarm)
    public void OnAlarm(View view){
        if(currentPage != 2){
            currentPage = 2;
            presenter.chekCurrentPage(currentPage);
            presenter.startActivity(currentPage);
        }

    }
    @OnClick(R.id.btn_message)
    public void OnMessage(View view){
        currentPage = 3;
        presenter.chekCurrentPage(currentPage);
        presenter.startActivity(currentPage);
    }
    @OnClick(R.id.btn_couple)
    public void OnCouple(View view){
        if(currentPage != 4) {
            currentPage = 4;
            presenter.chekCurrentPage(currentPage);
            presenter.startActivity(currentPage);
        }
    }

    @Override
    public void showImage(int current) {
        switch (current){
            case 0:
                mBinding.btnActivity.setBackgroundResource(R.drawable.icon_activity);
                mBinding.btnHeartrate.setBackgroundResource(R.drawable.icon_unheart);
                mBinding.btnAlarm.setBackgroundResource(R.drawable.icon_unalarm);
                mBinding.btnCouple.setBackgroundResource(R.drawable.icon_uncouple);
                mBinding.btnMessage.setBackgroundResource(R.drawable.icon_unmessage);
                break;
            case 1:
                mBinding.btnActivity.setBackgroundResource(R.drawable.icon_unactivity);
                mBinding.btnHeartrate.setBackgroundResource(R.drawable.icon_heart);
                mBinding.btnAlarm.setBackgroundResource(R.drawable.icon_unalarm);
                mBinding.btnCouple.setBackgroundResource(R.drawable.icon_uncouple);
                mBinding.btnMessage.setBackgroundResource(R.drawable.icon_unmessage);
                break;
            case 2:
                mBinding.btnActivity.setBackgroundResource(R.drawable.icon_unactivity);
                mBinding.btnHeartrate.setBackgroundResource(R.drawable.icon_unheart);
                mBinding.btnAlarm.setBackgroundResource(R.drawable.icon_alarm);
                mBinding.btnCouple.setBackgroundResource(R.drawable.icon_uncouple);
                mBinding.btnMessage.setBackgroundResource(R.drawable.icon_unmessage);
                break;
            case 3:
                mBinding.btnActivity.setBackgroundResource(R.drawable.icon_unactivity);
                mBinding.btnHeartrate.setBackgroundResource(R.drawable.icon_unheart);
                mBinding.btnAlarm.setBackgroundResource(R.drawable.icon_unalarm);
                mBinding.btnCouple.setBackgroundResource(R.drawable.icon_uncouple);
                mBinding.btnMessage.setBackgroundResource(R.drawable.icon_message);
                break;
            case 4:
                mBinding.btnActivity.setBackgroundResource(R.drawable.icon_unactivity);
                mBinding.btnHeartrate.setBackgroundResource(R.drawable.icon_unheart);
                mBinding.btnAlarm.setBackgroundResource(R.drawable.icon_unalarm);
                mBinding.btnCouple.setBackgroundResource(R.drawable.icon_couple);
                mBinding.btnMessage.setBackgroundResource(R.drawable.icon_unmessage);
                break;
        }
    }

    @Override
    public void startMainActivity(int current) {
        switch (current){
            case 0:
                Intent intent = new Intent(getContext(), StepActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case 1:
                Intent intent1 = new Intent(getContext(), HeartRateActivity.class);
                startActivity(intent1);
                getActivity().finish();
                break;
            case 2:
                Intent intent2 = new Intent(getContext(), SosActivity.class);
                startActivity(intent2);
                getActivity().finish();
                break;
            case 3:
                Intent intent3 = new Intent(getContext(), MessageAtivity.class);
                startActivity(intent3);
                getActivity().finish();
                break;
            case 4:
                Intent intent4 = new Intent(getContext(), CoupleActivity.class);
                startActivity(intent4);
                getActivity().finish();
                break;
        }
    }
}

