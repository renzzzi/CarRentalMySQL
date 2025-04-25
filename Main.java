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
        JFrame frame = new JFrame("Car Rental System");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        
        JPanel loginCard = new JPanel();
        loginCard.setBackground(ColorScheme.SURFACE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER, 2),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        
        JLabel logo = new JLabel("Car Rental System");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(ColorScheme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        final JTextField email = createStyledTextField("Email");
        final JPasswordField password = createStyledPasswordField();
        
        JButton loginButton = new JButton("Login", 22);
        loginButton.setBackground(ColorScheme.PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createAccountButton = new JButton("Create Account", 22);
        createAccountButton.setBackground(ColorScheme.ACCENT);
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        loginCard.add(Box.createRigidArea(new Dimension(0, 30)));
        loginCard.add(email);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        loginCard.add(password);
        loginCard.add(Box.createRigidArea(new Dimension(0, 25)));
        loginCard.add(loginButton);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(createAccountButton);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ColorScheme.BACKGROUND);
        centerPanel.add(loginCard);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setBackground(ColorScheme.BACKGROUND);
        field.setForeground(ColorScheme.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);  // Add this line
        return field;
    }
    
    private static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(ColorScheme.BACKGROUND);
        field.setForeground(ColorScheme.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);  // Add this line
        return field;
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
