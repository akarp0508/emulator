package akarp0508.emulator.components;

public class DataField {
    private final byte[] dataArray;

    public DataField(int size) {
        this.dataArray = new byte[size];
    }

    public void writeByte(int address, byte value){
        if(dataArray.length>address){
            dataArray[address] = value;
        }
    }

    public byte readByte(int address){
        if(dataArray.length>address){
            return  dataArray[address];
        }
        return 0;
    }
}
