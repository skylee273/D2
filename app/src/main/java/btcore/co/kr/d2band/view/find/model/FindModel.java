package btcore.co.kr.d2band.view.find.model;

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
import java.util.Random;

import btcore.co.kr.d2band.util.SHA256Util;

import static android.support.constraint.Constraints.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_GET_ID;
import static btcore.co.kr.d2band.database.mySql.URL_GET_PW;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FindModel {

    private String newPassword;
    private String mJsonString;

    private String phone, name, id;

    private FindApiListner findApiListner;
    private FindPwApiListner findPwApiListner;


    public void setIdInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setPwInfo(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public boolean checkPwInfo() {
        return id.length() > 0 && name.length() > 0 && phone.length() > 0;
    }

    public boolean checkInfo() {
        return name.length() > 0 && phone.length() > 0;
    }

    public void getInfo(FindApiListner listener) {
        this.findApiListner = listener;
        GetID getID = new GetID();
        getID.execute(URL_GET_ID, name, phone);
    }

    public void getPwInfo(FindPwApiListner listener) {
        try {
            this.findPwApiListner = listener;
            newPassword = getRandomPassword();
            String salt = SHA256Util.generateSalt();
            String Password = SHA256Util.getEncrypt(newPassword, salt);
            GetPassword getPassword = new GetPassword();
            getPassword.execute(URL_GET_PW, id, name, phone, Password, salt);

        } catch (Exception e) {
            findPwApiListner.onFail("정보가 잘못되었습니다.");
        }

    }


    private String getRandomPassword() {
        String[] passwords = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder builder = new StringBuilder("");

        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            builder.append(passwords[random.nextInt(passwords.length)]);
        }

        return builder.toString();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPassword extends AsyncTask<String, Void, String> {

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

            try {
                if (result == null || result.equals("ERROR") || result.equals("FAIL")) {
                    Log.d(TAG, "Error - " + errorString);
                } else if (result.equals("SUCCESS")) {
                    findPwApiListner.onSuccess("고객님의 임시 비밀번호는 ", newPassword);
                    mJsonString = result;
                } else {
                    findPwApiListner.onFail("임시 비밀번호 발급 실패");
                    Log.d(TAG, "Error - " + errorString);
                }
            } catch (NullPointerException ignored) {

            }

        }


        @Override
        protected String doInBackground(String... params) {

            String id = params[1];
            String name = params[2];
            String phone = params[3];
            String pw = params[4];
            String salt = params[5];

            String serverURL = params[0];
            String postParameters = "id=" + id + "&name=" + name
                    + "&phone=" + phone
                    + "&pw=" + pw
                    + "&salt=" + salt;

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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetID extends AsyncTask<String, Void, String> {

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
                if (result == null || result.equals("ERROR")) {
                    Log.d(TAG, "Error - " + errorString);
                } else {
                    mJsonString = result;
                    showIDResult();
                }
            } catch (NullPointerException e) {
                Log.d(TAG, "Error - " + errorString);
            }

        }


        @Override
        protected String doInBackground(String... params) {

            String name = params[1];
            String phone = params[2];
            String serverURL = params[0];
            String postParameters = "name=" + name + "&phone=" + phone;


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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    public interface FindApiListner {
        void onSuccess(String message);

        void onFail(String message);
    }

    public interface FindPwApiListner {
        void onSuccess(String message, String pw);

        void onFail(String message);
    }


    private void showIDResult() {

        String TAG_JSON = "ID";
        String TAG_ID = "ID";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString(TAG_ID);

                if (id != null) {
                    findApiListner.onSuccess(id);
                } else {
                    findApiListner.onFail("등록된 계정이 없습니다.");
                }

            }
        } catch (JSONException e) {

            Log.d(TAG, "showUserResult : ", e);
        }

    }
}
