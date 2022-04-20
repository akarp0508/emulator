package akarp0508.emulator.GPU;

import akarp0508.emulator.CPU.EmulationEngine;
import akarp0508.emulator.CPU.RAMDataField;
import akarp0508.gui.components.EmulationPreviewPanel;

import java.awt.*;

public class GPUEmulationEngine {

private Image image;
private final EmulationPreviewPanel emulationPreviewPanel;
    private final VRAMDataField VRAM = new VRAMDataField(544);
    private final EmulationEngine cpuEmulationEngine;

    private int currentWidth;
    private int currentHeight;
    private int currentBitsPerPixel;


    public GPUEmulationEngine(EmulationPreviewPanel emulationPreviewPanel, EmulationEngine cpuEmulationEngine) {
        this.emulationPreviewPanel = emulationPreviewPanel;
        this.cpuEmulationEngine = cpuEmulationEngine;
    }

    public VRAMDataField getVRAM() {
        return VRAM;
    }

    private void updateImage(){
        emulationPreviewPanel.setImage(image);
    }

    public void drawFrame(){
        if(VRAM.getMode()==0){
            drawTextImage();
        }
    }

    private void drawTextImage(){

    }

    private int getPageIndex(){
        if(VRAM.getFlags())
    }

    private enum ScreenModes
    {
        MODE1(160,100,1),
        MODE2(160,100,4),
        MODE3(160,100,8),

        MODE4(320,200,1),
        MODE5(320,200,4),
        MODE6(320,200,8),

        MODE7(640,400,1),
        MODE8(640,400,4),

        MODE9(1280,800,1);

        private final int height;
        private final int width;
        private final int bitsPerPixel;

        ScreenModes(int height, int width, int bitsPerPixel) {
            this.height = height;
            this.width = width;
            this.bitsPerPixel = bitsPerPixel;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public int getBitsPerPixel() {
            return bitsPerPixel;
        }
    }
}
