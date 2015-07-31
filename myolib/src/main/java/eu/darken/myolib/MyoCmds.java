/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib;

/**
 * A helper class to create the byte[] payload for sending instructions to a Myo
 */
public class MyoCmds {

    public static byte[] buildDeepSleepCmd() {
        return new byte[]{
                0x04,
                0
        };
    }

    public enum UnlockType {
        /**
         *
         */
        LOCK((byte) 0x00),
        /**
         *
         */
        TIMED((byte) 0x01),
        /**
         *
         */
        HOLD((byte) 0x02);
        private final byte mByte;

        UnlockType(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public static byte[] buildSetUnlockModeCmd(UnlockType unlockType) {
        return new byte[]{
                0x0a,
                1,
                unlockType.getByte()
        };
    }

    public enum VibrateType {
        NONE((byte) 0x00), SHORT((byte) 0x01), MEDIUM((byte) 0x02), LONG((byte) 0x03);
        private final byte mByte;

        VibrateType(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public static byte[] buildVibrateCmd(VibrateType vibrateType) {
        return new byte[]{
                0x03, // vibrate command
                1, // payload size
                vibrateType.getByte() // vibration type
        };
    }

    public enum SleepMode {
        /**
         * Go to sleep/standby after a few seconds of inactivity.
         */
        NORMAL((byte) 0),
        /**
         * Never go into sleep/standby while the device is connected.
         */
        NEVER((byte) 1);
        private final byte mByte;

        SleepMode(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public static byte[] buildSleepModeCmd(SleepMode sleepMode) {
        return new byte[]{
                0x09,
                1,
                sleepMode.getByte()
        };
    }

    public enum EmgMode {
        /**
         * No emg data is delivered.
         */
        NONE((byte) 0x00),
        /**
         * EMG data with powerline interface being filtered out.
         */
        FILTERED((byte) 0x02),
        /**
         * Raw unfiltered EMG data, this mode will implicitly set {@link eu.darken.myolib.MyoCmds.ClassifierMode#DISABLED}
         */
        RAW((byte) 0x03);

        private final byte mByte;

        EmgMode(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public enum ImuMode {
        /**
         * Disables Gyro/Acclerometer and Orientation data.
         */
        NONE((byte) 0x00),
        DATA((byte) 0x01),
        EVENTS((byte) 0x02),
        ALL((byte) 0x03),
        RAW((byte) 0x04);
        private final byte mByte;

        ImuMode(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public enum ClassifierMode {
        DISABLED((byte) 0x00),
        ENABLED((byte) 0x01);
        private final byte mByte;

        ClassifierMode(byte aByte) {
            mByte = aByte;
        }

        public byte getByte() {
            return mByte;
        }
    }

    public static byte[] buildSensorModeCmd(EmgMode emgMode, ImuMode imuMode, ClassifierMode classifierMode) {
        return new byte[]{
                0x01, // mode command
                3, // payload size
                emgMode.getByte(),
                imuMode.getByte(),
                classifierMode.getByte()
        };
    }

}
