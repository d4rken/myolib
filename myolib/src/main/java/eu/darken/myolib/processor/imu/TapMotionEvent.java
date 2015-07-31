package eu.darken.myolib.processor.imu;

import eu.darken.myolib.processor.DataPacket;

public class TapMotionEvent extends MotionEvent {
    private int mTapCount;

    public TapMotionEvent(DataPacket packet) {
        super(packet, Type.TAP);
    }

    public int getTapCount() {
        return mTapCount;
    }

    public void setTapCount(int tapCount) {
        mTapCount = tapCount;
    }
}
