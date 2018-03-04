/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.processor.emg;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.processor.BaseProcessor;
import eu.darken.myolib.services.Emg;

/**
 * Creates {@link EmgData} objects from {@link BaseDataPacket} objects.<br>
 * More specifically, two for each.
 */
public class EmgProcessor extends BaseProcessor {
    private static final String TAG = "MyoLib:EmgProcessor";

    public EmgProcessor() {
        super();
        getSubscriptions().add(Emg.EMGDATA0.getCharacteristicUUID());
        getSubscriptions().add(Emg.EMGDATA1.getCharacteristicUUID());
        getSubscriptions().add(Emg.EMGDATA2.getCharacteristicUUID());
        getSubscriptions().add(Emg.EMGDATA3.getCharacteristicUUID());
    }

    protected void doProcess(BaseDataPacket packet) {
        byte[] data1 = new byte[8];
        System.arraycopy(packet.getData(), 0, data1, 0, 8);
        EmgData emgData1 = new EmgData(packet.getDeviceAddress(), packet.getTimeStamp(), data1);

        byte[] data2 = new byte[8];
        System.arraycopy(packet.getData(), 8, data2, 0, 8);
        EmgData emgData2 = new EmgData(packet.getDeviceAddress(), packet.getTimeStamp() + 5, data2);

        for (DataListener listener : getDataListeners()) {
            EmgDataListener emgListener = (EmgDataListener) listener;
            emgListener.onNewEmgData(emgData1);
            emgListener.onNewEmgData(emgData2);
        }
    }

    public interface EmgDataListener extends DataListener {
        void onNewEmgData(EmgData emgData);
    }

    public void addListener(EmgDataListener listener) {
        super.addDataListener(listener);
    }
}
