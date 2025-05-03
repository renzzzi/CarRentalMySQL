import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        
        setTitle("Car Rental System by Renz - Dashboard");
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
        showCarInventory();
        
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(navigationPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createHeader() {
        header = new JPanel(new BorderLayout());
        header.setBackground(ColorScheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        CustomLabel logo = new CustomLabel("Car Rental System", 28);
        logo.setForeground(Color.WHITE);
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        userInfo.setBackground(ColorScheme.PRIMARY);
        
        CustomLabel userLabel = new CustomLabel(
            currentUser.getFirstName() + " " + currentUser.getLastName(), 
            16
        );
        userLabel.setForeground(Color.WHITE);
        
        CustomButton logoutBtn = new CustomButton("Logout", 22);
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.addActionListener(e -> {
            dispose();
            Main.start();
        });
        
        userInfo.add(userLabel);
        userInfo.add(logoutBtn);
        
        header.add(logo, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
    }
    
    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setBackground(ColorScheme.SURFACE);
        navigationPanel.setPreferredSize(new Dimension(250, 0));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        
        if (currentUser instanceof Admin) {
            addAdminNavButtons();
        } else {
            addClientNavButtons();
        }
    }
    
    private void addAdminNavButtons() {
        addNavButton("Add Car", () -> new AddNewCar().operation(database, this, currentUser));
        addNavButton("Update Car", () -> new UpdateCar().operation(database, this, currentUser));
        addNavButton("Delete Car", () -> new DeleteCar().operation(database, this, currentUser));
        addNavButton("Show All Rents", () -> new ShowAllRents().operation(database, this, currentUser));
        addNavButton("Show User Rents", () -> new ShowSpecUserRents().operation(database, this, currentUser));
        addNavButton("Add Admin", () -> new AddNewAccount(1).operation(database, this, currentUser));
        addNavButton("Edit Profile", () -> new EditUserData().operation(database, this, currentUser));
    }
    
    private void addClientNavButtons() {
        addNavButton("Rent Car", () -> new RentCar().operation(database, this, currentUser));
        addNavButton("Return Car", () -> new ReturnCar().operation(database, this, currentUser));
        addNavButton("My Rentals", () -> new ShowUserRents(-9999).operation(database, this, currentUser));
        addNavButton("Edit Profile", () -> new EditUserData().operation(database, this, currentUser));
    }
    
    private void addNavButton(String text, Runnable action) {
        CustomButton button = new CustomButton(text, 22);
        styleNavButton(button);
        button.addActionListener(e -> action.run());
        navigationPanel.add(button);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    private void showCarInventory() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(0, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.BACKGROUND);
        
        CustomLabel title = new CustomLabel("Car Inventory", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        CustomButton refreshBtn = new CustomButton("Refresh", 22);
        refreshBtn.setPreferredSize(new Dimension(120, 40));
        refreshBtn.addActionListener(e -> showCarInventory());
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(ColorScheme.BACKGROUND);
        titlePanel.add(title);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        String[] header = {
            "ID", "Brand", "Model", "Color", "Year", "Price", "Status"
        };

        ArrayList<Car> cars = new ArrayList<>();
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `cars`;");
            while (rs.next()) {
                Car car = new Car();
                car.setID(rs.getInt("ID"));
                car.setBrand(rs.getString("Brand"));
                car.setModel(rs.getString("Model"));
                car.setColor(rs.getString("Color"));
                car.setYear(rs.getInt("Year"));
                car.setPrice(rs.getDouble("Price"));
                int available = rs.getInt("Available");
                if (available < 2) {
                    car.setAvailable(available);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[][] carData = new String[cars.size()][header.length];
        for (int i = 0; i < cars.size(); i++) {
            Car c = cars.get(i);
            carData[i][0] = String.valueOf(c.getID());
            carData[i][1] = c.getBrand();
            carData[i][2] = c.getModel();
            carData[i][3] = c.getColor();
            carData[i][4] = String.valueOf(c.getYear());
            carData[i][5] = String.format("$%.2f", c.getPrice());
            carData[i][6] = c.isAvailable() == 0 ? "Available" : "Rented";
        }

        CustomTable table = new CustomTable(carData, header);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(ColorScheme.BACKGROUND);
        scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void styleNavButton(CustomButton button) {
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBackground(ColorScheme.SURFACE);
        button.setForeground(ColorScheme.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ColorScheme.SECONDARY);
                button.setForeground(Color.WHITE);
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(ColorScheme.SURFACE);
                button.setForeground(ColorScheme.TEXT_PRIMARY);
            }
        });
    }
}
