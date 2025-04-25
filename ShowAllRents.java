import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShowAllRents implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("All Rentals");
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.BACKGROUND);
        
        JLabel title = new JLabel("All Rentals", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        titlePanel.add(title, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Table Headers
        String[] header = {
            "ID", "Name", "Email", "Phone", "Car ID", "Car Details", 
            "Date Time", "Hours", "Total", "Status"
        };

        // Fetch Rentals Data
        ArrayList<Rent> rents = new ArrayList<>();
        ArrayList<Integer> carIDs = new ArrayList<>();
        ArrayList<Integer> userIDs = new ArrayList<>();
        
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `rents`;");
            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                userIDs.add(rs.getInt("User"));
                carIDs.add(rs.getInt("Car"));
                rent.setDateTime(rs.getString("DateTime"));
                rent.setHours(rs.getInt("Hours"));
                rent.setTotal(rs.getDouble("Total"));
                rent.setStatus(rs.getInt("Status"));
                rents.add(rent);
            }

            // Fetch associated user and car data
            for (int j = 0; j < rents.size(); j++) {
                Rent r = rents.get(j);

                // Get user data
                ResultSet rs2 = database.getStatement()
                    .executeQuery("SELECT * FROM `users` WHERE `ID` = '" + userIDs.get(j) + "';");
                if (rs2.next()) {
                    User u = new Client();
                    u.setID(rs2.getInt("ID"));
                    u.setFirstName(rs2.getString("FirstName"));
                    u.setLastName(rs2.getString("LastName"));
                    u.setEmail(rs2.getString("Email"));
                    u.setPhoneNumber(rs2.getString("PhoneNumber"));
                    r.setUser(u);
                }

                // Get car data
                ResultSet rs3 = database.getStatement()
                    .executeQuery("SELECT * FROM `cars` WHERE `ID` = '" + carIDs.get(j) + "';");
                if (rs3.next()) {
                    Car car = new Car();
                    car.setID(rs3.getInt("ID"));
                    car.setBrand(rs3.getString("Brand"));
                    car.setModel(rs3.getString("Model"));
                    car.setColor(rs3.getString("Color"));
                    car.setPrice(rs3.getDouble("Price"));
                    r.setCar(car);
                }
            }

        } catch (SQLException e) {
            showError(frame, e.getMessage());
            frame.dispose();
            return;
        }

        // Prepare table data
        String[][] rentData = new String[rents.size()][header.length];
        for (int i = 0; i < rents.size(); i++) {
            Rent r = rents.get(i);
            rentData[i][0] = String.valueOf(r.getID());
            rentData[i][1] = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            rentData[i][2] = r.getUser().getEmail();
            rentData[i][3] = r.getUser().getPhoneNumber();
            rentData[i][4] = String.valueOf(r.getCar().getID());
            rentData[i][5] = String.format("%s %s (%s)", 
                r.getCar().getBrand(), r.getCar().getModel(), r.getCar().getColor());
            rentData[i][6] = r.getDateTime();
            rentData[i][7] = String.valueOf(r.getHours());
            rentData[i][8] = String.format("$%.2f", r.getTotal());
            rentData[i][9] = r.getStatusToString();
        }

        // Create and style table
        JTable table = new JTable(rentData, header, ColorScheme.PRIMARY, ColorScheme.SURFACE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(ColorScheme.BACKGROUND);
        scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add close button
        JButton closeButton = new JButton("Close", 22);
        closeButton.setBackground(ColorScheme.ACCENT);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JTable createStyledTable(String[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(data, columnNames, ColorScheme.PRIMARY, ColorScheme.SURFACE);
        table.setBackground(ColorScheme.SURFACE);
        table.setForeground(ColorScheme.TEXT_PRIMARY);
        table.setGridColor(ColorScheme.BORDER);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 10));
        table.setShowGrid(true);

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(ColorScheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
        ((DefaultTableCellRenderer)header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set preferred column widths
        int[] columnWidths = {50, 150, 200, 120, 60, 200, 150, 70, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Add row highlighting
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                table.clearSelection();
            }
        });

        return table;
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
