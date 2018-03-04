/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.msgs.ReadMsg;
import eu.darken.myolib.msgs.WriteMsg;
import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.processor.BaseProcessor;
import eu.darken.myolib.processor.Processor;
import eu.darken.myolib.services.Battery;
import eu.darken.myolib.services.Classifier;
import eu.darken.myolib.services.Control;
import eu.darken.myolib.services.Emg;
import eu.darken.myolib.services.Imu;
import eu.darken.myolib.services.MyoDescriptor;
import eu.darken.myolib.tools.ApiHelper;
import eu.darken.myolib.tools.Logy;

/**
 * This is the base class for all Myo communication.
 * It wraps a {@link BluetoothGatt} object and supplies methods to easy communication.
 * Communication is encapsulated via {@link MyoMsg} and {@link #submit(MyoMsg)}.
 */
public class BaseMyo extends BluetoothGattCallback {
    protected static String TAG;

    private final BlockingQueue<MyoMsg> mDispatchQueue = new LinkedBlockingQueue<>();
    private final Object mThreadControl = new Object();
    private volatile boolean mRunning = false;
    private final Context mContext;
    private final BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private volatile ConnectionState mConnectionState = ConnectionState.DISCONNECTED;
    private final Semaphore mWaitToken = new Semaphore(0);
    private final Map<String, MyoMsg> mMsgCallbackMap = new HashMap<>();
    private final Map<UUID, List<Processor>> mSubscriptionMap = new HashMap<>();
    private final List<ConnectionListener> mConnectionListeners = new ArrayList<>();
    private ConnectionSpeed mConnectionSpeed = ConnectionSpeed.BALANCED;
    private volatile long mTimeoutSendQueue = 250;

    /**
     * The state of this device, relates to {@link BluetoothProfile#STATE_CONNECTED} etc.
     */
    public enum ConnectionState {
        CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public enum ConnectionSpeed {
        /**
         * Saves battery power but reducs the data rate.<br>
         * About ~50 packets/s.
         */
        BATTERY_CONSERVING(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER),
        /**
         * Balance between battery saving and data rate.<br>
         * About 84 packets/s.
         */
        BALANCED(BluetoothGatt.CONNECTION_PRIORITY_BALANCED),
        /**
         * Maximum performance, causes high battery drain.<br>
         * Data rates of 450+ packets/s
         */
        HIGH(BluetoothGatt.CONNECTION_PRIORITY_HIGH);

        private final int mPriority;

        ConnectionSpeed(int priority) {
            mPriority = priority;
        }

        public int getPriority() {
            return mPriority;
        }
    }

    public BaseMyo(Context context, BluetoothDevice device) {
        mContext = context;
        mDevice = device;
        TAG = "MyoLib:BaseMyo:" + device.getAddress();
    }

    /**
     * Time until a packet without confirmation is treated as failure.
     *
     * @return time in miliseconds, default of 250ms.
     */
    public long getTimeoutSendQueue() {
        return mTimeoutSendQueue;
    }

    /**
     * Sets the time interval for how long the dispatcher waits until it sends the next packet,
     * if there was still no confirmation for the current one.
     *
     * @param timeoutSendQueue time in milliseconds, default 250ms, -1 for infinite time, 0 for no waiting.
     */
    public void setTimeoutSendQueue(long timeoutSendQueue) {
        mTimeoutSendQueue = timeoutSendQueue;
    }

    public String getDeviceAddress() {
        return getBluetoothDevice().getAddress();
    }

    /**
     * Requires API21+ (Lollipop+)
     * Calling this on &lt; API21 will have no effect.<br>
     * Changes the connection speed of this Myo.
     * This can be done on the fly.
     *
     * @param speed a value from{@link eu.darken.myolib.BaseMyo.ConnectionSpeed}
     */
    public void setConnectionSpeed(@NonNull ConnectionSpeed speed) {
        if (ApiHelper.hasLolliPop())
            mConnectionSpeed = speed;
    }

