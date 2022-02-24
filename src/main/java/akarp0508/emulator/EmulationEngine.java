package akarp0508.emulator;

import akarp0508.gui.EmulatorWindow;
import akarp0508.gui.components.EmulationPreviewPanel;

public class EmulationEngine implements Runnable{
    private final EmulatorWindow window;

    private int[] registers = new int[8];

    private int stackPointer;
    private int programCounter;

    /*
         - bit 0 to flaga przeniesienia (C)
         - bit 1 to flaga przepełnienia (O)
         - bit 2 to flaga zera (Z)
         - bit 3 to flaga ujemności (N)
         - bit 4 to flaga przerwania (I)
    */
    private boolean[] flags = new boolean[32];

    private boolean running = true;

    private byte[] RAM = new byte[Integer.MAX_VALUE];


    //private Thread gpuThread;

    public EmulationEngine(EmulationPreviewPanel epp, EmulatorWindow window) {
        this.window = window;
        //new InstructionDecoder();
        //gpu = new GPUEmulationEngine(epp);
    }

    public void setRegister(byte index,int value){
        if(index>0 && index<64)
            registers[index] = value;
    }

    public void run(){

        long lastTime = System.nanoTime();
        long actualIPS=0;
        while(running){

            executeInstruction();

            actualIPS++;
            if(System.nanoTime()-lastTime>1000000000){
                window.setActualIPS(actualIPS);
                actualIPS=0;
                lastTime = System.nanoTime();
            }
        }
    }

