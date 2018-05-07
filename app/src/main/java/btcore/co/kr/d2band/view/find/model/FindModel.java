package btcore.co.kr.d2band.view.find.model;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import btcore.co.kr.d2band.util.SHA256Util;

import static btcore.co.kr.d2band.database.mySql.URL_FIND_ID;
import static btcore.co.kr.d2band.database.mySql.URL_FIND_PW;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FindModel {

    String phone, name, email, info, pw;
    String encrypt_password, encrypt_salt;

    FindApiListner findApiListner;
    FindPwApiListner findPwApiListner;


    public void setIdInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setPwInfo(String pw, String phone, String email) {
        this.pw = pw;
        this.phone = phone;
        this.email = email;
    }

    public boolean checkPwInfo() {
        if (pw.length() > 0 && phone.length() > 0 && email.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkInfo() {
        if (name.length() > 0 && phone.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void getInfo(FindApiListner listener) {
        this.findApiListner = listener;
        Select_Id task = new Select_Id();
        task.execute(name, phone);

    }

    public void getPwInfo(FindPwApiListner listener) {
        try {
            this.findPwApiListner = listener;
            Select_Pw task1 = new Select_Pw();
            task1.execute(phone, email);
        }catch (Exception e){
            findPwApiListner.onFail("정보가 잘못되었습니다.");
        }

    }



    class Select_Id extends AsyncTask<String, Void, String> {
        URL id_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String checkValue = s;

            if (!checkValue.equals("")) {

                try {
                    info = checkValue;
                    findApiListner.onSuccess(info);
                } catch (Exception e) {
                    findApiListner.onFail("정보가 입력되지 않았습니다.");
                    e.printStackTrace();
                }
            } else {
                findApiListner.onFail("등록된 계정이 없습니다.");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _name = params[0];
                String _phone = params[1];


                String url_address = URL_FIND_ID + "?name=" + _name
                        + "&phone=" + _phone;

                id_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(id_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                return new String("Login Exception: " + e.getMessage());
            }
        }
    }

    class Select_Pw extends AsyncTask<String, Void, String> {
        URL pw_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String checkValue = s;

            if (s.length() > 0) {
                String [] Value = s.split(",");
                try {

                        encrypt_password = Value[0].toString();
                        encrypt_salt = Value[1].toString();
                        String newPassword = SHA256Util.getEncrypt(pw, encrypt_salt);
                        if(newPassword.equals(encrypt_password)){
                            findPwApiListner.onSuccess(pw, phone);
                        }else{
                            findPwApiListner.onFail("입력하신 정보가 다릅니다.");
                        }

                } catch (Exception e) {
                    findPwApiListner.onFail("정보가 입력되지 않았습니다.");
                    e.printStackTrace();
                }
            } else {
                findPwApiListner.onFail("등록된 계정이 없습니다.");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _phone = params[0];
                String _email = params[1];


                String url_address = URL_FIND_PW + "?phone=" + _phone
                        + "&email=" + _email;

                pw_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(pw_url.openStream()));

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


    public interface FindApiListner {
        void onSuccess(String message);

        void onFail(String message);
    }
    public interface FindPwApiListner {
        void onSuccess(String message, String email);

        void onFail(String message);
    }

}
