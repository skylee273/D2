package btcore.co.kr.d2band.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by leehaneul on 2018-01-25.
 */

public class SmsReceiver extends BroadcastReceiver {
    private  final String TAG = getClass().getSimpleName();

    protected Context mSavedContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSavedContext = context;

        Log.d(TAG, "BroadcastReceiver Received");

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            Object[] messages = (Object[])bundle.get("pdus");
            SmsMessage[] smsMessage = new SmsMessage[messages.length];

            for(int i = 0; i < messages.length; i++) {
                smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
            }

            String message = smsMessage[0].getMessageBody().toString();
            String numberOrName = getDisplayName(mSavedContext, smsMessage[0].getOriginatingAddress());

        }
    }


    private int missMessage() {
        Uri sms_content = Uri.parse("content://sms/inbox");
        Cursor c = mSavedContext.getContentResolver().query(sms_content, null, "read = 0", null,null);
        c.moveToFirst();

        return c.getCount();
    }


    private String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
    private String getDisplayName(Context context, String number) {
        String displayName = number;
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
        return displayName;
    }






}
