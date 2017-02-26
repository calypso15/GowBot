package net.ryanogrady.gowbot;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GowBotController {
	
	boolean done = false;
	
	public void run() {
		while(!done) {
			
			int[][] a = new int[][] { 
					{ 1, 2, 3, 4, 1, 2, 3, 4 },
					{ 4, 5, 6, 7, 4, 5, 6, 7 }, 
					{ 1, 2, 3, 4, 1, 2, 3, 4 },
					{ 4, 5, 6, 7, 4, 5, 6, 7 }, 
					{ 1, 2, 3, 4, 1, 2, 3, 4 },
					{ 4, 5, 6, 7, 4, 5, 6, 7 }, 
					{ 1, 2, 3, 4, 1, 2, 3, 4 },
					{ 4, 5, 6, 7, 4, 5, 6, 7 }};
			
//					{ 1, 2, 1, 1, 2, 2, 1, 2 },
//					{ 2, 1, 2, 2, 1, 1, 2, 1 }, 
//					{ 1, 2, 1, 1, 2, 2, 1, 2 },
//					{ 2, 1, 2, 2, 1, 1, 2, 1 }, 
//					{ 1, 2, 1, 1, 2, 2, 1, 2 },
//					{ 2, 1, 2, 2, 1, 1, 2, 1 },  
//					{ 1, 2, 1, 1, 2, 2, 1, 2 },
//					{ 2, 1, 2, 2, 1, 1, 2, 1 }};

			Board b = Board.fromArray(a);
			b.findBestMove();
			displayImage(b.toImage(true, true));

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
