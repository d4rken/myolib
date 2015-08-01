package eu.darken.myolib;


import eu.darken.myolib.msgs.ReadMsg;
import eu.darken.myolib.processor.classifier.PoseClassifierEvent;
import eu.darken.myolib.tools.ByteHelper;

public class MyoInfo {
    /**
     * Unique serial number of this Myo.
     */
    private final int[] mSerialNumber;
    /**
     * Pose that should be interpreted as the unlock pose.
     */
    private PoseClassifierEvent.Pose mUnlockPose;

    public enum ActiveClassifierType {
        BUILTIN((byte) 0x0), CUSTOM((byte) 0x1);
        private final byte mValue;

        ActiveClassifierType(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    /**
     * Whether Myo is currently using a built-in or a custom classifier.
     */
    private final ActiveClassifierType mActiveClassifierType;
    /**
     * Index of the classifier that is currently active
     */
    private final int mActiveClassifierIndex;
    /**
     * Whether Myo contains a valid custom classifier. 1 if it does, otherwise 0.
     */
    private final boolean mHasCustomClassifier;
    /**
     * Set if the Myo uses BLE indicates to stream data, for reliable capture.
     */
    private final int mStreamIndicating;

    public enum Sku {
        UNKNOWN((byte) 0x0), BLACK((byte) 0x1), WHITE((byte) 0x2);
        private final byte mValue;

        Sku(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    /**
     * SKU value of the device. See myohw_sku_t
     */
    private final Sku mSKU;

    private final byte[] mReservedData;

    public MyoInfo(ReadMsg msg) {
        ByteHelper byteHelper = new ByteHelper(msg.getValue());
        int[] serialNumberValue = new int[6];
        for (int i = 0; i < 6; i++)
            serialNumberValue[i] = byteHelper.getUInt8();
        mSerialNumber = serialNumberValue;
        int unlockPoseValue = byteHelper.getUInt16();
        for (PoseClassifierEvent.Pose pose : PoseClassifierEvent.Pose.values()) {
            if (pose.getValue() == unlockPoseValue) {
                mUnlockPose = pose;
                break;
            }
        }
        if (mUnlockPose == null)
            mUnlockPose = PoseClassifierEvent.Pose.UNKNOWN;

        int classifierTypeValue = byteHelper.getUInt8();
        ActiveClassifierType classifierType = ActiveClassifierType.BUILTIN;
        for (ActiveClassifierType type : ActiveClassifierType.values()) {
            if (type.getValue() == classifierTypeValue) {
                classifierType = type;
                break;
            }
        }
        mActiveClassifierType = classifierType;

        mActiveClassifierIndex = byteHelper.getUInt8();

        mHasCustomClassifier = byteHelper.getUInt8() == 1;

        mStreamIndicating = byteHelper.getUInt8();

        int skuValue = byteHelper.getUInt8();
        Sku skuType = Sku.UNKNOWN;
        for (Sku type : Sku.values()) {
            if (type.getValue() == skuValue) {
                skuType = type;
                break;
            }
        }
        mSKU = skuType;

        mReservedData = new byte[byteHelper.getRemaining()];
        int rIndex = 0;
        while (byteHelper.hasRemaining()) {
            mReservedData[rIndex] = byteHelper.getByte();
            rIndex++;
        }
    }

    public int[] getSerialNumber() {
        return mSerialNumber;
    }

    public PoseClassifierEvent.Pose getUnlockPose() {
        return mUnlockPose;
    }

    public ActiveClassifierType getActiveClassifierType() {
        return mActiveClassifierType;
    }

    public int getActiveClassifierIndex() {
        return mActiveClassifierIndex;
    }

    public boolean isHasCustomClassifier() {
        return mHasCustomClassifier;
    }

    public int getStreamIndicating() {
        return mStreamIndicating;
    }

    public Sku getSKU() {
        return mSKU;
    }

    public byte[] getReservedData() {
        return mReservedData;
    }
}
