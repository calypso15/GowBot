package net.ryanogrady.gowbot;

import java.util.Set;
import java.util.TreeSet;

public class GowGame implements IEvaluator {
	static ExtLogger logger = ExtLogger.create(GowGame.class);
	
	private Team playerTeam, enemyTeam;
	private Board board;
	private boolean playerTurn = true;

	public GowGame(Team playerTeam, Team enemyTeam, Board board) {
		this.playerTeam = playerTeam;
		this.enemyTeam = enemyTeam;
		this.board = board;
	}

	public Team getPlayerTeam() {
		return playerTeam;
	}

	public Team getEnemyTeam() {
		return enemyTeam;
	}

	public Board getBoard() {
		return board;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	@Override
	public double evaluate(MatchResult[] results) {		
		double[] playerWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 1 };
		double[] enemyWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 1 };
		for(GemColor g : GemColor.values()) {
			for(Troop t : playerTeam.getTroops()) {
				for(GemColor g2 : t.getColors()) {
					if(g == g2) {
						playerWeights[g.getValue()] = 1.0;
					}
				}
			}
			
			for(Troop t : enemyTeam.getTroops()) {
				for(GemColor g2 : t.getColors()) {
					if(g == g2) {
						enemyWeights[g.getValue()] = 1.0;
					}
				}
			}
		}

		double value = 0;
		Set<Position> allMatches = new TreeSet<Position>();

		boolean extraTurn = false;

		for (MatchResult result : results) {
			for (GemColor g : GemColor.values()) {
				value += result.get(g) * playerWeights[g.getValue()];
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
}
