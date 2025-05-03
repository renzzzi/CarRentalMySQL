import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AddNewAccount implements Operation {
    private int accType;

    public AddNewAccount(int accType) {
        this.accType = accType;
    }

    @Override
    public void operation(Database database, JFrame f, User u) {
        JFrame frame = new JFrame("Create New Account");
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        CustomLabel title = new CustomLabel("Create New Account", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        CustomTextField firstName = new CustomTextField(22);
        CustomTextField lastName = new CustomTextField(22);
        CustomTextField email = new CustomTextField(22);
        CustomTextField phoneNumber = new CustomTextField(22);
        CustomPasswordField password = new CustomPasswordField(22);
        CustomPasswordField confirmPassword = new CustomPasswordField(22);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 45);
        firstName.setPreferredSize(fieldSize);
        lastName.setPreferredSize(fieldSize);
        email.setPreferredSize(fieldSize);
        phoneNumber.setPreferredSize(fieldSize);
        password.setPreferredSize(fieldSize);
        confirmPassword.setPreferredSize(fieldSize);

        formPanel.add(createFieldPanel("First Name", firstName));
        formPanel.add(createFieldPanel("Last Name", lastName));
        formPanel.add(createFieldPanel("Email", email));
        formPanel.add(createFieldPanel("Phone Number", phoneNumber));
        formPanel.add(createFieldPanel("Password", password));
        formPanel.add(createFieldPanel("Confirm Password", confirmPassword));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton loginBtn = new CustomButton("Back to Login", 22);
        CustomButton createAccBtn = new CustomButton("Create Account", 22);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        loginBtn.setPreferredSize(buttonSize);
        createAccBtn.setPreferredSize(buttonSize);

        loginBtn.addActionListener(e -> {
            Main.start();
            frame.dispose();
        });

        createAccBtn.addActionListener(e -> {
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
            if (password.getPassword().length == 0) {
                showError(frame, "Password cannot be empty");
                return;
            }
            if (confirmPassword.getPassword().length == 0) {
                showError(frame, "Confirm Password cannot be empty");
                return;
            }
            if (!new String(password.getPassword()).equals(new String(confirmPassword.getPassword()))) {
                showError(frame, "Passwords don't match");
                return;
            }

            try {
                ArrayList<String> emails = new ArrayList<>();
                ResultSet rs0 = database.getStatement().executeQuery("SELECT `Email` FROM `users`;");
                while (rs0.next()) {
                    emails.add(rs0.getString("Email"));
                }

                if (emails.contains(email.getText())) {
                    showError(frame, "This email is already used");
                    return;
                }

                ResultSet rs = database.getStatement().executeQuery("SELECT COUNT(*) FROM `users`;");
                rs.next();
                int ID = rs.getInt("COUNT(*)");

                String insert = "INSERT INTO `users`(`ID`, `FirstName`, `LastName`, `Email`, `PhoneNumber`, `Password`, `Type`) VALUES " +
                        "('" + ID + "','" + firstName.getText() + "','" + lastName.getText() + "','" + email.getText() + "'," +
                        "'" + phoneNumber.getText() + "','" + new String(password.getPassword()) + "','" + accType + "');";
                database.getStatement().execute(insert);
                
                JOptionPane.showMessageDialog(frame, 
                    "Account created successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);

                if (accType == 0) {
                    User user = new Client();
                    user.setID(ID);
                    user.setFirstName(firstName.getText());
                    user.setLastName(lastName.getText());
                    user.setEmail(email.getText());
                    user.setPhoneNumber(phoneNumber.getText());
                    user.setPassword(new String(password.getPassword()));
                    user.showList(database, frame);
                    frame.dispose();
                }

            } catch (SQLException ex) {
                showError(frame, ex.getMessage());
            }
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(createAccBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(title, BorderLayout.NORTH);
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

    private void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
