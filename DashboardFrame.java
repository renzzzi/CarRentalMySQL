import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DashboardFrame extends JFrame {
    private JPanel contentPanel;
    private User currentUser;
    private Database database;
    private JPanel mainPanel;
    private JPanel header;
    private JPanel navigationPanel;

    public DashboardFrame(User user, Database database) {
        this.currentUser = user;
        this.database = database;
        
        setTitle("Car Rental System - Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setupUI();
    }
    
    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        
        createHeader();
        createNavigationPanel();
        createContentPanel();
        
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(navigationPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createHeader() {
        header = new JPanel(new BorderLayout());
        header.setBackground(ColorScheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel logo = new JLabel("Car Rental System");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfo.setBackground(ColorScheme.PRIMARY);
        
        JLabel userLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton logoutBtn = new JButton("Logout", 22);  // Added font size parameter
        styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            dispose();
            Main.start();
        });
        
        userInfo.add(userLabel);
        userInfo.add(Box.createHorizontalStrut(20));
        userInfo.add(logoutBtn);
        
        header.add(logo, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
    }
    
    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setBackground(ColorScheme.SURFACE);
        navigationPanel.setPreferredSize(new Dimension(200, 0));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        
        if (currentUser instanceof Admin) {
            addAdminNavButtons();
        } else {
            addClientNavButtons();
        }
    }
    
    private void addAdminNavButtons() {
        addNavButton("View Cars", () -> new ViewCars().operation(database, this, currentUser));
        addNavButton("Add Car", () -> new AddNewCar().operation(database, this, currentUser));
        addNavButton("Update Car", () -> new UpdateCar().operation(database, this, currentUser));
        addNavButton("Delete Car", () -> new DeleteCar().operation(database, this, currentUser));
        addNavButton("Show All Rents", () -> new ShowAllRents().operation(database, this, currentUser));
        addNavButton("Show User Rents", () -> new ShowSpecUserRents().operation(database, this, currentUser));
        addNavButton("Add Admin", () -> new AddNewAccount(1).operation(database, this, currentUser));
        addNavButton("Edit Profile", () -> new EditUserData().operation(database, this, currentUser));
    }
    
    private void addClientNavButtons() {
        addNavButton("View Cars", () -> new ViewCars().operation(database, this, currentUser));
        addNavButton("Rent Car", () -> new RentCar().operation(database, this, currentUser));
        addNavButton("Return Car", () -> new ReturnCar().operation(database, this, currentUser));
        addNavButton("My Rentals", () -> new ShowUserRents(-9999).operation(database, this, currentUser));
        addNavButton("Edit Profile", () -> new EditUserData().operation(database, this, currentUser));
    }
    
    private void addNavButton(String text, Runnable action) {
        JButton button = new JButton(text, 22);  // Added font size parameter
        styleNavButton(button);
        button.addActionListener(e -> action.run());
        navigationPanel.add(button);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome to Car Rental System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button) {
        button.setBackground(ColorScheme.ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ColorScheme.ACCENT.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ColorScheme.ACCENT);
            }
        });
    }
    
    private void styleNavButton(JButton button) {
        button.setBackground(ColorScheme.SURFACE);
        button.setForeground(ColorScheme.TEXT_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ColorScheme.SECONDARY);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ColorScheme.SURFACE);
            }
        });
    }
}
