package net.ryanogrady.gowbot;

public class Team {
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
				troops[i] = troop;
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
}
