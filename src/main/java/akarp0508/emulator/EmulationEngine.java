package akarp0508.emulator;

import akarp0508.gui.EmulatorWindow;
import akarp0508.gui.components.EmulationPreviewPanel;

public class EmulationEngine implements Runnable{
    private final EmulatorWindow window;

    private final int[] registers = new int[8];

    private int stackPointer;
    private int programCounter = 0x40;
    private int backupFlags;
    private int backupPC;

    /*
         - bit 0 to flaga przeniesienia (C)
         - bit 1 to flaga przepełnienia (O)
         - bit 2 to flaga zera (Z)
         - bit 3 to flaga ujemności (N)
         - bit 4 to flaga przerwania (I)
    */
    private final boolean[] flags = new boolean[32];

    private boolean running = true;

    private final int[] RAM;

    //przerwania
    private final boolean[] interruptRegisters = new boolean[16];



    //private Thread gpuThread;

    public EmulationEngine(EmulationPreviewPanel epp, EmulatorWindow window,int RAMsize) {
        this.window = window;
        RAM = new int[RAMsize];
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
        byte v = (byte)readFromBus(programCounter+1,(byte)1,false);
        byte A = (byte)(v & 0b111);
        byte B = (byte)((v>>3) & 0b111);
        byte byteCount = (byte)((v>>6) & 0b11);
        int value = 0;
        if(byteCount == 3)
            byteCount = 4;
        for(int i=0; i<byteCount; i++){
            value = ( value << 8 ) | (byte)readFromBus(programCounter+2+i,(byte)1,false);
        }
        byteCount+=2;
        switch((byte)readFromBus(programCounter,(byte)1,false)) {
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
                flags[2] = ((registers[A]>>(registers[B]&0b11111))&1) == 0;
                break;
            case 43:        //BTST reg-imm
                flags[2] = ((registers[A]>>(value&0b11111))&1) == 0;
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
                writeToBus(programCounter+byteCount,stackPointer,(byte)4);
                stackPointer+=4;
                programCounter = registers[B] + value;
                break;
            case 50:        //RET
                stackPointer-=4;
                programCounter = readFromBus(stackPointer,(byte)4,false);
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
                writeToBus(registers[A],stackPointer,(byte)4);
                stackPointer+=4;
                break;
            case 69:        //POP
                byteCount=2;
                stackPointer-=4;
                registers[A] = readFromBus(stackPointer,(byte)4,false);
                break;
            case 70:        //PSHF
                byteCount = 1;
                int flagsInt =0;
                for(byte i=0;i<32;i++){
                    flagsInt = (flagsInt<<1) | (flags[31-i]?1:0);
                }
                writeToBus(flagsInt,stackPointer,(byte)4);
                stackPointer+=4;
                break;
            case 71:        //POPF
                byteCount = 1;
                flagsInt = readFromBus(stackPointer,(byte)4,false);
                for(byte i=0;i<32;i++){
                    flags[i] = (flagsInt&1)==1;
                    flagsInt>>=1;
                }
                break;
            case 72:        //LDB addr
                registers[A] = readFromBus(registers[B]+value,(byte)1,true);
                break;
            case 73:        //LDB addr++
                registers[A] = readFromBus(registers[B]+value,(byte)1,true);
                registers[B]++;
                break;
            case 74:        //LDB addr--
                registers[A] = readFromBus(registers[B]+value,(byte)1,true);
                registers[B]--;
                break;
            case 75:        //LDBU addr
                registers[A] = readFromBus(registers[B]+value,(byte)1,false);
                break;
            case 76:        //LDBU addr++
                registers[A] = readFromBus(registers[B]+value,(byte)1,false);
                registers[B]++;
                break;
            case 77:        //LDBU addr--
                registers[A] = readFromBus(registers[B]+value,(byte)1,false);
                registers[B]--;
                break;
            case 78:        //LDH addr
                registers[A] = readFromBus(registers[B]+value,(byte)2,true);
                break;
            case 79:        //LDH addr++
                registers[A] = readFromBus(registers[B]+value,(byte)2,true);
                registers[B]++;
                break;
            case 80:        //LDH addr--
                registers[A] = readFromBus(registers[B]+value,(byte)2,true);
                registers[B]--;
                break;
            case 81:        //LDHU addr
                registers[A] = readFromBus(registers[B]+value,(byte)2,false);
                break;
            case 82:        //LDHU addr++
                registers[A] = readFromBus(registers[B]+value,(byte)2,false);
                registers[B]++;
                break;
            case 83:        //LDHU addr--
                registers[A] = readFromBus(registers[B]+value,(byte)2,false);
                registers[B]--;
                break;
            case 84:        //LDW addr
                registers[A] = readFromBus(registers[B]+value,(byte)4,true);
                break;
            case 85:        //LDW addr++
                registers[A] = readFromBus(registers[B]+value,(byte)4,true);
                registers[B]++;
                break;
            case 86:        //LDW addr--
                registers[A] = readFromBus(registers[B]+value,(byte)4,true);
                registers[B]--;
                break;
            case 87:        //STB addr
                writeToBus(registers[A],registers[B]+value,(byte)1);
                break;
            case 88:        //STB addr++
                writeToBus(registers[A],registers[B]+value,(byte)1);
                registers[B]++;
                break;
            case 89:        //STB addr--
                writeToBus(registers[A],registers[B]+value,(byte)1);
                registers[B]--;
                break;
            case 90:        //STH addr
                writeToBus(registers[A],registers[B]+value,(byte)2);
                break;
            case 91:        //STH addr++
                writeToBus(registers[A],registers[B]+value,(byte)2);
                registers[B]++;
                break;
            case 92:        //STH addr--
                writeToBus(registers[A],registers[B]+value,(byte)2);
                registers[B]--;
                break;
            case 93:        //STW addr
                writeToBus(registers[A],registers[B]+value,(byte)4);
                break;
            case 94:        //STW addr++
                writeToBus(registers[A],registers[B]+value,(byte)4);
                registers[B]++;
                break;
            case 95:        //STW addr--
                writeToBus(registers[A],registers[B]+value,(byte)4);
                registers[B]--;
                break;
            case 96:        //CPB addr
                writeToBus(readFromBus(registers[B],(byte)1,false),registers[A],(byte)1);
                break;
            case 97:        //CPB addr++
                writeToBus(readFromBus(registers[B],(byte)1,false),registers[A],(byte)1);
                registers[A]++;
                registers[B]++;
                break;
            case 98:        //CPB addr--
                writeToBus(readFromBus(registers[B],(byte)1,false),registers[A],(byte)1);
                registers[A]--;
                registers[B]--;
                break;
            case 99:        //CPH addr
                writeToBus(readFromBus(registers[B],(byte)2,false),registers[A],(byte)2);
                break;
            case 100:       //CPH addr++
                writeToBus(readFromBus(registers[B],(byte)2,false),registers[A],(byte)2);
                registers[A]++;
                registers[B]++;
                break;
            case 101:       //CPH addr--
                writeToBus(readFromBus(registers[B],(byte)2,false),registers[A],(byte)2);
                registers[A]--;
                registers[B]--;
                break;
            case 102:       //CPW addr
                writeToBus(readFromBus(registers[B],(byte)4,false),registers[A],(byte)4);
                break;
            case 103:       //CPW addr++
                writeToBus(readFromBus(registers[B],(byte)4,false),registers[A],(byte)4);
                registers[A]++;
                registers[B]++;
                break;
            case 104:       //CPW addr--
                writeToBus(readFromBus(registers[B],(byte)4,false),registers[A],(byte)4);
                registers[A]--;
                registers[B]--;
                break;
            case 105:       //SEC
                flags[0]=true;
                break;
            case 106:       //SEO
                flags[1]=true;
                break;
            case 107:       //SEZ
                flags[2]=true;
                break;
            case 108:       //SEN
                flags[3]=true;
                break;
            case 109:       //SEI
                flags[4]=true;
                break;
            case 110:       //CLC
                flags[0]=false;
                break;
            case 111:       //CLO
                flags[1]=false;
                break;
            case 112:       //CLZ
                flags[2]=false;
                break;
            case 113:       //CLN
                flags[3]=false;
                break;
            case 114:       //CLI
                flags[4]=false;
                break;
            case 115:       //IRET
                byteCount = 1;
                flagsInt = backupFlags;
                for(byte i=0;i<32;i++){
                    flags[i] = (flagsInt&1)==1;
                    flagsInt>>=1;
                }
                flags[4]=false;
                programCounter = backupPC;
                break;
            case 116:       //WFI
                //todo zrobic
                break;
            case 117:       //STP
                //todo zrobic
                break;
            default:
                //todo pause emulation and show error window
        }
        programCounter = byteCount;
    }

    private void writeToBus(int value, int startAddress, byte size){
        startAddress = startAddress & (-size);
        int mask = ((0xffffffff>>>((4-size)*8)))<<((startAddress&0b11)*8);
        RAM[startAddress>>>2] = (RAM[startAddress>>>2]&(~mask)) | ((value<<((startAddress&0b11)*8))&mask);
    }

    private int readFromBus(int startAddress, byte size, boolean signed){
        int result = 0;
        startAddress = startAddress & (-size);
        int mask = ((0xffffffff>>>((4-size)*8)))<<((startAddress&0b11)*8);
        for(int i=0;i<size;i++){
            result = (RAM[startAddress>>>2]&mask)>>>((startAddress&0b11)*8);
        }
        if(signed || size !=4){
            result = (size==1)?(byte)result:(short)result;
        }
        return result;
    }




    public void kill() {
        this.running = false;
    }


}
