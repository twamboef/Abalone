package protocol;

import Game.Game;
import server.Lobby;

public interface ServerProtocol {
	
	public abstract String getConnect(String name);
	
	public abstract String createLobby(String name, int size);
	
	public abstract String getLobbyList();
	
	public abstract String joinLobby(String name, String lobby);
	
	public abstract String leaveLobby(String name);
	 
	public abstract String doReady(String name);
	 
	public abstract String doUnready(String name);
	 
	public abstract String lobbyChanged(Lobby lobby);
	 
	public abstract String startGame(Lobby lobby);
	
	public abstract String makeMove(String name, String move);
	
	public abstract String sendMove(String name, String move);
	
	public abstract String gameFinish(Game game);
	
	public abstract String playerDefeat(String name);
	
	public abstract String playerForfeit(String name);
	
	public abstract String getServerList();
	
	public abstract String challengePlayer(String challenger, String target);
	
	public abstract String challengeAccept(String accepter, String challenger);
	
	public abstract String sendPM(String sender, String receiver, String message);
	
	public abstract String receivePM(String receiver, String sender, String message);
	
	public abstract String sendLM(String sender, String message);
	
	public abstract String receiveLM(String sender, String message);
	
	public abstract String getLeaderboard();
}
