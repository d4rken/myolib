package eu.darken.myolib.processor.classifier;


public class SyncFailedClassifierEvent extends ClassifierEvent {
    /**
     * Possible outcomes when the user attempts a sync gesture.
     */
    public enum SyncResult {
        /**
         * Sync gesture was performed too hard.
         */
        FAILED_TOO_HARD((byte) 0x01);

        private final byte mValue;

        SyncResult(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private SyncResult mSyncResult;

    public SyncFailedClassifierEvent() {
        super(Type.SYNC_FAILED);
    }

    public SyncResult getSyncResult() {
        return mSyncResult;
    }

    public void setSyncResult(SyncResult syncResult) {
        mSyncResult = syncResult;
    }

}
