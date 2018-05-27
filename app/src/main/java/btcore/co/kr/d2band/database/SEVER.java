package btcore.co.kr.d2band.database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import btcore.co.kr.d2band.user.User;

/**
 *
 */
import static btcore.co.kr.d2band.database.mySql.URL_INSERT_HEART;
import static btcore.co.kr.d2band.database.mySql.URL_INSERT_STEP;

public class SEVER {

    private final String TAG = getClass().getSimpleName();
    private String DATE;
    private String STEP;
    private String HEART;
    User user;

    public SEVER() {
        user = new User();
    }

    public void INSERT_STEP(String date, String step) {
        this.DATE = date;
        this.STEP = step;
        InsertStep TaskStep = new InsertStep();
        TaskStep.execute(user.getId(), DATE, STEP);
    }

    public void INSERT_HEART(String date, String heart) {
        this.DATE = date;
        this.HEART = heart;
        InsertHeart TaskHeart = new InsertHeart();
        TaskHeart.execute(user.getId(), DATE, HEART);
    }

    class InsertStep extends AsyncTask<String, Void, String> {
        URL step_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("success")) {
                Log.d(TAG, "STEP DATA INSERT SUCCESS");
            } else {
                Log.d(TAG, "STEP DATA INSERT FAILE");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _date = params[1];
                String _step = params[2];

                String url_address = URL_INSERT_STEP + "?id=" + _id
                        + "&date=" + _date
                        + "&step=" + _step;

                step_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(step_url.openStream()));
                String result = "";
                String temp = "";

                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();
                return result;
            } catch (Exception e) {
                return new String(TAG + e.getMessage());
            }
        }
    }

    class InsertHeart extends AsyncTask<String, Void, String> {
        URL heart_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.contains("success")) {
                Log.d(TAG, "HEART DATA INSERT SUCCESS");
            } else {
                Log.d(TAG, "HEART DATA INSERT FAILE");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];
                String _date = params[1];
                String _heart = params[2];

                String url_address = URL_INSERT_HEART + "?id=" + _id
                        + "&date=" + _date
                        + "&heart=" + _heart;

                heart_url = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(heart_url.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();
                return result;
            } catch (Exception e) {
                return new String(TAG + e.getMessage());
            }
        }

    }

}