    private void executeInstruction(){
        byte v = RAM[programCounter+1];
        byte A = (byte)(v & 0b111);
        byte B = (byte)((v>>3) & 0b111);
        byte byteCount = (byte)((v>>6) & 0b11);
        int value = 0;
        if(byteCount == 3)
            byteCount = 4;
        for(int i=0; i<byteCount; i++){
            value = ( value << 8 ) | RAM[programCounter+2+i];
        }
        byteCount+=2;
        switch(RAM[programCounter]) {
            case 0:         //NOP
                byteCount = 1;
                break;
            case 1:         //ADD reg-reg
                byteCount = 2;
                long result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(registers[B]);
                flags[1] = (int)result!=(long)registers[A]+registers[B];
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 2:         //ADD reg-imm
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(value);
                flags[1] = (int)result!=(long)registers[A]+value;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 3:         //ADC reg-reg
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(registers[B])+(flags[0]?1:0);
                flags[1] = (int)result!=(long)registers[A]+registers[B]+(flags[0]?1:0);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 4:         //ADC reg-imm
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(value)+(flags[0]?1:0);
                flags[1] = (int)result!=(long)registers[A]+value+(flags[0]?1:0);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 5:         //SUB reg-reg
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(registers[B]);
                flags[1] = (int)result!=(long)registers[A]-registers[B];
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 6:         //SUB reg-imm
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(value);
                flags[1] = (int)result!=(long)registers[A]-value;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 7:         //SBC reg-reg
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(registers[B])-(flags[0]?1:0);
                flags[1] = (int)result!=(long)registers[A]-registers[B]-(flags[0]?1:0);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 8:         //SBC reg-imm
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(value)-(flags[0]?1:0);
                flags[1] = (int)result!=(long)registers[A]-value-(flags[0]?1:0);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 9:         //AND reg-reg
                byteCount = 2;
                result = registers[A] & registers[B];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 10:        //AND reg-imm
                result = registers[A] & value;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 11:        //OR reg-reg
                byteCount = 2;
                result = registers[A] | registers[B];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 12:        //OR reg-imm
                result = registers[A] | value;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 13:        //XOR reg-reg
                byteCount = 2;
                result = registers[A] ^ registers[B];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 14:        //XOR reg-imm
                result = registers[A] ^ value;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 15:        //LSL reg-reg
                byteCount = 2;
                result = (long) registers[A] << (registers[B]&0b11111);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 16:        //LSL reg-imm
                result = (long) registers[A] << (value&0b11111);
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 17:        //LSR reg-reg
                byteCount = 2;
                result = registers[A] >>> (registers[B]&0b11111);
                flags[0] = (registers[B]&0b11111)!=0 && ((registers[A] >>> ((registers[B]&0b11111)-1))&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 18:        //LSR reg-imm
                result = registers[A] >>> (value&0b11111);
                flags[0] = (value&0b11111)!=0 && ((registers[A] >>> ((value&0b11111)-1))&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 19:        //ASR reg-reg
                byteCount = 2;
                result = registers[A] >> (registers[B]&0b11111);
                flags[0] = (value&0b11111)!=0 && ((registers[A] >> ((value&0b11111)-1))&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 20:        //ASR reg-imm
                result = registers[A] >> (value&0b11111);
                flags[0] = (value&0b11111)!=0 && ((registers[A] >> ((value&0b11111)-1))&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 21:        //ROT reg-reg
                byteCount = 2;
                result = Integer.rotateLeft(registers[A],registers[B]&0b11111);
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 22:        //ROT reg-imm
                result = Integer.rotateLeft(registers[A],value&0b11111);
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 23:        //MLTL reg-reg
                byteCount = 2;
                result = (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(registers[B]));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 24:        //MLTL reg-imm
                result = (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(value));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 25:        //MLTH reg-reg
                byteCount = 2;
                result = (((long)registers[A])*registers[B])>>32;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 26:        //MLTH reg-imm
                result = (((long)registers[A])*value)>>32;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 27:        //MLTHU reg-reg
                byteCount = 2;
                result = (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(registers[B]))>>32;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 28:        //MLTHU reg-imm
                result = (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(value))>>32;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 29:        //DIV reg-reg
                byteCount = 2;
                result = ((long)registers[A])/registers[B];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 30:        //DIV reg-imm
                result = ((long)registers[A])/value;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 31:        //DIVU reg-reg
                byteCount = 2;
                result = (Integer.toUnsignedLong(registers[A])/Integer.toUnsignedLong(registers[B]));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 32:        //DIVU reg-imm
                result = (Integer.toUnsignedLong(registers[A])/Integer.toUnsignedLong(value));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 33:        //MOD reg-reg
                byteCount = 2;
                result = ((long)registers[A])%registers[B];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 34:        //MOD reg-imm
                result = ((long)registers[A])%value;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 35:        //MODU reg-reg
                byteCount = 2;
                result = (Integer.toUnsignedLong(registers[A])%Integer.toUnsignedLong(registers[B]));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 36:        //MODU reg-imm
                result = (Integer.toUnsignedLong(registers[A])%Integer.toUnsignedLong(value));
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 37:        //INC
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) + 1;
                flags[1] = (int)result!=(long)registers[A]+1;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 38:        //DEC
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) - 1;
                flags[1] = (int)result!=(long)registers[A]-1;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 39:        //NOT
                byteCount = 2;
                result = ~registers[A];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 40:        //NEG
                byteCount = 2;
                result = -registers[A];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 41:        //TST
                byteCount = 2;
                result = registers[A];
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 42:        //BTST reg-reg
                byteCount = 2;
                flags[2] = ((registers[A]>>(registers[B]&0b11111))&1) == 1;
                break;
            case 43:        //BTST reg-imm
                flags[2] = ((registers[A]>>(value&0b11111))&1) == 1;
                break;
            case 44:        //CMP reg-reg
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(registers[B]);
                flags[1] = (int)result!=(long)registers[A]-registers[B];
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 45:        //CMP reg-imm
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) - Integer.toUnsignedLong(value);
                flags[1] = (int)result!=(long)registers[A]-value;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 46:        //ADSP
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(stackPointer);
                flags[1] = (int)result!=(long)registers[A]+stackPointer;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 47:        //ADPC
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(programCounter);
                flags[1] = (int)result!=(long)registers[A]+programCounter;
                flags[0] = ((result>>32)&1) == 1;
                flags[2] = (int)result == 0;
                flags[3] = ((result>>31)&1) == 1;
                registers[A] = (int)result;
                break;
            case 48:        //JMP
                programCounter = registers[B] + value;
                break;
            case 49:        //CALL
                saveToRam(programCounter+byteCount,stackPointer,(byte)4);
                stackPointer+=4;
                programCounter = registers[B] + value;
                break;
            case 50:        //RET
                stackPointer-=4;
                programCounter = readFromRam(stackPointer,(byte)4);
                break;
            case 51:        //BEQ
                programCounter = flags[2]?(registers[B] + value):programCounter;
                break;
            case 52:        //BNE
                programCounter = flags[2]?programCounter:(registers[B] + value);
                break;
            case 53:        //BLS
                programCounter = (flags[1]!=flags[3])?(registers[B] + value):programCounter;
                break;
            case 54:        //BLE
                programCounter = ((flags[1]!=flags[3])||flags[2])?(registers[B] + value):programCounter;
                break;
            case 55:        //BGR
                programCounter = ((flags[1]==flags[3])&&(!flags[2]))?(registers[B] + value):programCounter;
                break;
            case 56:        //BGE
                programCounter = (flags[1]==flags[3])?(registers[B] + value):programCounter;
                break;
            case 57:        //BLSU
                programCounter = flags[0]?(registers[B] + value):programCounter;
                break;
            case 58:        //BLEU
                programCounter = (flags[0]||flags[2])?(registers[B] + value):programCounter;
                break;
            case 59:        //BGRU
                programCounter = ((!flags[0]) && (!flags[2]))?(registers[B] + value):programCounter;
                break;
            case 60:        //BGEU
                programCounter = (!flags[0])?(registers[B] + value):programCounter;
                break;
            case 61:        //BOF
                programCounter = flags[1]?(registers[B] + value):programCounter;
                break;
            case 62:        //BNO
                programCounter = (!flags[1])?(registers[B] + value):programCounter;
                break;
            case 63:        //BPS
                programCounter = (!flags[3])?(registers[B] + value):programCounter;
                break;
            case 64:        //BNG
                programCounter = flags[3]?(registers[B] + value):programCounter;
                break;
            case 65:        //MOV reg-reg
                byteCount=2;
                registers[A] = registers[B];
                break;
            case 66:        //MOV reg-imm
                registers[A] = value;
                break;
            case 67:        //WSP
                byteCount=2;
                stackPointer = registers[A];
                break;
            case 68:        //PSH
                byteCount=2;
                saveToRam(registers[A],stackPointer,(byte)4);
                stackPointer+=4;
                break;
            case 69:        //POP
                byteCount=2;
                stackPointer-=4;
                registers[A] = readFromRam(stackPointer,(byte)4);
                break;
            case 70:        //PSHF
                int flagsInt =0;
                for(byte i=0;i<32;i++){
                    flagsInt = (flagsInt<<1) + (flags[31-i]?1:0);
                }
                break;
            case 71:        //POPF
                break;
            case 72:        //LDB addr
                break;
            case 73:        //LDB addr++
                break;
            case 74:        //LDB addr--
                break;
            case 75:        //LDBU addr
                break;
            case 76:        //LDBU addr++
                break;
            case 77:        //LDBU addr--
                break;
            case 78:        //LDH addr
                break;
            case 79:        //LDH addr++
                break;
            case 80:        //LDH addr--
                break;
            case 81:        //LDHU addr
                break;
            case 82:        //LDHU addr++
                break;
            case 83:        //LDHU addr--
                break;
            case 84:        //LDW addr
                break;
            case 85:        //LDW addr++
                break;
            case 86:        //LDW addr--
                break;
            case 87:        //STB addr
                break;
            case 88:        //STB addr++
                break;
            case 89:        //STB addr--
                break;
            case 90:        //STH addr
                break;
            case 91:        //STH addr++
                break;
            case 92:        //STH addr--
                break;
            case 93:        //STW addr
                break;
            case 94:        //STW addr++
                break;
            case 95:        //STW addr--
                break;
            case 96:        //CPB addr
                break;
            case 97:        //CPB addr++
                break;
            case 98:        //CPB addr--
                break;
            case 99:        //CPH addr
                break;
            case 100:       //CPH addr++
                break;
            case 101:       //CPH addr--
                break;
            case 102:       //CPW addr
                break;
            case 103:       //CPW addr++
                break;
            case 104:       //CPW addr--
                break;
            case 105:       //SEC
                break;
            case 106:       //SEO
                break;
            case 107:       //SEZ
                break;
            case 108:       //SEN
                break;
            case 109:       //SEI
                break;
            case 110:       //CLC
                break;
            case 111:       //CLO
                break;
            case 112:       //CLZ
                break;
            case 113:       //CLN
                break;
            case 114:       //CLI
                break;
            case 115:       //IRET
                break;
            case 116:       //WFI
                break;
            case 117:       //STP
                break;
            default:
                //todo pause emulation and show error window
        }
        programCounter = byteCount;
    }

    private void saveToRam(int value, int startAddress, byte size){
        startAddress = startAddress & (-size);
        for(int i=0;i<size;i++){
            RAM[startAddress+size-1-i] = (byte)value;
            value = value >> 8;
        }
    }

    private int readFromRam(int startAddress, byte size){
        int result = 0;
        startAddress = startAddress & (-size);
        for(int i=0;i<size;i++){
            result = (result << 8) | RAM[startAddress+size-1-i];
        }
        return result;
    }




    public void kill() {
        this.running = false;
    }


}
