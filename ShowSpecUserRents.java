import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ShowSpecUserRents implements Operation {

	@Override
	public void operation(Database database, JFrame f, User user) {
		
		JFrame frame = new JFrame("Show User's Rents");
		frame.setSize(600, 260);
		frame.setLocationRelativeTo(f);
		frame.getContentPane().setBackground(new Color(250, 206, 27));
		frame.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Rents", 35);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		frame.add(title, BorderLayout.NORTH);
		
		JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
		panel.setBackground(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		panel.add(new JLabel("User ID:", 22));
		
		ArrayList<Integer> ids = new ArrayList<>();
		
		try {
			ResultSet rs0 = database.getStatement()
					.executeQuery("SELECT `ID` FROM `users` WHERE `Type` = '0';");
			while (rs0.next()) {
				ids.add(rs0.getInt("ID"));
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			frame.dispose();
		}
		
		String[] idsArr = new String[ids.size()+1];
		idsArr[0] = " ";
		for (int i=0;i<ids.size();i++) {
			idsArr[i+1] = String.valueOf(ids.get(i));
		}
		JComboBox id = new JComboBox(idsArr, 22);
		panel.add(id);
		
		JButton showUsers = new JButton("Show All Users", 22);
		showUsers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showUsers(database, frame);
			}
		});
		panel.add(showUsers);
		
		JButton confirm = new JButton("Confirm", 22);
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (id.getSelectedItem().toString().equals(" ")) {
					JOptionPane.showMessageDialog(frame, "User ID cannot be empty");
					return;
				}
				new ShowUserRents(Integer.parseInt(id.getSelectedItem().toString())).operation(database, f, user);
				frame.dispose();
			}
		});
		panel.add(confirm);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.requestFocus();
	}
		
	private void showUsers(Database database, JFrame f) {
		JFrame frame = new JFrame("Clients List");
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(f);
		frame.getContentPane().setBackground(new Color(250, 206, 27));
		frame.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Clients", 35);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		frame.add(title, BorderLayout.NORTH);
		
		String[] header = new String[] {"ID", "First Name", "Last Name", "Email", "Tel"};
		
		ArrayList<User> users = new ArrayList<>();
		try {
			ResultSet rs = database.getStatement().executeQuery("SELECT * FROM `users`;");
			while (rs.next()) {
				int accType = rs.getInt("Type");
				if (accType==0) {
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
			JOptionPane.showMessageDialog(frame, e.getMessage());
			frame.dispose();
		}
		
		String[][] usersData = new String[users.size()][5];
		for (int i=0;i<users.size();i++) {
			User u = users.get(i);
			usersData[i][0] = String.valueOf(u.getID());
			usersData[i][1] = u.getFirstName();
			usersData[i][2] = u.getLastName();
			usersData[i][3] = u.getEmail();
			usersData[i][4] = u.getPhoneNumber();
		}
		
		Color color2 = new Color(252, 242, 202);
		
		JScrollPane panel = new JScrollPane(new JTable(usersData, header, Color.black, color2));
		panel.setBackground(null);
		panel.getViewport().setBackground(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
		
	}

}
