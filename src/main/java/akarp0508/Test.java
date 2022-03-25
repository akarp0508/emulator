package akarp0508;

import akarp0508.gui.MemoryTableWindow;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryTableWindow::new);
    }
}
