package net.ryanogrady.gowbot;

import java.util.Set;
import java.util.TreeSet;

public class GowBot {

	public static void main(String[] args) {
		int[][] a = new int[][] {
//				 { 1, 2, 1, 2, 1, 2, 1, 2 },
//				 { 3, 4, 3, 1, 3, 4, 3, 4 },
//				 { 1, 2, 1, 2, 1, 2, 1, 2 },
//				 { 3, 4, 3, 1, 3, 4, 3, 4 },
//				 { 1, 2, 1, 2, 1, 2, 1, 2 },
//				 { 3, 4, 3, 4, 3, 4, 3, 4 },
//				 { 1, 2, 1, 2, 1, 2, 1, 2 },
//				 { 3, 4, 3, 4, 3, 4, 3, 4 }};

				{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
				{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
				{ 2, 2, 1, 2, 2, 1, 1, 2 },
				{ 2, 2, 1, 2, 2, 1, 1, 2 }, 
				{ 1, 1, 2, 1, 1, 2, 2, 1 }, 
				{ 1, 1, 2, 1, 1, 2, 2, 1 },
				{ 2, 2, 1, 2, 2, 1, 1, 2 }, 
				{ 1, 1, 2, 1, 1, 2, 2, 1 } };
		
		Board board = Board.fromArray(a);

		Troop warlock = Troop.name("Warlock")
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
		
		Team playerTeam = new Team() {
			@Override
			public double evaluate(MatchResult[] results) {
				double[] weight = new double[] {0, 2, 1, 1, 2, 1, 1, 1};
				double value = 0;
				Set<Position> allMatches = new TreeSet<Position>();

				boolean extraTurn = false;

				for (MatchResult result : results) {
					for (GemColor g : GemColor.values()) {
						if (g != GemColor.INVALID) {
							value += result.get(g) * weight[g.getValue()];
						}
					}

					if (result.get(4) > 0 || result.get(5) > 0) {
						extraTurn = true;
					}
					
					for(Position p : result.getPositions()) {
						allMatches.add(p);
					}
				}

				if (extraTurn) {
					value += 10.0;
				}

				return value;
			}
		};
		
		board.setEvaluator(playerTeam);
		
		playerTeam.addTroop(warlock);
		playerTeam.addTroop(warlock);
		playerTeam.addTroop(valkyrie);
		playerTeam.addTroop(warlock);
		
		Troop generic = Troop.name("Generic")
		.life(10)
		.attack(5)
		.armor(5)
		.magic(1)
		.build();
		
		Team enemyTeam = new Team();
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);
		enemyTeam.addTroop(generic);
		
		GowGame game = new GowGame(playerTeam, enemyTeam, board);
		
		GowBotController controller = new GowBotController(game);
		controller.run();
	}
}
