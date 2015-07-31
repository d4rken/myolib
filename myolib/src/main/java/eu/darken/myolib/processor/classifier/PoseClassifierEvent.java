package eu.darken.myolib.processor.classifier;


public class PoseClassifierEvent extends ClassifierEvent {
    public enum Pose {
        REST((short) 0x0000), FIST((short) 0x0001), WAVE_IN((short) 0x0002), WAVE_OUT((short) 0x0003), FINGERS_SPREAD((short) 0x0004), DOUBLE_TAP((short) 0x0005), UNKNOWN((short) 0xFFFF);

        private final short mValue;

        Pose(short value) {
            mValue = value;
        }

        public short getValue() {
            return mValue;
        }
    }

    private Pose mPose;

    public PoseClassifierEvent() {
        super(Type.POSE);
    }


    public Pose getPose() {
        return mPose;
    }

    public void setPose(Pose pose) {
        mPose = pose;
    }
}
