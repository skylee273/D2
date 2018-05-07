package btcore.co.kr.d2band.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class KakaoNotificationService extends NotificationListenerService {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private String mTitle = null, mText = null, mSubText = null, mTime;
    private Bundle extras;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.d(TAG, "Notification Listener created!");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {

        if (statusBarNotification == null) {
            return;
        }

        String Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis());
        Notification mNotification = statusBarNotification.getNotification();

        extras = mNotification.extras;
        String packName = statusBarNotification.getPackageName();

        if (packName.equalsIgnoreCase("com.kakao.talk")) {

            this.mSubText = extras.getString(Notification.EXTRA_SUB_TEXT);

            this.mTitle = extras.getString(Notification.EXTRA_TITLE);

            this.mText = extras.getString(Notification.EXTRA_TEXT);

            this.mTime = Date;

            if (mSubText != null && mTitle != null && mText != null) {
                sendKakaoData();
            }
        }

    }

    private void sendKakaoData() {
        Intent msgrcv = new Intent("LocalMsg");
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        String data0 = mSubText.replaceAll("[^0-9]", "");
        String data1 = mTitle.replaceAll(match, "");
        String data2 = mText.replaceAll(match, "");

        Log.i(TAG, "부재중 메시지 : " + data0 + " 제목 : " + data1 + " 본문 : " + data2 + " 시간 : " + mTime);
        msgrcv.putExtra("kakaoInfo", data0 + "," + data1 + "," + data2);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(msgrcv);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Log.d(TAG, "Notification Removed:\n");

    }


}