    public ConnectionSpeed getConnectionSpeed() {
        return mConnectionSpeed;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mDevice;
    }

    public ConnectionState getConnectionState() {
        return mConnectionState;
    }


    public interface ConnectionListener {
        void onConnectionStateChanged(BaseMyo myo, ConnectionState state);
    }

    public void addConnectionListener(ConnectionListener listener) {
        mConnectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        mConnectionListeners.remove(listener);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTING) {
            mConnectionState = ConnectionState.CONNECTING;
        } else if (newState == BluetoothProfile.STATE_CONNECTED) {
            mConnectionState = ConnectionState.CONNECTED;
            Logy.d(TAG, "Device connected, discovering services...");
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
            mConnectionState = ConnectionState.DISCONNECTING;
            mWaitToken.drainPermits();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            mConnectionState = ConnectionState.DISCONNECTED;
        } else {
            throw new RuntimeException("Unknown connection state");
        }
        Logy.d(TAG, "status:" + status + ", newState:" + mConnectionState.name());
        for (ConnectionListener listener : mConnectionListeners)
            listener.onConnectionStateChanged(this, mConnectionState);
        super.onConnectionStateChange(gatt, status, newState);
    }

    /**
     * Checks available Myo services and enables EMG and IMU characteristic notifications.
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Logy.w(TAG, "Service discovered failed!");
            return;
        }

        BluetoothGattService controlService = mBluetoothGatt.getService(Control.getServiceUUID());
        if (controlService != null) {
            Logy.d(TAG, "Service Control: available");
            BluetoothGattCharacteristic myoInfo = controlService.getCharacteristic(Control.MYOINFO.getCharacteristicUUID());
            Logy.d(TAG, "Characteristic MyoInfo: " + (myoInfo != null ? "available" : "unavailable"));
            BluetoothGattCharacteristic fimwareInfo = controlService.getCharacteristic(Control.FIRMWARE_VERSION.getCharacteristicUUID());
            Logy.d(TAG, "Characteristic FirmwareInfo: " + (fimwareInfo != null ? "available" : "unavailable"));
            BluetoothGattCharacteristic commandCharacteristic = controlService.getCharacteristic(Control.COMMAND.getCharacteristicUUID());
            Logy.d(TAG, "Characteristic Command: " + (commandCharacteristic != null ? "available" : "unavailable"));
        } else {
            Logy.w(TAG, "Service Control: unavailable");
        }

        BluetoothGattService emgService = mBluetoothGatt.getService(Emg.SERVICE.getServiceUUID());
        if (emgService != null) {
            Logy.d(TAG, "Service EMG: available");
            enableNotifications(emgService, Emg.EMGDATA0_DESCRIPTOR);
            enableNotifications(emgService, Emg.EMGDATA1_DESCRIPTOR);
            enableNotifications(emgService, Emg.EMGDATA2_DESCRIPTOR);
            enableNotifications(emgService, Emg.EMGDATA3_DESCRIPTOR);
        } else {
            Logy.w(TAG, "Service EMG: unavailable");
        }

        BluetoothGattService imuService = mBluetoothGatt.getService(Imu.getServiceUUID());
        if (imuService != null) {
            Logy.d(TAG, "Service IMU: available");
            enableNotifications(imuService, Imu.IMUDATA_DESCRIPTOR);
            enableIndication(imuService, Imu.MOTIONEVENT_DESCRIPTOR);
        } else {
            Logy.w(TAG, "Service IMU: unavailable");
        }

        BluetoothGattService classifierService = mBluetoothGatt.getService(Classifier.getServiceUUID());
        if (classifierService != null) {
            Logy.d(TAG, "Service Classifier: available");
            enableIndication(classifierService, Classifier.CLASSIFIEREVENT_DESCRIPTOR);
        } else {
            Logy.w(TAG, "Service Classifier: unavailable");
        }

        BluetoothGattService batteryService = mBluetoothGatt.getService(Battery.getServiceUUID());
        if (batteryService != null) {
            Logy.d(TAG, "Service Battery: available");
        } else {
            Logy.w(TAG, "Service Battery: unavailable");
        }
        super.onServicesDiscovered(gatt, status);

        Logy.d(TAG, "Services discovered.");
        mWaitToken.release();
    }

    private void enableNotifications(BluetoothGattService service, final MyoDescriptor descriptor) {
        BluetoothGattCharacteristic classifier = service.getCharacteristic(descriptor.getCharacteristicUUID());
        if (classifier != null && mBluetoothGatt.setCharacteristicNotification(classifier, true)) {
            WriteMsg msg = new WriteMsg(descriptor,
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE,
                    new MyoMsg.Callback() {
                        @Override
                        public void onResult(MyoMsg msg) {
                            Logy.d(TAG, "Notification '" + descriptor.getName() + "' enabled");
                        }
                    });
            submit(msg);
        }
    }

    private void enableIndication(BluetoothGattService service, final MyoDescriptor descriptor) {
        BluetoothGattCharacteristic classifier = service.getCharacteristic(descriptor.getCharacteristicUUID());
        if (classifier != null && mBluetoothGatt.setCharacteristicNotification(classifier, true)) {
            WriteMsg msg = new WriteMsg(descriptor,
                    BluetoothGattDescriptor.ENABLE_INDICATION_VALUE,
                    new MyoMsg.Callback() {
                        @Override
                        public void onResult(MyoMsg msg) {
                            Logy.d(TAG, "Indication '" + descriptor.getName() + "' enabled");
                        }
                    });
            submit(msg);
        }
    }

    /**
     * Submits a new message to the dispatcher of this device.
     * It will be put at the end of the queue and once it reaches the front.
     * Messages are sequentially as otherwise instruction can be lost.<br>
     * If dispatcher of this Myo is not yet running, {@link #connect()} will be called.
     * It will be taken care of that the Myo is ready before any transmission attempt will be made.
     * Don't alter the message object after submitting it
     *
     * @param msg A {@link WriteMsg} or {@link ReadMsg}
     */
    public void submit(@NonNull MyoMsg msg) {
        mDispatchQueue.add(msg);
        synchronized (mThreadControl) {
            if (!mRunning)
                connect();
        }
    }

