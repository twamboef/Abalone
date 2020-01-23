package Protocol;

import java.util.List;

import Game.Game;
import Server.Lobby;

public interface ServerProtocol {
	
	public String getConnect(String name);
	
	public String createLobby(String name, int size);
	
	public String getLobbyList();
	
	public String joinLobby(String name, String lobby);
	
	public String leaveLobby(String name);
	 
	public String doReady(String name);
	 
	public String doUnready(String name);
	 
	public String lobbyChanged(Lobby lobby);
	 
	public String startGame(Lobby lobby);
	
	public String makeMove(Game game, String name);
	
	public String gameFinish(Game game);
	
	public String playerDefeat(String name);
	
	public String playerForfeit(String name);
	
	public String getServerList();
	
	public String challengePlayer(String challenger, String target);
	
	public String challengeAccept(String accepter, String challenger);
	
	public String sendPM(String sender, String receiver, String message);
	
	public String receivePM(String receiver, String sender, String message);
	
	public String sendLM(String sender, String message);
	
	public String receiveLM(String sender, String message);
	
	public String getLeaderboard();
}
