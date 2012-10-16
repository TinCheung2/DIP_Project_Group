/*
 * This is a file to define a frame to show 
 * the result of processing the image.
 */

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ResultFrame extends JFrame {
	
	public ResultFrame(Image image)
	{
		super("Result");
		this.setVisible(true);
		JLabel label;
		label = new JLabel(new ImageIcon(image));
		this.add(label);
		this.pack();
	}

}
