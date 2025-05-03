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
    private CustomTextField carDetails, rentDate, rentHours, baseTotal, delayHours, finalTotal;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Return Car");
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Return Car", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        ArrayList<Rent> rents = new ArrayList<>();
        try {
            String select = "SELECT r.*, c.Brand, c.Model, c.Color, c.Price, c.Available " +
                          "FROM rents r " +
                          "JOIN cars c ON r.Car = c.ID " +
                          "WHERE r.User = '" + user.getID() + "' " +
                          "AND r.Status = 0 " +
                          "AND c.Available = 1;";
            
            ResultSet rs = database.getStatement().executeQuery(select);
            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                rent.setDateTime(rs.getString("DateTime"));
                rent.setHours(rs.getInt("Hours"));
                rent.setTotal(rs.getDouble("Total"));
                rent.setStatus(rs.getInt("Status"));

                Car car = new Car();
                car.setID(rs.getInt("Car"));
                car.setBrand(rs.getString("Brand"));
                car.setModel(rs.getString("Model"));
                car.setColor(rs.getString("Color"));
                car.setPrice(rs.getDouble("Price"));
                car.setAvailable(rs.getInt("Available"));
                rent.setCar(car);
                
                rents.add(rent);
            }
            rs.close();

            if (rents.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "You don't have any cars to return.", 
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

        String[] rentIds = new String[rents.size() + 1];
        rentIds[0] = " ";
        for (int i = 0; i < rents.size(); i++) {
            rentIds[i + 1] = String.valueOf(rents.get(i).getID());
        }

        CustomComboBox rentCombo = new CustomComboBox(rentIds, 22);
        rentCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(createFieldPanel("Select Rental", rentCombo));

        carDetails = new CustomTextField(22);
        rentDate = new CustomTextField(22);
        rentHours = new CustomTextField(22);
        baseTotal = new CustomTextField(22);
        delayHours = new CustomTextField(22);
        finalTotal = new CustomTextField(22);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        carDetails.setPreferredSize(fieldSize);
        rentDate.setPreferredSize(fieldSize);
        rentHours.setPreferredSize(fieldSize);
        baseTotal.setPreferredSize(fieldSize);
        delayHours.setPreferredSize(fieldSize);
        finalTotal.setPreferredSize(fieldSize);

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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton cancelBtn = new CustomButton("Cancel", 22);
        CustomButton returnBtn = new CustomButton("Return Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        returnBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        returnBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        cancelBtn.setPreferredSize(buttonSize);
        returnBtn.setPreferredSize(buttonSize);

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
                    ResultSet checkRs = database.getStatement()
                        .executeQuery("SELECT r.Status, c.Available FROM rents r " +
                                    "JOIN cars c ON r.Car = c.ID " +
                                    "WHERE r.ID = '" + rentId + "';");
                    
                    if (checkRs.next()) {
                        if (checkRs.getInt("Status") != 0) {
                            showError(frame, "This rental has already been completed.");
                            frame.dispose();
                            return;
                        }
                        if (checkRs.getInt("Available") != 1) {
                            showError(frame, "This car has already been returned.");
                            frame.dispose();
                            return;
                        }
                    }
                    checkRs.close();

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
