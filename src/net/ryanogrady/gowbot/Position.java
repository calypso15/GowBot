package net.ryanogrady.gowbot;

public class Position implements Comparable<Position> {
	public final int row;
	public final int col;

	public Position(int r, int c) {
		row = r;
		col = c;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append("(").append(row).append(",").append(col).append(")").toString();
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof Position) {
			Position that = (Position) other;
			result = (that.canEqual(this) && this.row == that.row && this.col == that.col);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return (37 * (41 + 37 * row) + col);
	}

	public boolean canEqual(Object other) {
		return (other instanceof Position);
	}

	@Override
	public int compareTo(Position o) {
		if (this.row != o.row) {
			return this.row - o.row;
		} else {
			return this.col - o.col;
		}
	}
}