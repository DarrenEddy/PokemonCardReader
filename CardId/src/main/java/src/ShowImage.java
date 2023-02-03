package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ShowImage {
	
	final static int SCREEN_WIDTH = 1080;
	final static int SCREEN_HEIGHT = 1920;
	final static int THRESHOLD = 210;
	final static int NOISE_REDUCTION = 4;
	final static int ZERO_RING_TOLLERANCE = 10;
	
	@SuppressWarnings("unused")
	private static void display(File f) 
	{
		BufferedImage inputPicture = null;
		try 
		{
			inputPicture = ImageIO.read(f);
			JFrame jf = new JFrame();
			
			double mag = (double)(inputPicture.getWidth())/(double)(SCREEN_WIDTH);
			
			int scaledHeight = (int)(inputPicture.getHeight()/mag);
			
			Image scaled = inputPicture.getScaledInstance(SCREEN_WIDTH, scaledHeight, java.awt.Image.SCALE_SMOOTH);
			ImageIcon ic = new ImageIcon(scaled);
			
			
			jf.setLayout(new FlowLayout());
			jf.setSize(SCREEN_WIDTH,scaledHeight);
			JLabel jl = new JLabel();
			jl.setIcon(ic);
			jf.add(jl);
			jf.setVisible(true);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			BufferedImage bimg = new BufferedImage(scaled.getWidth(null),scaled.getHeight(null),BufferedImage.TYPE_INT_ARGB); 
			Graphics2D bGr = bimg.createGraphics();
			bGr.drawImage(scaled, 0, 0, null);
			bGr.dispose();
			
			
			jf.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
					
				}

				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

			
			});
			
			
			
		} catch (IOException e) {e.printStackTrace();}
		

	}
	
	@SuppressWarnings("unused")
	private static void display(Image img) 
	{
		JFrame jf = new JFrame();
		
		double mag = (double)(img.getWidth(null))/(double)(SCREEN_WIDTH);
		
		int scaledHeight = (int)(img.getHeight(null)/mag);
		
		Image scaled = img.getScaledInstance(SCREEN_WIDTH, scaledHeight, java.awt.Image.SCALE_SMOOTH);
		ImageIcon ic = new ImageIcon(scaled);
		
		
		jf.setLayout(new FlowLayout());
		jf.setSize(SCREEN_WIDTH,scaledHeight);
		JLabel jl = new JLabel();
		jl.setIcon(ic);
		jf.add(jl);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

	}
	
	@SuppressWarnings("unused")
	private static Image ScaleImage(Image img,double mag) 
	{
		return img.getScaledInstance((int)(img.getWidth(null)*mag),(int)(img.getHeight(null)*mag), java.awt.Image.SCALE_SMOOTH);
	}
	
	private static int averagePixel(File f) 
	{
		double aAlpha = 0;
		double aRed = 0;
		double aGreen = 0;
		double aBlue = 0;
		int n = 1;
		
		try {
			Image img = ImageIO.read(f);
			img = ScaleImage(img,0.3);
			BufferedImage bimg = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB); 
			Graphics2D bGr = bimg.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();
			
			n = img.getHeight(null)*img.getWidth(null);
			
			int pixel;
			
			
			for (int y = 0; y < img.getHeight(null);y++) 
			{
				for (int x = 0; x<img.getWidth(null);x++) 
				{
					pixel = bimg.getRGB(x, y);
					Color col = new Color(pixel,true);
					aAlpha +=  (double)col.getAlpha()/n;
					aRed += (double)col.getRed()/n;
					aGreen += (double)col.getGreen()/n;
					aBlue += (double)col.getBlue()/n;
				
					
					
				}
			}
			
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (int)aAlpha <<24|(int)aRed<<16|(int)aGreen<<8|(int)aBlue;
		
		
		
	}
	
	@SuppressWarnings("unused")
	private static double ColourDistance(int pix1, int pix2) 
	{
		Color col1 = new Color(pix1,true);
		Color col2 = new Color(pix2,true);
		
		int sum = (col2.getRed()-col1.getRed())*(col2.getRed()-col1.getRed())+
				(col2.getGreen()-col1.getGreen())*(col2.getGreen()-col1.getGreen())+
				(col2.getBlue()-col1.getBlue())*(col2.getBlue()-col1.getBlue());
		
		return Math.sqrt(sum);
	}

	private static int CheckSurrounding(BufferedImage img, int xPos, int yPos,int avg) 
	{
		int sum = 0;
		
		//checks how many pixels in the 9 (the 1 + the 8 surrounding) are greater that the threshold
		for (int x = xPos-1; x <= xPos+1;x++) 
		{
			for (int y = yPos-1;y<=yPos+1;y++) 
			{
				if (x >= 0 && y >= 0 && x<= img.getWidth(null) && y <= img.getHeight(null)) 
				{
					
					int pixel = img.getRGB(x, y);
					
					if ((int)ColourDistance(pixel,avg)>THRESHOLD) 
					{
						sum++;
					}
				}
			}
		}
		
		return sum;
	}
	
	@SuppressWarnings("unused")
	private static void displayRectangle(File f, int xStart, int yStart, int w, int h) 
	{
		try {
			Image img = ImageIO.read(f);
			img = ScaleImage(img,0.3); // TODO make scale dynamic?
			BufferedImage bimg = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB); 
			Graphics2D bGr = bimg.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();
			
			int pixel;
			int hold = h;
			h= w;
			w=hold;
			
			
			
			for (int y = 0; y < img.getHeight(null);y++) 
			{
				for (int x = 0; x<img.getWidth(null);x++) 
				{
						if (x == xStart || x == xStart+h) 
						{
							if (yStart <= y && yStart+w >= y) 
							{
								bimg.setRGB(x, y, 0xffff0000);
							}
						}
						if (y==yStart || y == yStart+w) 
						{
							if (xStart <= x && xStart+h >= x) 
							{
								bimg.setRGB(x, y, 0xffff0000);
							}
						}
					
				}
				System.out.println();
			}
			
			display(bimg);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	private static boolean inBounds(BufferedImage img , int x, int y) 
	{
		if (x < 0 || x >= img.getWidth()) return false;
		if (y < 0 || y >= img.getHeight()) return false;
		return true;
	}
	
	private static String crackImage(BufferedImage bimg) 
	{
		String out = "";
		Tesseract instance = new Tesseract();
		instance.setDatapath("C:\\Users\\darre\\OneDrive\\Documents\\Tess4J\\tessdata"); // donno if i need
		try {
			out = instance.doOCR(bimg);
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	private static BufferedImage findRectangle(File f)
	{
		BufferedImage returnImg = null;
		try {
			Image img = ImageIO.read(f);
			img = ScaleImage(img,0.3);
			BufferedImage bimg = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB); 
			Graphics2D bGr = bimg.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();
			
			int pixel;
			int avg = averagePixel(f);
			
			int xCenter = (int)bimg.getWidth(null)/2;
			int yCenter = (int)bimg.getHeight(null)/2;
			
			int w  = 5;
			int h = 7;
			int i = xCenter - 2;
			int j = yCenter - 3;
			int sumThresh= 0;
			System.out.println(sumThresh);
			
			ArrayList<Ring> allRings = new ArrayList<Ring>();
			
			
			// keep going until theres a spike >10 than previous 
			// then go until no more growing 
			
			while (i >0 && j>0) 
			{
				for (int x = i -5; x <= i + w + 5; x ++) 
				{
					for (int y = j -7; y <= j + h + 7 ;y++) 
					{
						if (((x < i || x > i + w)||(y < j || y > j+h))&& inBounds(bimg,x,y)) 
						{
							pixel = bimg.getRGB(x, y);
							int colDis = (int) ColourDistance(pixel,avg);
							if (colDis > THRESHOLD && (CheckSurrounding(bimg,x,y,avg)>NOISE_REDUCTION)) 
							{
								sumThresh++;
							}

						}
					}
				}
				allRings.add(new Ring (i,j,w,h,sumThresh));
				i = i -5;
				j= j-7;
				w = w+10;
				h = h+14;
				System.out.println(sumThresh);
				sumThresh = 0;
			}
			
			// now we do something with all Rings;
			//other idea is to break at the largest 0 ring??? //TODO
			
			int zCount = 0;
			int lastz = allRings.size();
			
			for (int l = 0; l < allRings.size(); l++) 
			{
				Ring ring = allRings.get(l);
				if (ring.threshold == 0 || ring.threshold < NOISE_REDUCTION) 
				{
					zCount ++;
				}
				else {zCount = 0;}
				
				if (zCount >= ZERO_RING_TOLLERANCE) 
				{
					lastz = l;
				}
			}
			System.out.println("Rings= " + allRings.size());
			System.out.println("last=" + lastz);
			while(allRings.get(lastz).threshold == 0) lastz--;
			
			Ring lastRing = allRings.get(lastz);
			displayRectangle(f,lastRing.xPos,lastRing.yPos,lastRing.width,lastRing.height);
			returnImg = bimg.getSubimage(lastRing.xPos,lastRing.yPos,lastRing.width,lastRing.height);
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnImg;
		
	}
	
	
	
	public static void main(String[] args) 
	{
		JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		j.showOpenDialog(null);
		
		System.out.println(j.getSelectedFile().getAbsolutePath());
		
		BufferedImage img= findRectangle(j.getSelectedFile());
		display(img);
		System.out.println(crackImage(img));

		
	}
}


