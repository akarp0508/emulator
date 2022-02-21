package akarp0508.gui;

import akarp0508.emulator.EmulationEngine;
import akarp0508.gui.components.EmulationPreviewPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class EmulatorWindow {
    JFrame jFrame;

    JLabel emulationClockLabel;
    JLabel emulationInstructionAddressLabel;

    JButton startButton;
    JButton resetButton;
    JButton pauseButton;
    JButton stepButton;
    SpinnerNumberModel clockLimitSNM;
    JButton clockLimitSetButton;

    SpinnerNumberModel jumpSNM;
    JButton jumpButton;

    JButton registerEditButton;
    JButton ramEditButton;
    JButton VRamEditButton;
    JButton ROMEditButton;

    EmulationPreviewPanel emulationPreviewPanel;

    EmulationEngine emulationEngine;


    public EmulatorWindow(){
        jFrame = new JFrame();
        BorderLayout bl = new BorderLayout();
        JPanel cp = new JPanel(bl);
        cp.setBackground(Values.BACKGROUND_COLOR);


        JPanel editGraphicPanel = new JPanel();
        editGraphicPanel.setPreferredSize(new Dimension(206,0));
        editGraphicPanel.setLayout(null);
        editGraphicPanel.setBackground(Values.BACKGROUND_COLOR);

        // making add graphic button
        emulationClockLabel = new JLabel("IPS: 0");

        emulationClockLabel.setBackground(Values.ELEMENTS_COLOR);
        emulationClockLabel.setForeground(Values.TEXT_COLOR);
        emulationClockLabel.setBounds(2,2,100,30);
        editGraphicPanel.add(emulationClockLabel);

        emulationInstructionAddressLabel = new JLabel("Inst: 0");

        emulationInstructionAddressLabel.setBackground(Values.ELEMENTS_COLOR);
        emulationInstructionAddressLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        emulationInstructionAddressLabel.setForeground(Values.TEXT_COLOR);
        emulationInstructionAddressLabel.setBounds(104,2,100,30);
        editGraphicPanel.add(emulationInstructionAddressLabel);

        startButton= new JButton("Start");
        //startButton.addActionListener(e-> deleteGraphic());

        startButton.setBackground(Values.ELEMENTS_COLOR);
        startButton.setForeground(Values.TEXT_COLOR);
        startButton.setBounds(2,32,100,30);

        editGraphicPanel.add(startButton);

        resetButton= new JButton("Reset");
        //resetButton.addActionListener(e-> deleteGraphic());

        resetButton.setBackground(Values.ELEMENTS_COLOR);
        resetButton.setForeground(Values.TEXT_COLOR);
        resetButton.setBounds(104,32,100,30);

        editGraphicPanel.add(resetButton);

        pauseButton= new JButton("Pauza");
        //startButton.addActionListener(e-> deleteGraphic());

        pauseButton.setBackground(Values.ELEMENTS_COLOR);
        pauseButton.setForeground(Values.TEXT_COLOR);
        pauseButton.setBounds(2,64,100,30);

        editGraphicPanel.add(pauseButton);

        stepButton= new JButton("Step");
        //resetButton.addActionListener(e-> deleteGraphic());

        stepButton.setBackground(Values.ELEMENTS_COLOR);
        stepButton.setForeground(Values.TEXT_COLOR);
        stepButton.setBounds(104,64,100,30);

        editGraphicPanel.add(stepButton);



        clockLimitSNM = new SpinnerNumberModel(0, 0, 15, 1);
        JSpinner clockLimitSpinner = new JSpinner(clockLimitSNM);
        //resetButton.addActionListener(e-> deleteGraphic());

        clockLimitSpinner.setBackground(Values.ELEMENTS_COLOR);
        clockLimitSpinner.setForeground(Values.TEXT_COLOR);
        clockLimitSpinner.setBounds(2,96,100,30);
        clockLimitSpinner.getEditor().getComponent(0).setBackground(Values.ELEMENTS_COLOR);
        clockLimitSpinner.getEditor().getComponent(0).setForeground(Values.TEXT_COLOR);
        clockLimitSpinner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        clockLimitSpinner.getComponent(0).setBackground(Values.ELEMENTS_COLOR);
        clockLimitSpinner.getComponent(1).setBackground(Values.ELEMENTS_COLOR);
        ((BasicArrowButton)clockLimitSpinner.getComponent(0)).setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((BasicArrowButton)clockLimitSpinner.getComponent(1)).setBorder(BorderFactory.createLineBorder(Color.BLACK));

        editGraphicPanel.add(clockLimitSpinner);

        clockLimitSetButton= new JButton("Ustaw IPS");
        //resetButton.addActionListener(e-> deleteGraphic());

        clockLimitSetButton.setBackground(Values.ELEMENTS_COLOR);
        clockLimitSetButton.setForeground(Values.TEXT_COLOR);
        clockLimitSetButton.setBounds(104,96,100,30);

        editGraphicPanel.add(clockLimitSetButton);



        jumpSNM = new SpinnerNumberModel(0, 0, 15, 1);
        JSpinner jumpSpinner = new JSpinner(jumpSNM);
        //resetButton.addActionListener(e-> deleteGraphic());

        jumpSpinner.setBackground(Values.ELEMENTS_COLOR);
        jumpSpinner.setForeground(Values.TEXT_COLOR);
        jumpSpinner.setBounds(2,150,100,30);
        jumpSpinner.getEditor().getComponent(0).setBackground(Values.ELEMENTS_COLOR);
        jumpSpinner.getEditor().getComponent(0).setForeground(Values.TEXT_COLOR);
        jumpSpinner.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jumpSpinner.getComponent(0).setBackground(Values.ELEMENTS_COLOR);
        jumpSpinner.getComponent(1).setBackground(Values.ELEMENTS_COLOR);
        ((BasicArrowButton)jumpSpinner.getComponent(0)).setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ((BasicArrowButton)jumpSpinner.getComponent(1)).setBorder(BorderFactory.createLineBorder(Color.BLACK));

        editGraphicPanel.add(jumpSpinner);

        jumpButton= new JButton("Skocz");
        //resetButton.addActionListener(e-> deleteGraphic());

        jumpButton.setBackground(Values.ELEMENTS_COLOR);
        jumpButton.setForeground(Values.TEXT_COLOR);
        jumpButton.setBounds(104,150,100,30);

        editGraphicPanel.add(jumpButton);





        registerEditButton= new JButton("Edytuj rejestry");
        //startButton.addActionListener(e-> deleteGraphic());

        registerEditButton.setBackground(Values.ELEMENTS_COLOR);
        registerEditButton.setForeground(Values.TEXT_COLOR);
        registerEditButton.setBounds(2,200,100,30);

        editGraphicPanel.add(registerEditButton);

        ramEditButton= new JButton("Edytuj RAM");
        //resetButton.addActionListener(e-> deleteGraphic());

        ramEditButton.setBackground(Values.ELEMENTS_COLOR);
        ramEditButton.setForeground(Values.TEXT_COLOR);
        ramEditButton.setBounds(104,200,100,30);

        editGraphicPanel.add(ramEditButton);

        VRamEditButton= new JButton("Edytuj VRAM");
        //startButton.addActionListener(e-> deleteGraphic());

        VRamEditButton.setBackground(Values.ELEMENTS_COLOR);
        VRamEditButton.setForeground(Values.TEXT_COLOR);
        VRamEditButton.setBounds(2,232,100,30);

        editGraphicPanel.add(VRamEditButton);

        ROMEditButton= new JButton("Edytuj ROM");
        //resetButton.addActionListener(e-> deleteGraphic());

        ROMEditButton.setBackground(Values.ELEMENTS_COLOR);
        ROMEditButton.setForeground(Values.TEXT_COLOR);
        ROMEditButton.setBounds(104,232,100,30);

        editGraphicPanel.add(ROMEditButton);

        cp.add(BorderLayout.EAST,editGraphicPanel);

        emulationPreviewPanel = new EmulationPreviewPanel();
        emulationPreviewPanel.setBackground(Values.BACKGROUND_COLOR);

        cp.add(BorderLayout.CENTER,emulationPreviewPanel);


        jFrame.setContentPane(cp);
        jFrame.setTitle("Emulator");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setSize(650,400);
        jFrame.setMinimumSize(new Dimension(650,400));
        jFrame.setLocationRelativeTo(null);
        URL systemResource = ClassLoader.getSystemResource("icon.png");
        ImageIcon img = new ImageIcon(systemResource);
        jFrame.setIconImage(img.getImage());
        jFrame.setVisible(true);

        jFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                emulationEngine.kill();
            }
        });

        emulationEngine = new EmulationEngine(emulationPreviewPanel,this);
        Thread emulationThread = new Thread(emulationEngine);
        emulationThread.start();

    }

    public void setActualIPS(long ips){
        emulationClockLabel.setText("IPS: "+ips);
    }
}
