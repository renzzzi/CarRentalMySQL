import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AddNewCar implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("Add New Car");
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Add New Car", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        CustomTextField brand = new CustomTextField(22);
        CustomTextField model = new CustomTextField(22);
        CustomTextField color = new CustomTextField(22);
        CustomTextField year = new CustomTextField(22);
        CustomTextField price = new CustomTextField(22);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        brand.setPreferredSize(fieldSize);
        model.setPreferredSize(fieldSize);
        color.setPreferredSize(fieldSize);
        year.setPreferredSize(fieldSize);
        price.setPreferredSize(fieldSize);

        formPanel.add(createFieldPanel("Brand", brand));
        formPanel.add(createFieldPanel("Model", model));
        formPanel.add(createFieldPanel("Color", color));
        formPanel.add(createFieldPanel("Year", year));
        formPanel.add(createFieldPanel("Price per Hour", price));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton cancelBtn = new CustomButton("Cancel", 22);
        CustomButton confirmBtn = new CustomButton("Confirm", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        confirmBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        confirmBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        cancelBtn.setPreferredSize(buttonSize);
        confirmBtn.setPreferredSize(buttonSize);

        cancelBtn.addActionListener(e -> frame.dispose());

        confirmBtn.addActionListener(e -> {
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

            int yearInt;
            double priceDouble;
            try {
                yearInt = Integer.parseInt(year.getText());
            } catch (Exception ex) {
                showError(frame, "Year must be a number");
                return;
            }
            try {
                priceDouble = Double.parseDouble(price.getText());
            } catch (Exception ex) {
                showError(frame, "Price must be a number");
                return;
            }

            int available = 0;

            try {
                ResultSet rs = database.getStatement().executeQuery("SELECT COUNT(*) FROM `cars`;");
                rs.next();
                int ID = rs.getInt("COUNT(*)");

                String insert = "INSERT INTO `cars`(`ID`, `Brand`, `Model`, `Color`, `Year`, `Price`, `Available`) VALUES " +
                        "('" + ID + "','" + brand.getText() + "','" + model.getText() + "','" + color.getText() + "'," +
                        "'" + yearInt + "','" + priceDouble + "','" + available + "');";
                database.getStatement().execute(insert);
                
                JOptionPane.showMessageDialog(frame, "Car added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();

            } catch (SQLException er) {
                showError(frame, er.getMessage());
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
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
