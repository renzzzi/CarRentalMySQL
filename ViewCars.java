import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ViewCars implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("Available Cars");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.BACKGROUND);
        
        JLabel title = new JLabel("Car Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        titlePanel.add(title, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        String[] header = {
            "ID", "Brand", "Model", "Color", "Year", "Price", "Status"
        };

        // Fetch car data
        ArrayList<Car> cars = new ArrayList<>();
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `cars`;");
            while (rs.next()) {
                Car car = new Car();
                car.setID(rs.getInt("ID"));
                car.setBrand(rs.getString("Brand"));
                car.setModel(rs.getString("Model"));
                car.setColor(rs.getString("Color"));
                car.setYear(rs.getInt("Year"));
                car.setPrice(rs.getDouble("Price"));
                int available = rs.getInt("Available");
                if (available < 2) {
                    car.setAvailable(available);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

        // Prepare table data
        String[][] carData = new String[cars.size()][header.length];
        for (int i = 0; i < cars.size(); i++) {
            Car c = cars.get(i);
            carData[i][0] = String.valueOf(c.getID());
            carData[i][1] = c.getBrand();
            carData[i][2] = c.getModel();
            carData[i][3] = c.getColor();
            carData[i][4] = String.valueOf(c.getYear());
            carData[i][5] = String.format("$%.2f", c.getPrice());
            carData[i][6] = c.isAvailable() == 0 ? "Available" : "Rented";
        }

        // Create and style table
        JTable table = new JTable(carData, header, ColorScheme.PRIMARY, ColorScheme.SURFACE);
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

    private void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
