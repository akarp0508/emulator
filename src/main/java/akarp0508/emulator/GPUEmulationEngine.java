package akarp0508.emulator;

import akarp0508.gui.components.EmulationPreviewPanel;

import java.awt.*;

public class GPUEmulationEngine {

private Image image;
private final EmulationPreviewPanel emulationPreviewPanel;



    public GPUEmulationEngine(EmulationPreviewPanel emulationPreviewPanel) {
        this.emulationPreviewPanel = emulationPreviewPanel;
    }

    private void updateImage(){
        emulationPreviewPanel.setImage(image);
    }
}
