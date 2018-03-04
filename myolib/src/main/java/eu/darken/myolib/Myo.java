/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;

import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.msgs.ReadMsg;
import eu.darken.myolib.msgs.WriteMsg;
import eu.darken.myolib.services.Battery;
import eu.darken.myolib.services.Control;
import eu.darken.myolib.services.Device;
import eu.darken.myolib.services.Generic;
import eu.darken.myolib.tools.ByteHelper;
import eu.darken.myolib.tools.Logy;

/**
 * Extension of {@link BaseMyo} that provides direct methods to change Myo settings or read them.
 * It constructs the appropriate {@link MyoMsg} message objects and converts the byte[] result into meaningful values.
 */
public class Myo extends BaseMyo {

    public Myo(Context context, BluetoothDevice device) {
        super(context, device);
    }

    public interface ReadMyoInfoCallback {
        void onReadMyoInfo(Myo myo, MyoMsg msg, MyoInfo myoInfo);
    }

    private MyoInfo mMyoInfo;

    public MyoInfo getMyoInfo() {
        return mMyoInfo;
    }

    public void readInfo(final ReadMyoInfoCallback callback) {
        MyoMsg msg = new ReadMsg(Control.MYOINFO, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    mMyoInfo = new MyoInfo((ReadMsg) msg);
                if (callback != null)
                    callback.onReadMyoInfo(Myo.this, msg, mMyoInfo);
            }
        });
        submit(msg);
    }

    /**
     * Sets a new device name, if successful {@link #getDeviceName()} will also be updated.
     *
     * @param newName  make it confirms to bluetooth device name specs
     * @param callback optional
     */
    public void writeDeviceName(final String newName, @Nullable final MyoCommandCallback callback) {
        MyoMsg writeMsg = new WriteMsg(Generic.DEVICE_NAME, newName.getBytes(), new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS) {
                    Logy.d(TAG, "Name set to:" + newName);
                    mDeviceName = newName;
                }
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);
            }
        });
        submit(writeMsg);
    }

    public interface ReadDeviceNameCallback {
        void onDeviceNameRead(Myo myo, MyoMsg msg, String deviceName);
    }

    private String mDeviceName;

    /**
     * Returns a cached devicename, call {@link #readDeviceName(ReadDeviceNameCallback)} to update it.<br>
     * Is also updated on successfull {@link #writeDeviceName(String, MyoCommandCallback)} calls.
     *
     */
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * Reads the current device name through from the device.<br>
     * While there also is {@link BluetoothDevice#getName()} it often only returns a short version of the name.
     *
     * @param callback optional
     */
    public void readDeviceName(@Nullable final ReadDeviceNameCallback callback) {
        MyoMsg msg = new ReadMsg(Generic.DEVICE_NAME, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    mDeviceName = new String(((ReadMsg) msg).getValue());
                if (callback != null)
                    callback.onDeviceNameRead(Myo.this, msg, mDeviceName);
            }
        });
        submit(msg);
    }

    public interface ManufacturerNameCallback {
        void onManufacturerNameRead(Myo myo, MyoMsg msg, String manufacturer);
    }

    private String mManufacturerName;

    public String getManufacturerName() {
        return mManufacturerName;
    }

    /**
     * @param callback optional
     */
    public void readManufacturerName(@Nullable final ManufacturerNameCallback callback) {
        MyoMsg msg = new ReadMsg(Device.MANUFACTURER_NAME, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    mManufacturerName = new String(((ReadMsg) msg).getValue());
                if (callback != null)
                    callback.onManufacturerNameRead(Myo.this, msg, mManufacturerName);
            }
        });
        submit(msg);
    }

    public interface FirmwareCallback {
        void onFirmwareRead(Myo myo, MyoMsg msg, String version);
    }

    private String mFirmware;

    public String getFirmware() {
        return mFirmware;
    }

    /**
     * @param callback optional
     */
    public void readFirmware(@Nullable final FirmwareCallback callback) {
        MyoMsg msg = new ReadMsg(Control.FIRMWARE_VERSION, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS) {
                    ByteHelper byteHelper = new ByteHelper(((ReadMsg) msg).getValue());
                    mFirmware = String.format("v%d.%d.%d - %d", byteHelper.getUInt16(), byteHelper.getUInt16(), byteHelper.getUInt16(), byteHelper.getUInt16());
                }
                if (callback != null)
                    callback.onFirmwareRead(Myo.this, msg, mFirmware);
            }
        });
        submit(msg);
    }

    public interface BatteryCallback {
        void onBatteryLevelRead(Myo myo, MyoMsg msg, int batteryLevel);
    }

    private int mBatteryLevel = -1;

    public int getBatteryLevel() {
        return mBatteryLevel;
    }

    /**
     * @param callback optional
     */
    public void readBatteryLevel(@Nullable final BatteryCallback callback) {
        MyoMsg msg = new ReadMsg(Battery.BATTERYLEVEL, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    mBatteryLevel = ((ReadMsg) msg).getValue()[0];
                if (callback != null)
                    callback.onBatteryLevelRead(Myo.this, msg, mBatteryLevel);
            }
        });
        submit(msg);
    }

    private MyoCmds.EmgMode mEmgMode = MyoCmds.EmgMode.NONE;
    private MyoCmds.ImuMode mImuMode = MyoCmds.ImuMode.ALL;
    private MyoCmds.ClassifierMode mClassifierMode = MyoCmds.ClassifierMode.DISABLED;

    public MyoCmds.EmgMode getEmgMode() {
        return mEmgMode;
    }

    public MyoCmds.ImuMode getImuMode() {
        return mImuMode;
    }

    public MyoCmds.ClassifierMode getClassifierMode() {
        return mClassifierMode;
    }

    /**
     * Sets the Myo's sensor modes. It's not possible to set these individually.
     *
     * @param emgMode emgMode
     * @param imuMode imuMode
     * @param classifierMode classifier on/off
     * @param callback       optional
     */
    public void writeMode(final MyoCmds.EmgMode emgMode, final MyoCmds.ImuMode imuMode, final MyoCmds.ClassifierMode classifierMode, @Nullable final MyoCommandCallback callback) {
        byte[] cmd = MyoCmds.buildSensorModeCmd(emgMode, imuMode, classifierMode);
        MyoMsg writeMsg = new WriteMsg(Control.COMMAND, cmd, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS) {
                    Logy.d(TAG, "Mode set. (EMG:" + emgMode.name() + ", INU:" + imuMode.name() + ", CLASSIFIER:" + classifierMode.name() + ")");
                    mEmgMode = emgMode;
                    mImuMode = imuMode;
                    mClassifierMode = classifierMode;
                }
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);

            }
        });
        submit(writeMsg);
    }

    private MyoCmds.SleepMode mSleepMode = MyoCmds.SleepMode.NORMAL;

    public MyoCmds.SleepMode getSleepMode() {
        return mSleepMode;
    }

    /**
     * Sets the Myo's sleep mode. If the Myo disconnects, it's sleep mode will return to the default state.
     *
     * @param sleepMode default {@link eu.darken.myolib.MyoCmds.SleepMode}
     * @param callback  optional
     */
    public void writeSleepMode(final MyoCmds.SleepMode sleepMode, @Nullable final MyoCommandCallback callback) {
        byte[] cmd = MyoCmds.buildSleepModeCmd(sleepMode);
        MyoMsg writeMsg = new WriteMsg(Control.COMMAND, cmd, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    mSleepMode = sleepMode;
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);
            }
        });
        submit(writeMsg);
    }

    /**
     * Puts the Myo into a deep-sleep state.
     * Moving the Myo will no longer wake it up.
     * Waking it up requires plugging the usb cable into it.
     *
     * @param callback optional
     */
    public void writeDeepSleep(@Nullable final MyoCommandCallback callback) {
        byte[] cmd = MyoCmds.buildDeepSleepCmd();
        MyoMsg writeMsg = new WriteMsg(Control.COMMAND, cmd, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    Logy.d(TAG, "DeepSleep!");
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);
            }
        });
        submit(writeMsg);
    }

    public void writeVibrate(MyoCmds.VibrateType vibrateType, @Nullable final MyoCommandCallback callback) {
        byte[] cmd = MyoCmds.buildVibrateCmd(vibrateType);
        MyoMsg writeMsg = new WriteMsg(Control.COMMAND, cmd, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    Logy.d(TAG, "Vibrated!");
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);
            }
        });
        submit(writeMsg);
    }

    /**
     * @param unlockType LOCK, TIMED or HOLD
     * @param callback   optional
     */
    public void writeUnlock(final MyoCmds.UnlockType unlockType, @Nullable final MyoCommandCallback callback) {
        byte[] cmd = MyoCmds.buildSetUnlockModeCmd(unlockType);
        final MyoMsg writeMsg = new WriteMsg(Control.COMMAND, cmd, new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if (msg.getState() == MyoMsg.State.SUCCESS)
                    Logy.d(TAG, "SleepMode set: " + unlockType.name());
                if (callback != null)
                    callback.onCommandDone(Myo.this, msg);
            }
        });
        submit(writeMsg);
    }

    /**
     * Call back for submitted {@link MyoMsg} objects
     */
    public interface MyoCommandCallback {
        /**
         * @param myo the myo this was run on
         * @param msg the same object that was submitted, it now contains information from the transmission
         */
        void onCommandDone(Myo myo, MyoMsg msg);
    }
}
