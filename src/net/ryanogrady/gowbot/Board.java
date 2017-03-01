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
	static ExtLogger logger = ExtLogger.create(Board.class);

	private final int width;
	private final int height;
	private GemColor[][] board;
	private boolean[][] changed;

	public Board() {
		this(8, 8);
	}

	private Board(int h, int w) {
		height = h;
		width = w;

		board = new GemColor[h][w];
		changed = new boolean[h][w];
	}

	public Board(Board b) {
		this(b.height, b.width);

		for (int r = 0; r < height; ++r) {
			for (int c = 0; c < width; ++c) {
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

	public GemColor get(int r, int c) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return GemColor.INVALID;
		} else {
			return (board[r][c]);
		}
	}

	public GemColor get(Position p) {
		return get(p.row, p.col);
	}

	public GemColor set(int r, int c, GemColor val) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return GemColor.INVALID;
		} else {
			GemColor retval = board[r][c];
			board[r][c] = val;
			return retval;
		}
	}

	public GemColor set(Position p, GemColor val) {
		return set(p.row, p.col, val);
	}

	public boolean getChanged(int r, int c) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return false;
		} else {
			return (changed[r][c]);
		}
	}

	public boolean getChanged(Position p) {
		return getChanged(p.row, p.col);
	}

	public boolean setChanged(int r, int c, boolean val) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return false;
		} else {
			boolean retval = changed[r][c];
			changed[r][c] = val;
			return retval;
		}
	}
	
	public void clearChanged() {
		changed = new boolean[height][width];
	}

	public boolean setChanged(Position p, boolean val) {
		return setChanged(p.row, p.col, val);
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
		return toImage(false, false);
	}

	public Image toImage(boolean showMatches, boolean showChanged) {
		logger.timing("removeMatches() started");
		Instant start = Instant.now();

		BufferedImage img = new BufferedImage(320, 320, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setStroke(new BasicStroke(3));

		MatchResult[] m = null;

		for (int row = 0; row < height; ++row) {
			for (int col = 0; col < width; ++col) {
				Color c = GemColor.toColor(board[row][col]);
				g.setColor(c);
				g.fillOval(4 + 40 * col, 4 + 40 * row, 32, 32);
				g.setColor(Color.gray);

				if (showChanged && getChanged(row, col)) {
					g.drawOval(4 + 40 * col, 4 + 40 * row, 32, 32);
				}

				g.setColor(Color.black);
				g.drawString(new Position(row, col).toString(), 6 + 40 * col, 24 + 40 * row);
			}
		}

		if (showMatches) {
			g.setColor(Color.orange);

			m = findMatches();

			for (MatchResult result : m) {
				for (Position p : result.positions) {
					g.drawOval(4 + 40 * p.col, 4 + 40 * p.row, 32, 32);
				}
			}
		}

		Instant end = Instant.now();
		logger.timing("toImage() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

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
			if (get(r, p.col) == color) {
				rowMatches.add(new Position(r, p.col));
			} else {
				break;
			}
		}

		for (int r = p.row + 1; r < height; ++r) {
			if (get(r, p.col) == color) {
				rowMatches.add(new Position(r, p.col));
			} else {
				break;
			}
		}

		for (int c = p.col; c >= 0; --c) {
			if (get(p.row, c) == color) {
				colMatches.add(new Position(p.row, c));
			} else {
				break;
			}
		}

		for (int c = p.col + 1; c < width; ++c) {
			if (get(p.row, c) == color) {
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
		logger.timing("findMatches() started");
		Instant start = Instant.now();

		Set<MatchResult> matches = new HashSet<MatchResult>();

		for (int r = 0; r < getHeight(); ++r) {
			for (int c = 0; c < getWidth(); ++c) {
				if (changed[r][c]) {
					MatchResult result = new MatchResult();
					match(new Position(r, c), result);

					if (result.getPositions().length > 0) {
						matches.add(result);
					}
				}
			}
		}

		Instant end = Instant.now();
		logger.timing("findMatches() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return matches.toArray(new MatchResult[0]);
	}

	private void collapse(ReplacementMethod method) {
		for (int c = 0; c < width; ++c) {
			for (int r = height - 1; r > 0; --r) {
				if (get(r, c) == GemColor.UNKNOWN) {
					boolean found = false;

					for (int r2 = r - 1; r2 >= 0; --r2) {
						if (get(r2, c) != GemColor.UNKNOWN) {
							found = true;
							set(r, c, get(new Position(r2, c)));
							set(r2, c, GemColor.UNKNOWN);
							setChanged(r, c, true);
							break;
						}
					}

					if (!found) {
						set(r, c, GemColor.UNKNOWN);
						setChanged(r, c, true);
					}
				}
			}
		}

		for (int r = 0; r < height; ++r) {
			for (int c = 0; c < width; ++c) {
				if(get(r,c) == GemColor.UNKNOWN) {
					setChanged(r, c, true);
					
					if (method == ReplacementMethod.UNKNOWN) {
						// do nothing
					} else if (method == ReplacementMethod.RANDOM) {
						set(r, c, GemColor.random());
					} else {
						logger.warn("Unexpected ReplacementMethod '" + method.toString() + "', using UNKNOWN.");
						// then do nothing
					}
				}
			}
		}
	}

	private MatchResult[] removeMatches(boolean cascading, ReplacementMethod method, boolean render) {
		logger.timing("removeMatches() started");
		Instant start = Instant.now();

		MatchResult[] results;
		List<MatchResult> allResults = new ArrayList<MatchResult>();

		do {
			results = findMatches();

			for (MatchResult result : results) {
				for (Position p : result.positions) {
					set(p, GemColor.UNKNOWN);
				}

				allResults.add(result);
			}

			collapse(method);
			
			if(render) {
				Util.displayImage(this.toImage());
			}
		} while (cascading && results.length > 0);

		Instant end = Instant.now();
		logger.timing("removeMatches() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return allResults.toArray(new MatchResult[0]);
	}

	private double evaluate() {
		return evaluate(new double[] { 0, 1, 1, 1, 1, 1, 1, 1 });
	}

	private double evaluate(double[] weight) {
		double value = 0;
		MatchResult[] results = removeMatches(true, ReplacementMethod.UNKNOWN, false);
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

		if (extraTurn) {
			value += 10.0;
		}

		return value;
	}

	public double evaluateMove(Position p1, Position p2) {
		Board b = new Board(this);
		b.swap(p1, p2);
		return b.evaluate();
	}

	/**
	 * Evaluates every possible move on a board to determine which move has the
	 * highest weighted value.
	 */
	public Position[] findBestMove() {
		logger.timing("findBestMove() started");
		Instant start = Instant.now();

		Position[] retval = null;
		Position p1, p2;
		double current, currentBest = 0.0;
		Position currentBestP1 = null, currentBestP2 = null;

		// Loop over every position on the game board
		for (int r = 0; r < getHeight(); ++r) {
			for (int c = 0; c < getWidth(); ++c) {
				p1 = new Position(r, c);
				p2 = new Position(r + 1, c);
				GemColor g1 = get(p1);
				GemColor g2 = get(p2);

				//
				if (get(r + 1, c - 1) == g1 || get(r + 1, c + 1) == g1 || get(r, c - 1) == g2 || get(r, c + 1) == g2) {

					logger.debug(p1 + " -> " + p2);
					current = evaluateMove(p1, p2);
					logger.debug("Score: " + current + System.lineSeparator());
					if (current > currentBest) {
						currentBest = current;
						currentBestP1 = p1;
						currentBestP2 = p2;
					}
				} else {
					logger.debug("Skipping " + p1 + " -> " + p2);
				}

				p2 = new Position(r, c + 1);
				g2 = get(p2);

				if (get(r - 1, c + 1) == g1 || get(r + 1, c + 1) == g1 || get(r - 1, c) == g2 || get(r + 1, c) == g2) {
					logger.debug(p1 + " -> " + p2);
					current = evaluateMove(p1, p2);
					logger.debug("Score: " + current + System.lineSeparator());
					if (current > currentBest) {
						currentBest = current;
						currentBestP1 = p1;
						currentBestP2 = p2;
					}
				} else {
					logger.debug("Skipping " + p1 + " -> " + p2);
				}
			}
		}

		if (currentBestP1 != null && currentBestP2 != null) {
			logger.info("Best move " + currentBestP1 + " -> " + currentBestP2 + " has a score of " + currentBest);
			retval = new Position[2];
			retval[0] = currentBestP1;
			retval[1] = currentBestP2;
		} else {
			logger.info("No best move found.");
		}

		Instant end = Instant.now();
		logger.timing("findBestMove() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return retval;
	}

	public void move(Position p1, Position p2) {
		clearChanged();
		swap(p1, p2);
		removeMatches(true, ReplacementMethod.RANDOM, false);
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
			if (!positions.contains(p)) {
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
}
