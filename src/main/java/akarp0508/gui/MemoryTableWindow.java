package akarp0508.gui;

import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class MemoryTableWindow {
    JFrame jf;
    JTable jt;

    public MemoryTableWindow() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBorder(BorderFactory.createTitledBorder(
        //        BorderFactory.createEtchedBorder(), "ODI Rankings", TitledBorder.CENTER, TitledBorder.TOP));
        String[][] rec = {
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" }
        };
        String[] header = { "Rank", "Player", "Country" ,"a","a","a","a"};

        JTable table = new JTable(rec,header);
        panel.add(new JScrollPane(table));
        MyTableModel tableModel = new MyTableModel(rec,header);

        //making menubar todo: add listeners
        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Read from file"));
        fileMenu.add(new JMenuItem("Write to file"));
        menubar.add(fileMenu);


        JMenu formatMenu = new JMenu("Values format");
        formatMenu.add(new JMenuItem("Binary"));
        formatMenu.add(new JMenuItem("Decimal"));
        formatMenu.add(new JMenuItem("Hexadecimal"));
        menubar.add(formatMenu);

        panel.add(menubar,BorderLayout.NORTH);
        frame.add(panel);
        table.setModel(tableModel);
        //frame.setSize(550, 400);
        frame.setVisible(true);
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
