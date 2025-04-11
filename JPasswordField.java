import java.awt.Font;

@SuppressWarnings("serial")
public class JPasswordField extends javax.swing.JPasswordField {
	
	public JPasswordField(int textSize) {
		super();
		setFont(new Font("SansSerif", Font.BOLD, textSize));
		setHorizontalAlignment(JLabel.CENTER);
		setBorder(null);
	}

}
