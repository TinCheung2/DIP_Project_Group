/*
 * This is a file for the frame of the application.
 * 
 * Author: Tam Tin Cheung
 * Version: 1.0.0
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.*;
import java.io.IOException;

public class TheFrame extends JFrame
{
	private String fileName;
	private FileDialog fd;
	private JFrame self;
	private Image image;
	private ImageProcessor processor;
	private JLabel label;
	
	public TheFrame()
	{
		super("Image Processing");
		
		self = this;
		processor = new ImageProcessor();
		
		/*
		 * Create the menu bar.
		 */
		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open Image");
		fileMenu.add(openItem);
		JMenuItem saveItem = new JMenuItem("Save Image");
		fileMenu.add(saveItem);
		
		JMenu ProcessMenu = new JMenu("Process");
		JMenuItem BlurItem = new JMenuItem("Blur Image");
		ProcessMenu.add(BlurItem);
		JMenuItem UnsharpMaskItem = new JMenuItem("Unsharp Mask");
		ProcessMenu.add(UnsharpMaskItem);
		
		/*
		 * Add the action listener.
		 */
		openItem.addActionListener(new openHandler());
		saveItem.addActionListener(new saveHandler());
		BlurItem.addActionListener(new blurHandler());
		UnsharpMaskItem.addActionListener(new unsharpHandler());
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(ProcessMenu);
	}
	
	private class openHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			fd = new FileDialog(self, "Open file", FileDialog.LOAD);
			fd.setVisible(true);
			fileName = fd.getDirectory();
			fileName += fd.getFile();
			try {
				image = processor.myRead(fileName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(label != null)
				self.remove(label);
			label = new JLabel(new ImageIcon(image));
			self.add(label);
			self.pack();
		}
	}
	
	private class saveHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			fd = new FileDialog(self, "Save file", FileDialog.SAVE);
			fd.setVisible(true);
			fileName = fd.getDirectory();
			fileName += fd.getFile();
			try {
				processor.myWrite(fileName, image);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private class blurHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			showImage(processor.blurImage(image, 1, 1, 1, 1, 1, 1, 1, 1, 1));
		}
	}
	
	private class unsharpHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			showImage(processor.unsharpMasking(image));
		}
	}
	
	/*
	 * Show the picture in the application.
	 */
	private void showImage(Image i)
	{
		ResultFrame rf = new ResultFrame(i);
	}
}
