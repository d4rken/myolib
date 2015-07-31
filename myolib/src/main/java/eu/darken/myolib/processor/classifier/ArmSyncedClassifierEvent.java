package eu.darken.myolib.processor.classifier;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.tools.ByteHelper;

public class ArmSyncedClassifierEvent extends ClassifierEvent {
    /**
     * Possible warm-up states for Myo.
     */
    public enum WarmUpState {
        UNKNOWN((byte) 0x00), COLD((byte) 0x01), WARM((byte) 0x02);
        private final byte mValue;

        WarmUpState(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private WarmUpState mWarmUpState = WarmUpState.UNKNOWN;


    /**
     * Enumeration identifying a right arm or left arm.
     */
    public enum Arm {
        UNKNOWN((byte) 0xFF), RIGHT((byte) 0x01), LEFT(((byte) 0x02));
        private final byte mValue;

        Arm(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private Arm mArm = Arm.UNKNOWN;

    /**
     * Possible directions for Myo's +x axis relative to a user's arm.
     */
    public enum Direction {
        UNKNOWN((byte) 0xFF), TOWARDS_WRIST((byte) 0x01), TOWARDS_ELBOW((byte) 0x02);
        private final byte mValue;

        Direction(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private Direction mDirection = Direction.UNKNOWN;
    private float mRotation;

    public ArmSyncedClassifierEvent(BaseDataPacket packet) {
        super(packet, Type.ARM_SYNCED);
        ByteHelper byteHelper = new ByteHelper(packet.getData());
        int typeValue = byteHelper.getUInt8();
        if (getType().getValue() != typeValue)
            throw new RuntimeException("Incompatible BaseDataPacket:" + typeValue);

        int armValue = byteHelper.getUInt8();
        for (ArmSyncedClassifierEvent.Arm arm : ArmSyncedClassifierEvent.Arm.values()) {
            if (arm.getValue() == armValue) {
                mArm = arm;
                break;
            }
        }
        int directionValue = byteHelper.getUInt8();
        for (ArmSyncedClassifierEvent.Direction direction : ArmSyncedClassifierEvent.Direction.values()) {
            if (direction.getValue() == directionValue) {
                mDirection = direction;
                break;
            }
        }
        if (mDirection == null)
            mDirection = Direction.UNKNOWN;
        // FIXME what is the correct scale for this?
        // https://github.com/logotype/myodaemon/blob/master/native-osx/libs/myo.framework/Versions/A/Headers/cxx/impl/Hub_impl.hpp#L144
        if (packet.getData().length > 3) {
            int warmUpStateValue = byteHelper.getUInt8();
            for (ArmSyncedClassifierEvent.WarmUpState warmUpState : ArmSyncedClassifierEvent.WarmUpState.values()) {
                if (warmUpState.getValue() == warmUpStateValue) {
                    mWarmUpState = warmUpState;
                    break;
                }
            }
            mRotation = byteHelper.getUInt16() / 16384.0f;
        }
    }

    public WarmUpState getWarmUpState() {
        return mWarmUpState;
    }


    public Arm getArm() {
        return mArm;
    }

    public Direction getDirection() {
        return mDirection;
    }

}
