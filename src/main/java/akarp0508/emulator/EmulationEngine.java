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
                registers[A] = (int)result;
                flags[0] = ((result>>32)&1) == 1;
                flags[1] = (int)result!=(long)registers[A]+registers[B];
                flags[2] = result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 2:         //ADD reg-imm
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(value);
                registers[A] = (int)result;
                flags[0] = ((result>>32)&1) == 1;
                flags[1] = (int)result!=(long)registers[A]+value;
                flags[2] = result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 3:         //ADC reg-reg
                byteCount = 2;
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(registers[B])+(flags[0]?1:0);
                registers[A] = (int)result;
                flags[0] = ((result>>32)&1) == 1;
                flags[1] = (int)result!=(long)registers[A]+registers[B]+(flags[0]?1:0);
                flags[2] = result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 4:         //ADC reg-imm
                result = Integer.toUnsignedLong(registers[A]) + Integer.toUnsignedLong(value)+(flags[0]?1:0);
                registers[A] = (int)result;
                flags[0] = ((result>>32)&1) == 1;
                flags[1] = (int)result!=(long)registers[A]+value+(flags[0]?1:0);
                flags[2] = result == 0;
                flags[3] = ((result>>31)&1) == 1;
                break;
            case 5:         //SUB reg-reg
                byteCount = 2;
                registers[A] = registers[A] - registers[B];
                break;
            case 6:         //SUB reg-imm
                registers[A] = registers[A] - value;
                break;
            case 7:         //SBC reg-reg
                byteCount = 2;
                registers[A] = registers[A] - registers[B] - (flags[0]?1:0);
                break;
            case 8:         //SBC reg-imm
                registers[A] = registers[A] - value - (flags[0]?1:0);
                break;
            case 9:         //AND reg-reg
                byteCount = 2;
                registers[A] = registers[A] & registers[B];
                break;
            case 10:        //AND reg-imm
                registers[A] = registers[A] & value;
                break;
            case 11:        //OR reg-reg
                byteCount = 2;
                registers[A] = registers[A] | registers[B];
                break;
            case 12:        //OR reg-imm
                registers[A] = registers[A] | value;
                break;
            case 13:        //XOR reg-reg
                byteCount = 2;
                registers[A] = registers[A] ^ registers[B];
                break;
            case 14:        //XOR reg-imm
                registers[A] = registers[A] ^ value;
                break;
            case 15:        //LSL reg-reg
                byteCount = 2;
                registers[A] = registers[A] << (registers[B]&0b11111);
                break;
            case 16:        //LSL reg-imm
                registers[A] = registers[A] << (value&0b11111);
                break;
            case 17:        //LSR reg-reg
                byteCount = 2;
                registers[A] = registers[A] >>> (registers[B]&0b11111);
                break;
            case 18:        //LSR reg-imm
                registers[A] = registers[A] >>> (value&0b11111);
                break;
            case 19:        //ASR reg-reg
                byteCount = 2;
                registers[A] = registers[A] >> (registers[B]&0b11111);
                break;
            case 20:        //ASR reg-imm
                registers[A] = registers[A] >> (value&0b11111);
                break;
            case 21:        //ROT reg-reg
                byteCount = 2;
                registers[A] = Integer.rotateLeft(registers[A],registers[B]&0b11111);
                break;
            case 22:        //ROT reg-imm
                registers[A] = Integer.rotateLeft(registers[A],value&0b11111);
                break;
            case 23:        //MLTL reg-reg
                byteCount = 2;
                registers[A] = (int) (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(registers[B]));
                break;
            case 24:        //MLTL reg-imm
                registers[A] = (int) (Integer.toUnsignedLong(registers[A])*Integer.toUnsignedLong(value));
                break;
            case 25:        //MLTH reg-reg
                break;
            case 26:        //MLTH reg-imm
                break;
            case 27:        //MLTHU reg-reg
                break;
            case 28:        //MLTHU reg-imm
                break;
            case 29:        //DIV reg-reg
                break;
            case 30:        //DIV reg-imm
                break;
            case 31:        //DIVU reg-reg
                break;
            case 32:        //DIVU reg-imm
                break;
            case 33:        //MOD reg-reg
                break;
            case 34:        //MOD reg-imm
                break;
            case 35:        //MODU reg-reg
                break;
            case 36:        //MODU reg-imm
                break;
            case 37:        //INC
                break;
            case 38:        //DEC
                break;
            case 39:        //NOT
                break;
            case 40:        //NEG
                break;
            case 41:        //TST
                break;
            case 42:        //BTST reg-reg
                break;
            case 43:        //BTST reg-imm
                break;
            case 44:        //CMP reg-reg
                break;
            case 45:        //CMP reg-imm
                break;
            case 46:        //ADSP
                break;
            case 47:        //ADPC
                break;
            case 48:        //JMP
                break;
            case 49:        //CALL
                break;
            case 50:        //RET
                break;
            case 51:        //BEQ
                break;
            case 52:        //BNE
                break;
            case 53:        //BLS
                break;
            case 54:        //BLE
                break;
            case 55:        //BGR
                break;
            case 56:        //BGE
                break;
            case 57:        //BLSU
                break;
            case 58:        //BLEU
                break;
            case 59:        //BGRU
                break;
            case 60:        //BGEU
                break;
            case 61:        //BOF
                break;
            case 62:        //BNO
                break;
            case 63:        //BPS
                break;
            case 64:        //BNG
                break;
            case 65:        //MOV reg-reg
                break;
            case 66:        //MOV reg-imm
                break;
            case 67:        //PSH
                break;
            case 68:        //POP
                break;
            case 69:        //PSHF
                break;
            case 70:        //POPF
                break;
            case 71:        //LDB addr
                break;
            case 72:        //LDB addr++
                break;
            case 73:        //LDB addr--
                break;
            case 74:        //LDBU addr
                break;
            case 75:        //LDBU addr++
                break;
            case 76:        //LDBU addr--
                break;
            case 77:        //LDH addr
                break;
            case 78:        //LDH addr++
                break;
            case 79:        //LDH addr--
                break;
            case 80:        //LDHU addr
                break;
            case 81:        //LDHU addr++
                break;
            case 82:        //LDHU addr--
                break;
            case 83:        //LDW addr
                break;
            case 84:        //LDW addr++
                break;
            case 85:        //LDW addr--
                break;
            case 86:        //STB addr
                break;
            case 87:        //STB addr++
                break;
            case 88:        //STB addr--
                break;
            case 89:        //STH addr
                break;
            case 90:        //STH addr++
                break;
            case 91:        //STH addr--
                break;
            case 92:        //STW addr
                break;
            case 93:        //STW addr++
                break;
            case 94:        //STW addr--
                break;
            case 95:        //CPB addr
                break;
            case 96:        //CPB addr++
                break;
            case 97:        //CPB addr--
                break;
            case 98:        //CPH addr
                break;
            case 99:        //CPH addr++
                break;
            case 100:       //CPH addr--
                break;
            case 101:       //CPW addr
                break;
            case 102:       //CPW addr++
                break;
            case 103:       //CPW addr--
                break;
            case 104:       //SEC
                break;
            case 105:       //SEO
                break;
            case 106:       //SEZ
                break;
            case 107:       //SEN
                break;
            case 108:       //SEI
                break;
            case 109:       //CLC
                break;
            case 110:       //CLO
                break;
            case 111:       //CLZ
                break;
            case 112:       //CLN
                break;
            case 113:       //CLI
                break;
            case 114:       //IRET
                break;
            case 115:       //WFI
                break;
            case 116:       //STP
                break;
            default:
                //todo pause sim and show error window
        }
        programCounter = byteCount;
    }




    public void kill() {
        this.running = false;
    }


}
