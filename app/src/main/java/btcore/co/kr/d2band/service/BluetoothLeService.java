package btcore.co.kr.d2band.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import btcore.co.kr.d2band.database.SEVER;
import btcore.co.kr.d2band.util.ParserUtils;
import btcore.co.kr.d2band.view.sos.SosActivity;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothDevice.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress = null;
    private int mConnectionState = STATE_DISCONNECTED;

    private final static String HEART_RATE = "AF-04-01";
    private final static String STPES = "AF-04-02";
    private final static String BATTERY = "AF-04-03";
    private final static String CALORIE = "AF-04-04";
    private final static String EMERGENCY = "AF-04-05-99-99";
    private final static String CONNECTIONACK = "AF-04-07-99-99-FF";

    private String DATA[];
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "btcore.co.kr.d2band.service.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "btcore.co.kr.d2band.service.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "btcore.co.kr.d2band.service.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "btcore.co.kr.d2band.service.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "btcore.co.kr.d2band.service.EXTRA_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
            "btcore.co.kr.d2band.service.DEVICE_DOES_NOT_SUPPORT_UART";
    public final static String D2_BLUETOOTH_DATA =
            "btcore.co.kr.d2band.service.D2_BLUETOOTH_DATA";
    public final static String D2_CONNECTION_ACK =
            "btcore.co.kr.d2band.service.D2_CONNECTION_ACK";
    public final static String D2_STEP_DATA =
            "btcore.co.kr.d2band.service.D2_STEP_DATA";
    public final static String D2_HEART_DATA =
            "btcore.co.kr.d2band.service.D2_HEART_DATA";
    public final static String D2_CALORIE =
            "btcore.co.kr.d2band.service.D2_CALORIE";

    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public static boolean STATE = false;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    SEVER sever;

    // GATT 이벤트에 대한 콜벡 메소드를 구현합니다.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                STATE = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);
                STATE = false;
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, String.format("BluetoothGatt ReadRssi[%d]", rssi));
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            final String data = ParserUtils.parse(characteristic);

            final BluetoothGattDescriptor cccd = characteristic.getDescriptor(CCCD);
            final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;

            try {
                if (notifications) {
                    Log.d("Noti received from ", characteristic.getUuid() + ", value: " + data);
                    DATA = data.split("-");
                    if (data.contains(HEART_RATE)) {
                        String heart = DATA[4] + DATA[5];
                        heart = String.valueOf(Integer.parseInt(heart, 16));
                        sever.INSERT_HEART(getTime(), heart);
                        broadcastUpdate(D2_HEART_DATA, heart);
                    }
                    if (data.contains(STPES)) {
                        String steps = DATA[4] + DATA[5];
                        steps = String.valueOf(Integer.parseInt(steps, 16));
                        sever.INSERT_STEP(getTime(), steps);
                        broadcastUpdate(D2_STEP_DATA, steps);
                    }
                    if (data.contains(BATTERY)) {
                        String battery = DATA[4] + DATA[5];
                        broadcastUpdate(D2_BLUETOOTH_DATA, battery);
                    }
                    if (data.contains(CALORIE)) {
                        String calorie = DATA[4] + DATA[5];
                        broadcastUpdate(D2_CALORIE, calorie);
                    }
                    if (data.contains(EMERGENCY)) {
                       Intent intent = new Intent(getApplicationContext(), SosActivity.class);
                       startActivity(intent);
                    }
                    if(data.contains(CONNECTIONACK)){
                        broadcastUpdate(D2_CONNECTION_ACK, "ACK");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.d(TAG, e.toString());
            }
            onCharacteristicNotified(gatt, characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        protected void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (TX_CHAR_UUID.equals(characteristic.getUuid())) {
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        } else {
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                Log.d("connect", "STATE_CONNECTING");
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;

        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        // mBluetoothGatt.close();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAG, "mBluetoothGatt closed");
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
    }

    public void enableTXNotification() {
        if (mBluetoothGatt == null) {
            showMessage("mBluetoothGatt null" + mBluetoothGatt);
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
        if (RxService == null) {
            showMessage("enable TXNotification - Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
        if (TxChar == null) {
            showMessage("enable RXNotification - Tx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        } else {
            this.readCharacteristic(TxChar);
        }

        if (TxChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, true);
        mBluetoothGatt.readCharacteristic(TxChar);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

    }

    public void writeRXCharacteristic(byte[] value) {
        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);

        showMessage("mBluetoothGatt null" + mBluetoothGatt);
        if (RxService == null) {
            showMessage("write RX Characteristic - Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) {
            showMessage("Rx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);

        Log.d(TAG, "write TXchar - status=" + status);
    }

    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private String getTime() {
        long mNow;
        Date mDate;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

}
