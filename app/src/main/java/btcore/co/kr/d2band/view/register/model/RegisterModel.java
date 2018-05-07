package btcore.co.kr.d2band.view.register.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import btcore.co.kr.d2band.util.SHA256Util;

import static btcore.co.kr.d2band.database.mySql.URL_REGISTER;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class RegisterModel {

    String id;
    String pw;
    String pwConfirm;
    String salt;
    String newPw;
    String name;
    String birthday;
    String gender;
    String height;
    String weight;
    String phone;
    String address;
    ApiListener apiListener;
    /**
     * 생성자와 같은 역활
     * @param id
     * @param pw
     * @param pwConfirm
     * @param name
     * @param birthday
     * @param height
     * @param weight
     * @param phone
     */
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
        if(pw.length() > 0){
            return true;
        }else
            return false;
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
        RegisterUser task = new RegisterUser();
        task.execute(id, newPw, salt, name, birthday, gender, height, weight, phone, address);
    }

    public interface ApiListener {
        void onSuccess(String message);
        void onFail (String message);

    }

    class RegisterUser extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        URL register_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                loading = ProgressDialog.show(Register.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.contains("success")){
                apiListener.onSuccess("회원가입 성공");
            }else{
                apiListener.onFail("회원가입 실패");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _pw = params[1];
                String _salt = params[2];
                String _name = params[3];
                String _birthday = params[4];
                String _gender = params[5];
                String _height = params[6];
                String _weight = params[7];
                String _phone = params[8];
                String _address = params[9];

                String url_address = URL_REGISTER + "?id=" + _id
                        + "&pw=" + _pw
                        + "&salt=" + _salt
                        + "&name=" + _name
                        + "&birthday=" + _birthday
                        + "&gender=" + _gender
                        + "&height=" + _height
                        + "&weight=" + _weight
                        + "&phone=" + _phone
                        + "&address=" + _address;

                register_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(register_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                return new String("RegisterExcepTion: " + e.getMessage());
            }
        }

    }





}
