package net.ryanogrady.gowbot;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.ryanogrady.gowbot.Board.Position;


public class GowBotController {
	
	boolean done = false;
	
	public void run() {
		while(!done) {

			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("resources/screen1.jpg"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int[][] a = new int[][] { 
					{ 5, 1, 1, 5, 1, 2, 5, 5 },
					{ 1, 2, 5, 1, 5, 6, 1, 3 }, 
					{ 5, 1, 2, 3, 4, 5, 3, 7 },
					{ 1, 2, 3, 4, 5, 6, 1, 5 }, 
					{ 5, 1, 2, 3, 1, 1, 7, 1 },
					{ 1, 2, 3, 4, 5, 1, 1, 5 }, 
					{ 5, 1, 2, 3, 4, 5, 1, 7 },
					{ 1, 2, 3, 4, 1, 1, 5, 5 }};

			Board b = Board.fromArray(a);
			displayImage(b.toImage());
			

			b.findMove();
			
			return;
		}
	}
	
    public static void displayImage(Image img)
    {
    	  JFrame frame = new JFrame();
    	  ImageIcon icon = new ImageIcon(img);
    	  JLabel label = new JLabel(icon);
    	  frame.add(label);
    	  frame.setDefaultCloseOperation
    	         (JFrame.EXIT_ON_CLOSE);
    	  frame.pack();
    	  frame.setVisible(true);
    }
}
