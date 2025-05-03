import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;

@SuppressWarnings("serial")
public class CustomButton extends javax.swing.JButton {
    private Color defaultBackground = ColorScheme.PRIMARY;
    private Color hoverBackground = ColorScheme.SECONDARY;
    private Color pressedBackground = ColorScheme.ACCENT;

    public CustomButton(String text, int textSize) {
        super(text);
        setBackground(defaultBackground);
        setFont(new Font("Segoe UI", Font.BOLD, textSize));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setFocusPainted(false);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverBackground);
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setBackground(pressedBackground);
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                setBackground(hoverBackground);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(defaultBackground);
            }
        });
    }
}
