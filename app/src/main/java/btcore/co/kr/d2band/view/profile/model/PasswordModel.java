package btcore.co.kr.d2band.view.profile.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.util.SHA256Util;

import static btcore.co.kr.d2band.database.mySql.URL_REGISTER;
import static btcore.co.kr.d2band.database.mySql.URL_UPDATE_PASSWORD;

public class PasswordModel {

    private String newPassword;
    private String salt;
    ApiListener apiListener;
    User user;
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

    public void setSalt(){
        this.salt = SHA256Util.generateSalt();
        newPassword= SHA256Util.getEncrypt(newPassword,salt);
    }


    public void UpdatePassword(ApiListener listener){
        this.apiListener = listener;
        user = new User();
        setSalt();
        UpdatePassword updatePassword = new UpdatePassword();
        updatePassword.execute(user.getId(), newPassword, salt);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail (String message);

    }

    class UpdatePassword extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        URL update_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                loading = ProgressDialog.show(Register.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.contains("success")){
                apiListener.onSuccess("비밀번호 변경 성공");
            }else{
                apiListener.onFail("비밀번호 변경 실패");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _pw = params[1];
                String _salt = params[2];


                String url_address = URL_UPDATE_PASSWORD + "?id=" + _id
                        + "&pw=" + _pw
                        + "&salt=" + _salt;

                update_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(update_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                return new String("PasswordChangeUpdateErro: " + e.getMessage());
            }
        }

    }


}
