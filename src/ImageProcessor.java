/*
 * This is a class file for the image processor.
 * The processor can open and write a image.
 * It also provide some function for the image process.
 * 
 * Author: Tam Tin Cheung
 * Version: 2.0.0
 */

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.ImageIcon;

public class ImageProcessor {
	/* Read an image from file. */
	public Image myRead(String filePath) throws IOException {
		Image myImage;
		File inputFile = new File(filePath);
		BufferedImage bufferedImage = ImageIO.read(inputFile);
		myImage = Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
		
		return myImage;
	}
	
	/* Write the image to the file. */
	public void myWrite(String filePath, Image myImage) throws IOException {
		BufferedImage buf = toBufferedImage(myImage);
		ImageIO.write(buf, "tiff",new File(filePath+".tiff"));
	}
	
	
	/* Turn a image to bufferedimage */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
		    return (BufferedImage)image;
		 }
		
		 image = new ImageIcon(image).getImage();

		 BufferedImage bimage = null;
		 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
		    int transparency = Transparency.OPAQUE;
		     GraphicsDevice gs = ge.getDefaultScreenDevice();
		     GraphicsConfiguration gc = gs.getDefaultConfiguration();
		     bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		 } catch (HeadlessException e) {
		 }

		if (bimage == null) {
		    int type = BufferedImage.TYPE_INT_RGB;
		     bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		 }

		// Copy image to buffered image
		 Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		 g.drawImage(image, 0, 0, null);
		 g.dispose();

		return bimage;
	}
	
	/* A function that grab the pixel from the image into the array
	 * pixels[].
	 */
	private void getPixels(int pixels[], Image image, int w, int h)
	{
		PixelGrabber pg = new PixelGrabber(image, 0, 0, w,
			h, pixels, 0, w);
		try 
		{
			pg.grabPixels();                                
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	/* A function that get the intensity of the pixel. */
	private int getIntensity(int pixel)
	{
		int intensity = pixel & 0x000000ff;
		return intensity;
	}
	
	private int setIntensityToPixel(int intensity)
	{
		int result = 0xff000000;
		result |= (intensity << 16);
		result |= (intensity << 8);
		result |= intensity;
		
		return result;
	}
	
	/*
	 * Unsharp masking the image we opened.
	 */
	public Image unsharpMasking(Image myImage)
	{
		int w = myImage.getWidth(null);
		int h = myImage.getHeight(null);
		int pixels[] = new int[w * h];
		int averagePixels[] = new int[w * h];
		int mask[] = new int[w * h];
		int maxIntensity, minIntensity;
		final int K = 4;
		double scale;
		
		maxIntensity = -1000;
		minIntensity = 1000;
		getPixels(pixels, myImage, w, h);
		
		/* Blur the image. */
		Image bImage = blurImage(myImage, 1, 1, 1, 1, 1, 1, 1, 1, 1);
		
		getPixels(averagePixels, bImage, w, h);
		
		/* Subtract blur image from the original image. 
		 * And add the mask to the result of subtraction. 
		 */
		for(int i = 0; i < w * h; i++) {
			mask[i] = getIntensity(pixels[i]) - getIntensity(averagePixels[i]);
			pixels[i] = (getIntensity(pixels[i]) + K * mask[i]);
			if(pixels[i] > maxIntensity)maxIntensity = pixels[i];
			if(pixels[i] < minIntensity)minIntensity = pixels[i];
		}
		
		/* Make the intensity of the image in the range of 0 - 255. */
		for(int i = 0; i < w * h; i++) {
			scale = 255 / ((double)(maxIntensity - minIntensity));
			pixels[i] = setIntensityToPixel((int)((pixels[i]+0-minIntensity) * scale));
		}
		
		/* Create the new image. */
		MemoryImageSource source;
		source = new MemoryImageSource(w, h, pixels, 0, w);
        myImage = Toolkit.getDefaultToolkit().createImage(source);
		
		return myImage;
	}
	
	/*
	 * Blur the image with 3 * 3 average filter. 
	 */
	public Image blurImage(Image myImage, int a1, int a2, int a3,
						   				  int a4, int a5, int a6,
						   				  int a7, int a8, int a9 )
	{
		int spacialFilter[] = new int[] {a1, a2, a3, a4, a5, a6, a7, a8, a9};
		final int SIZE = 9;
		
		int w = myImage.getWidth(null);
		int h = myImage.getHeight(null);
		int pixels[] = new int[w * h];
		int newPixels[] = new int[w * h];
		int sum, count, sub;
		
		getPixels(pixels, myImage, w, h);
		
		/* Blur the image
		 * Calculate the blurred pixel one by one.
		 */
		for(int row = 0; row < h; row++) {
			for(int col = 0; col < w; col++) {
				sum = 0;
				count = 0;
				sub = 0;
				/* Use the average fileter. */
				for(int i = -1; i <= 1; i++) {
					for(int v = -1; v <= 1; v++) {
						if((w * (row + i) + col + v >= 0) && 
								(w * (row + i) + col + v < w * h)) {
							sum += getIntensity(pixels[w * (row+i) + col + v]) * spacialFilter[sub];
							count += spacialFilter[sub];
						}
						sub++;
					}
				}
				newPixels[w * (row) + col] = setIntensityToPixel(sum / count);
			}
		}
		
		/* Create the new image. */
		MemoryImageSource source;
		source = new MemoryImageSource(w, h, newPixels, 0, w);
        myImage = Toolkit.getDefaultToolkit().createImage(source);
		
		return myImage;
	}
	
}
