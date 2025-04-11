import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ViewCars implements Operation {

	@Override
	public void operation(Database database, JFrame f, User user) {
		
		JFrame frame = new JFrame("Cars");
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(f);
		frame.getContentPane().setBackground(new Color(250, 206, 27));
		frame.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Cars", 35);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		frame.add(title, BorderLayout.NORTH);
		
		String[] header = new String[] {
				"ID", "Brand", "Model", "Color", "Year", "Price", "Available"
		};

		String select = "SELECT * FROM `cars`;";
		ArrayList<Car> cars = new ArrayList<>();
		try {
			ResultSet rs = database.getStatement().executeQuery(select);
			while (rs.next()) {
				Car car = new Car();
				car.setID(rs.getInt("ID"));
				car.setBrand(rs.getString("Brand"));
				car.setModel(rs.getString("Model"));
				car.setColor(rs.getString("Color"));
				car.setYear(rs.getInt("Year"));
				car.setPrice(rs.getDouble("Price"));
				int available = rs.getInt("Available");
				if (available<2) {
					car.setAvailable(available);
					cars.add(car);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
		}
		
		String[][] carsData = new String[cars.size()][7];
		for (int j=0;j<cars.size();j++) {
			Car c = cars.get(j);
			if (c.isAvailable()<2) {
				carsData[j][0] = String.valueOf(c.getID());
				carsData[j][1] = c.getBrand();
				carsData[j][2] = c.getModel();
				carsData[j][3] = c.getColor();
				carsData[j][4] = String.valueOf(c.getYear());
				carsData[j][5] = String.valueOf(c.getPrice()) + " $";
				if (c.isAvailable()==0) {
					carsData[j][6] = "Available";
				} else {
					carsData[j][6] = "Not Available";
				}
			}
		}
		
		Color color2 = new Color(252, 242, 202);
		
		JScrollPane panel = new JScrollPane(new JTable(carsData, header, Color.black, color2));
		panel.setBackground(null);
		panel.getViewport().setBackground(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);

	}

}
