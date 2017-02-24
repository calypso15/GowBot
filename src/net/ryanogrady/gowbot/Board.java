package net.ryanogrady.gowbot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Board {
	static Logger logger = LogManager.getLogger(Board.class);

	private final int width;
	private final int height;
	private GemColor[][] board;
	private boolean[][] changed;

	public Board() {
		this(8, 8);
	}

	private Board(int w, int h) {
		width = w;
		height = h;

		board = new GemColor[w][h];
		changed = new boolean[w][h];
	}
	
	public Board(Board b) {
		this(b.width, b.height);
		
		for(int r = 0; r < width; ++r) {
			for(int c = 0; c < height; ++c) {
				board[r][c] = b.board[r][c];
				changed[r][c] = b.changed[r][c];
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public GemColor get(Position p) {
		if (p.row < 0 || p.row >= height || p.col < 0 || p.col >= width) {
			return GemColor.INVALID;
		} else {
			return (board[p.row][p.col]);
		}
	}

	public GemColor set(Position p, GemColor val) {
		if (p.row < 0 || p.row >= height || p.col < 0 || p.col >= width) {
			return GemColor.INVALID;
		} else {
			GemColor retval = board[p.row][p.col];
			board[p.row][p.col] = val;
			return retval;
		}
	}

	public boolean getChanged(Position p) {
		if (p.row < 0 || p.row >= height || p.col < 0 || p.col >= width) {
			return false;
		} else {
			return (changed[p.row][p.col]);
		}
	}

	public void setChanged(Position p, boolean val) {
		if (p.row < 0 || p.row >= height || p.col < 0 || p.col >= width) {
			return;
		} else {
			changed[p.row][p.col] = val;
		}
	}

	public static Board fromImage(Image img) {
		return null;
	}

	public static Board fromArray(int[][] array) {
		Board b = new Board();

		b.board = new GemColor[array.length][];
		for (int r = 0; r < array.length; ++r) {
			b.board[r] = new GemColor[array[r].length];

			for (int c = 0; c < array[r].length; ++c) {
				b.board[r][c] = GemColor.fromInt(array[r][c]);
			}
		}

		return b;
	}

	public Image toImage() {
		logger.info("removeMatches() started");
		Instant start = Instant.now();
		
		BufferedImage img = new BufferedImage(320, 320, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setStroke(new BasicStroke(3));

		for (int row = 0; row < height; ++row) {
			for (int col = 0; col < width; ++col) {
				Color c = GemColor.toColor(board[row][col]);
				g.setColor(c);
				g.fillOval(4 + 40 * col, 4 + 40 * row, 32, 32);
				g.setColor(Color.gray);
				
				if(getChanged(new Position(row, col))) {
					g.drawOval(4 + 40 * col, 4 + 40 * row, 32, 32);
				}
				
				g.setColor(Color.black);
				g.drawString(new Position(row, col).toString(), 6 + 40 * col, 24 + 40 * row);
			}
		}
		
		Instant end = Instant.now();
		logger.info("toImage() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return img;
	}

	public boolean swap(Position p1, Position p2) {
		if (p1.row >= height || p1.col >= width || p2.row >= height || p2.col >= width) {
			return false;
		}

		if (get(p1) == get(p2)) {
			return false;
		}

		GemColor temp = board[p1.row][p1.col];
		set(p1, get(p2));
		set(p2, temp);

		changed[p1.row][p1.col] = true;
		changed[p2.row][p2.col] = true;

		return true;
	}

	public void unswap(Position p1, Position p2) {
		if (swap(p1, p2)) {
			changed[p1.row][p1.col] = false;
			changed[p2.row][p2.col] = false;
		}
	}

	private void match(Position p, MatchResult result) {

		if (result.positions.contains(p)) {
			return;
		}

		GemColor color = get(p);

		if (color == GemColor.INVALID || color == GemColor.UNKNOWN) {
			return;
		}

		Set<Position> rowMatches = new HashSet<Position>(), colMatches = new HashSet<Position>();

		for (int r = p.row; r >= 0; --r) {
			if (get(new Position(r, p.col)) == color) {
				rowMatches.add(new Position(r, p.col));
			} else {
				break;
			}
		}

		for (int r = p.row + 1; r < height; ++r) {
			if (get(new Position(r, p.col)) == color) {
				rowMatches.add(new Position(r, p.col));
			} else {
				break;
			}
		}

		for (int c = p.col; c >= 0; --c) {
			if (get(new Position(p.row, c)) == color) {
				colMatches.add(new Position(p.row, c));
			} else {
				break;
			}
		}

		for (int c = p.col + 1; c < width; ++c) {
			if (get(new Position(p.row, c)) == color) {
				colMatches.add(new Position(p.row, c));
			} else {
				break;
			}
		}

		if (rowMatches.size() >= 3 || colMatches.size() >= 3) {
			result.add(color, p);
		}

		if (rowMatches.size() >= 3) {

			for (Position rm : rowMatches) {
				if (changed[rm.row][rm.col]) {
					match(rm, result);
				} else {
					result.add(color, rm);
				}
			}
		}

		if (colMatches.size() >= 3) {
			for (Position cm : colMatches) {
				if (changed[cm.row][cm.col]) {
					match(cm, result);
				} else {
					result.add(color, cm);
				}
			}
		}

		if (rowMatches.size() >= 3 && colMatches.size() >= 3) {
			result.add(rowMatches.size() + colMatches.size() - 1);
		} else if (rowMatches.size() >= 3) {
			result.add(rowMatches.size());
		} else if (colMatches.size() >= 3) {
			result.add(colMatches.size());
		}

		return;
	}

	public MatchResult[] findMatches() {
		logger.info("findMatches() started");
		Instant start = Instant.now();
		
		List<MatchResult> matches = new ArrayList<MatchResult>();

		for (int r = 0; r < getHeight(); ++r) {
			for (int c = 0; c < getWidth(); ++c) {
				if (changed[r][c]) {
					MatchResult result = new MatchResult();
					match(new Position(r, c), result);
					
					if(result.getPositions().length > 0) {
						matches.add(result);
					}
				}
			}
		}
		
		Instant end = Instant.now();
		logger.info("findMatches() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return matches.toArray(new MatchResult[0]);
	}

	private void removeMatch(MatchResult result) {
		for (Position p : result.getPositions()) {
			for (int r = p.row; r > 0; --r) {
				set(new Position(r, p.col), get(new Position(r - 1, p.col)));
				setChanged(new Position(r, p.col), true);
			}

			set(new Position(0, p.col), GemColor.UNKNOWN);
			setChanged(new Position(0, p.col), true);
		}
	}

	public MatchResult[] removeMatches(boolean cascading) {
		logger.info("removeMatches() started");
		Instant start = Instant.now();

		Set<MatchResult> retval = new HashSet<MatchResult>();
		MatchResult[] results;

		do {
			results = findMatches();

			for (MatchResult result : results) {
				logger.debug(result.toString());
				retval.add(result);
				removeMatch(result);
			}
		} while (cascading && results.length > 0);
		

		Instant end = Instant.now();
		logger.info("removeMatches() completed in " + Duration.between(start, end).toMillis() + " milliseconds");
		
		return retval.toArray(new MatchResult[0]);
	}

	private double evaluate() {
		return evaluate(new double[] { 0, 1, 1, 1, 1, 1, 1, 1 });
	}

	private double evaluate(double[] weight) {
		double value = 0;
		MatchResult[] results = removeMatches(true);
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

			allMatches.addAll(result.positions);
		}
		
		if(extraTurn) {
			value += 10.0;
		}

		return value;
	}
	
	public double evaluateMove(Position p1, Position p2) {
		Board b = new Board(this);
		b.swap(p1, p2);
		return b.evaluate();
	}

	public void findBestMove() {
		logger.info("findBestMove() started");
		Instant start = Instant.now();

		Position p1, p2;
		double current, currentBest = 0.0;
		Position currentBestP1 = null, currentBestP2 = null;

		for (int r = 0; r < getHeight(); ++r) {
			for (int c = 0; c < getWidth(); ++c) {
				p1 = new Position(r, c);
				p2 = new Position(r + 1, c);
				logger.debug(p1 + " -> " + p2);
				current = evaluateMove(p1, p2);
				logger.debug("Score: " + current + System.lineSeparator());
				if(current > currentBest) {
					currentBest = current;
					currentBestP1 = p1;
					currentBestP2 = p2;
				}

				p2 = new Position(r, c + 1);
				logger.debug(p1 + " -> " + p2);
				current = evaluateMove(p1, p2);
				logger.debug("Score: " + current + System.lineSeparator());
				if(current > currentBest) {
					currentBest = current;
					currentBestP1 = p1;
					currentBestP2 = p2;
				}
			}
		}
		
		if(currentBestP1 != null && currentBestP2 != null) {
			logger.info("Best move " + currentBestP1 + " -> " + currentBestP2 + " has a score of " + currentBest);
		} else {
			logger.info("No best move found.");
		}

		Instant end = Instant.now();
		logger.info("findBestMove() completed in " + Duration.between(start, end).toMillis() + " milliseconds");
	}

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
			return (41 * (41 + row) + col);
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
			if(!positions.contains(p)) {
				positions.add(p);
				add(c, 1);
			}
		}

		private void add(GemColor c, int n) {
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

		private void add(int c) {
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

			for(Position p : positions) {
				sb.append(p.toString());
			}

			return sb.toString();
		}
	}
}
