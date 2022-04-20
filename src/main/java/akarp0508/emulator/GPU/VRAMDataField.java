package akarp0508.emulator.GPU;

import akarp0508.emulator.components.DataField;

public class VRAMDataField extends DataField {
    public VRAMDataField(int size) {
        super(size);
    }

    public byte getMode(){
        return readByte(0x0);
    }
    public byte getFlags(){
        return readByte(0x1);
    }
    public boolean getFlag(){
        return read
    }
    public short getHBlankLineNumber(){
        return readShort(0x2);
    }
    public int getFirstPageAddress(){
        return readInt(0x4);
    }
    public int getSecondPageAddress(){
        return readInt(0x4);
    }
    public short getColor(byte colorIndex){
        return readShort(0x20 + colorIndex*2);
    }
}
