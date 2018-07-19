package btcore.co.kr.d2band.database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import btcore.co.kr.d2band.item.StepItem;
import btcore.co.kr.d2band.user.Contact;
import btcore.co.kr.d2band.user.User;
import btcore.co.kr.d2band.view.login.model.LoginModel;

import static btcore.co.kr.d2band.database.mySql.URL_INSERT_HEART;
import static btcore.co.kr.d2band.database.mySql.URL_INSERT_STEP;
import static btcore.co.kr.d2band.database.mySql.URL_SELECT_STEP;
import static btcore.co.kr.d2band.database.mySql.URL_SET_RECEIVE;
import static btcore.co.kr.d2band.database.mySql.URL_SET_USER;

public class ServerCommand {

    private final String TAG = getClass().getSimpleName();
    private String DATE;
    private String STEP;
    private String HEART;
    User user;
    Contact contact;
    public static ArrayList<StepItem> StepList = new ArrayList<StepItem>();

    public void addStep(String date, String step){
        StepItem item = new StepItem();
        item.setDate(date);
        item.setStep(step);
        StepList.add(item);

    }

    public ArrayList getStep(){
        return StepList;
    }
    public ServerCommand() {
        user = new User();
    }

    public void SELECT_MSG(){
        setMSG task = new setMSG();
        task.execute(user.getId());
    }
    public void SELECT_STEP(){
        SelectStep TaskSelect = new SelectStep();
        TaskSelect.execute(user.getId());
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



    class SelectStep extends AsyncTask<String, Void, String> {
        URL stepUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null && !s.equals("")){
                StepList.clear();
                String [] Step  = s.split("&&&&&");
                for (String val : Step){
                    String [] addData = val.split("#####");
                    addStep(addData[0], addData[1]);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String _id = params[0];

                String url_address = URL_SELECT_STEP + "?id=" + _id;

                stepUrl = new URL(url_address);
                BufferedReader in = new BufferedReader(new InputStreamReader(stepUrl.openStream()));

                String result = "";
                String temp = "";
                while ((temp = in.readLine()) != null) {
                    result += temp;
                }
                in.close();

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return new String("Step Exception: " + e.getMessage());
            }
        }
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
    class setMSG extends AsyncTask<String, Void, String> {
        URL userUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String Recv[] = s.split("&&&&&");
                String name[] = new String[Recv.length];
                String phone[] = new String[Recv.length];
                int index = 0;
                contact = new Contact();
                contact.clear();
                for (String recv : Recv) {
                    String con[] = recv.split("#####");
                    name[index] = con[0];
                    phone[index] = con[1];
                    index++;
                }
                contact.setName(name);
                contact.setPhone(phone);
            } catch (ArrayIndexOutOfBoundsException e) {
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
}
