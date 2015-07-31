/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.processor.imu;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.tools.ByteHelper;

/**
 * Class representing data from the Myo's IMU sensors.
 */
public class ImuData {
    private static final double MYOHW_ORIENTATION_SCALE = 16384.0f; ///< See myohw_imu_data_t::orientation
    private static final double MYOHW_ACCELEROMETER_SCALE = 2048.0f; ///< See myohw_imu_data_t::accelerometer
    private static final double MYOHW_ACCEMYOHW_GYROSCOPE_SCALELEROMETER_SCALE = 16.0f; ///< See myohw_imu_data_t::gyroscope
    private final double[] mOrientationData;
    private final double[] mAccelerometerData;
    private final double[] mGyroData;
    private final long mTimestamp;
    private final String mDeviceAddress;

    public ImuData(String deviceAddress, long timestamp, double[] orientationData, double[] accelerometerData, double[] gyroData) {
        mDeviceAddress = deviceAddress;
        mTimestamp = timestamp;
        mOrientationData = orientationData;
        mAccelerometerData = accelerometerData;
        mGyroData = gyroData;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    /**
     * Values range form -1.0 to 1.0<br>
     * Format: [w,x,y,z]
     *
     * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h">Myo protocol specification</a>
     */
    public double[] getOrientationData() {
        return mOrientationData;
    }

    /**
     * Values range from -1.0 to 1.0 <br>
     * Format: [?,?,?]
     *
     * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h">Myo protocol specification</a>
     */
    public double[] getAccelerometerData() {
        return mAccelerometerData;
    }

    /**
     * Values range from -? to ? <br>
     * Format: [?,?,?]
     *
     * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h">Myo protocol specification</a>
     */
    public double[] getGyroData() {
        return mGyroData;
    }

    public static String format(double[] data) {
        StringBuilder builder = new StringBuilder();
        for (double d : data)
            builder.append(String.format("%+.3f", d)).append(" ");
        return builder.toString();
    }

    public static ImuData from(BaseDataPacket packet) {
        ByteHelper byteHelper = new ByteHelper(packet.getData());
        double[] orientationData = new double[4];
        for (int i = 0; i < 4; i++)
            orientationData[i] = byteHelper.getUInt16() / MYOHW_ORIENTATION_SCALE;

        double[] accelerometerData = new double[3];
        for (int i = 0; i < 3; i++)
            accelerometerData[i] = byteHelper.getUInt16() / MYOHW_ACCELEROMETER_SCALE;

        double[] gyroData = new double[3];
        for (int i = 0; i < 3; i++)
            gyroData[i] = byteHelper.getUInt16() / MYOHW_ACCEMYOHW_GYROSCOPE_SCALELEROMETER_SCALE;
        return new ImuData(packet.getDeviceAddress(), packet.getTimeStamp(), orientationData, accelerometerData, gyroData);
    }
}
