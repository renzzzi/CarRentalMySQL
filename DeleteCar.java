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
    private CustomTextField brand, model, color, year, price;
    private Database database;
    private JFrame frame;

    @Override
    public void operation(Database database, JFrame f, User user) {
        this.database = database;

        frame = new JFrame("Delete Car");
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Delete Car", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

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

        CustomComboBox idCombo = new CustomComboBox(ids, 22);
        idCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        
        brand = new CustomTextField(22);
        model = new CustomTextField(22);
        color = new CustomTextField(22);
        year = new CustomTextField(22);
        price = new CustomTextField(22);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        brand.setPreferredSize(fieldSize);
        model.setPreferredSize(fieldSize);
        color.setPreferredSize(fieldSize);
        year.setPreferredSize(fieldSize);
        price.setPreferredSize(fieldSize);

        brand.setEditable(false);
        model.setEditable(false);
        color.setEditable(false);
        year.setEditable(false);
        price.setEditable(false);

        formPanel.add(createFieldPanel("Car ID", idCombo));
        formPanel.add(createFieldPanel("Brand", brand));
        formPanel.add(createFieldPanel("Model", model));
        formPanel.add(createFieldPanel("Color", color));
        formPanel.add(createFieldPanel("Year", year));
        formPanel.add(createFieldPanel("Price per Hour", price));

        idCombo.addActionListener(e -> updateData(idCombo.getSelectedItem().toString()));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton cancelBtn = new CustomButton("Cancel", 22);
        CustomButton deleteBtn = new CustomButton("Delete Car", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        deleteBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        deleteBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        cancelBtn.setPreferredSize(buttonSize);
        deleteBtn.setPreferredSize(buttonSize);

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
