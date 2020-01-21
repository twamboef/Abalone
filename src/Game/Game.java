package Game;

public class Game {
	public static int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;
	/**
	 * Creates game for 2 players
	 * @param p1 player 1
	 * @param p2 player 2
	 * @ensures board != null
	 */
	public Game(Player p1, Player p2) {
		board = new Board(2);
		players = new Player[2];
		players[0] = p1;
	    players[1] = p2;
		current = 0;
	}
	/**
	 * Creates game for 3 players
	 * @param p1 player 1
	 * @param p2 player 2
	 * @param p3 player 3
	 * @ensures board != null
	 */
	public Game(Player p1, Player p2, Player p3) {
		board = new Board(3);
		players = new Player[3];
		players[0] = p1;
	    players[1] = p2;
	    players[2] = p3;
		current = 0;
	}
	/**
	 * Creates game for 4 players
	 * @param p1 player 1
	 * @param p2 player 2
	 * @param p3 player 3
	 * @param p4 player 4
	 * @ensures board != null
	 */
	public Game(Player p1, Player p2, Player p3, Player p4) {
		board = new Board(4);
		players = new Player[4];
		players[0] = p1;
	    players[1] = p2;
	    players[2] = p3;
	    players[3] = p4;
		current = 0;
	}
	/**
	 * Initialises the game
	 * @requires board != null
	 */
	public void start() {
		board.reset();
		play();
	}
	/**
	 * Plays the game until finished
	 * @requires board != null
	 */
	public void play() {
		System.out.println(board.toString());
		while (!gameOver()) {
			players[current].makeMove(board);
			for (Player p : players) System.out.println(p.getName() + "'s points: " + p.getPoints());
			System.out.println(board.toString());
			current++;
			if (current >= players.length) current = 0;
		}
			
	}
	/**
	 * @requires players.length >= 2
	 * @ensures total team points >= 6 => gameOver
	 * @return game finished or not
	 */
	public boolean gameOver() {
		for (Player p : players) {
			if (board.getPlayers()!= 4) if (p.getPoints() >= 6) return true;
			else {
				for (Player ps : players) {
					if (ps.getMarble() == p.getMarble().next(4).next(4)) {
						if (p.getPoints() + ps.getPoints() >= 6) return true;
					}
				}
				
			}
		}
		return false;
	}
}
