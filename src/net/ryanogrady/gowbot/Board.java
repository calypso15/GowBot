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

public class Board implements IEvaluator {
	static ExtLogger logger = ExtLogger.create(Board.class);

	private final int width;
	private final int height;
	private GemColor[][] board;
	private boolean[][] changed;
	private IEvaluator evaluator = this;

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

	private GemColor set(int r, int c, GemColor val) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return GemColor.INVALID;
		} else {
			GemColor retval = board[r][c];
			board[r][c] = val;
			return retval;
		}
	}

	private GemColor set(Position p, GemColor val) {
		return set(p.row, p.col, val);
	}

	private boolean getChanged(int r, int c) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return false;
		} else {
			return (changed[r][c]);
		}
	}

	private boolean getChanged(Position p) {
		return getChanged(p.row, p.col);
	}

	private boolean setChanged(int r, int c, boolean val) {
		if (r < 0 || r >= height || c < 0 || c >= width) {
			return false;
		} else {
			boolean retval = changed[r][c];
			changed[r][c] = val;
			return retval;
		}
	}
	
	private void clearChanged() {
		logger.trace("Clearing changed array");
		changed = new boolean[height][width];
	}

	private boolean setChanged(Position p, boolean val) {
		return setChanged(p.row, p.col, val);
	}
	
	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public IEvaluator getEvaluator() {
		return evaluator;
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
				for (Position p : result.getPositions()) {
					g.drawOval(4 + 40 * p.col, 4 + 40 * p.row, 32, 32);
				}
			}
		}

		Instant end = Instant.now();
		logger.timing("toImage() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return img;
	}

	private boolean swap(Position p1, Position p2) {
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

	private void match(Position p, MatchResult result) {
		logger.timing("match() started");
		Instant start = Instant.now();
		
		logger.debug("Looking for matches at Position " + p.toString());
		
		if (result.contains(p)) {
			logger.debug("Results already contain this Position");
			Instant end = Instant.now();
			logger.timing("match() completed in " + Duration.between(start, end).toMillis() + " milliseconds");
			return;
		}

		GemColor color = get(p);

		if (color == GemColor.INVALID || color == GemColor.UNKNOWN) {
			logger.debug("Gem is not a valid color");
			Instant end = Instant.now();
			logger.timing("match() completed in " + Duration.between(start, end).toMillis() + " milliseconds");
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
		
		Instant end = Instant.now();
		logger.timing("match() completed in " + Duration.between(start, end).toMillis() + " milliseconds");

		return;
	}

	private MatchResult[] findMatches() {
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
				for (Position p : result.getPositions()) {
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

	public double evaluateMove(Position p1, Position p2) {
		Board b = new Board(this);
		b.swap(p1, p2);
		double value = evaluator.evaluate(b.findMatches());
		logger.info("Evaluated move " + p1 + " -> " + p2 + ": " + value);
		return value;
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
			logger.info("Best move " + currentBestP1 + " -> " + currentBestP2 + ": " + currentBest);
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
		boolean[][] oldChanged = changed;
		clearChanged();
		
		swap(p1, p2);
		MatchResult[] results = removeMatches(true, ReplacementMethod.RANDOM, false);
		
		if(results.length == 0) {
			logger.info("Move " + p1 + " -> " + p2 + " is not a valid move");
			swap(p1, p2);
			changed = oldChanged;
		} else {
			double val = evaluator.evaluate(results);
			logger.info("Made move " + p1 + " -> " + p2 + ": " + val );
		}
	}


}
