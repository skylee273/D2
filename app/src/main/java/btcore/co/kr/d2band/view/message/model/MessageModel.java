package btcore.co.kr.d2band.view.message.model;

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

import btcore.co.kr.d2band.database.ServerCommand;
import btcore.co.kr.d2band.user.User;

import static android.support.constraint.Constraints.TAG;
import static btcore.co.kr.d2band.database.mySql.URL_DELETE_RECEIVER;
import static btcore.co.kr.d2band.database.mySql.URL_INSERT_RECV;

public class MessageModel {
    private String name, phone;
    private ApiListener apiListener;
    private DeleteApiListener deleteApiListener;
    private User user;


    public boolean checkMessage(String msg) {
        try {
            return msg.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkReceiver(String _name, String _phone) {
        this.name = _name;
        this.phone = _phone;

        try {
            return name.length() > 0 && phone.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void deleteReceiver(DeleteApiListener listener){
        this.deleteApiListener = listener;
        user = new User();
        DeleteReceiver deleteReceiver = new DeleteReceiver();
        deleteReceiver.execute(URL_DELETE_RECEIVER, user.getId(), name, phone);
    }


    public void getReceiver() {
        user = new User();
        ServerCommand serverCommand = new ServerCommand();
        serverCommand.SELECT_MSG();
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

    @SuppressLint("StaticFieldLeak")
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

                StringBuilder result = new StringBuilder();
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result.append(temp);
                }
                in.close();

                return result.toString();
            } catch (Exception e) {
                return "Message: " + e.getMessage();
            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class DeleteReceiver extends AsyncTask<String, Void, String> {

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
                    deleteApiListener.onFail();
                    Log.d(TAG, "Error - " + errorString);
                } else if (result.equals("SUCCESS")) {
                    deleteApiListener.onSuccess();
                } else {
                    deleteApiListener.onFail();
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


            String serverURL = params[0];
            String postParameters = "id=" + id + "&name=" + name
                    + "&phone=" + phone;

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
