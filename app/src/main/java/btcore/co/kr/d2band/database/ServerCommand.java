package btcore.co.kr.d2band.database;

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

import btcore.co.kr.d2band.item.StepItem;
import btcore.co.kr.d2band.user.ContactItem;
import btcore.co.kr.d2band.user.User;

import static btcore.co.kr.d2band.database.mySql.URL_GET_RECEIVER;
import static btcore.co.kr.d2band.database.mySql.URL_GET_STEP;
import static btcore.co.kr.d2band.database.mySql.URL_SET_HEART;
import static btcore.co.kr.d2band.database.mySql.URL_SET_STEP;


public class ServerCommand {

    private final String TAG = getClass().getSimpleName();
    private String DATE;
    private String mJsonString;
    private User user;
    private static ArrayList<StepItem> StepList = new ArrayList<StepItem>();
    public static ArrayList<ContactItem> contactArrayList = null;


    public ArrayList getStep(){
        return StepList;
    }

    public ServerCommand() {
        user = new User();
    }

    public void SELECT_MSG(){
        contactArrayList = new ArrayList<>();
        GetReceiver getReceiver = new GetReceiver();
        getReceiver.execute(URL_GET_RECEIVER, user.getId());
    }
    public void SELECT_STEP(){
        GetStep getStep = new GetStep();
        getStep.execute(URL_GET_STEP, user.getId());
    }
    public void INSERT_STEP(String date, String step) {
        this.DATE = date;
        SetStep setStep = new SetStep();
        setStep.execute(URL_SET_STEP, user.getId(), DATE, step);
    }

    public void INSERT_HEART(String date, String heart) {
        this.DATE = date;
        SetHeart setHeart = new SetHeart();
        setHeart.execute(URL_SET_HEART, user.getId(), DATE, heart);
    }


    @SuppressLint("StaticFieldLeak")
    private class SetStep extends AsyncTask<String, Void, String> {

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

            try {
                if (result == null || result.equals("ERROR") || result.equals("FAIL")) {
                    Log.d(TAG, "STEP DATA INSERT FAILE");
                } else if (result.equals("SUCCESS")) {
                    Log.d(TAG, "STEP DATA INSERT SUCCESS");
                } else {
                    Log.d(TAG, "STEP DATA INSERT FAILE");
                }
            } catch (NullPointerException ignored) {

            }

        }


        @Override
        protected String doInBackground(String... params) {

            String id = params[1];
            String date = params[2];
            String step = params[3];

            String serverURL = params[0];

            String postParameters = "id=" + id
                    + "&date=" + date
                    + "&step=" + step;

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
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;

                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private class SetHeart extends AsyncTask<String, Void, String> {

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

            try {
                if (result == null || result.equals("ERROR") || result.equals("FAIL")) {
                    Log.d(TAG, "HEART DATA INSERT FAILE");
                } else if (result.equals("SUCCESS")) {
                    Log.d(TAG, "HEART DATA INSERT SUCCESS");
                } else {
                    Log.d(TAG, "HEART DATA INSERT FAILE");
                }
            } catch (NullPointerException ignored) {

            }

        }


        @Override
        protected String doInBackground(String... params) {

            String id = params[1];
            String date = params[2];
            String heart = params[3];

            String serverURL = params[0];

            String postParameters = "id=" + id
                    + "&date=" + date
                    + "&heart=" + heart;

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
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;

                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private class GetStep extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null || result.equals("ERROR") || result.equals("")){
                Log.d(TAG, "Error - " + errorString);
            }
            else {
                mJsonString = result;
                StepList.clear();
                showStepResult();
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



    private class GetReceiver extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null || result.equals("ERROR") || result.equals("")){
                Log.d(TAG, "Error - " + errorString);
            }
            else {
                mJsonString = result;
                contactArrayList.clear();
                showReceiverResult();
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

    private void showReceiverResult(){

        String TAG_JSON="RECEIVER";
        String TAG_NAME = "NAME";
        String TAG_PHONE = "PHONE";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String phone = item.getString(TAG_PHONE);

                ContactItem contactItem = new ContactItem();

                contactItem.setName(name);
                contactItem.setPhone(phone);

                contactArrayList.add(contactItem);

            }
        } catch (JSONException e) {

            Log.d(TAG, "showUserResult : ", e);
        }

    }
    private void showStepResult(){

        String TAG_JSON="STEP";
        String TAG_DATE = "DATE";
        String TAG_STEP = "STEP";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(TAG_DATE);
                String step = item.getString(TAG_STEP);


                StepItem stepItem = new StepItem();

                stepItem.setStep(step);
                stepItem.setDate(date);

                StepList.add(stepItem);

            }
        } catch (JSONException e) {

            Log.d(TAG, "showUserResult : ", e);
        }

    }
}
