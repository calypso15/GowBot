package net.ryanogrady.gowbot;

import java.util.Set;
import java.util.TreeSet;

public class GowBot {

	public static void main(String[] args) {
		int[][] a = new int[][] {
				// { 1, 2, 1, 2, 1, 2, 1, 2 },
				// { 3, 4, 3, 1, 3, 4, 3, 4 },
				// { 1, 2, 1, 2, 1, 2, 1, 2 },
				// { 3, 4, 3, 1, 3, 4, 3, 4 },
				// { 1, 2, 1, 2, 1, 2, 1, 2 },
				// { 3, 4, 3, 4, 3, 4, 3, 4 },
				// { 1, 2, 1, 2, 1, 2, 1, 2 },
				// { 3, 4, 3, 4, 3, 4, 3, 4 }};

				{ 1, 1, 2, 1, 1, 2, 2, 1 }, { 1, 1, 2, 1, 1, 2, 2, 1 }, { 2, 2, 1, 2, 2, 1, 1, 2 },
				{ 2, 2, 1, 2, 2, 1, 1, 2 }, { 1, 1, 2, 1, 1, 2, 2, 1 }, { 1, 1, 2, 1, 1, 2, 2, 1 },
				{ 2, 2, 1, 2, 2, 1, 1, 2 }, { 1, 1, 2, 1, 1, 2, 2, 1 } };

		// Board board = Board.fromArray(a);
		Board board = Board.random();

		Troop warlock = Troop.name("Warlock")
				.life(19).attack(11)
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
		Troop valkyrie = Troop.name("Valkyrie")
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

		Team playerTeam = new Team();

		board.setEvaluator(playerTeam);

		playerTeam.addTroop(warlock);
		playerTeam.addTroop(warlock);
		playerTeam.addTroop(valkyrie);
		playerTeam.addTroop(warlock);

		Troop generic = Troop.name("Generic").life(10).attack(5).armor(5).magic(1).addColor(GemColor.RED).build();

		Team enemyTeam = new Team();
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);

		GowGame game = new GowGame(playerTeam, enemyTeam, board);
		game.getBoard().setEvaluator(game);

		GowBotController controller = new GowBotController(game);
		controller.run();
	}
}
