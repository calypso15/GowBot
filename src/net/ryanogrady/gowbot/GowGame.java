package net.ryanogrady.gowbot;

public class GowGame {
	private Team playerTeam, enemyTeam;
	private Board board;
	private boolean playerTurn = true;
	
	public GowGame(Team playerTeam, Team enemyTeam, Board board) {
		this.playerTeam = playerTeam;
		this.enemyTeam = enemyTeam;
		this.board = board;
	}
	
	public Team getPlayerTeam() {
		return playerTeam;
	}
	
	public Team getEnemyTeam() {
		return enemyTeam;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public boolean isPlayerTurn() {
		return playerTurn;
	}
}
