package eu.darken.myolib.processor.imu;

public class TapMotionEvent extends MotionEvent {
    private int mTapCount;

    public TapMotionEvent() {
        super(Type.TAP);
    }

    public int getTapCount() {
        return mTapCount;
    }

    public void setTapCount(int tapCount) {
        mTapCount = tapCount;
    }
}
