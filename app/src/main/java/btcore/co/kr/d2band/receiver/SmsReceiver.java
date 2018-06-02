package btcore.co.kr.d2band.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


import btcore.co.kr.d2band.bus.SmsBusEvent;
import btcore.co.kr.d2band.bus.SmsProvider;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by leehaneul on 2018-01-25.
 */

public class SmsReceiver extends BroadcastReceiver {
    protected Context mSavedContext;
    private final String TAG = getClass().getSimpleName();

    public SmsReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mSavedContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                receiveMessage(message);
            }
        } else {
            try {
                final Bundle bundle = intent.getExtras();
                if (bundle == null || !bundle.containsKey("pdus")) {
                    return;
                }

                final Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    receiveMessage(SmsMessage.createFromPdu((byte[]) pdu));
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void receiveMessage(SmsMessage message) {

        Uri sms_content = Uri.parse("content://sms/inbox");
        Cursor c = mSavedContext.getContentResolver().query(sms_content, null, "read = 0", null, null);
        c.moveToFirst();
        int countStr = c.getCount();
        String numberOrName = getDisplayName(mSavedContext, message.getOriginatingAddress());
        if (numberOrName.length() > 0 ) {
            String sms = numberOrName + "&&&&&" + String.valueOf(countStr);
            SmsProvider.getInstance().post(new SmsBusEvent(sms));
        }
    }

    private String getDisplayName(Context context, String number) {
        String displayName = number;
        if (context == null) {
            return displayName;
        }

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        if (contactUri == null) {
            return displayName;
        }

        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            displayName = cursor.getString(nameIdx);
            cursor.close();
        }
        return displayName;
    }

}
