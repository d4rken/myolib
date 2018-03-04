/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.msgs;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import eu.darken.myolib.services.MyoCharacteristic;
import eu.darken.myolib.services.MyoDescriptor;
import eu.darken.myolib.services.MyoService;

/**
 * The base class for all communication done with a Myo.
 * Intended to be used with {@link eu.darken.myolib.BaseMyo#submit(MyoMsg)}.
 * <br>
 * Normally you would want to use one of its subclasses:
 * {@link WriteMsg},{@link ReadMsg}
 */
public class MyoMsg {
    private final UUID mServiceUUID;
    private final UUID mCharacteristicUUID;
    private final UUID mDescriptorUUID;
    private final Callback mCallback;
    private int mRetryCounter = -1;
    private Integer mGattStatus = null;
    private State mState = State.NEW;

    /**
     * Values reflecting state of this {@link MyoMsg}
     */
    public enum State {
        /**
         * Message has not been submitted yet via {@link eu.darken.myolib.BaseMyo#submit(MyoMsg)}.
         */
        NEW,
        /**
         * Message was send and possibly confirmed.
         */
        SUCCESS,
        /**
         * The message failed to be transmitted.
         * This includes failure to submit, timeouts, running out of retries.
         */
        ERROR
    }

    /**
     * See <br>
     * {@link eu.darken.myolib.services.Battery}<br>
     * {@link eu.darken.myolib.services.Classifier}<br>
     * {@link eu.darken.myolib.services.Control}<br>
     * {@link eu.darken.myolib.services.Device}<br>
     * {@link eu.darken.myolib.services.Emg}<br>
     * {@link eu.darken.myolib.services.Generic}<br>
     * {@link eu.darken.myolib.services.Imu}<br>
     * <p>
     *
     * @param serviceUUID        e.g. {@link MyoService#getServiceUUID()} or {@link MyoCharacteristic#getServiceUUID()}
     * @param characteristicUUID e.g. {@link MyoCharacteristic#getCharacteristicUUID()}
     * @param descriptorUUID     Optional, depending on whether you want to target a descriptor or not.
     *                           See {@link MyoDescriptor}
     * @param callback           A callback that will return this object after it has been processed
     */
    public MyoMsg(@NonNull UUID serviceUUID, @NonNull UUID characteristicUUID, @Nullable UUID descriptorUUID, @Nullable Callback callback) {
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
        mDescriptorUUID = descriptorUUID;
        mCallback = callback;
    }

    /**
     * Convenience constructor for creating a characteristic only message.<br>
     * See {@link #MyoMsg(UUID, UUID, UUID, Callback)}
     *
     */
    public MyoMsg(@NonNull UUID serviceUUID, @NonNull UUID characteristicUUID, @Nullable Callback callback) {
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
        mDescriptorUUID = null;
        mCallback = callback;
    }

    public interface Callback {
        void onResult(MyoMsg msg);
    }

    /**
     * The current state of this message.
     *
     */
    public State getState() {
        return mState;
    }

    /**
     * This will be used by {@link eu.darken.myolib.BaseMyo}. Don't call it yourself.
     *
     */
    public void setState(State state) {
        mState = state;
    }

    /**
     * The {@link android.bluetooth.BluetoothGatt} status for this transmission.
     * This will be NULL until the message has been send.
     * It will be set shortly before the callback is issued.
     *
     * @return e.g. {@link android.bluetooth.BluetoothGatt#GATT_SUCCESS}
     */
    public Integer getGattStatus() {
        return mGattStatus;
    }

    /**
     * Set by {@link eu.darken.myolib.BaseMyo} after a message is send, shortly before the callback is triggered.
     * Don't set this yourself.
     *
     */
    public void setGattStatus(Integer gattStatus) {
        mGattStatus = gattStatus;
    }

    public int decreaseRetryCounter() {
        if (mRetryCounter > 0)
            mRetryCounter--;
        return mRetryCounter;
    }

    public int getRetryCounter() {
        return mRetryCounter;
    }

    /**
     * How often this message should be retried when:<br>
     * {@link #getGattStatus()} != {@link android.bluetooth.BluetoothGatt#GATT_SUCCESS} <br>
     * after a transmission.
     *
     * @param retryCounter -1 for infinite tries otherwise &gt;=0 tries
     */
    public void setRetryCounter(int retryCounter) {
        mRetryCounter = retryCounter;
    }

    public UUID getServiceUUID() {
        return mServiceUUID;
    }

    public UUID getCharacteristicUUID() {
        return mCharacteristicUUID;
    }

    public UUID getDescriptorUUID() {
        return mDescriptorUUID;
    }

    public Callback getCallback() {
        return mCallback;
    }

    /**
     * An identifier string for this message. This is not unique.
     * Two messages can have the identifier but as we are transmitting messages in sequence and not in parallel, it is not an issue.
     *
     */
    public String getIdentifier() {
        StringBuilder builder = new StringBuilder();
        builder.append(mServiceUUID.toString());
        builder.append(":").append(mCharacteristicUUID.toString());
        if (mDescriptorUUID != null)
            builder.append(":").append(mDescriptorUUID.toString());
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Identifier: " + getIdentifier();
    }

    /**
     * A convenience method to generate the identifier based on a {@link BluetoothGattCharacteristic} object.<br>
     * Also see {@link #getIdentifier()}.
     */
    public static String toIdentifier(BluetoothGattCharacteristic characteristic) {
        StringBuilder builder = new StringBuilder();
        builder.append(characteristic.getService().getUuid().toString());
        builder.append(":").append(characteristic.getUuid().toString());
        return builder.toString();
    }

    /**
     * See {@link #toIdentifier(BluetoothGattCharacteristic)}
     */
    public static String toIdentifier(BluetoothGattDescriptor descriptor) {
        StringBuilder builder = new StringBuilder();
        builder.append(descriptor.getCharacteristic().getService().getUuid().toString());
        builder.append(":").append(descriptor.getCharacteristic().getUuid().toString());
        builder.append(":").append(descriptor.getUuid().toString());
        return builder.toString();
    }
}
