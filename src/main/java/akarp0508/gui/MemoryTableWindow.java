package akarp0508.gui;


import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class MemoryTableWindow {
    private JTable table;
    private DefaultTableCellRenderer cellRenderer;

    public MemoryTableWindow() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new BorderLayout());
        String[][] rec = {
                { "0x00000000" , "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00", "0x00" }
        };
        String[] header = { "Address" , "0x0", "0x1", "0x2", "0x3", "0x4", "0x5", "0x6", "0x7", "0x8", "0x9", "0xA", "0xB", "0xC", "0xD", "0xE", "0xF"};

        table = new JTable(rec,header);
        panel.add(new JScrollPane(table));
        MyTableModel tableModel = new MyTableModel(rec,header);
        table.setModel(tableModel);

        JMenuBar menubar = new JMenuBar();

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        for(int i=0; i<16;i++)
            table.getColumnModel().getColumn(i+1).setPreferredWidth(5);
        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Read from file"));
        fileMenu.add(new JMenuItem("Write to file"));
        menubar.add(fileMenu);


        //table.addComponentListener(ComponentEvent e -> componentResized(e));

        frame.setResizable(false);
        panel.add(menubar,BorderLayout.NORTH);
        frame.add(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public void componentResized(ComponentEvent e){

    }



    private final class MyTableModel extends DefaultTableModel {
        private final ArrayList<TableModelListener> listener = new ArrayList<>();

        private MyTableModel(String[][] rec, String[] header) {
            super(rec,header);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listener.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listener.remove(l);
        }

    }
}
