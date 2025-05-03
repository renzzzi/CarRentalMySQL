import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CustomTable extends javax.swing.JTable {
    public CustomTable(String[][] data, String[] header) {
        super(new DefaultTableModel(data, header));
        setRowHeight(50);
        setBackground(ColorScheme.BACKGROUND);
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1, 1));

        DefaultTableModel tableModel = new DefaultTableModel(data, header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setModel(tableModel);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 16));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                if (isSelected) {
                    setBackground(ColorScheme.SECONDARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? ColorScheme.BACKGROUND : ColorScheme.SURFACE);
                    setForeground(ColorScheme.TEXT_PRIMARY);
                }
                return this;
            }
        };

        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 16));
                setBackground(ColorScheme.PRIMARY);
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return this;
            }
        };
        
        getTableHeader().setDefaultRenderer(headerRenderer);
        getTableHeader().setBorder(null);
        setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
        setGridColor(ColorScheme.BORDER);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
