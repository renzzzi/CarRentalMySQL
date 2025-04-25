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

public class ReturnCar implements Operation {
    private Database database;
    private JFrame frame;
    private JTextField carDetails, rentDate, rentHours, baseTotal, delayHours, finalTotal;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Return Car");
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel("Return Car", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        // Get active rentals
        ArrayList<Rent> rents = new ArrayList<>();
        try {
            String select = "SELECT * FROM `rents` WHERE `User` = '" + user.getID() + 
                          "' AND `Status` = '0';";
            ResultSet rs = database.getStatement().executeQuery(select);
            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                rent.setDateTime(rs.getString("DateTime"));
                rent.setHours(rs.getInt("Hours"));
                rent.setTotal(rs.getDouble("Total"));
                rent.setStatus(rs.getInt("Status"));

                ResultSet rs2 = database.getStatement()
                    .executeQuery("SELECT * FROM `cars` WHERE `ID` = '" + rs.getInt("Car") + "';");
                if (rs2.next()) {
                    Car car = new Car();
                    car.setID(rs2.getInt("ID"));
                    car.setBrand(rs2.getString("Brand"));
                    car.setModel(rs2.getString("Model"));
                    car.setColor(rs2.getString("Color"));
                    car.setPrice(rs2.getDouble("Price"));
                    rent.setCar(car);
                }
                rents.add(rent);
            }
        } catch (SQLException e) {
            showError(frame, e.getMessage());
            frame.dispose();
            return;
        }

        String[] rentIds = new String[rents.size() + 1];
        rentIds[0] = " ";
        for (int i = 0; i < rents.size(); i++) {
            rentIds[i + 1] = String.valueOf(rents.get(i).getID());
        }

        JComboBox rentCombo = new JComboBox(rentIds, 22);
        formPanel.add(createFieldPanel("Select Rental", rentCombo));

        // Rental Details Fields
        carDetails = createStyledField("");
        rentDate = createStyledField("");
        rentHours = createStyledField("");
        baseTotal = createStyledField("");
        delayHours = createStyledField("");
        finalTotal = createStyledField("");

        carDetails.setEditable(false);
        rentDate.setEditable(false);
        rentHours.setEditable(false);
        baseTotal.setEditable(false);
        delayHours.setEditable(false);
        finalTotal.setEditable(false);

        formPanel.add(createFieldPanel("Car Details", carDetails));
        formPanel.add(createFieldPanel("Rental Date", rentDate));
        formPanel.add(createFieldPanel("Rental Hours", rentHours));
        formPanel.add(createFieldPanel("Base Total", baseTotal));
        formPanel.add(createFieldPanel("Delay Hours", delayHours));
        formPanel.add(createFieldPanel("Final Total", finalTotal));

        rentCombo.addActionListener(e -> {
            String selectedId = rentCombo.getSelectedItem().toString();
            if (!selectedId.equals(" ")) {
                updateRentalDetails(rents.stream()
                    .filter(r -> r.getID() == Integer.parseInt(selectedId))
                    .findFirst()
                    .orElse(null));
            } else {
                clearFields();
            }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel", 22);
        JButton returnBtn = new JButton("Return Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        returnBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        returnBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> frame.dispose());

        returnBtn.addActionListener(e -> {
            if (rentCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a rental to return");
                return;
            }

            try {
                int rentId = Integer.parseInt(rentCombo.getSelectedItem().toString());
                Rent selectedRent = rents.stream()
                    .filter(r -> r.getID() == rentId)
                    .findFirst()
                    .orElse(null);

                if (selectedRent != null) {
                    String updateRent = "UPDATE `rents` SET `Status`='1' WHERE `ID` = '" + rentId + "';";
                    String updateCar = "UPDATE `cars` SET `Available`='0' WHERE `ID` = '" + 
                                     selectedRent.getCar().getID() + "';";
                    
                    database.getStatement().execute(updateRent);
                    database.getStatement().execute(updateCar);

                    double finalTotalAmount = calculateFinalTotal(selectedRent);
                    
                    JOptionPane.showMessageDialog(frame, 
                        String.format("Car returned successfully\nFinal Total: $%.2f", finalTotalAmount),
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(returnBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void updateRentalDetails(Rent rent) {
        if (rent != null) {
            carDetails.setText(String.format("%s %s (%s)", 
                rent.getCar().getBrand(), 
                rent.getCar().getModel(), 
                rent.getCar().getColor()));
            rentDate.setText(rent.getDateTime());
            rentHours.setText(String.valueOf(rent.getHours()));
            baseTotal.setText(String.format("$%.2f", rent.getTotal()));
            
            long delayedHrs = rent.getDelayedHours();
            delayHours.setText(String.valueOf(Math.max(0, delayedHrs)));
            
            double finalTotalAmount = calculateFinalTotal(rent);
            finalTotal.setText(String.format("$%.2f", finalTotalAmount));
        } else {
            clearFields();
        }
    }

    private double calculateFinalTotal(Rent rent) {
        long delayedHours = rent.getDelayedHours();
        if (delayedHours > 0) {
            return rent.getTotal() + (delayedHours * rent.getCar().getPrice() * 1.5);
        }
        return rent.getTotal();
    }

    private void clearFields() {
        carDetails.setText("");
        rentDate.setText("");
        rentHours.setText("");
        baseTotal.setText("");
        delayHours.setText("");
        finalTotal.setText("");
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

    private JTextField createStyledField(String initialText) {
        JTextField field = new JTextField(initialText);
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
