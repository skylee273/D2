package btcore.co.kr.d2band.view.find.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import btcore.co.kr.d2band.util.SHA256Util;

import static btcore.co.kr.d2band.database.mySql.URL_FIND_ID;
import static btcore.co.kr.d2band.database.mySql.URL_FIND_PW;
import static btcore.co.kr.d2band.database.mySql.URL_UPDATE_PASSWORD;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FindModel {

    private String newPassword;
    private String salt;

    String phone, name, info, pw;
    String encrypt_password, encrypt_salt;

    FindApiListner findApiListner;
    FindPwApiListner findPwApiListner;


    public void setIdInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setPwInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public boolean checkPwInfo() {
        if (name.length() > 0 && phone.length() > 0) {
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
            task1.execute(name, phone);
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

    public static String getRandomPassword(int length) {
        String[] passwords = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder builder = new StringBuilder("");

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            builder.append(passwords[random.nextInt(passwords.length)]);
        }

        return builder.toString();
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

            if (s.length() > 0) {
                try {
                        newPassword = getRandomPassword(9);
                        salt = SHA256Util.generateSalt();
                        String Password= SHA256Util.getEncrypt(newPassword,salt);
                        UpdatePassword task = new UpdatePassword();
                        task.execute(s,Password,salt);
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
                String _name = params[0];
                String _phone = params[1];


                String url_address = URL_FIND_PW + "?name=" + _name
                        + "&phone=" + _phone;

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
                return new String("FindModel" + e.getMessage());
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
                findPwApiListner.onSuccess("고객님의 임시 비밀번호는 ",newPassword);
            }else{
                findPwApiListner.onFail("임시 비밀번호 발급 실패");
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
