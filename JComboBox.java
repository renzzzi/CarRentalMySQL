import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class JComboBox extends javax.swing.JComboBox<String> {
    public JComboBox(String[] items, int fontSize) {
        super(items);
        setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        setBackground(null);
        ((JLabel)getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }
}
