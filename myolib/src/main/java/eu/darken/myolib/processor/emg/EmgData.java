/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.processor.emg;

import eu.darken.myolib.processor.BaseDataPacket;

/**
 * Class to hold EMG data from one of the Myo's 8 EMG sensors.
 */
public class EmgData {
    private final byte[] mData;
    private final long mTimestamp;
    private final String mDeviceAddress;

    public EmgData(String deviceAddress, long timestamp, byte[] data) {
        mDeviceAddress = deviceAddress;
        mTimestamp = timestamp;
        mData = data;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    /**
     * @return Array of byte values ranging from -128 to 127.
     * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h#L371">Myo protocol specification</a>
     */
    public byte[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int b : mData)
            builder.append(String.format("%+04d", b)).append(" ");
        return builder.toString();
    }

    /**
     * Timestamp of this EMG dataset. Based on {@link BaseDataPacket#getTimeStamp()}.
     * As every {@link BaseDataPacket} contains two EMG datasets.
     * The second EMG data step will have {@link BaseDataPacket#getTimeStamp()}+5ms as timestamp.
     *
     * @return timestamp in miliseconds
     * @see <a href="http://developerblog.myo.com/myocraft-emg-in-the-bluetooth-protocol/">Myo blog: EMG data</a>
     */
    public long getTimestamp() {
        return mTimestamp;
    }
}
