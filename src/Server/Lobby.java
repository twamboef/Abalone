package Server;

import java.util.ArrayList;
import java.util.List;

import Game.Player;

public class Lobby {
	private String name;
	private int size;
	private int readyAmount;
	private List<String> players;
	private boolean joinable;
	
	public Lobby(String name, int size) {
		this.name = name;
		this.size = size;
		players = new ArrayList<>();
	}
	
	public void join(String player) {
		players.add(player);
	}
	
	public void leave(String player) {
		for (int i=0; i < players.size(); i++) {
			if (players.get(i) == player) {
				players.remove(i);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getSize() {
		return size;
	}
	
	public List<String> getPlayers() {
		return players;
	}
	
	public int getReadyAmount() {
		return readyAmount;
	}
	
	public boolean isJoinable() {
		return joinable;
	}
	
	public boolean isReady() {
		return readyAmount == size;
	}
	
	public boolean isFull() {
		return players.size() == size;
	}
	
	public boolean containsPlayer(String name) {
		for (String p : players) {
			if (p.equals(name)) return true;
		}
		return false;
	}
	
	public void ready() {
		readyAmount++;
	}
	
	public void unready() {
		readyAmount--;
	}
}
