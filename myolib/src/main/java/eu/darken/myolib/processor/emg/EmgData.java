/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.processor.emg;

import eu.darken.myolib.processor.BaseDataPacket;

/**
 * Class to hold EMG data from one of the Myo's 8 EMG sensors.
 */
public class EmgData {
    private final int[] mData;
    private final long mTimestamp;
    private final String mDeviceAddress;

    public EmgData(String deviceAddress, long timestamp, int[] data) {
        mDeviceAddress = deviceAddress;
        mTimestamp = timestamp;
        mData = data;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    /**
     * @return Array of int values ranging from 0 to 255.
     * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h">Myo protocol specification</a>
     */
    public int[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int b : mData)
            builder.append(String.format("%03d", b)).append(" ");
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
