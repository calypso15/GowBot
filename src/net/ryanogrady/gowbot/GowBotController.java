package net.ryanogrady.gowbot;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.ryanogrady.gowbot.Board.Position;


public class GowBotController {
	
	boolean done = false;
	
	public void run() {
		while(!done) {
			
			int[][] a = new int[][] { 
					{ 1, 2, 1, 1, 2, 2, 1, 2 },
					{ 2, 1, 2, 2, 1, 1, 2, 1 }, 
					{ 1, 2, 1, 1, 2, 2, 1, 2 },
					{ 2, 1, 2, 2, 1, 1, 2, 1 }, 
					{ 1, 2, 1, 1, 2, 2, 1, 2 },
					{ 2, 1, 2, 2, 1, 1, 2, 1 },  
					{ 1, 2, 1, 1, 2, 2, 1, 2 },
					{ 2, 1, 2, 2, 1, 1, 2, 1 }};

			Board b = Board.fromArray(a);
//			b.swap(b.new Position(6, 0), b.new Position(6, 1));
//			displayImage(b.toImage());
//			b.removeMatches(false);
//			displayImage(b.toImage());
//			b.removeMatches(false);
//			displayImage(b.toImage());
			b.findBestMove();

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
