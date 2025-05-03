import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShowAllRents implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        JFrame frame = new JFrame("All Rentals");
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(f);
        frame.setBackground(ColorScheme.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.BACKGROUND);
        
        CustomLabel title = new CustomLabel("All Rentals", 32);
        title.setForeground(ColorScheme.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        String[] header = {
            "ID", "Name", "Email", "Phone", "Car ID", "Car Details", 
            "Date Time", "Hours", "Total", "Status"
        };

        ArrayList<Rent> rents = new ArrayList<>();
        ArrayList<Integer> carIDs = new ArrayList<>();
        ArrayList<Integer> userIDs = new ArrayList<>();
        
        try {
            ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `rents`;");
            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                userIDs.add(rs.getInt("User"));
                carIDs.add(rs.getInt("Car"));
                rent.setDateTime(rs.getString("DateTime"));
                rent.setHours(rs.getInt("Hours"));
                rent.setTotal(rs.getDouble("Total"));
                rent.setStatus(rs.getInt("Status"));
                rents.add(rent);
            }

            for (int j = 0; j < rents.size(); j++) {
                Rent r = rents.get(j);

                ResultSet rs2 = database.getStatement()
                    .executeQuery("SELECT * FROM `users` WHERE `ID` = '" + userIDs.get(j) + "';");
                if (rs2.next()) {
                    User u = new Client();
                    u.setID(rs2.getInt("ID"));
                    u.setFirstName(rs2.getString("FirstName"));
                    u.setLastName(rs2.getString("LastName"));
                    u.setEmail(rs2.getString("Email"));
                    u.setPhoneNumber(rs2.getString("PhoneNumber"));
                    r.setUser(u);
                }

                ResultSet rs3 = database.getStatement()
                    .executeQuery("SELECT * FROM `cars` WHERE `ID` = '" + carIDs.get(j) + "';");
                if (rs3.next()) {
                    Car car = new Car();
                    car.setID(rs3.getInt("ID"));
                    car.setBrand(rs3.getString("Brand"));
                    car.setModel(rs3.getString("Model"));
                    car.setColor(rs3.getString("Color"));
                    car.setPrice(rs3.getDouble("Price"));
                    r.setCar(car);
                }
            }

        } catch (SQLException e) {
            showError(frame, e.getMessage());
            frame.dispose();
            return;
        }

        String[][] rentData = new String[rents.size()][header.length];
        for (int i = 0; i < rents.size(); i++) {
            Rent r = rents.get(i);
            rentData[i][0] = String.valueOf(r.getID());
            rentData[i][1] = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            rentData[i][2] = r.getUser().getEmail();
            rentData[i][3] = r.getUser().getPhoneNumber();
            rentData[i][4] = String.valueOf(r.getCar().getID());
            rentData[i][5] = String.format("%s %s (%s)", 
                r.getCar().getBrand(), r.getCar().getModel(), r.getCar().getColor());
            rentData[i][6] = r.getDateTime();
            rentData[i][7] = String.valueOf(r.getHours());
            rentData[i][8] = String.format("$%.2f", r.getTotal());
            rentData[i][9] = r.getStatusToString();
        }

        CustomTable table = new CustomTable(rentData, header);
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
