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
    private Database database;
    private JFrame frame;
    private CustomTextField carDetails, price, hours, total;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Rent Car");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Rent Car", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        ArrayList<Car> cars = new ArrayList<>();
        try {
            String select = "SELECT * FROM cars WHERE Available = 0;";
            ResultSet rs = database.getStatement().executeQuery(select);
            while (rs.next()) {
                Car car = new Car();
                car.setID(rs.getInt("ID"));
                car.setBrand(rs.getString("Brand"));
                car.setModel(rs.getString("Model"));
                car.setColor(rs.getString("Color"));
                car.setPrice(rs.getDouble("Price"));
                car.setAvailable(rs.getInt("Available"));
                cars.add(car);
            }
            rs.close();

            if (cars.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "No cars are available for rent at the moment.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                return;
            }
        } catch (SQLException e) {
            showError(frame, e.getMessage());
            frame.dispose();
            return;
        }

        String[] carIds = new String[cars.size() + 1];
        carIds[0] = " ";
        for (int i = 0; i < cars.size(); i++) {
            carIds[i + 1] = String.valueOf(cars.get(i).getID());
        }

        CustomComboBox carCombo = new CustomComboBox(carIds, 22);
        carCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(createFieldPanel("Select Car", carCombo));

        carDetails = new CustomTextField(22);
        price = new CustomTextField(22);
        hours = new CustomTextField(22);
        total = new CustomTextField(22);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        carDetails.setPreferredSize(fieldSize);
        price.setPreferredSize(fieldSize);
        hours.setPreferredSize(fieldSize);
        total.setPreferredSize(fieldSize);

        carDetails.setEditable(false);
        price.setEditable(false);
        total.setEditable(false);

        formPanel.add(createFieldPanel("Car Details", carDetails));
        formPanel.add(createFieldPanel("Price per Hour", price));
        formPanel.add(createFieldPanel("Hours", hours));
        formPanel.add(createFieldPanel("Total", total));

        carCombo.addActionListener(e -> {
            String selectedId = carCombo.getSelectedItem().toString();
            if (!selectedId.equals(" ")) {
                Car selectedCar = cars.stream()
                    .filter(c -> c.getID() == Integer.parseInt(selectedId))
                    .findFirst()
                    .orElse(null);
                updateCarDetails(selectedCar);
            } else {
                clearFields();
            }
        });

        hours.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    if (!hours.getText().isEmpty()) {
                        int hrs = Integer.parseInt(hours.getText());
                        double pricePerHour = Double.parseDouble(price.getText().substring(1));
                        total.setText(String.format("$%.2f", hrs * pricePerHour));
                    } else {
                        total.setText("");
                    }
                } catch (NumberFormatException ex) {
                    total.setText("");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton cancelBtn = new CustomButton("Cancel", 22);
        CustomButton rentBtn = new CustomButton("Rent Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        rentBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        rentBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        cancelBtn.setPreferredSize(buttonSize);
        rentBtn.setPreferredSize(buttonSize);

        cancelBtn.addActionListener(e -> frame.dispose());

        rentBtn.addActionListener(e -> {
            if (carCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a car");
                return;
            }
            if (hours.getText().isEmpty()) {
                showError(frame, "Please enter number of hours");
                return;
            }

            try {
                int hrs = Integer.parseInt(hours.getText());
                if (hrs <= 0) {
                    showError(frame, "Hours must be greater than 0");
                    return;
                }

                Car selectedCar = cars.stream()
                    .filter(c -> c.getID() == Integer.parseInt(carCombo.getSelectedItem().toString()))
                    .findFirst()
                    .orElse(null);

                if (selectedCar != null) {
                    ResultSet checkRs = database.getStatement()
                        .executeQuery("SELECT Available FROM cars WHERE ID = '" + selectedCar.getID() + "';");
                    if (checkRs.next() && checkRs.getInt("Available") != 0) {
                        showError(frame, "This car is no longer available.");
                        frame.dispose();
                        return;
                    }
                    checkRs.close();

                    double totalAmount = hrs * selectedCar.getPrice();
                    
                    String insert = "INSERT INTO `rents`(`User`, `Car`, `DateTime`, `Hours`, `Total`, `Status`) VALUES " +
                            "('" + user.getID() + "','" + selectedCar.getID() + "',NOW(),'" + hrs + "','" + totalAmount + "','0');";
                    String update = "UPDATE `cars` SET `Available`='1' WHERE `ID` = '" + selectedCar.getID() + "';";
                    
                    database.getStatement().execute(insert);
                    database.getStatement().execute(update);
                    
                    JOptionPane.showMessageDialog(frame, 
                        String.format("Car rented successfully\nTotal: $%.2f", totalAmount),
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }
            } catch (NumberFormatException ex) {
                showError(frame, "Invalid number of hours");
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(rentBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void updateCarDetails(Car car) {
        if (car != null) {
            carDetails.setText(String.format("%s %s (%s)", 
                car.getBrand(), car.getModel(), car.getColor()));
            price.setText(String.format("$%.2f", car.getPrice()));
            if (!hours.getText().isEmpty()) {
                try {
                    int hrs = Integer.parseInt(hours.getText());
                    total.setText(String.format("$%.2f", hrs * car.getPrice()));
                } catch (NumberFormatException e) {
                    total.setText("");
                }
            }
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        carDetails.setText("");
        price.setText("");
        hours.setText("");
        total.setText("");
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(ColorScheme.BACKGROUND);
        
        CustomLabel label = new CustomLabel(labelText, 14);
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
