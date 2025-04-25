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

public class UpdateCar implements Operation {
    private JTextField brand, model, color, year, price;
    private Database database;
    private JFrame frame;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Update Car");
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel("Update Car Details", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        // Get available cars
        ArrayList<Integer> idsArray = new ArrayList<>();
        try {
            ResultSet rs0 = database.getStatement()
                .executeQuery("SELECT `ID`, `Available` FROM `cars`;");
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

        // Create form fields
        JComboBox idCombo = new JComboBox(ids, 22);
        brand = createStyledField("Brand");
        model = createStyledField("Model");
        color = createStyledField("Color");
        year = createStyledField("Year");
        price = createStyledField("Price per Hour");

        formPanel.add(createFieldPanel("Select Car", idCombo));
        formPanel.add(createFieldPanel("Brand", brand));
        formPanel.add(createFieldPanel("Model", model));
        formPanel.add(createFieldPanel("Color", color));
        formPanel.add(createFieldPanel("Year", year));
        formPanel.add(createFieldPanel("Price per Hour", price));

        idCombo.addActionListener(e -> updateData(idCombo.getSelectedItem().toString()));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel", 22);
        JButton updateBtn = new JButton("Update Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        updateBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        updateBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> frame.dispose());

        updateBtn.addActionListener(e -> {
            if (idCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a car to update");
                return;
            }
            if (brand.getText().isEmpty()) {
                showError(frame, "Brand cannot be empty");
                return;
            }
            if (model.getText().isEmpty()) {
                showError(frame, "Model cannot be empty");
                return;
            }
            if (color.getText().isEmpty()) {
                showError(frame, "Color cannot be empty");
                return;
            }
            if (year.getText().isEmpty()) {
                showError(frame, "Year cannot be empty");
                return;
            }
            if (price.getText().isEmpty()) {
                showError(frame, "Price cannot be empty");
                return;
            }

            try {
                int yearInt = Integer.parseInt(year.getText());
                double priceDouble = Double.parseDouble(price.getText());

                String update = "UPDATE `cars` SET " +
                    "`Brand`='" + brand.getText() + "'," +
                    "`Model`='" + model.getText() + "'," +
                    "`Color`='" + color.getText() + "'," +
                    "`Year`='" + yearInt + "'," +
                    "`Price`='" + priceDouble + "' " +
                    "WHERE `ID` = '" + idCombo.getSelectedItem().toString() + "';";

                database.getStatement().execute(update);
                
                JOptionPane.showMessageDialog(frame, 
                    "Car updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                
            } catch (NumberFormatException ex) {
                showError(frame, "Invalid year or price format");
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(updateBtn);
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
                    price.setText(String.valueOf(rs1.getDouble("Price")));
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
