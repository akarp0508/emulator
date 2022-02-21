package akarp0508.emulator;

public class Instruction {
    private final Instructions inst;
    private final int[] parameters;

    public Instruction(Instructions inst, int[] parameters) {
        this.inst = inst;
        this.parameters = parameters;
    }

    public Instructions getInst() {
        return inst;
    }

    public int[] getParameters() {
        return parameters;
    }
}
