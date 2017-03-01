package net.ryanogrady.gowbot;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GowBotController {

	boolean done = false;

	public void run() {
		while (!done) {

			int[][] a = new int[][] {
					// { 1, 2, 3, 4, 1, 2, 3, 4 },
					// { 4, 5, 6, 7, 4, 5, 6, 7 },
					// { 1, 2, 3, 4, 1, 2, 3, 4 },
					// { 4, 5, 6, 7, 4, 5, 6, 7 },
					// { 1, 2, 3, 4, 1, 2, 3, 4 },
					// { 4, 5, 6, 7, 4, 5, 6, 7 },
					// { 1, 2, 3, 4, 1, 2, 3, 4 },
					// { 4, 5, 6, 7, 4, 5, 6, 7 }};

					{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
					{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
					{ 2, 2, 1, 2, 2, 1, 1, 2 },
					{ 2, 2, 1, 2, 2, 1, 1, 2 }, 
					{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
					{ 1, 1, 2, 1, 1, 2, 2, 1 },
					{ 2, 2, 1, 2, 2, 1, 1, 2 }, 
					{ 1, 1, 2, 1, 1, 2, 2, 1 } };

			Troop t1 = Troop.name("Warlock")
				.life(19)
				.attack(11)
				.armor(8)
				.magic(9)
				.addColor(GemColor.BLUE)
				.addColor(GemColor.BROWN)
				.addSpell(new Spell())
				.kingdom(new Kingdom("Karakoth"))
				.addType(new TroopType("Human"))
				.addType(new TroopType("Mystic"))
				.rarity(Rarity.EPIC)
				.build();
			Troop t2 = new Troop(t1);
			Troop t3 = Troop.name("Valkyrie")
			.life(12)
			.attack(10)
			.armor(19)
			.magic(4)
			.addColor(GemColor.RED)
			.addColor(GemColor.YELLOW)
			.addSpell(new Spell())
			.kingdom(new Kingdom("Stormheim"))
			.addType(new TroopType("Divine"))
			.rarity(Rarity.EPIC)
			.build();
			Troop t4 = new Troop(t1);
			
			Team team = new Team();
			team.addTroop(t1);
			team.addTroop(t2);
			team.addTroop(t3);
			team.addTroop(t4);

			Board b = Board.fromArray(a);
			displayImage(b.toImage(true, true));
			Position[] results = b.findBestMove();

			if (results != null) {
				b.move(results[0], results[1]);
			}

			displayImage(b.toImage(true, true));

			return;
		}
	}

	public static void displayImage(Image img) {
		JFrame frame = new JFrame();
		ImageIcon icon = new ImageIcon(img);
		JLabel label = new JLabel(icon);
		frame.add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
