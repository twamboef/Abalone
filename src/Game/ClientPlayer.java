package Game;

import server.Server;

public class ClientPlayer extends Player {
	private Server server;
	private Game game;
	private ClientHandler ch;

	public ClientPlayer(Server server, String name, Marble marble) {
		super(name, marble);
		this.server = server;
	}
	
	
	
	@Override
	public String determineMove(Board board) {
		while (!game.gameOver()) {
			if (game.currentPlayer() != this) {
				ch.
			}
		}
	}
	
}
