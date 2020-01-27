package game;

public class ServerGame extends Game {
	public static int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;
	private int turnCount;
	
	public ServerGame(Player p1, Player p2) {
		super(p1, p2);
	}
	
	public ServerGame(Player p1, Player p2, Player p3) {
		super(p1, p2, p3);
	}
	
	public ServerGame(Player p1, Player p2, Player p3, Player p4) {
		super(p1, p2, p3, p4);
	}
	
	@Override
	public void play() {
		while (!gameOver()) {
			players[current].makeMove(board);
		}
	}
}
