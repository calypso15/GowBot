package net.ryanogrady.gowbot;

public class Team {
	public final int MAX_TROOPS = 4;

	private Troop[] troops = new Troop[MAX_TROOPS];

	public Team() {
	}

	public Troop getTroop(int index) {
		return troops[index];
	}

	public void addTroop(Troop troop, int index) {
		troops[index] = troop;
	}

	public void removeTroop(int index) {
		troops[index] = null;
	}
}
