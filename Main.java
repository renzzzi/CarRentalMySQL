import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    private static Database database;

    public static void main(String[] args) {
        database = new Database();
        setupLookAndFeel();
        start();
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        JFrame frame = new JFrame("Car Rental System by Renz");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        
        JPanel loginCard = new JPanel();
        loginCard.setBackground(ColorScheme.SURFACE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, ColorScheme.PRIMARY),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        
        CustomLabel logo = new CustomLabel("Car Rental System", 36);
        logo.setForeground(ColorScheme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        emailPanel.setBackground(ColorScheme.SURFACE);
        CustomTextField email = new CustomTextField(22);
        email.setPreferredSize(new Dimension(300, 40));
        emailPanel.add(email);
        
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        passwordPanel.setBackground(ColorScheme.SURFACE);
        CustomPasswordField password = new CustomPasswordField(22);
        password.setPreferredSize(new Dimension(300, 40));
        passwordPanel.add(password);
        
        JPanel loginButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginButtonPanel.setBackground(ColorScheme.SURFACE);
        CustomButton loginButton = new CustomButton("Login", 22);
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButtonPanel.add(loginButton);
        
        JPanel createAccountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        createAccountPanel.setBackground(ColorScheme.SURFACE);
        CustomButton createAccountButton = new CustomButton("Create Account", 22);
        createAccountButton.setPreferredSize(new Dimension(300, 45));
        createAccountPanel.add(createAccountButton);

        final ArrayList<User> users = new ArrayList<>();
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM users;");
            while (rs.next()) {
                User user;
                if (rs.getInt("Type") == 0) {
                    user = new Client();
                } else {
                    user = new Admin();
                }
                user.setID(rs.getInt("ID"));
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));
                user.setEmail(rs.getString("Email"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setPassword(rs.getString("Password"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (email.getText().isEmpty()) {
                    showError(frame, "Email cannot be empty");
                    return;
                }
                if (password.getPassword().length == 0) {
                    showError(frame, "Password cannot be empty");
                    return;
                }

                for (User u : users) {
                    if (u.getEmail().equals(email.getText()) && 
                        u.getPassword().equals(new String(password.getPassword()))) {
                        new DashboardFrame(u, database).setVisible(true);
                        frame.dispose();
                        return;
                    }
                }
                showError(frame, "Invalid email or password");
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddNewAccount(0).operation(database, frame, null);
                frame.dispose();
            }
        });

        loginCard.add(logo);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));
        loginCard.add(emailPanel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        loginCard.add(passwordPanel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 30)));
        loginCard.add(loginButtonPanel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        loginCard.add(createAccountPanel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ColorScheme.BACKGROUND);
        centerPanel.add(loginCard);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
