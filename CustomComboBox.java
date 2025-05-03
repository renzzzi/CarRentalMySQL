import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

@SuppressWarnings("serial")
public class CustomComboBox extends javax.swing.JComboBox<String> {
    public CustomComboBox(String[] items, int fontSize) {
        super(items);
        setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        setBackground(ColorScheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.PRIMARY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        ((JLabel)getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }
}
