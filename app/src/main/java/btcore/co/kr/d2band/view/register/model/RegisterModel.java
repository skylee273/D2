package btcore.co.kr.d2band.view.register.model;

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

import btcore.co.kr.d2band.util.SHA256Util;

import static android.support.constraint.Constraints.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_CALL_REGISTER;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class RegisterModel {

    private String id;
    private String pw;
    private String pwConfirm;
    private String salt;
    private String newPw;
    private String name;
    private String birthday;
    private String gender;
    private String height;
    private String weight;
    private String phone;
    private String address;
    private ApiListener apiListener;

    public void setUserData(String id, String pw, String pwConfirm, String name, String birthday, String gender, String height, String weight, String phone, String address) {
        this.id = id;
        this.pw = pw;
        this.pwConfirm = pwConfirm;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.phone = phone;
        this.address = address;

    }

    public boolean checkPw(){
        return pw.length() > 0;
    }

    public void setSalt(){
        this.salt = SHA256Util.generateSalt();
        this.newPw = SHA256Util.getEncrypt(pw,salt);
    }

    public String checkUserDataLength(){
        String message = null;
        if(id.length() < 8){
            message =  "아이디는 8자 이상입니다.";
        }else if(pw.length() < 8){
            message =  "비밀번호는 8자 이상입니다.";
        }else if(!pw.equals(pwConfirm)){
            message = "비밀번호가 같지 않습니다.";
        }else if(name.length() == 0){
            message = "이름을 입력해 주세요";
        }else if(birthday.length() == 0){
            message = "생년월일을 입력해주세요";
        }else if(gender.length() == 0){
            message = "성별을 체크해 주세요";
        }else if(height.length() == 0){
            message = "신장을 입력하세요";
        }else if(weight.length() == 0 ){
            message = "몸무게를 입력하세요";
        }else if(phone.length() == 0){
            message = "핸드폰 번호를 입력하세요";
        }else if(address.length() == 0){
            message = "주소를 입력하세요";
        }else{
            message = "성공";
        }

        return message;
    }


    public void callSignup(ApiListener listener){
        this.apiListener = listener;
        CallRegister callRegister = new CallRegister();
        callRegister.execute(URL_CALL_REGISTER, id, newPw, salt, name, birthday, gender, height, weight, phone, address);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail (String message);
    }

    @SuppressLint("StaticFieldLeak")
    private class CallRegister extends AsyncTask<String, Void, String> {

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
                    Log.d(TAG, "Error - " + errorString);
                } else if (result.equals("SUCCESS")) {
                    apiListener.onSuccess("회원가입 성공");
                } else {
                    apiListener.onFail("회원가입 실패");
                    Log.d(TAG, "Error - " + errorString);
                }
            } catch (NullPointerException ignored) {

            }

        }


        @Override
        protected String doInBackground(String... params) {

            String id = params[1];
            String pw = params[2];
            String salt = params[3];
            String name = params[4];
            String birthday = params[5];
            String gender = params[6];
            String height = params[7];
            String weight = params[8];
            String phone = params[9];
            String address = params[10];

            String serverURL = params[0];

            String postParameters = "id=" + id
                    + "&pw=" + pw
                    + "&salt=" + salt
                    + "&name=" + name
                    + "&birthday=" + birthday
                    + "&gender=" + gender
                    + "&height=" + height
                    + "&weight=" + weight
                    + "&phone=" + phone
                    + "&address=" + address;

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


}
