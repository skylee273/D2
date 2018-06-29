package btcore.co.kr.d2band.view.message.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;

import btcore.co.kr.d2band.user.User;

import static btcore.co.kr.d2band.database.mySql.URL_DELETE_RECV;
import static btcore.co.kr.d2band.database.mySql.URL_INSERT_RECV;
import static btcore.co.kr.d2band.database.mySql.URL_REGISTER;
import static btcore.co.kr.d2band.database.mySql.URL_SET_RECEIVE;

public class MessageModel {
    private String msg;
    private String name, phone;
    private String Receiver[];
    ApiListener apiListener;
    RecvApiListener recvApliListener;
    DeleteApiListener deleteApiListener;
    User user;


    public boolean checkMessage(String msg) {
        this.msg = msg;
        try {
            if (msg.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkReceiver(String _name, String _phone) {
        this.name = _name;
        this.phone = _phone;

        try {
            if (name.length() > 0 && phone.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void deleteReceiver(DeleteApiListener listener){
        this.deleteApiListener = listener;
        user = new User();
        deleteTask task = new deleteTask();
        task.execute(user.getId(), name, phone);
    }
    public void getReceiver(RecvApiListener listener) {
        this.recvApliListener = listener;
        user = new User();
        GetReceiver task = new GetReceiver();
        task.execute(user.getId());
    }

    public void InsertReceiver(ApiListener listener) {
        this.apiListener = listener;
        user = new User();
        InsertReceiver task = new InsertReceiver();
        task.execute(user.getId(), name, phone);
    }

    public interface RecvApiListener{
        void onSuccess(String [] Recv);
        void onFail();
    }
    public interface ApiListener {
        void onSuccess();
        void onFail();
    }
    public interface DeleteApiListener {
        void onSuccess();
        void onFail();
    }

    class InsertReceiver extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        URL receiver_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.contains("success")) {
                apiListener.onSuccess();
            } else {
                apiListener.onFail();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _name = params[1];
                String _phone = params[2];


                String url_address = URL_INSERT_RECV + "?id=" + _id
                        + "&name=" + _name
                        + "&phone=" + _phone;


                receiver_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(receiver_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                return new String("Message: " + e.getMessage());
            }
        }

    }

    class GetReceiver extends AsyncTask<String, Void, String> {
        URL userUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s != null) {
                    Receiver = s.split("&&&&&");
                    recvApliListener.onSuccess(Receiver);
                }else{
                    recvApliListener.onFail();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                recvApliListener.onFail();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];

                String url_address = URL_SET_RECEIVE + "?id=" + _id;

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

    class deleteTask extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        URL receiver_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.contains("success")) {
                deleteApiListener.onSuccess();
            } else {
                deleteApiListener.onFail();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _name = params[1];
                String _phone = params[2];


                String url_address = URL_DELETE_RECV + "?id=" + _id
                        + "&name=" + _name
                        + "&phone=" + _phone;


                receiver_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(receiver_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                return new String("Message: " + e.getMessage());
            }
        }

    }


}
