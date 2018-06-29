package btcore.co.kr.d2band.view.heartrate.model;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.view.message.model.MessageModel;

import static btcore.co.kr.d2band.database.mySql.URL_SELECT_HEART;
import static btcore.co.kr.d2band.database.mySql.URL_SET_RECEIVE;

/**
 * Created by leehaneul on 2018-01-17.
 */

// 모델 : 데이터 + 상태 + 비즈니스 로직 ( 두뇌 역활) 만약 심박수 상태를 계산하기 위한 로직을 짜야 한다면 모델부분에 짜야한다.
public class HeartRateModel {

    User user;
    String heart;
    String avgHeart;
    String MaxHeart;
    String MinHeart;
    String State;
    String Error;
    String [] HeartStruct;
    HeartApiListener apiListener;

    public HeartRateModel(){
        user = new User();
    }

    public String getAvgHeart() {
        int avg = 0;
        int sum = 0;
        for(int i = 0; i < HeartStruct.length;i++){
            sum += Integer.valueOf(HeartStruct[i]);
        }
        avg = sum / HeartStruct.length;
        avgHeart = String.valueOf(avg);
        return avgHeart;
    }

    public String getMaxHeart() {
        int max = 0;
        for(int i = 0; i < HeartStruct.length;i++){
            if(Integer.valueOf(HeartStruct[i]) > max){
                max = Integer.valueOf(HeartStruct[i]);
            }
        }
        MaxHeart = String.valueOf(max);
        return MaxHeart;
    }
    public String getMinHeart() {
        int min = 999;
        for(int i = 0; i < HeartStruct.length;i++){
            if(Integer.valueOf(HeartStruct[i]) < min){
                min = Integer.valueOf(HeartStruct[i]);
            }
        }
        MinHeart = String.valueOf(min);
        return MinHeart;
    }

    public String getState() {
        int bpm = Integer.parseInt(heart);
        if(bpm >= 50  && bpm < 66){ State = "좋음";}
        if(bpm >= 66  && bpm < 80){ State = "평균";}
        if(bpm >= 80 || bpm < 50) { State = "나쁨";}

        return State;
    }

    public String getError() {
        Random random = new Random();
        int error = random.nextInt(5);
        Error = String.valueOf(error);
        return Error;
    }

    public String getHeart(){
        return heart;
    }

    public boolean checkHeartData(String h){
        this.heart = h;
        try{
            if(heart.length() > 0){
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }

    public void setHeart(HeartApiListener listener) {
        this.apiListener = listener;
        user = new User();
        GetHeart TaskHeart = new GetHeart();
        TaskHeart.execute(user.getId());
    }

    public interface HeartApiListener {
        void onSuccess();
        void onFail();
    }

    class GetHeart extends AsyncTask<String, Void, String> {
        URL userUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s != null) {
                    HeartStruct = s.split("&&&&&");
                    apiListener.onSuccess();
                }else{
                    apiListener.onFail();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                apiListener.onFail();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];

                String url_address = URL_SELECT_HEART + "?id=" + _id;

                userUrl = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(userUrl.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return new String("User Exception: " + e.getMessage());
            }
        }
    }





}
