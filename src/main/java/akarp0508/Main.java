package akarp0508;

import akarp0508.gui.InitialSetupWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InitialSetupWindow();
            }
        });
    }
}
