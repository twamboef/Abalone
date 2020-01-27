package server;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private String name;
    private int size;
    private int readyAmount;
    private List<String> players;
    private boolean joinable;

    /**
     * Constructor for the lobby.
     * 
     * @param name of the lobby
     * @param size of the lobby
     */
    public Lobby(String name, int size) {
        this.name = name;
        this.size = size;
        players = new ArrayList<>();
    }

    /**
     * Lets a player join this lobby.
     * 
     * @param player who wants to join
     */
    public void join(String player) {
        players.add(player);
    }

    /**
     * Lets a player leave this lobby.
     * 
     * @param player who wants to leave
     */
    public void leave(String player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == player) {
                players.remove(i);
            }
        }
    }

    /**
     * Returns the name of this lobby.
     * 
     * @return name of lobby
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the size of this lobby.
     * 
     * @return size of lobby
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the current players in this lobby.
     * @return players in lobby
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * Returns the amount of players currently ready in this lobby.
     * 
     * @return players ready in lobby
     */
    public int getReadyAmount() {
        return readyAmount;
    }

    /**
     * Returns boolean if lobby is joinable.
     * 
     * @return joinable or not
     */
    public boolean isJoinable() {
        return joinable;
    }

    /**
     * Returns whether all players are currently ready if lobby is full.
     * 
     * @return all ready or not
     */
    public boolean isReady() {
        return readyAmount == size;
    }

    /**
     * Returns whether the lobby is full.
     * 
     * @return player amount == size
     */
    public boolean isFull() {
        return players.size() == size;
    }

    /**
     * Returns whether the given player is currently in this lobby.
     * 
     * @param name of the player
     * @return players.contains(player)
     */
    public boolean containsPlayer(String name) {
        for (String p : players) {
            if (p.equals(name)) {
                return true;
            }
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
