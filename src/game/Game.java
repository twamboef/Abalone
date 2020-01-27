package game;

import exceptions.OffBoardException;

public class Game {
	public static int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;
	private int turnCount;
	
	/**
	 * Creates game for 2 players.
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
	 * Creates game for 3 players.
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
	 * Creates game for 4 players.
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
		turnCount = 0;
	}
	
	/**
	 * Initialises the game.
	 * @requires board != null
	 */
	public void start() {
		try {
			board.reset();
		} catch (OffBoardException e) {
			e.printStackTrace();
		}
		play();
		printResult();
	}
	
	/**
	 * Plays the game until finished.
	 * @requires board != null
	 */
	public void play() {
		System.out.println(board.toString());
		while (!gameOver()) {
			players[current].makeMove(board);
			for (Player p : players) {
				System.out.println(p.getName() + "'s points: " + p.getPoints());
			}
			System.out.println(board.toString());
			current++;
			turnCount++;
			if (current >= players.length) {
				current = 0;
			}
		}
			
	}
	
	/**
	 * Checks if the game is over (there is a winner or 96 turns have passed).
	 * @requires players.length >= 2 && players.length <= 4
	 * @ensures total team points >= 6 => gameOver
	 * @return game finished or not
	 */
	public boolean gameOver() {
		return (getWinner() != null || turnCount >= 96);
	}
	
	/**
	 * Returns the player who is now in turn.
	 * @return Player whose turn it is right now
	 */
	public Player currentPlayer() {
		return players[current];
	}
	
	public Player[] getPlayers() {
		return players;
	}
	
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Returns the winner of the game.
	 * @return winner if there is a winner, or null if there is not a winner (yet)
	 */
	public Player getWinner() {
		for (Player p : players) {
			if (board.getPlayers() != 4 && p.getPoints() >= 6) {
				return p;
			} else if (board.getPlayers() == 4) {
				for (Player ps : players) {
					if (ps.getMarble() == p.getMarble().next(4).next(4)) {
						if (p.getPoints() + ps.getPoints() >= 6) {
							return p;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Prints the result of the game to the standard output (winner/winners/draw).
	 * @ensures result != null
	 */
	public void printResult() {
		for (Player p : players) {
			if (board.getPlayers() != 4 && p.getPoints() >= 6) {
				System.out.println(p.getName() + " won!");
				return;
			} else if (board.getPlayers() == 4) {
				for (Player ps : players) {
					if (ps.getMarble() == p.getMarble().next(4).next(4)) {
						if (p.getPoints() + ps.getPoints() >= 6) {
							System.out.println("Team " + p.getName()
								+ " and " + ps.getName() + " won!");
							return;
						}
					}
				}
			}
			System.out.println("96 turns have passed. It's a draw!");
		}
	}
}
