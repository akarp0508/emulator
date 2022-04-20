package akarp0508.emulator.components;

//DataField is a class made to easily store data (max size is 268435456)
public class DataField {
    protected final byte[] dataArray;

    public DataField(int size) {
        this.dataArray = new byte[size];
    }

    public void writeInt(int address, int value){
        address -= address%4;
        writeShort(address,(short) value);
        value >>= 16;
        writeShort(address+1,(short) value);
    }

    public int readInt(int address){
        address -= address%4;
        return  ( ( Short.toUnsignedInt(readShort(address+1) )<<16) | Short.toUnsignedInt(readShort(address) ) );
    }

    public void writeShort(int address, short value){
        address -= address%2;
        writeByte(address,(byte) value);
        value >>= 8;
        writeByte(address+1,(byte) value);
    }

    public short readShort(int address){
        address -= address%2;
        return (short) ((Byte.toUnsignedInt(readByte(address+1))<<8) | (Byte.toUnsignedInt(readByte(address))));
    }

    public void writeByte(int address, byte value){
        address &= 0x0FFFFFFF;
        if(dataArray.length>address){
            dataArray[address] = value;
        }
    }

    public byte readByte(int address){
        address &= 0x0FFFFFFF;
        if(dataArray.length>address){
            return  dataArray[address];
        }
        return 0;
    }

    public boolean readBoolean(int address,int bit){
        return ((readByte(address) >>> bit) & 1) == 1;
    }

    public void writeBoolean(int address, int bit, boolean value){
        byte base = readByte(address);
        byte base2 = (byte)(1<<bit);
        base2 = (byte)~base2;
        base &= base2;
        //todo
    }
}
