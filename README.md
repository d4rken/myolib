# myolib
darken's Myo lib is an opensource Android library to communicate with Myo devices

# Quickstart
## Utilizing callbacks
```java
MyoConnector connector = new MyoConnector(getContext());
connector.scan(5000, new MyoConnector.ScannerCallback() {
    @Override
    public void onScanFinished(List<Myo> myos) {
        Myo myo = myos.get(0);
        myo.connect();
        myo.writeUnlock(MyoCmds.UnlockType.HOLD, new Myo.MyoCommandCallback() {
            @Override
            public void onCommandDone(Myo myo, MyoMsg msg) {
                myo.writeVibrate(MyoCmds.VibrateType.LONG, null);
            }
        });
    }
});
```

## Receiving sensor data
```java
/**
 * ...
 */
Myo myo = myos.get(0);
myo.connect();
EmgProcessor emgProcessor = new EmgProcessor();
myo.addProcessor(emgProcessor);
emgProcessor.addListener(new EmgProcessor.EmgDataListener() {
    @Override
    public void onNewEmgData(EmgData emgData) {
        Log.i("EMG-Data", Arrays.toString(emgData.getData()));
    }
});
```

## Advanced use
```java
BaseMyo baseMyo = new BaseMyo(getContext(), bluetoothDevice);
baseMyo.connect();
ReadMsg readMsg = new ReadMsg(Battery.BATTERYLEVEL, new MyoMsg.Callback() {
    @Override
    public void onResult(MyoMsg msg) {
        byte[] result = ((ReadMsg) msg).getValue();
    }
});
baseMyo.submit(readMsg);
```

## Advanced advanced use
```java
BaseMyo baseMyo = new BaseMyo(getContext(), bluetoothDevice);
baseMyo.connect();
WriteMsg writeMsg = new WriteMsg(
        UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),
        UUID.fromString("00002A00-0000-1000-8000-00805f9b34fb"),
        null,
        new byte[]{ /*....*/},
        new MyoMsg.Callback() {
            @Override
            public void onResult(MyoMsg msg) {
                if(msg.getGattStatus() == BluetoothGatt.GATT_SUCCESS)
                    Log.i("MYOAPP", "Data written!");
            }
        }

);
baseMyo.submit(writeMsg);
```
# License
