import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

@SuppressWarnings("serial")
public class CustomPasswordField extends javax.swing.JPasswordField {
    public CustomPasswordField(int textSize) {
        super();
        setFont(new Font("Segoe UI", Font.PLAIN, textSize));
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.PRIMARY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setBackground(ColorScheme.SURFACE);
    }
}
