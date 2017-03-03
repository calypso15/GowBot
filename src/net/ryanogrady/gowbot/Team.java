package net.ryanogrady.gowbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Team implements IEvaluator {
	static ExtLogger logger = ExtLogger.create(Team.class);
	
	public final int MAX_TROOPS = 4;

	private List<Troop> troops = new ArrayList<Troop>(MAX_TROOPS);

	public Team() {
		for(int i = 0; i < MAX_TROOPS; ++i) {
			troops.add(null);
		}
	}

	public Troop getTroop(int index) {
		return troops.get(index);
	}
	
	public Troop[] getTroops() {
		return troops.toArray(new Troop[0]);
	}

	public boolean hasOpening() {
		for (int i = 0; i < MAX_TROOPS; ++i) {
			if (troops.get(i) == null) {
				return true;
			}
		}

		return false;
	}

	public boolean addTroop(Troop troop) {
		for (int i = 0; i < MAX_TROOPS; ++i) {
			if (troops.get(i) == null) {
				troops.set(i, new Troop(troop));
				return true;
			}
		}

		return false;
	}

	public boolean addTroop(Troop troop, int index) {
		if (troops.get(index) == null) {
			troops.set(index, new Troop(troop));
			return true;
		} else {
			return false;
		}
	}

	public boolean removeTroop(int index) {
		if (troops.get(index) != null) {
			troops.set(index, null);
			return true;
		} else {
			return false;
		}
	}
	
	public void reorder(int[] order) {
		List<Troop> newTroops = new ArrayList<Troop>(MAX_TROOPS);
		
		for(int i = 0; i < MAX_TROOPS; ++i) {
			newTroops.set(i, troops.get(order[i]));
		}
		
		troops = newTroops;
	}

	@Override
	public double evaluate(MatchResult[] results) {		
		double[] weights = new double[] { 0, 0, 0, 0, 0, 0, 0, 1 };
		for(GemColor g : GemColor.values()) {
			for(Troop t : troops) {
				for(GemColor g2 : t.getColors()) {
					if(g == g2) {
						weights[g.getValue()] = 1.0;
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder("Evaluating board with weights [");
		for(double d : weights) {
			sb.append(d + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		logger.debug(sb.toString());
	
		double value = 0;
		Set<Position> allMatches = new TreeSet<Position>();

		boolean extraTurn = false;

		for (MatchResult result : results) {
			for (GemColor g : GemColor.values()) {
				value += result.get(g) * weights[g.getValue()];
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
