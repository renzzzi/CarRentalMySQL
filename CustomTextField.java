import java.awt.Font;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class CustomTextField extends javax.swing.JTextField {
    public CustomTextField(int textSize) {
        super();
        setFont(new Font("Segoe UI", Font.BOLD, textSize));
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(null);
    } 
}