    /**
     * "Starts this Myo"<br>
     * Launches the innerloop that dispatches {@link MyoMsg}.
     * This loop will wait until {@link #getConnectionState()} changes to {@link eu.darken.myolib.BaseMyo.ConnectionState#CONNECTED}
     * <p>
     * Calling this multiple times has no effect.
     */
    public void connect() {
        synchronized (mThreadControl) {
            if (mRunning) {
                return;
            } else {
                Logy.d(TAG, "Connecting to " + mDevice.getName());
                mWaitToken.drainPermits();
                mRunning = true;
                new Thread(mLoop).start();
            }
        }
    }

    /**
     * Disconnects the bluetooth connection and stops the dispatcher loop.
     */
    public void disconnect() {
        synchronized (mThreadControl) {
            if (!mRunning) {
                return;
            } else {
                mRunning = false;
                mWaitToken.release();

                Logy.d(TAG, "Disconnecting from " + mDevice.getName());
            }
        }
    }

    /**
     * Whether the Dispatcher is running.<br>
     * NOT if the Myo device is connected.
     * Use {@link #getConnectionState()} for that.<br>
     * The dispatcher can be running, but the Myo device temporarily disconnected.
     *
     * @return true if the dispatcher is running
     */
    public boolean isRunning() {
        return mRunning;
    }

