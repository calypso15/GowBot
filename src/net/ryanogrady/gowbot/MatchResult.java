package net.ryanogrady.gowbot;

import java.util.HashSet;
import java.util.Set;

public class MatchResult {
	private int red, green, blue, purple, yellow, brown, skull;
	private int three, four, five;

	private Set<Position> positions = new HashSet<Position>();

	public MatchResult() {
		red = green = blue = purple = yellow = brown = skull = three = four = five = 0;
	}

	public Position[] getPositions() {
		return positions.toArray(new Position[0]);
	}
	
	public boolean contains(Position p) {
		return positions.contains(p);
	}

	public int get(GemColor c) {
		switch (c) {
		case RED:
			return red;
		case GREEN:
			return green;
		case BLUE:
			return blue;
		case PURPLE:
			return purple;
		case YELLOW:
			return yellow;
		case BROWN:
			return brown;
		case SKULL:
			return skull;
		case UNKNOWN:
		case INVALID:
		default:
			break;
		}

		return 0;
	}

	public int get(int c) {
		switch (c) {
		case 3:
			return three;
		case 4:
			return four;
		case 5:
			return five;
		default:
			break;
		}

		return 0;
	}

	public void add(GemColor c, Set<Position> s) {
		for (Position p : s) {
			add(c, p);
		}
	}

	public void add(GemColor c, Position p) {
		if (!positions.contains(p)) {
			positions.add(p);
			add(c, 1);
		}
	}

	public void add(GemColor c, int n) {
		switch (c) {
		case RED:
			red += n;
			break;
		case GREEN:
			green += n;
			break;
		case BLUE:
			blue += n;
			break;
		case PURPLE:
			purple += n;
			break;
		case YELLOW:
			yellow += n;
			break;
		case BROWN:
			brown += n;
			break;
		case SKULL:
			skull += n;
			break;
		case UNKNOWN:
		case INVALID:
		default:
			break;
		}

		add(n);
	}

	public void add(int c) {
		if (c == 3) {
			three++;
		} else if (c == 4) {
			four++;
		} else if (c >= 5) {
			five++;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("R: " + red);
		sb.append(" G: " + green);
		sb.append(" B: " + blue);
		sb.append(" P: " + purple);
		sb.append(" Y: " + yellow);
		sb.append(" N: " + brown);
		sb.append(" S: " + skull);
		sb.append(" III: " + three);
		sb.append(" IV: " + four);
		sb.append(" V+: " + five);
		sb.append("; ");

		for (Position p : positions) {
			sb.append(p.toString());
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof MatchResult) {
			MatchResult that = (MatchResult) other;
			result = (that.canEqual(this) && this.positions.equals(that.positions));
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = 41;

		for (Position p : positions) {
			result = 37 * result + p.hashCode();
		}

		return result;
	}

	public boolean canEqual(Object other) {
		return (other instanceof MatchResult);
	}
}