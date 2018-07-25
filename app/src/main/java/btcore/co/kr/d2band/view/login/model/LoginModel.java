package btcore.co.kr.d2band.view.login.model;

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

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.util.SHA256Util;

import static android.support.constraint.Constraints.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_CALL_LOGIN;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class LoginModel {

    private String mJsonString;
    private String id, pw;
    private ApiListener apiListener;
    private User user;

    public void setUserData(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public boolean checkUserData() {
        return id.length() != 0 && pw.length() != 0;
    }


    public void callLogin(ApiListener listener) {
        this.apiListener = listener;
        CallLogin callLogin =new CallLogin();
        callLogin.execute(URL_CALL_LOGIN, id);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail(String message);
    }

    @SuppressLint("StaticFieldLeak")
    private class CallLogin extends AsyncTask<String, Void, String> {

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

            if (result == null || result.equals("ERROR")) {
                Log.d(TAG, "Error - " + errorString);
            } else {
                mJsonString = result;
                showLoginResult();
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


    private void showLoginResult() {

        String TAG_JSON = "LOGIN";
        String TAG_PW = "PW";
        String TAG_SALT = "SALT";
        String TAG_NUMBER = "NUMBER";
        String TAG_TIME = "TIME";
        String TAG_ID = "ID";
        String TAG_NAME = "NAME";
        String TAG_BIRTHDAY = "BIRTHDAY";
        String TAG_GENDER = "GENDER";
        String TAG_HEIGHT = "HEIGHT";
        String TAG_WEIGHT = "WEIGHT";
        String TAG_PHONE = "PHONE";
        String TAG_ADDR = "ADDR";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String password = item.getString(TAG_PW);
                String salt = item.getString(TAG_SALT);
                String name = item.getString(TAG_NAME);
                String birthday = item.getString(TAG_BIRTHDAY);
                String gender = item.getString(TAG_GENDER);
                String height = item.getString(TAG_HEIGHT);
                String weight = item.getString(TAG_WEIGHT);
                String phone = item.getString(TAG_PHONE);
                String addr = item.getString(TAG_ADDR);

                String newPassword = SHA256Util.getEncrypt(pw, salt);
                if (newPassword.equals(password)) {
                    user = new User();
                    user.setId(id);
                    user.setName(name);
                    user.setBirthday(birthday);
                    user.setGender(gender);
                    user.setHeight(height);
                    user.setWeight(weight);
                    user.setPhone(phone);
                    user.setAddress(addr);
                    apiListener.onSuccess("로그인 성공");
                } else {
                    apiListener.onFail("아이디 또는 비밀번호가 잘못 되었습니다.");
                }


            }
        } catch (JSONException e) {

            Log.d(TAG, "showUserResult : ", e);
        }

    }

}
