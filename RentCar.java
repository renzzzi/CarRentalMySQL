import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RentCar implements Operation {
    private JTextField brand, model, color, year, price;
    private Database database;
    private JFrame frame;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Rent a Car");
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel("Rent a Car", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        // Car ID Selection
        ArrayList<Integer> idsArray = new ArrayList<>();
        try {
            ResultSet rs0 = database.getStatement().executeQuery("SELECT `ID`, `Available` FROM `cars`;");
            while (rs0.next()) {
                if (rs0.getInt("Available") < 2) idsArray.add(rs0.getInt("ID"));
            }
        } catch (Exception e0) {
            showError(frame, e0.getMessage());
            frame.dispose();
            return;
        }

        String[] ids = new String[idsArray.size() + 1];
        ids[0] = " ";
        for (int i = 0; i < idsArray.size(); i++) {
            ids[i + 1] = String.valueOf(idsArray.get(i));
        }

        JComboBox idCombo = new JComboBox(ids, 22);
        formPanel.add(createFieldPanel("Select Car", idCombo));

        // Car Details Fields
        brand = createStyledField("Brand");
        model = createStyledField("Model");
        color = createStyledField("Color");
        year = createStyledField("Year");
        price = createStyledField("Price per Hour");
        JTextField hours = createStyledField("Number of Hours");

        brand.setEditable(false);
        model.setEditable(false);
        color.setEditable(false);
        year.setEditable(false);
        price.setEditable(false);

        formPanel.add(createFieldPanel("Brand", brand));
        formPanel.add(createFieldPanel("Model", model));
        formPanel.add(createFieldPanel("Color", color));
        formPanel.add(createFieldPanel("Year", year));
        formPanel.add(createFieldPanel("Price per Hour", price));
        formPanel.add(createFieldPanel("Rental Hours", hours));

        idCombo.addActionListener(e -> updateData(idCombo.getSelectedItem().toString()));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton viewCarsBtn = new JButton("View All Cars", 22);
        JButton cancelBtn = new JButton("Cancel", 22);
        JButton rentBtn = new JButton("Rent Car", 22);

        viewCarsBtn.setBackground(ColorScheme.SECONDARY);
        cancelBtn.setBackground(ColorScheme.ACCENT);
        rentBtn.setBackground(ColorScheme.PRIMARY);

        viewCarsBtn.setForeground(Color.WHITE);
        cancelBtn.setForeground(Color.WHITE);
        rentBtn.setForeground(Color.WHITE);

        viewCarsBtn.addActionListener(e -> new ViewCars().operation(database, frame, user));
        cancelBtn.addActionListener(e -> frame.dispose());

        rentBtn.addActionListener(e -> {
            if (idCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a car");
                return;
            }
            if (hours.getText().isEmpty()) {
                showError(frame, "Please enter rental hours");
                return;
            }

            try {
                int hoursInt = Integer.parseInt(hours.getText());
                
                ResultSet rs0 = database.getStatement()
                    .executeQuery("SELECT * FROM `cars` WHERE `ID` = '" + 
                                idCombo.getSelectedItem().toString() + "';");
                rs0.next();
                
                if (rs0.getInt("Available") != 0) {
                    showError(frame, "This car is not available");
                    return;
                }

                ResultSet rs1 = database.getStatement()
                    .executeQuery("SELECT COUNT(*) FROM `rents`;");
                rs1.next();
                int ID = rs1.getInt("COUNT(*)");

                double total = rs0.getDouble("Price") * hoursInt;

                Rent rent = new Rent();
                String insert = "INSERT INTO `rents`(`ID`, `User`, `Car`, `DateTime`, `Hours`, " +
                    "`Total`, `Status`) VALUES ('" + ID + "','" + user.getID() + "'," +
                    "'" + rs0.getInt("ID") + "','" + rent.getDateTime() + "','" + hoursInt + "'," +
                    "'" + total + "','0');";

                database.getStatement().execute(insert);
                
                String updateCar = "UPDATE `cars` SET `Available`='1' WHERE `ID` = '" + 
                                 rs0.getInt("ID") + "';";
                database.getStatement().execute(updateCar);

                JOptionPane.showMessageDialog(frame, 
                    String.format("Car rented successfully\nTotal Cost: $%.2f", total),
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (NumberFormatException ex) {
                showError(frame, "Hours must be a valid number");
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(viewCarsBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(rentBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void updateData(String ID) {
        if (ID.equals(" ")) {
            brand.setText("");
            model.setText("");
            color.setText("");
            year.setText("");
            price.setText("");
        } else {
            try {
                ResultSet rs1 = database.getStatement()
                    .executeQuery("SELECT * FROM `cars` WHERE `ID` = '" + ID + "';");
                if (rs1.next()) {
                    brand.setText(rs1.getString("Brand"));
                    model.setText(rs1.getString("Model"));
                    color.setText(rs1.getString("Color"));
                    year.setText(String.valueOf(rs1.getInt("Year")));
                    price.setText(String.format("$%.2f", rs1.getDouble("Price")));
                }
            } catch (Exception e1) {
                showError(frame, e1.getMessage());
                frame.dispose();
            }
        }
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(ColorScheme.BACKGROUND);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBackground(ColorScheme.SURFACE);
        field.setForeground(ColorScheme.TEXT_PRIMARY);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
