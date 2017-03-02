package net.ryanogrady.gowbot;

import java.util.Set;
import java.util.TreeSet;

public class Team implements IEvaluator {
	public final int MAX_TROOPS = 4;

	private Troop[] troops = new Troop[MAX_TROOPS];

	public Team() {
	}

	public Troop getTroop(int index) {
		return troops[index];
	}

	public boolean hasOpening() {
		for (int i = 0; i < MAX_TROOPS; ++i) {
			if (troops[i] == null) {
				return true;
			}
		}

		return false;
	}

	public boolean addTroop(Troop troop) {
		for (int i = 0; i < MAX_TROOPS; ++i) {
			if (troops[i] == null) {
				troops[i] = new Troop(troop);
				return true;
			}
		}

		return false;
	}

	public boolean addTroop(Troop troop, int index) {
		if (troops[index] == null) {
			troops[index] = troop;
			return true;
		} else {
			return false;
		}
	}

	public boolean removeTroop(int index) {
		if (troops[index] != null) {
			troops[index] = null;
			return true;
		} else {
			return false;
		}
	}
	
	public void reorder(int[] order) {
		Troop[] newTroops = new Troop[MAX_TROOPS];
		
		for(int i = 0; i < MAX_TROOPS; ++i) {
			newTroops[i] = troops[order[i]];
		}
		
		troops = newTroops;
	}

	@Override
	public double evaluate(MatchResult[] results) {
		double value = 0;
		Set<Position> allMatches = new TreeSet<Position>();

		boolean extraTurn = false;

		for (MatchResult result : results) {
			for (GemColor g : GemColor.values()) {
				if (g != GemColor.INVALID) {
					value += result.get(g);
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
}
