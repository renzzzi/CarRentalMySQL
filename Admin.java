import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;  // Added this import

public class Admin extends User {
    
    private Operation[] operations = new Operation[] {
                new AddNewCar(),
                new ViewCars(),
                new UpdateCar(),
                new DeleteCar(),
                new AddNewAccount(1),
                new ShowAllRents(),
                new ShowSpecUserRents(),
                new EditUserData(),
                new ChangePassword()};
    
    private JButton[] btns = new JButton[] {
            new JButton("Add New Car", 22),
            new JButton("View Cars", 22),
            new JButton("Update Car", 22),
            new JButton("Delete Car", 22),
            new JButton("Add New Admin", 22),
            new JButton("Show Rents", 22),
            new JButton("Show User's Rents", 22),
            new JButton("Edit my Data", 22),
            new JButton("Change Password", 22)
    };
    
    public Admin() {
        super();
    }

    @Override
    public void showList(Database database, JFrame f) {
    
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(500, btns.length*80);
        frame.setLocationRelativeTo(f);
        frame.getContentPane().setBackground(ColorScheme.BACKGROUND);
        frame.setLayout(new BorderLayout(0, 15));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Welcome " + getFirstName(), 26);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerPanel.add(title, BorderLayout.CENTER);
        
        frame.add(headerPanel, BorderLayout.NORTH);
        
        JPanel panel = new JPanel(new GridLayout(btns.length, 1, 15, 15));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        for (int i=0;i<btns.length;i++) {
            final int j = i;
            JButton button = btns[i];
            panel.add(button);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    operations[j].operation(database, frame, Admin.this);
                }
            });
        }
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    
}
