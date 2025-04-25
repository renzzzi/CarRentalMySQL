import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;
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
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Title
        JLabel title = new JLabel("Create New Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 15));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        // Create and style fields
        JTextField firstName = createStyledField();
        JTextField lastName = createStyledField();
        JTextField email = createStyledField();
        JTextField phoneNumber = createStyledField();
        JPasswordField password = createStyledPasswordField();
        JPasswordField confirmPassword = createStyledPasswordField();

        formPanel.add(createFieldPanel("First Name", firstName));
        formPanel.add(createFieldPanel("Last Name", lastName));
        formPanel.add(createFieldPanel("Email", email));
        formPanel.add(createFieldPanel("Phone Number", phoneNumber));
        formPanel.add(createFieldPanel("Password", password));
        formPanel.add(createFieldPanel("Confirm Password", confirmPassword));

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton loginBtn = new JButton("Back to Login", 22);
        JButton createAccBtn = new JButton("Create Account", 22);

        loginBtn.setBackground(ColorScheme.ACCENT);
        createAccBtn.setBackground(ColorScheme.PRIMARY);
        
        loginBtn.setForeground(Color.WHITE);
        createAccBtn.setForeground(Color.WHITE);

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

    private JTextField createStyledField() {
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(22);  // Added size parameter
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

    private void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
