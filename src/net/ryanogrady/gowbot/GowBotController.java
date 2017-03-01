package net.ryanogrady.gowbot;

public class GowBotController {

	private boolean done = false;
	private GowGame game;
	private int round = 1;
	
	public GowBotController(GowGame game) {
		this.game = game;
	}

	public void run() {
		Util.displayImage(game.getBoard().toImage(false, true));
		
		while (!done) {
			Position[] results = game.getBoard().findBestMove();

			if (results != null) {
				game.getBoard().move(results[0], results[1]);
			}
			
			Util.displayImage(game.getBoard().toImage(false, true));
			
			round++;
			
			if(round > 1) {
				return;
			}
		}
	}
}
