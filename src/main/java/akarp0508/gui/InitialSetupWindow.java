package akarp0508.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class InitialSetupWindow {

    JFrame jf;
    JTextField tf;
    JComboBox<String> cb;
    JLabel label;

    public InitialSetupWindow() {
        jf = new JFrame();
        jf.setBounds(0, 0, 300, 130);
        tf = new JTextField(25);
        jf.setLayout(null);
        jf.setResizable(false);
        JLabel tl = new JLabel("Choose RAM size");
        tl.setBounds(10,10,290,20);
        jf.add(tl);
        tf.setBounds(10,30,200,20);
        jf.add(tf);
        label = new JLabel("");
        label.setBounds(10,60,200,20);
        jf.add(label);
        cb = new JComboBox<String>();
        cb.addItem("B");
        cb.addItem("KiB");
        cb.addItem("MiB");
        cb.addItem("GiB");
        cb.setBounds(220,30,60,20);
        jf.add(cb);

        JButton jb = new JButton("OK");
        jb.setBounds(220,60,60,20);
        jb.addActionListener(e->okClicked());
        jf.add(jb);


        label.setForeground(Color.red);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    private void okClicked(){
        boolean onlyDigits = true;
        String text = tf.getText();
        int multiplier = (int)Math.pow(1024, cb.getSelectedIndex());
        for(int i=0;i<text.length();i++){
            onlyDigits = (text.charAt(i)>='0' && text.charAt(i)<='9') && onlyDigits;
        }
        if(onlyDigits){
            long RAMsize = Long.parseLong(text)*multiplier;
            if(RAMsize-1<=Integer.MAX_VALUE){
                new EmulatorWindow((int)(RAMsize>>2));
                jf.setVisible(false); //you can't see me!
                jf.dispose(); //Destroy the JFrame object
            }
            else{
                label.setText("Too large number");
            }
        } else {
            label.setText("Use only digits");
        }
    }
}
