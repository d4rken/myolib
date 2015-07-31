package eu.darken.myolib.processor.classifier;

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

    public ArmSyncedClassifierEvent() {
        super(Type.ARM_SYNCED);
    }

    public WarmUpState getWarmUpState() {
        return mWarmUpState;
    }

    public void setWarmUpState(WarmUpState warmUpState) {
        mWarmUpState = warmUpState;
    }

    public Arm getArm() {
        return mArm;
    }

    public void setArm(Arm arm) {
        mArm = arm;
    }

    public Direction getDirection() {
        return mDirection;
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }
}
