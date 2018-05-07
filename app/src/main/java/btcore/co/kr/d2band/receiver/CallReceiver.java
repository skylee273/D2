package btcore.co.kr.d2band.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;



import static android.content.Context.MODE_PRIVATE;

/**
 * Created by leehaneul on 2018-01-25.
 */

public class CallReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();
    protected Context mSavedContext;
    private  CallStartEndDetector mCallStartEndDetector = null;
    SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            pref = context.getSharedPreferences("pref", MODE_PRIVATE);
            Log.d(TAG, "onReceive");
            mSavedContext = context;
            if (mCallStartEndDetector == null) {
                mCallStartEndDetector = new CallStartEndDetector();
            }

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                mCallStartEndDetector.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
            } else {
                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephony.listen(mCallStartEndDetector, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }


    }

    class CallStartEndDetector extends PhoneStateListener {
        int mLastState = TelephonyManager.CALL_STATE_IDLE;
        boolean mIsIncoming;
        String mSavedNumber;

        public CallStartEndDetector() {
        }

        public void setOutgoingNumber(String number) {
            mSavedNumber = number;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);


                if (mLastState == state) {
                    return;
                }

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        mIsIncoming = true;
                        mSavedNumber = incomingNumber;
                        onIncomingCallStarted(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        onOutgoingCallStarted(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        onMissedCall(incomingNumber);
                        break;
                    default:
                        break;
                }
                mLastState = state;


        }
    }

    private void onIncomingCallStarted(String number) {
        //showToast("onIncomingCallStarted", number);

        try {
            String eventType = "IncomingCallEvent";
            String numberOrName = getDisplayName(mSavedContext, number);

        }catch (Exception e){
            Log.d(TAG,e.toString());

        }


    }

    private void onOutgoingCallStarted(String number) {
        String numberOrName = getDisplayName(mSavedContext, number);
    }

    private void onIncomingCallEnded(String number) {
        //showToast("onIncomingCallEnded", number);

        try {
            String eventType = "CallEndEvent";
            String numberOrName = getDisplayName(mSavedContext, number);
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }

    }

    private void onOutgoingCallEnded(String number) {

    }

    private void onMissedCall(String number) {
        //showToast("onMissedCall", number);
        int missedCount = 0;

        String[] projection = {CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE};
        String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + "=1";

        if (ActivityCompat.checkSelfPermission(mSavedContext, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor c = mSavedContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null,
                null);
            c.moveToFirst();
            missedCount = (c.getCount() + 1) & 0xFF;
            //showToast("onMissedCall", Integer.toString(missedCount));

        String eventType = "MissedCallEvent";

    }


    private void showToast(String eventType, String numberOrName) {
        Toast.makeText(mSavedContext, eventType + "," + numberOrName, Toast.LENGTH_LONG).show();
        Log.d(TAG, eventType + "," + numberOrName);
    }

    private String getDisplayName(Context context, String number) {
        String displayName = number;
        try{

            if (context == null) {
                return displayName;
            }
            // Retrieve the lookup URI to the contact in the database
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            if (contactUri == null) {
                return displayName;
            }
            // Get a cursor to the contact's entry
            String[] projection = { ContactsContract.Contacts.DISPLAY_NAME };
            Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                displayName = cursor.getString(nameIdx);
                cursor.close(); // Release resources
            }

        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return displayName;
    }

}
