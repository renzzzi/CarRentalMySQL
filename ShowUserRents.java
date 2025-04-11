import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ShowUserRents implements Operation {
	
	private int userID;
	
	public ShowUserRents(int userID) {
		this.userID = userID;
	}

	@Override
	public void operation(Database database, JFrame f, User user) {
		if (userID==-9999) userID = user.getID();
		
		JFrame frame = new JFrame("Rents");
		frame.setSize(1200, 600);
		frame.setLocationRelativeTo(f);
		frame.getContentPane().setBackground(new Color(250, 206, 27));
		frame.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Rents", 35);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		frame.add(title, BorderLayout.NORTH);
		
		String[] header = new String[] {
			"ID", "Name", "Email", "Tel", "Car ID", "Car", "Date Time",
			"Hours", "Total", "Status"
		};
		
		ArrayList<Rent> rents = new ArrayList<>();
		ArrayList<Integer> carIDs = new ArrayList<>();
		try {
			String select = "SELECT * FROM `rents` WHERE `User` = '"+userID+"';";
			ResultSet rs = database.getStatement().executeQuery(select);
			while (rs.next()) {
				Rent rent = new Rent();
				rent.setID(rs.getInt("ID"));
				carIDs.add(rs.getInt("Car"));
				rent.setDateTime(rs.getString("DateTime"));
				rent.setHours(rs.getInt("Hours"));
				rent.setTotal(rs.getDouble("Total"));
				rent.setStatus(rs.getInt("Status"));
				rents.add(rent);
			}
			
			String selectUser = "SELECT * FROM `users` WHERE `ID` = '"+userID+"';";
			ResultSet rs2 = database.getStatement().executeQuery(selectUser);
			rs2.next();
			User u = new Client();
			u.setID(rs2.getInt("ID"));
			u.setFirstName(rs2.getString("FirstName"));
			u.setLastName(rs2.getString("LastName"));
			u.setEmail(rs2.getString("Email"));
			u.setPhoneNumber(rs2.getString("PhoneNumber"));
			u.setPassword(rs2.getString("Password"));
		
			for (int j=0;j<rents.size();j++) {
				Rent r = rents.get(j);
				r.setUser(u);
				ResultSet rs3 = database.getStatement()
				.executeQuery("SELECT * FROM `cars` WHERE `ID` = '"+carIDs.get(j)+"';");
				rs3.next();
				Car car = new Car();
				car.setID(rs3.getInt("ID"));
				car.setBrand(rs3.getString("Brand"));
				car.setModel(rs3.getString("Model"));
				car.setColor(rs3.getString("Color"));
				car.setYear(rs3.getInt("Year"));
				car.setPrice(rs3.getDouble("Price"));
				car.setAvailable(rs3.getInt("Available"));
				r.setCar(car);
			}
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
			frame.dispose();
		}
		
		String[][] rentsData = new String[rents.size()][10];
		for (int j=0;j<rents.size();j++) {
			Rent r = rents.get(j);
			rentsData[j][0] = String.valueOf(r.getID());
			rentsData[j][1] = r.getUser().getFirstName()+" "+r.getUser().getLastName();
			rentsData[j][2] = r.getUser().getEmail();
			rentsData[j][3] = r.getUser().getPhoneNumber();
			rentsData[j][4] = String.valueOf(r.getCar().getID());
			rentsData[j][5] = r.getCar().getBrand()+" "+r.getCar().getModel()+" "+r.getCar().getColor();
			rentsData[j][6] = r.getDateTime();
			rentsData[j][7] = String.valueOf(r.getHours());
			rentsData[j][8] = String.valueOf(r.getTotal()) + " $";
			rentsData[j][9] = r.getStatusToString();
		}
		
		Color color2 = new Color(252, 242, 202);
		
		JScrollPane panel = new JScrollPane(new JTable(rentsData, header, Color.black, color2));
		panel.setBackground(null);
		panel.getViewport().setBackground(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