    private Runnable mLoop = new Runnable() {
        private int mPriority = ConnectionSpeed.BALANCED.getPriority();

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
            mBluetoothGatt = mDevice.connectGatt(mContext, true, BaseMyo.this);
            while (mRunning) {
                if (mConnectionState != ConnectionState.CONNECTED)
                    continue;
                if (ApiHelper.hasLolliPop()) {
                    if (getConnectionSpeed().getPriority() != mPriority) {
                        mPriority = getConnectionSpeed().getPriority();
                        mBluetoothGatt.requestConnectionPriority(mPriority);
                    }
                }

                try {
                    if (mTimeoutSendQueue == -1) {
                        mWaitToken.acquire();
                    } else {
                        if (!mWaitToken.tryAcquire(mTimeoutSendQueue, TimeUnit.MILLISECONDS))
                            Logy.w(TAG, "Lost packet!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!mRunning)
                    break;

                MyoMsg msg = mDispatchQueue.poll();
                if (msg != null) {
                    internalSend(msg);
                } else {
                    mWaitToken.release();
                }
            }

            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    };
    private long mDispatchTime = 0;

    private void internalSend(MyoMsg msg) {
        BluetoothGattService gattService = mBluetoothGatt.getService(msg.getServiceUUID());
        if (gattService == null) {
            Logy.w(TAG, "BluetoothGattService unavailable!: " + msg.toString());
            return;
        }
        BluetoothGattCharacteristic gattChar = gattService.getCharacteristic(msg.getCharacteristicUUID());
        if (gattChar == null) {
            Logy.w(TAG, "BluetoothGattCharacteristic unavailable!: " + msg.toString());
            return;
        }

        mDispatchTime = System.currentTimeMillis();
        if (msg.getDescriptorUUID() != null) {
            BluetoothGattDescriptor gattDesc = gattChar.getDescriptor(msg.getDescriptorUUID());
            if (gattDesc == null) {
                Logy.w(TAG, "BluetoothGattDescriptor unavailable!: " + msg.toString());
                return;
            }
            mMsgCallbackMap.put(msg.getIdentifier(), msg);
            if (msg instanceof WriteMsg) {
                gattDesc.setValue(((WriteMsg) msg).getData());
                mBluetoothGatt.writeDescriptor(gattDesc);
            } else {
                mBluetoothGatt.readDescriptor(gattDesc);
            }
        } else {
            mMsgCallbackMap.put(msg.getIdentifier(), msg);
            if (msg instanceof WriteMsg) {
                gattChar.setValue(((WriteMsg) msg).getData());
                mBluetoothGatt.writeCharacteristic(gattChar);
            } else {
                mBluetoothGatt.readCharacteristic(gattChar);
            }
        }
        Logy.v(TAG, "Processed: " + msg.getIdentifier());
    }


    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int gattStatus) {
        ReadMsg msg = (ReadMsg) mMsgCallbackMap.remove(MyoMsg.toIdentifier(characteristic));
        mWaitToken.release();

        msg.setGattStatus(gattStatus);
        if (gattStatus == BluetoothGatt.GATT_SUCCESS) {
            Logy.v(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | SUCCESS | " + msg.toString());
            msg.setState(MyoMsg.State.SUCCESS);
            msg.setValue(characteristic.getValue());
            if (msg.getCallback() != null)
                msg.getCallback().onResult(msg);
        } else {
            Logy.w(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | ERROR(" + gattStatus + ") | " + msg.toString());
            msg.setState(MyoMsg.State.ERROR);
            if (msg.getRetryCounter() == 0) {
                if (msg.getCallback() != null)
                    msg.getCallback().onResult(msg);
            } else {
                msg.decreaseRetryCounter();
                submit(msg);
            }
        }
        super.onCharacteristicRead(gatt, characteristic, gattStatus);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int gattStatus) {
        WriteMsg msg = (WriteMsg) mMsgCallbackMap.remove(MyoMsg.toIdentifier(characteristic));
        mWaitToken.release();

        msg.setGattStatus(gattStatus);
        if (gattStatus == BluetoothGatt.GATT_SUCCESS) {
            Logy.v(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | SUCCESS | " + msg.toString());
            msg.setState(MyoMsg.State.SUCCESS);
            if (msg.getCallback() != null)
                msg.getCallback().onResult(msg);
        } else {
            Logy.w(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | ERROR(" + gattStatus + ") | " + msg.toString());
            msg.setState(MyoMsg.State.ERROR);
            if (msg.getRetryCounter() == 0) {
                if (msg.getCallback() != null)
                    msg.getCallback().onResult(msg);
            } else {
                msg.decreaseRetryCounter();
                submit(msg);
            }
        }
        super.onCharacteristicWrite(gatt, characteristic, gattStatus);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int gattStatus) {
        ReadMsg msg = (ReadMsg) mMsgCallbackMap.remove(MyoMsg.toIdentifier(descriptor));
        mWaitToken.release();

        msg.setGattStatus(gattStatus);
        if (gattStatus == BluetoothGatt.GATT_SUCCESS) {
            Logy.v(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | SUCCESS | " + msg.toString());
            msg.setState(MyoMsg.State.SUCCESS);
            msg.setValue(descriptor.getValue());
            if (msg.getCallback() != null)
                msg.getCallback().onResult(msg);
        } else {
            Logy.w(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | ERROR(" + gattStatus + ") | " + msg.toString());
            msg.setState(MyoMsg.State.ERROR);
            if (msg.getRetryCounter() == 0) {
                if (msg.getCallback() != null)
                    msg.getCallback().onResult(msg);
            } else {
                msg.decreaseRetryCounter();
                submit(msg);
            }
        }
        super.onDescriptorRead(gatt, descriptor, gattStatus);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int gattStatus) {
        WriteMsg msg = (WriteMsg) mMsgCallbackMap.remove(MyoMsg.toIdentifier(descriptor));
        mWaitToken.release();

        msg.setGattStatus(gattStatus);
        if (gattStatus == BluetoothGatt.GATT_SUCCESS) {
            Logy.v(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | SUCCESS | " + msg.toString());
            msg.setState(MyoMsg.State.SUCCESS);
            if (msg.getCallback() != null)
                msg.getCallback().onResult(msg);
        } else {
            Logy.w(TAG, "rtt: " + (System.currentTimeMillis() - mDispatchTime) + "ms | ERROR(" + gattStatus + ") | " + msg.toString());
            msg.setState(MyoMsg.State.ERROR);
            if (msg.getRetryCounter() == 0) {
                if (msg.getCallback() != null)
                    msg.getCallback().onResult(msg);
            } else {
                msg.decreaseRetryCounter();
                submit(msg);
            }
        }
        super.onDescriptorWrite(gatt, descriptor, gattStatus);
    }

    /**
     * Adds a Processor object to this Myo, make sure it is unique.
     */
    public void addProcessor(Processor processor) {
        for (UUID subscriptionTarget : processor.getSubscriptions()) {
            List<Processor> subscriberList = mSubscriptionMap.get(subscriptionTarget);
            if (subscriberList == null) {
                subscriberList = new ArrayList<>();
                mSubscriptionMap.put(subscriptionTarget, subscriberList);
            } else {
                if (subscriberList.contains(processor))
                    continue;
            }
            subscriberList.add(processor);
        }
        processor.onAdded();
    }

    public void removeProcessor(BaseProcessor processor) {
        processor.onRemoved();
        for (UUID subscriptionTarget : processor.getSubscriptions()) {
            List<Processor> subscriberList = mSubscriptionMap.get(subscriptionTarget);
            if (subscriberList != null)
                subscriberList.remove(processor);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        BaseDataPacket packet = new BaseDataPacket(gatt, characteristic);
        List<Processor> subscribers = mSubscriptionMap.get(characteristic.getUuid());
        if (subscribers != null) {
            for (Processor subscriber : subscribers)
                subscriber.submit(packet);
        }
        super.onCharacteristicChanged(gatt, characteristic);
    }

}
