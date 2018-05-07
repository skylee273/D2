package btcore.co.kr.d2band.view.login.model;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.util.SHA256Util;

import static btcore.co.kr.d2band.database.mySql.URL_LOGIN;
import static btcore.co.kr.d2band.database.mySql.URL_SET_USER;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class LoginModel {

    String id, pw;
    String encrypt_password, encrypt_salt;
    ApiListener apiListener, UserApi;
    User user;
    public void setUserData(String id, String pw){
        this.id = id;
        this.pw = pw;
    }

    public boolean checkUserId(){

            if(id.length() >  0){
                return  true;
            }else{
                return false;
            }

    }
    public boolean checkUserData(){

        if(id.length() == 0){
            return  false;
        }else if(pw.length() == 0){
            return false;
        }else {
            return true;
        }
    }

    public void callSetUser(ApiListener listener){
        this.UserApi = listener;
        GetUser getUser = new GetUser();
        getUser.execute(id);

    }

    public void callLogin(ApiListener listener){
        this.apiListener = listener;
        Login task = new Login();
        task.execute(id);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail (String message);
    }


    class Login extends AsyncTask<String, Void, String> {
        URL login_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String loginValue = s;

            if(!loginValue.equals("")){
                String [] Value = loginValue.split(",");
                try {
                    encrypt_password = Value[0].toString();
                    encrypt_salt = Value[1].toString();
                    String newPassword = SHA256Util.getEncrypt(pw, encrypt_salt);
                    if(newPassword.equals(encrypt_password)){
                        apiListener.onSuccess("로그인 성공");
                    }else{
                        apiListener.onFail("아이디 또는 비밀번호가 잘못 되었습니다.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                apiListener.onFail("아이디 또는 비밀번호가 잘못 되었습니다.");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];


                String url_address = URL_LOGIN + "?id=" + _id;

                login_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(login_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return new String("Login Exception: " + e.getMessage());
            }
        }
    }
    class GetUser extends AsyncTask<String, Void, String> {
        URL userUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String User[] = s.split("#####");
                user = new User();
                user.setId(User[0]);
                user.setName(User[1]);
                user.setBirthday(User[2]);
                user.setHeight(User[3]);
                user.setWeight(User[4]);
                user.setPhone(User[5]);
                user.setAddress(User[6]);
                UserApi.onSuccess("유저 정보 저장 완료");
            }catch (ArrayIndexOutOfBoundsException e){
                UserApi.onFail("유저 정보 저장 실패");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];

                String url_address = URL_SET_USER + "?id=" + _id;

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
