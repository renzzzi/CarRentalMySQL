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

public class DeleteCar implements Operation {
    private JTextField brand, model, color, year, price;
    private Database database;
    private JFrame frame;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Delete Car");
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel("Delete Car", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 15));
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
        idCombo.setBackground(ColorScheme.SURFACE);
        idCombo.setForeground(ColorScheme.TEXT_PRIMARY);
        idCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        ((JLabel)idCombo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(createFieldPanel("Car ID", idCombo));

        // Car Details Fields
        brand = createStyledField("Brand");
        model = createStyledField("Model");
        color = createStyledField("Color");
        year = createStyledField("Year");
        price = createStyledField("Price per Hour");

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

        idCombo.addActionListener(e -> updateData(idCombo.getSelectedItem().toString()));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel", 22);
        JButton deleteBtn = new JButton("Delete Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        deleteBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        deleteBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> frame.dispose());

        deleteBtn.addActionListener(e -> {
            if (idCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a car to delete");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to delete this car?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String update = "UPDATE `cars` SET `Available`='2' WHERE `ID` = '" + 
                                  idCombo.getSelectedItem().toString() + "';";
                    database.getStatement().execute(update);
                    JOptionPane.showMessageDialog(frame, "Car deleted successfully", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } catch (SQLException ex) {
                    showError(frame, ex.getMessage());
                }
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(deleteBtn);
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
