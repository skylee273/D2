package btcore.co.kr.d2band.view.heartrate.model;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.view.heartrate.item.HeartItem;

import static android.content.ContentValues.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_GET_HEART;

/**
 * Created by leehaneul on 2018-01-17.
 */

// 모델 : 데이터 + 상태 + 비즈니스 로직 ( 두뇌 역활) 만약 심박수 상태를 계산하기 위한 로직을 짜야 한다면 모델부분에 짜야한다.
public class HeartRateModel {

    private User user;
    private String heart;
    private String State;
    private HeartApiListener apiListener;
    private String mJsonString;
    private ArrayList<HeartItem> HeartList = new ArrayList<HeartItem>();

    public HeartRateModel(){
        user = new User();
    }

    public String getAvgHeart() {
        int avg = 0;
        int sum = 0;
        for(HeartItem aHeartArrayList : HeartList){
            sum += Integer.valueOf(aHeartArrayList.getHeart());
        }
        avg = sum / HeartList.size();
        return String.valueOf(avg);
    }

    public String getMaxHeart() {
        int max = 0;
        for(HeartItem aHeartArrayList : HeartList){
            if (Integer.valueOf(aHeartArrayList.getHeart()) > max) {
                max = Integer.valueOf(aHeartArrayList.getHeart());
            }
        }
        return String.valueOf(max);
    }
    public String getMinHeart() {
        int min = 999;
        for(HeartItem aHeartArrayList : HeartList){
            if(Integer.valueOf(aHeartArrayList.getHeart()) < min){
                min = Integer.valueOf(aHeartArrayList.getHeart());
            }
        }
        return String.valueOf(min);
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
        return String.valueOf(error);
    }

    public String getHeart(){
        return heart;
    }

    public boolean checkHeartData(String h){
        this.heart = h;
        try{
            return heart.length() > 0;
        }catch (NullPointerException e){
            return false;
        }
    }

    public void setHeart(HeartApiListener listener) {
        this.apiListener = listener;
        user = new User();
        GetHeart getHeart = new GetHeart();
        getHeart.execute(URL_GET_HEART, user.getId());
    }

    public interface HeartApiListener {
        void onSuccess();
        void onFail();
    }


    @SuppressLint("StaticFieldLeak")
    private class GetHeart extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "response - " + result);

            if (result == null || result.equals("ERROR") || result.equals("")){
                Log.d(TAG, "Error - " + errorString);
                apiListener.onFail();
            }
            else {
                mJsonString = result;
                HeartList.clear();
                showHeartResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String id = params[1];
            String serverURL = params[0];
            String postParameters = "id=" + id;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
    private void showHeartResult(){

        String TAG_JSON="BPM";
        String TAG_DATE = "DATE";
        String TAG_HEART = "HEART";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(TAG_DATE);
                String heart = item.getString(TAG_HEART);


                HeartItem heartItem = new HeartItem();
                heartItem.setHeart(heart);
                heartItem.setDate(date);
                HeartList.add(heartItem);

            }
            apiListener.onSuccess();
        } catch (JSONException e) {

            Log.d(TAG, "showUserResult : ", e);
        }

    }

}
