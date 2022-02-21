package akarp0508.emulator;

public class PICEmulationEngine{
    private EmulationEngine emulationEngine;
    private int[] picRAM = new int[1];
    private int maska;
    private int retReg;

    public PICEmulationEngine(EmulationEngine emulationEngine) {
        this.emulationEngine = emulationEngine;
    }

    public void inputFromCPU(int inst, int adr, int data){
        switch(inst){
            case 0:
                if(inst<picRAM.length)
                    picRAM[adr] = data;
                break;
            case 1:
                //todo zapis masek
                break;
            case 2:
                retReg = data;
        }
    }
}
