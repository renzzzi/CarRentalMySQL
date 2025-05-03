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

        CustomLabel title = new CustomLabel("Edit Profile", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        CustomTextField firstName = new CustomTextField(22);
        firstName.setText(user.getFirstName());
        
        CustomTextField lastName = new CustomTextField(22);
        lastName.setText(user.getLastName());
        
        CustomTextField email = new CustomTextField(22);
        email.setText(user.getEmail());
        
        CustomTextField phoneNumber = new CustomTextField(22);
        phoneNumber.setText(user.getPhoneNumber());

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        firstName.setPreferredSize(fieldSize);
        lastName.setPreferredSize(fieldSize);
        email.setPreferredSize(fieldSize);
        phoneNumber.setPreferredSize(fieldSize);

        formPanel.add(createFieldPanel("First Name", firstName));
        formPanel.add(createFieldPanel("Last Name", lastName));
        formPanel.add(createFieldPanel("Email", email));
        formPanel.add(createFieldPanel("Phone Number", phoneNumber));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton cancelBtn = new CustomButton("Cancel", 22);
        CustomButton saveBtn = new CustomButton("Save Changes", 22);

        cancelBtn.setBackground(ColorScheme.ACCENT);
        saveBtn.setBackground(ColorScheme.PRIMARY);
        
        cancelBtn.setForeground(Color.WHITE);
        saveBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        cancelBtn.setPreferredSize(buttonSize);
        saveBtn.setPreferredSize(buttonSize);

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
