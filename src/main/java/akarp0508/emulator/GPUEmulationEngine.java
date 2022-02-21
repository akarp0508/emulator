package akarp0508.emulator;

import akarp0508.gui.components.EmulationPreviewPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class GPUEmulationEngine implements Runnable{
    private final EmulationPreviewPanel emulationPreviewPanel;

    private Color[] spritePalette = new Color[256];
    private Color[] bg0Palette = new Color[256];
    private Color[] bg1Palette = new Color[256];
    private Color[] bg2Palette = new Color[256];
    private Color[] fbPalette = new Color[16];

    private byte[] spritesTextures = new byte[256*64];
    private byte[] tilesTextures = new byte[256*64];
    private byte[] letterTextures = new byte[256*64];

    // gdy litera to kolor spod 0xfe a jezeli jest to bierze z 0xff;

    private Sprite[] sprites = new Sprite[256];

    private Tile[] bg0 = new Tile[1536];
    private Tile[] bg1 = new Tile[1536];
    private Tile[] bg2 = new Tile[1536];

    private byte[] framebuffer = new byte[256*192];

    private boolean spriteDisable = false;
    private boolean renderingDisable = false;

    private Color fixedColor = Color.BLACK;

    private byte BG0fineXScroll;
    private byte BG1fineXScroll;
    private byte BG2fineXScroll;
    private byte BG0fineYScroll;
    private byte BG1fineYScroll;
    private byte BG2fineYScroll;
    private byte BG0blockXScroll;
    private byte BG1blockXScroll;
    private byte BG2blockXScroll;
    private byte BG0blockYScroll;
    private byte BG1blockYScroll;
    private byte BG2blockYScroll;

    private byte[] inputs = new byte[5];

    private boolean[] colorEffectsBG0 = new boolean[4];
    private boolean[] colorEffectsBG1 = new boolean[4];
    private boolean[] colorEffectsBG2 = new boolean[4];
    private boolean[] colorEffectsSprite = new boolean[4];
    private boolean[] colorEffectsFixedColor = new boolean[4];
    private boolean[] colorEffectsFrameBuffer = new boolean[4];

    private boolean framebufferPageSelect;

    public GPUEmulationEngine(EmulationPreviewPanel epp){
        emulationPreviewPanel = epp;
    }


    public void run(){
        Image image = new BufferedImage(256,192,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,256,192);

        if(!renderingDisable){
            for(int i=inputs.length;i-->0;){
                byte input=inputs[i];
                switch (Math.floorMod(input,8)){
                    case 0:
                    case 6:
                    case 7:
                        graphics.setColor(Color.BLACK);
                        graphics.fillRect(0,0,256,192);
                        break;
                    case 1:
                        drawBG(graphics,(byte)0);
                        break;
                    case 2:
                        drawBG(graphics,(byte)1);
                        break;
                    case 3:
                        drawBG(graphics,(byte)2);
                        break;
                    case 4:
                        drawFixedColor();
                        break;

                }
                //drawSprites();
            }
        }
        emulationPreviewPanel.setImage(image);
    }

    private void drawFixedColor(){

    }

    private void drawBG(Graphics g,byte bg){
        Tile[] tiles = new Tile[1536];
        int xOffset=0;
        int yOffset=0;

        switch (bg){
            case 0:
                tiles = bg0;
                xOffset = 8 * BG0blockXScroll + BG0fineXScroll;
                yOffset = 8 * BG0blockYScroll + BG0fineYScroll;
                break;
            case 1:
                tiles = bg1;
                xOffset = 8 * BG1blockXScroll + BG1fineXScroll;
                yOffset = 8 * BG1blockYScroll + BG1fineYScroll;
                break;
            case 2:
                tiles = bg2;
                xOffset = 8 * BG2blockXScroll + BG2fineXScroll;
                yOffset = 8 * BG2blockYScroll + BG2fineYScroll;
                break;
        }
        Tile tile;
        for(int fx=0;fx<64;fx++){
            for(int fy=0;fy<32;fy++){
                tile = tiles[fx+fy*64];
                if(tile!=null) {
                    Image tileImg = tile.getImage(bg);
                    g.drawImage(tileImg,fx*8-xOffset,fy*8-yOffset,null);
                }
            }
        }
    }

    public void saveData(int inst,int adr,int data){
        switch (inst){
            case 1:
                saveToReg(adr,data);
                break;
            case 2:
                saveToSprite(adr, data);
                break;
            case 3:
                saveToSpriteTextures(adr, data);
                break;
            case 4:
                saveToSpritePalette(adr, data);
                break;
            case 5:
                saveToBG0(adr, data);
                break;
            case 6:
                saveToBG1(adr, data);
                break;
            case 7:
                saveToBG2(adr, data);
                break;
            case 8:
                saveToBGTextures(adr, data);
                break;
            case 9:
                saveToBG0Palette(adr, data);
                break;
            case 10:
                saveToBG1Palette(adr, data);
                break;
            case 11:
                saveToBG2Palette(adr, data);
                break;
            case 12:
                saveToFB(adr, data);
                break;
            case 13:
                saveToFBPalette(adr, data);
                break;
        }
    }

    private void saveToFBPalette(int adr, int data){
        float r = (data>>8)%16/15f;
        float g = (data>>4)%16/15f;
        float b = (data)%16/15f;
        fbPalette[Math.floorMod(adr,fbPalette.length)] = new Color(r,g,b);
    }

    private void saveToFB(int adr, int data){
        framebuffer[Math.floorMod(adr,framebuffer.length)] = (byte)data;
    }

    private void saveToBG0Palette(int adr, int data){
        float r = (data>>8)%16/15f;
        float g = (data>>4)%16/15f;
        float b = (data)%16/15f;
        bg0Palette[Math.floorMod(adr,bg0Palette.length)] = new Color(r,g,b);
    }

    private void saveToBG1Palette(int adr, int data){
        float r = (data>>8)%16/15f;
        float g = (data>>4)%16/15f;
        float b = (data)%16/15f;
        bg1Palette[Math.floorMod(adr,bg1Palette.length)] = new Color(r,g,b);
    }

    private void saveToBG2Palette(int adr, int data){
        float r = (data>>8)%16/15f;
        float g = (data>>4)%16/15f;
        float b = (data)%16/15f;
        bg2Palette[Math.floorMod(adr,bg2Palette.length)] = new Color(r,g,b);
    }

    private void saveToBGTextures(int adr, int data){
        tilesTextures[Math.floorMod(adr,tilesTextures.length)] = (byte)data;
    }

    private void saveToBG0(int adr, int data){
        bg0[Math.floorMod(adr,bg0.length)] = new Tile(data,this);
    }

    private void saveToBG1(int adr, int data){
        bg1[Math.floorMod(adr,bg1.length)] = new Tile(data,this);
    }

    private void saveToBG2(int adr, int data){
        bg2[Math.floorMod(adr,bg2.length)] = new Tile(data,this);
    }

    private void saveToSpritePalette(int adr, int data){ // 0bRRRRGGGGBBBB
        float r = (data>>8)%16/15f;
        float g = (data>>4)%16/15f;
        float b = (data)%16/15f;
        spritePalette[Math.floorMod(adr,256)] = new Color(r,g,b);
    }

    private void saveToSpriteTextures(int adr, int data){
        spritesTextures[Math.floorMod(adr,256)] = (byte) data;
    }

    private void saveToSprite(int adr, int data){
        sprites[Math.floorMod(adr,256)] = new Sprite(data,this);
    }

    private void saveToReg(int adr,int data){
        switch (adr){
            case 0:
                spriteDisable = data%2 == 1;
                break;
            case 1:
                renderingDisable = data%2 == 1;
                break;
            case 2: // 0bRRRRGGGGBBBB
                int r = (data>>8)%16;
                int g = (data>>4)%16;
                int b = data%16;
                fixedColor = new Color(16*r,16*g,16*b);
                break;
            case 3:
                BG0fineXScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 4:
                BG1fineXScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 5:
                BG2fineXScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 6:
                BG0fineYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 7:
                BG1fineYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 8:
                BG2fineYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 9:
                BG0blockXScroll = (byte)(Math.floorMod(data, 5));
                break;
            case 10:
                BG1blockXScroll = (byte)(Math.floorMod(data, 5));
                break;
            case 11:
                BG2blockXScroll = (byte)(Math.floorMod(data, 5));
                break;
            case 12:
                BG0blockYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 13:
                BG1blockYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 14:
                BG2blockYScroll = (byte)(Math.floorMod(data, 3));
                break;
            case 15:
                inputs[0] =(byte)(data % 8);
                break;
            case 16:
                inputs[1] =(byte)(data % 8);
                break;
            case 17:
                inputs[2] =(byte)(data % 8);
                break;
            case 18:
                inputs[3] =(byte)(data % 8);
                break;
            case 19:
                inputs[4] =(byte)(data % 8);
                break;
            case 20:
                colorEffectsBG0[0] = data % 2 == 1;
                colorEffectsBG0[1] = (data>>1) % 2 == 1;
                colorEffectsBG0[2] = (data>>2) % 2 == 1;
                colorEffectsBG0[3] = (data>>3) % 2 == 1;
                break;
            case 21:
                colorEffectsBG1[0] = data % 2 == 1;
                colorEffectsBG1[1] = (data>>1) % 2 == 1;
                colorEffectsBG1[2] = (data>>2) % 2 == 1;
                colorEffectsBG1[3] = (data>>3) % 2 == 1;
                break;
            case 22:
                colorEffectsBG2[0] = data % 2 == 1;
                colorEffectsBG2[1] = (data>>1) % 2 == 1;
                colorEffectsBG2[2] = (data>>2) % 2 == 1;
                colorEffectsBG2[3] = (data>>3) % 2 == 1;
                break;
            case 23:
                colorEffectsSprite[0] = data % 2 == 1;
                colorEffectsSprite[1] = (data>>1) % 2 == 1;
                colorEffectsSprite[2] = (data>>2) % 2 == 1;
                colorEffectsSprite[3] = (data>>3) % 2 == 1;
                break;
            case 24:
                colorEffectsFixedColor[0] = data % 2 == 1;
                colorEffectsFixedColor[1] = (data>>1) % 2 == 1;
                colorEffectsFixedColor[2] = (data>>2) % 2 == 1;
                colorEffectsFixedColor[3] = (data>>3) % 2 == 1;
                break;
            case 25:
                colorEffectsFrameBuffer[0] = data % 2 == 1;
                colorEffectsFrameBuffer[1] = (data>>1) % 2 == 1;
                colorEffectsFrameBuffer[2] = (data>>2) % 2 == 1;
                colorEffectsFrameBuffer[3] = (data>>3) % 2 == 1;
                break;
            case 26:
                framebufferPageSelect = data%2==1;
                break;
        }
    }

    public byte[] getLetterTexture(byte index){
        int i = Byte.toUnsignedInt(index);
        return Arrays.copyOfRange(letterTextures,i*64,i*64+64);
    }

    public byte[] getTileTexture(byte index){
        int i = Byte.toUnsignedInt(index);
        return Arrays.copyOfRange(tilesTextures,i*64,i*64+64);
    }

    public Color getBGColor(byte index,byte BG){
        switch (BG){
            case 0:
                return bg0Palette[Byte.toUnsignedInt(index)];
            case 1:
                return bg1Palette[Byte.toUnsignedInt(index)];
            case 2:
                return bg2Palette[Byte.toUnsignedInt(index)];
        }
        return null;
    }

    public byte[] getSpriteTexture(byte index){
        int i = Byte.toUnsignedInt(index);
        return Arrays.copyOfRange(spritesTextures,i*64,i*64+64);
    }

    public Color getSpriteColor(byte index){
        return spritePalette[Byte.toUnsignedInt(index)];
    }

    public Color[] getSpritePalette() {
        return spritePalette;
    }

    public void setSpritePalette(Color[] spritePalette) {
        this.spritePalette = spritePalette;
    }

    public Color[] getBg0Palette() {
        return bg0Palette;
    }

    public void setBg0Palette(Color[] bg0Palette) {
        this.bg0Palette = bg0Palette;
    }

    public Color[] getBg1Palette() {
        return bg1Palette;
    }

    public void setBg1Palette(Color[] bg1Palette) {
        this.bg1Palette = bg1Palette;
    }

    public Color[] getBg2Palette() {
        return bg2Palette;
    }

    public void setBg2Palette(Color[] bg2Palette) {
        this.bg2Palette = bg2Palette;
    }

    public Color[] getFbPalette() {
        return fbPalette;
    }

    public void setFbPalette(Color[] fbPalette) {
        this.fbPalette = fbPalette;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public void setSprites(Sprite[] sprites) {
        this.sprites = sprites;
    }

    public static void main(String[] args){

    }
}
