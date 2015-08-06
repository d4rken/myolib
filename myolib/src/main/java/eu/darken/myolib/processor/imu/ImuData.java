/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.processor.imu;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.processor.DataPacket;
import eu.darken.myolib.tools.ByteHelper;

/**
 * Class representing data from the Myo's IMU sensors.
 */
public class ImuData extends DataPacket {
    private static final double MYOHW_ORIENTATION_SCALE = 16384.0f; ///< See myohw_imu_data_t::orientation
    private static final double MYOHW_ACCELEROMETER_SCALE = 2048.0f; ///< See myohw_imu_data_t::accelerometer
    private static final double MYOHW_ACCEMYOHW_GYROSCOPE_SCALELEROMETER_SCALE = 16.0f; ///< See myohw_imu_data_t::gyroscope
    private final double[] mOrientationData;
    private final double[] mAccelerometerData;
    private final double[] mGyroData;

    public ImuData(BaseDataPacket packet) {
        super(packet.getDeviceAddress(), packet.getTimeStamp());
        ByteHelper byteHelper = new ByteHelper(packet.getData());
        mOrientationData = new double[4];
        for (int i = 0; i < 4; i++)
            mOrientationData[i] = byteHelper.getUInt16() / MYOHW_ORIENTATION_SCALE;

        mAccelerometerData = new double[3];
        for (int i = 0; i < 3; i++)
            mAccelerometerData[i] = byteHelper.getUInt16() / MYOHW_ACCELEROMETER_SCALE;

        mGyroData = new double[3];
        for (int i = 0; i < 3; i++)
            mGyroData[i] = byteHelper.getUInt16() / MYOHW_ACCEMYOHW_GYROSCOPE_SCALELEROMETER_SCALE;
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

}
