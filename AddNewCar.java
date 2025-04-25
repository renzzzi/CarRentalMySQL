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

        JLabel title = new JLabel("Add New Car", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        JTextField brand = createStyledField("Brand");
        JTextField model = createStyledField("Model");
        JTextField color = createStyledField("Color");
        JTextField year = createStyledField("Year");
        JTextField price = createStyledField("Price per Hour");

        formPanel.add(createFieldPanel("Brand", brand));
        formPanel.add(createFieldPanel("Model", model));
        formPanel.add(createFieldPanel("Color", color));
        formPanel.add(createFieldPanel("Year", year));
        formPanel.add(createFieldPanel("Price per Hour", price));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel", 22);  // Fixed constructor
        JButton confirmBtn = new JButton("Confirm", 22);  // Fixed constructor

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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text, 22);  // Fixed constructor
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
