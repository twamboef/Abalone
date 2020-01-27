package protocol;

import Game.Game;

public interface ClientProtocol {
	
	public abstract void connect(String name);
	
	public abstract void createLobby(String name, int size);
	
	public abstract void getLobbyList();
	
	public abstract void joinLobby(String lobby);
	
	public abstract void leaveLobby();
	 
	public abstract void doReady();
	 
	public abstract void doUnready();
	
	public abstract void makeMove(String move);
	
	public abstract void playerForfeit();
	
	public abstract void getServerList();
	
	public abstract void challengePlayer(String target);
	
	public abstract void challengeAccept(String challenger);
	
	public abstract void sendPM(String receiver, String message);
	
	public abstract void sendLM(String message);
	
	public abstract void getLeaderboard();
}
