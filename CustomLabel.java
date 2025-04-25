import java.awt.Font;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class CustomLabel extends javax.swing.JLabel {
    public CustomLabel(String text, int fontSize) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        setBackground(null);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
}
