import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EditUserData implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("Edit Profile");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel title = new JLabel("Edit Profile", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        // Create fields
        JTextField firstName = createStyledField("First Name");
        firstName.setText(user.getFirstName());
        
        JTextField lastName = createStyledField("Last Name");
        lastName.setText(user.getLastName());
        
        JTextField email = createStyledField("Email");
        email.setText(user.getEmail());
        
        JTextField phoneNumber = createStyledField("Phone Number");
        phoneNumber.setText(user.getPhoneNumber());

        // Add field panels
        formPanel.add(createFieldPanel("First Name", firstName));
        formPanel.add(createFieldPanel("Last Name", lastName));
        formPanel.add(createFieldPanel("Email", email));
        formPanel.add(createFieldPanel("Phone Number", phoneNumber));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelBtn = new JButton("Cancel", 22);
        JButton saveBtn = new JButton("Save Changes", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        saveBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> frame.dispose());

        saveBtn.addActionListener(e -> {
            if (firstName.getText().isEmpty()) {
                showError(frame, "First Name cannot be empty");
                return;
            }
            if (lastName.getText().isEmpty()) {
                showError(frame, "Last Name cannot be empty");
                return;
            }
            if (email.getText().isEmpty()) {
                showError(frame, "Email cannot be empty");
                return;
            }
            if (phoneNumber.getText().isEmpty()) {
                showError(frame, "Phone Number cannot be empty");
                return;
            }

            try {
                String update = "UPDATE `users` SET " +
                    "`FirstName`='" + firstName.getText() + "'," +
                    "`LastName`='" + lastName.getText() + "'," +
                    "`Email`='" + email.getText() + "'," +
                    "`PhoneNumber`='" + phoneNumber.getText() + "' " +
                    "WHERE `ID` = '" + user.getID() + "';";

                database.getStatement().execute(update);
                
                // Update user object
                user.setFirstName(firstName.getText());
                user.setLastName(lastName.getText());
                user.setEmail(email.getText());
                user.setPhoneNumber(phoneNumber.getText());

                JOptionPane.showMessageDialog(frame, 
                    "Profile updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
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

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
