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

public class AddNewAccount implements Operation {
	
	private int accType;
	
	public AddNewAccount(int accType) {
		this.accType = accType;
	}

	@Override
	public void operation(Database database, JFrame f, User u) {
		
		JFrame frame = new JFrame("Create New Account");
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(f);
		frame.getContentPane().setBackground(new Color(250, 206, 27));
		frame.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Welcome to Car Rental System", 35);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		frame.add(title, BorderLayout.NORTH);
		
		JPanel panel = new JPanel(new GridLayout(7, 2, 15, 15));
		panel.setBackground(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		panel.add(new JLabel("First Name:", 22));
		
		JTextField firstName = new JTextField(22);
		panel.add(firstName);
		
		panel.add(new JLabel("Last Name:", 22));
		
		JTextField lastName = new JTextField(22);
		panel.add(lastName);
		
		panel.add(new JLabel("Email:", 22));
		
		JTextField email = new JTextField(22);
		panel.add(email);
		
		panel.add(new JLabel("Phone Number:", 22));
		
		JTextField phoneNumber = new JTextField(22);
		panel.add(phoneNumber);
		
		panel.add(new JLabel("Password:", 22));
		
		JPasswordField password = new JPasswordField(22);
		panel.add(password);
		
		panel.add(new JLabel("Confirm Password:", 22));
		
		JPasswordField confirmPassword = new JPasswordField(22);
		panel.add(confirmPassword);
		
		JButton login = new JButton("Login", 22);
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.start();
				frame.dispose();
			}
		});
		panel.add(login);
		
		JButton createAcc = new JButton("Create Account", 22);
		createAcc.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (firstName.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "First Name cannot be empty");
					return;
				}
				if (lastName.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Last Name cannot be empty");
					return;
				}
				if (email.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Email cannot be empty");
					return;
				}
				if (phoneNumber.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Phone Number cannot be empty");
					return;
				}
				if (password.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Password cannot be empty");
					return;
				}
				if (confirmPassword.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Confirm Password cannot be empty");
					return;
				}
				if (!password.getText().equals(confirmPassword.getText())) {
					JOptionPane.showMessageDialog(frame, "Password doesn't match");
					return;
				}
				
				try {
				
				ArrayList<String> emails = new ArrayList<>();
				ResultSet rs0 = database.getStatement().executeQuery("SELECT `Email` FROM `users`;");
				while (rs0.next()) {
					emails.add(rs0.getString("Email"));
				}
				
				if (emails.contains(email.getText())) {
					JOptionPane.showMessageDialog(frame, "This email is already used");
					return;
				}
				
				ResultSet rs = database.getStatement().executeQuery("SELECT COUNT(*) FROM `users`;");
				rs.next();
				int ID = rs.getInt("COUNT(*)");
				
				String insert = "INSERT INTO `users`(`ID`, `FirstName`, `LastName`,"
						+ " `Email`, `PhoneNumber`, `Password`, `Type`) VALUES"
						+ " ('"+ID+"','"+firstName.getText()+"','"+lastName.getText()+"','"+email.getText()+"',"
								+ "'"+phoneNumber.getText()+"','"+password.getText()+"','"+accType+"');";
				database.getStatement().execute(insert);
				JOptionPane.showMessageDialog(frame, "Account created successfully");
	
				if (accType==0) {
					User user = new Client();
					user.setID(ID);
					user.setFirstName(firstName.getText());
					user.setLastName(lastName.getText());
					user.setEmail(email.getText());
					user.setPhoneNumber(phoneNumber.getText());
					user.setPassword(password.getText());
					user.showList(database, frame);
					frame.dispose();
				}
				
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(frame, e1.getMessage());
			}
				
			}
		});
		panel.add(createAcc);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);	
	}
	
}
