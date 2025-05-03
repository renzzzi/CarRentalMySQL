import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ShowSpecUserRents implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("Show User's Rentals");
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Select User", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        formPanel.setBackground(ColorScheme.BACKGROUND);

        ArrayList<Integer> ids = new ArrayList<>();
        try {
            ResultSet rs0 = database.getStatement()
                .executeQuery("SELECT `ID` FROM `users` WHERE `Type` = '0';");
            while (rs0.next()) {
                ids.add(rs0.getInt("ID"));
            }
        } catch (SQLException e1) {
            showError(frame, e1.getMessage());
            frame.dispose();
            return;
        }

        String[] idsArr = new String[ids.size() + 1];
        idsArr[0] = " ";
        for (int i = 0; i < ids.size(); i++) {
            idsArr[i + 1] = String.valueOf(ids.get(i));
        }

        CustomComboBox userCombo = new CustomComboBox(idsArr, 22);
        userCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JPanel comboPanel = new JPanel(new BorderLayout(0, 5));
        comboPanel.setBackground(ColorScheme.BACKGROUND);
        
        CustomLabel comboLabel = new CustomLabel("User ID", 14);
        comboLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        
        comboPanel.add(comboLabel, BorderLayout.NORTH);
        comboPanel.add(userCombo, BorderLayout.CENTER);
        
        formPanel.add(comboPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        CustomButton showUsersBtn = new CustomButton("Show All Users", 22);
        CustomButton viewRentsBtn = new CustomButton("View Rentals", 22);

        showUsersBtn.setBackground(ColorScheme.SECONDARY);
        viewRentsBtn.setBackground(ColorScheme.PRIMARY);
        
        showUsersBtn.setForeground(Color.WHITE);
        viewRentsBtn.setForeground(Color.WHITE);

        Dimension buttonSize = new Dimension(Integer.MAX_VALUE, 45);
        showUsersBtn.setPreferredSize(buttonSize);
        viewRentsBtn.setPreferredSize(buttonSize);

        showUsersBtn.addActionListener(e -> showUsers(database, frame));
        
        viewRentsBtn.addActionListener(e -> {
            if (userCombo.getSelectedItem().toString().equals(" ")) {
                showError(frame, "Please select a user");
                return;
            }
            new ShowUserRents(Integer.parseInt(userCombo.getSelectedItem().toString()))
                .operation(database, frame, user);
            frame.dispose();
        });

        buttonPanel.add(showUsersBtn);
        buttonPanel.add(viewRentsBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void showUsers(Database database, JFrame f) {
        JFrame frame = new JFrame("Users List");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        CustomLabel title = new CustomLabel("Client List", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        String[] header = {"ID", "First Name", "Last Name", "Email", "Phone"};

        ArrayList<User> users = new ArrayList<>();
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `users`;");
            while (rs.next()) {
                if (rs.getInt("Type") == 0) {
                    User u = new Client();
                    u.setID(rs.getInt("ID"));
                    u.setFirstName(rs.getString("FirstName"));
                    u.setLastName(rs.getString("LastName"));
                    u.setEmail(rs.getString("Email"));
                    u.setPhoneNumber(rs.getString("PhoneNumber"));
                    users.add(u);
                }
            }
        } catch (SQLException e) {
            showError(frame, e.getMessage());
            frame.dispose();
            return;
        }

        String[][] userData = new String[users.size()][5];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            userData[i][0] = String.valueOf(u.getID());
            userData[i][1] = u.getFirstName();
            userData[i][2] = u.getLastName();
            userData[i][3] = u.getEmail();
            userData[i][4] = u.getPhoneNumber();
        }

        CustomTable table = new CustomTable(userData, header);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(ColorScheme.BACKGROUND);
        scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        CustomButton closeButton = new CustomButton("Close", 22);
        closeButton.setBackground(ColorScheme.ACCENT);
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(120, 45));
        closeButton.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
