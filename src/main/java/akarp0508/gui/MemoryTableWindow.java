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
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "ODI Rankings", TitledBorder.CENTER, TitledBorder.TOP));
        String[][] rec = {
                { "1", "Steve", "AUS" },
                { "2", "Virat", "IND" },
                { "3", "Kane", "NZ" },
                { "4", "David", "AUS" },
                { "5", "Ben", "ENG" },
                { "6", "Eion", "ENG" },
        };
        String[] header = { "Rank", "Player", "Country" };

        JTable table = new JTable(rec,header);
        panel.add(new JScrollPane(table));

        MyTableModel tableModel = new MyTableModel(rec,header);

        frame.add(panel);
        table.setModel(tableModel);
        frame.setSize(550, 400);
        frame.setVisible(true);
    }



    private final class MyTableModel extends DefaultTableModel {
        private final ArrayList<TableModelListener> listener = new ArrayList<>();

        private MyTableModel(String[][] rec, String[] header) {
            super(rec,header);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Long.class;
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
