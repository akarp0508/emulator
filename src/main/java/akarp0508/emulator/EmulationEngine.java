package akarp0508.emulator;

import akarp0508.gui.EmulatorWindow;
import akarp0508.gui.components.EmulationPreviewPanel;

import java.util.Random;

public class EmulationEngine implements Runnable{
    private final EmulatorWindow window;

    private int[] registers = new int[8];

    private int stackPointer;
    private int programCounter;
    private int flags;

    private boolean running = true;


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
            Instruction currentInstruction;
            if(currentInstructionIndex<instructions.length)
                currentInstruction= instructions[currentInstructionIndex];
            else
                currentInstruction= new Instruction(Instructions.ADD, new int[]{0, 0, 0});
            currentInstructionIndex=(currentInstructionIndex+1)%16384;

            executeInstruction(currentInstruction);

            actualIPS++;
            if(System.nanoTime()-lastTime>1000000000){
                window.setActualIPS(actualIPS);
                actualIPS=0;
                lastTime = System.nanoTime();
            }
        }
    }

    private void executeInstruction(Instruction instruction){
        int[] p = instruction.getParameters();
        switch(instruction.getInst()){
            case ADD:
                registers[p[0]]=registers[p[1]]+registers[p[2]];
                break;
            case SUB:
                registers[p[0]]=registers[p[1]]-registers[p[2]];
                break;
            case MUL:
                registers[p[0]]=registers[p[1]]*registers[p[2]];
                break;
            case NEG:
                registers[p[0]]=-registers[p[1]];
                break;
            case AND:
                registers[p[0]]=registers[p[1]]&registers[p[2]];
                break;
            case OR:
                registers[p[0]]=registers[p[1]]|registers[p[2]];
                break;
            case XOR:
                registers[p[0]]=registers[p[1]]^registers[p[2]];
                break;
            case NOT:
                registers[p[0]]=~registers[p[1]];
                break;
            case SLT:
                registers[p[0]]=registers[p[1]]<registers[p[2]] ? 1 : 0;
                break;
            case SLTU:
                registers[p[0]]=Integer.toUnsignedLong(registers[p[1]])<Integer.toUnsignedLong(registers[p[2]]) ? 1 : 0;
                break;
            case RUPW:
                registers[p[0]]=(int)((Integer.toUnsignedLong(registers[p[1]])&0x0000ffff)+(Integer.toUnsignedLong(registers[p[2]])&0xffff0000));
                break;
            case SHL:
                registers[p[0]]=registers[p[1]] << registers[p[2]];
                break;
            case SHR:
                registers[p[0]]=registers[p[1]] >>> registers[p[2]];
                break;
            case SHA:
                registers[p[0]]=registers[p[1]] >> registers[p[2]];
                break;
            case ROT:
                registers[p[0]]=Integer.rotateRight(registers[p[1]], registers[p[2]]);
                break;
            case RND:
                registers[p[0]]=random.nextInt();
                break;
            case ADDI:
                registers[p[0]]=registers[p[1]]+p[2];
                break;
            case SUBI:
                registers[p[0]]=registers[p[1]]-p[2];
                break;
            case MULI:
                registers[p[0]]=registers[p[1]]*p[2];
                break;
            case ANDI:
                registers[p[0]]=registers[p[1]]&p[2];
                break;
            case ORI:
                registers[p[0]]=registers[p[1]]|p[2];
                break;
            case XORI:
                registers[p[0]]=registers[p[1]]^p[2];
                break;
            case SLTI:
                registers[p[0]]=registers[p[1]]<p[2] ? 1 : 0;
                break;
            case SLTUI:
                registers[p[0]]=Integer.toUnsignedLong(registers[p[1]])<Integer.toUnsignedLong(p[2]) ? 1 : 0;
                break;
            case RUPWI:
                registers[p[0]]=(int)((Integer.toUnsignedLong(registers[p[1]])&0x0000ffff)+(Integer.toUnsignedLong(p[2])&0xffff0000));
                break;
            case SHLI:
                registers[p[0]]=registers[p[1]] << p[2];
                break;
            case SHRI:
                registers[p[0]]=registers[p[1]] >>> p[2];
                break;
            case SHAI:
                registers[p[0]]=registers[p[1]] >> p[2];
                break;
            case ROTI:
                registers[p[0]]=Integer.rotateRight(registers[p[1]], p[2]);
                break;
            case BEQ:
                currentInstructionIndex = registers[p[1]] == registers[p[2]] ? p[0] : currentInstructionIndex;
                break;
            case BNEQ:
                currentInstructionIndex = registers[p[1]] != registers[p[2]] ? p[0] : currentInstructionIndex;
                break;
            case BLT:
                currentInstructionIndex = registers[p[1]] < registers[p[2]] ? p[0] : currentInstructionIndex;
                break;
            case BGE:
                currentInstructionIndex = registers[p[1]] >= registers[p[2]] ? p[0] : currentInstructionIndex;
                break;
            case BLTU:
                currentInstructionIndex = Integer.toUnsignedLong(registers[p[1]]) < Integer.toUnsignedLong(registers[p[2]]) ? p[0] : currentInstructionIndex;
                break;
            case BGEU:
                currentInstructionIndex = Integer.toUnsignedLong(registers[p[1]]) >= Integer.toUnsignedLong(registers[p[2]]) ? p[0] : currentInstructionIndex;
                break;
            case BGR:
                currentInstructionIndex = registers[p[1]] > registers[p[2]] ? p[0] : currentInstructionIndex;
                break;
            case BGRU:
                currentInstructionIndex = Integer.toUnsignedLong(registers[p[1]]) > Integer.toUnsignedLong(registers[p[2]]) ? p[0] : currentInstructionIndex;
                break;
            case LLIM:
                registers[p[0]] = p[1];
                break;
            case LUIM:
                registers[p[0]] &= 0x0000ffff;
                registers[p[0]] |= p[1] << 16;
                break;
            case JMP:
                currentInstructionIndex = registers[p[0]]+p[1];
                //todo
                break;
            case JMPL:
                registers[p[0]]=currentInstructionIndex;
                currentInstructionIndex = registers[p[1]]+p[2];
                //todo
                break;
            case LD1: //ram1[registers[p[1]]+p[2]]
                registers[p[0]] &= 0xfffffffe;
                registers[p[0]] |= ram1[Math.floorMod((registers[p[1]]+p[2]),ram1.length)] ? 1 : 0;
                break;
            case LD4: //ram4[registers[p[1]]+p[2]]
                registers[p[0]] = ram4[Math.floorMod((registers[p[1]]+p[2]),ram4.length)];
                break;
            case LD8: //ram8[registers[p[1]]+p[2]]
                registers[p[0]] = ram8[Math.floorMod((registers[p[1]]+p[2]),ram8.length)];
                break;
            case LD16: //ram8[registers[p[1]]+p[2]]
                registers[p[0]] = ram16[Math.floorMod((registers[p[1]]+p[2]),ram16.length)];
                break;
            case LD32: //ram8[registers[p[1]]+p[2]]
                registers[p[0]] = ram32[Math.floorMod((registers[p[1]]+p[2]),ram32.length)];
                break;
            case LDU4:
                registers[p[0]] = ram4[Math.floorMod((registers[p[1]]+p[2]),ram4.length)] & 0xf;
                break;
            case LDU8:
                registers[p[0]] = ram8[Math.floorMod((registers[p[1]]+p[2]),ram8.length)] & 0xff;
                break;
            case LDU16:
                registers[p[0]] = ram16[Math.floorMod((registers[p[1]]+p[2]),ram16.length)] & 0xffff;
                break;
            case STR1:
                ram1[Math.floorMod((registers[p[1]]+p[2]),ram1.length)] = (registers[p[0]] & 0b1) == 1;
                break;
            case STR4:
                ram4[Math.floorMod((registers[p[1]]+p[2]),ram4.length)] = (byte) (registers[p[0]] & 0xf);
                break;
            case STR8:
                ram8[Math.floorMod((registers[p[1]]+p[2]),ram8.length)] = (byte) (registers[p[0]] & 0xff);
                break;
            case STR16:
                ram16[Math.floorMod((registers[p[1]]+p[2]),ram16.length)] = (short)(registers[p[0]] & 0xffff);
                break;
            case STR32:
                ram32[Math.floorMod((registers[p[1]]+p[2]),ram32.length)] = registers[p[0]];
                break;
            case SDIO:
                gpu.saveData(registers[p[1]],registers[p[2]],registers[p[3]]);
                gpuThread = new Thread(gpu);
                gpuThread.start();
                break;
            case RCIO:
                //todo
                break;
        }
        registers[0]=0;
    }

    public void kill() {
        this.running = false;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public void setInstructions(Instruction[] instructions) {
        this.instructions = instructions;
    }

    public int getCurrentInstructionIndex() {
        return currentInstructionIndex;
    }

    public void setCurrentInstructionIndex(int currentInstructionIndex) {
        this.currentInstructionIndex = currentInstructionIndex;
    }

    public int[] getRegisters() {
        return registers;
    }

    public void setRegisters(int[] registers) {
        this.registers = registers;
    }

    public int[] getRam32() {
        return ram32;
    }

    public void setRam32(int[] ram32) {
        this.ram32 = ram32;
    }

    public short[] getRam16() {
        return ram16;
    }

    public void setRam16(short[] ram16) {
        this.ram16 = ram16;
    }

    public byte[] getRam8() {
        return ram8;
    }

    public void setRam8(byte[] ram8) {
        this.ram8 = ram8;
    }

    public byte[] getRam4() {
        return ram4;
    }

    public void setRam4(byte[] ram4) {
        this.ram4 = ram4;
    }

    public boolean[] getRam1() {
        return ram1;
    }

    public void setRam1(boolean[] ram1) {
        this.ram1 = ram1;
    }
}
