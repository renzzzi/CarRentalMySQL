import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChangePassword implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("Change Password");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(f);
        frame.getContentPane().setBackground(ColorScheme.BACKGROUND);
        frame.setLayout(new BorderLayout(0, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        CustomLabel title = new CustomLabel("Change Password", 32);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title, BorderLayout.CENTER);
        
        frame.add(titlePanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        CustomPasswordField oldPassword = new CustomPasswordField(22);
        CustomPasswordField newPassword = new CustomPasswordField(22);
        CustomPasswordField confirmPassword = new CustomPasswordField(22);
        
        formPanel.add(createFieldPanel("Old Password", oldPassword));
        formPanel.add(createFieldPanel("New Password", newPassword));
        formPanel.add(createFieldPanel("Confirm Password", confirmPassword));
        
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
            if (oldPassword.getPassword().length == 0) {
                showError(frame, "Old Password cannot be empty");
                return;
            }
            if (newPassword.getPassword().length == 0) {
                showError(frame, "New Password cannot be empty");
                return;
            }
            if (confirmPassword.getPassword().length == 0) {
                showError(frame, "Confirm Password cannot be empty");
                return;
            }
            if (!String.valueOf(oldPassword.getPassword()).equals(user.getPassword())) {
                showError(frame, "Incorrect Password");
                return;
            }
            if (!String.valueOf(newPassword.getPassword()).equals(String.valueOf(confirmPassword.getPassword()))) {
                showError(frame, "Passwords don't match");
                return;
            }
            
            try {
                String update = "UPDATE `users` SET `Password`='" + 
                    String.valueOf(newPassword.getPassword()) + 
                    "' WHERE `ID` = '" + user.getID() + "';";
                database.getStatement().execute(update);
                JOptionPane.showMessageDialog(frame, "Password changed successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                user.setPassword(String.valueOf(newPassword.getPassword()));
                frame.dispose();
            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);
        formPanel.add(buttonPanel);
        
        frame.add(formPanel, BorderLayout.CENTER);
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
