package btcore.co.kr.d2band.view.profile.model;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.util.SHA256Util;

import static android.support.constraint.Constraints.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_GET_PW;

public class PasswordModel {

    private String newPassword;
    private String salt;

    private ApiListener apiListener;

    public boolean checkPassword(String password){
        this.newPassword = password;

        try{
            if(newPassword.length() < 8){
                return false;
            }

        }catch (NullPointerException e){
            return false;
        }
        return  true;
    }

    private void setSalt(){
        this.salt = SHA256Util.generateSalt();
        newPassword= SHA256Util.getEncrypt(newPassword,salt);
    }


    public void UpdatePassword(ApiListener listener){
        this.apiListener = listener;
        User user = new User();
        setSalt();
        GetPassword getPassword = new GetPassword();
        getPassword.execute(URL_GET_PW, user.getId(), user.getName(), user.getPhone(), newPassword, salt);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail (String message);

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
                    apiListener.onSuccess("비밀번호 변경 성공");
                } else {
                    apiListener.onFail("비밀번호 변경 실패");
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



}
