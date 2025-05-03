import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Client extends User {
    
    private Operation[] operations = new Operation[] {
            new ViewCars(),
            new RentCar(),
            new ReturnCar(),
            new ShowUserRents(-9999),
            new EditUserData(),
            new ChangePassword()};
    
    private CustomButton[] btns = new CustomButton[] {
            new CustomButton("View Cars", 22),
            new CustomButton("Rent Car", 22),
            new CustomButton("Return Car", 22),
            new CustomButton("Show My Rents", 22),
            new CustomButton("Edit My Data", 22),
            new CustomButton("Change Password", 22)
    };
    
    public Client() {
        super();
    }

    @Override
    public void showList(Database database, JFrame f) {
        JFrame frame = new JFrame("Client Dashboard");
        frame.setSize(500, btns.length*90);
        frame.setLocationRelativeTo(f);
        frame.getContentPane().setBackground(ColorScheme.BACKGROUND);
        frame.setLayout(new BorderLayout(0, 15));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        CustomLabel title = new CustomLabel("Welcome " + getFirstName(), 26);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerPanel.add(title, BorderLayout.CENTER);
        
        frame.add(headerPanel, BorderLayout.NORTH);
        
        JPanel panel = new JPanel(new GridLayout(btns.length, 1, 15, 15));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        
        for (int i = 0; i < btns.length; i++) {
            final int j = i;
            CustomButton button = btns[i];
            button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
            panel.add(button);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    operations[j].operation(database, frame, Client.this);
                }
            });
        }
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
