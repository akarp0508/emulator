package akarp0508.emulator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {
    private short x;
    private byte y;
    private byte textureNumber;
    private boolean active;
    private boolean flipX;
    private boolean flipY;
    private boolean effects;
    private byte priorityOverBG; // 2 bits
    private boolean priorityOverSprites;
    private GPUEmulationEngine gpu;

    public Sprite(int input, GPUEmulationEngine parent) {
        x = (short) (input % Math.pow(2,9));
        input>>=9;
        y = (byte) (input % Math.pow(2,8));
        input>>=8;
        textureNumber = (byte)input;
        input>>=8;
        active = input % 2 == 1;
        input>>=1;
        flipX = input % 2 == 1;
        input>>=1;
        flipY = input % 2 == 1;
        input>>=1;
        effects = input % 2 == 1;
        input>>=1;
        priorityOverBG = (byte)(input % 2);
        input>>=2;
        priorityOverSprites = input % 2 == 1;
        gpu = parent;
    }

    public Image getImage(){
        BufferedImage bi = new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        byte[] texture = gpu.getSpriteTexture(textureNumber);

        for(byte xx = 0; xx<8 ; xx++){
            for(byte yy = 0;yy<8;yy++){
                Color c = gpu.getSpriteColor(texture[yy*8+xx]);
                g.setColor(c);
                byte px = flipX ? (byte) (8 - xx) : xx;
                byte py = flipX ? (byte) (8 - yy) : yy;
                g.fillRect(px,py,1,1);
            }
        }
        return bi;
    }
}
