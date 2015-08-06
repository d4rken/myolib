/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A convenience class for creating {@link Processor} compliant classes.
 * It takes packets submitted via {@link #submit(BaseDataPacket)} and processes them sequentially on a worker thread.
 * To create a custom Processor you can extend this and just do your processing in {@link #doProcess(BaseDataPacket)}.
 */
public abstract class BaseProcessor implements Processor {
    private final List<UUID> mSubscriptions = new ArrayList<>();
    private final List<DataListener> mDataListeners = new ArrayList<>();
    private final LinkedBlockingQueue<BaseDataPacket> mQueue = new LinkedBlockingQueue<>();
    private Thread mProcessor;
    private long mPacketCounter;
    private long mPacketCounterTimeStamp;
    private long mPacketThroughput;

    public BaseProcessor() {
    }

    @Override
    public List<UUID> getSubscriptions() {
        return mSubscriptions;
    }

    @Override
    public void submit(BaseDataPacket packet) {
        mQueue.add(packet);
        mPacketCounter++;
        if (System.currentTimeMillis() - mPacketCounterTimeStamp > 1000) {
            mPacketThroughput = mPacketCounter;
            mPacketCounter = 0;
            mPacketCounterTimeStamp = System.currentTimeMillis();
//            Logy.v("BaseProcessor:" + packet.getDeviceAddress(), "throughput: " + mPacketThroughput + " packets/s");
        }
    }

    public long getPacketCounter() {
        return mPacketThroughput;
    }

    @Override
    public void onAdded() {
        mProcessor = new Thread(mLoop);
        mRunning = true;
        mProcessor.start();
    }

    @Override
    public void onRemoved() {
        mRunning = false;
        mProcessor.interrupt();
    }

    private volatile boolean mRunning = false;
    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
            while (mRunning) {
                BaseDataPacket packet;
                try {
                    packet = mQueue.take();
                } catch (InterruptedException e) {
                    continue;
                }
                doProcess(packet);
            }
        }
    };

    protected abstract void doProcess(BaseDataPacket packet);

    public List<? extends DataListener> getDataListeners() {
        return mDataListeners;
    }

    protected void addDataListener(DataListener listener) {
        if (!mDataListeners.contains(listener))
            mDataListeners.add(listener);
    }

    public boolean hasListeners() {
        return mDataListeners.size() > 0;
    }

    public void removeDataListener(DataListener listener) {
        mDataListeners.remove(listener);
    }

    public interface DataListener {
    }


}
