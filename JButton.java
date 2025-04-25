import java.awt.Color;
import java.awt.Font;

@SuppressWarnings("serial")
public class JButton extends javax.swing.JButton {
    public JButton(String text, int textSize) {
        super(text);
        setBackground(Color.BLACK);
        setFont(new Font("Segoe UI", Font.BOLD, textSize));
        setForeground(Color.WHITE);
        setBorder(null);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Color.DARK_GRAY);
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setBackground(Color.GRAY);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(Color.BLACK);
            }
        });
    }
}
