package akarp0508.emulator;

import akarp0508.assembler.InstructionDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstructionDecoder {
    private final InstructionDecodingDetails[] instructionsDetails = new InstructionDecodingDetails[Instructions.values().length];

    public InstructionDecoder() {
        Map<String, InstructionDetails> instructionDetailsDictionary = new HashMap<>();
        try {
            URL path = ClassLoader.getSystemResource("newInstructionsDetails.json");


            //File file = Paths.get(path.toURI()).toFile();
            //BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(path.openStream()));

            Gson gson = new Gson();
            // convert the json string back to object
            Type type = new TypeToken<Map<String, InstructionDetails>>() {
            }.getType();
            instructionDetailsDictionary = gson.fromJson(br, type);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Set<String> keys = instructionDetailsDictionary.keySet();
        int i=0;
        for(String key:keys){
            try{
                Instructions inst = Instructions.valueOf(key);
                instructionsDetails[i]=new InstructionDecodingDetails(instructionDetailsDictionary.get(key),inst);
                i++;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public Instruction[] decodeInstructions(String[] input){
        Instruction[] instructions = new Instruction[input.length];
        for(int i=0;i<input.length;i++){
            instructions[i] = decodeSingleInstruction(input[i]);
        }
        return  instructions;
    }

    private Instruction decodeSingleInstruction(String bin){
        int index = getInstructionIndex(bin);
        InstructionDecodingDetails idd = instructionsDetails[index];
        return idd.decodeInstruction(bin);
    }

    private int getInstructionIndex(String bin){
        int ii=0;
        for(InstructionDecodingDetails idd:instructionsDetails){
            String base = idd.getBase();
            boolean isGood=true;
            for(int i=0;i<32;i++){
                char a = base.charAt(i);
                char b = bin.charAt(i);
                if(a!='2'){
                    if(a!=b){
                        isGood=false;
                    }
                }
            }
            if(isGood)
                return ii;
            ii++;
        }
        return -1;
    }
}
