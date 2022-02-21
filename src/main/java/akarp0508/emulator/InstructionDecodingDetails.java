package akarp0508.emulator;

import akarp0508.assembler.InstructionDetails;
import akarp0508.assembler.ParameterDetails;

public class InstructionDecodingDetails {
    private final String base;
    private final ParameterDetails[] parameterDetails;
    private final Instructions inst;

    public InstructionDecodingDetails(InstructionDetails details, Instructions name){
        inst = name;
        parameterDetails = details.getParametersDetails();
        StringBuilder baseBuilder = new StringBuilder(details.getBase());
        for(ParameterDetails pd:parameterDetails){
            int sb = pd.getStartBit();
            int eb = pd.getEndBit();
            StringBuilder fill = new StringBuilder();
            for(int i=0;i<eb-sb;i++)
                fill.append("2");
            baseBuilder.replace(sb,eb, fill.toString());
        }
        base = baseBuilder.toString();
    }

    public Instruction decodeInstruction(String input){
        int[] parameters = new int[parameterDetails.length];
        for(int i=0;i<parameterDetails.length;i++){
            ParameterDetails pd = parameterDetails[i];
            parameters[i]=pd.decode(input);
        }
        return new Instruction(inst,parameters);
    }

    public String getBase() {
        return base;
    }

    public ParameterDetails[] getParameterDetails() {
        return parameterDetails;
    }

    public Instructions getInst() {
        return inst;
    }
}
