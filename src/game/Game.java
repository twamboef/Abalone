package game;

import exceptions.OffBoardException;

public class Game {
    public static int playerAmount;
    private Board board;
    private Player[] players;
    private Marble current;
    private int turnCount;

    /**
     * Creates game for 2 players.
     * 
     * @param p1 player 1
     * @param p2 player 2
     * @ensures board != null
     */
    public Game(Player p1, Player p2) {
        playerAmount = 2;
        board = new Board(2);
        players = new Player[2];
        players[0] = p1;
        players[1] = p2;
        current = Marble.BLACK;
        turnCount = 0;
    }

    /**
     * Creates game for 3 players.
     * 
     * @param p1 player 1
     * @param p2 player 2
     * @param p3 player 3
     * @ensures board != null
     */
    public Game(Player p1, Player p2, Player p3) {
        playerAmount = 3;
        board = new Board(3);
        players = new Player[3];
        players[0] = p1;
        players[1] = p2;
        players[2] = p3;
        current = Marble.BLACK;
        turnCount = 0;
    }

    /**
     * Creates game for 4 players.
     * 
     * @param p1 player 1
     * @param p2 player 2
     * @param p3 player 3
     * @param p4 player 4
     * @ensures board != null
     */
    public Game(Player p1, Player p2, Player p3, Player p4) {
        playerAmount = 4;
        board = new Board(4);
        players = new Player[4];
        players[0] = p1;
        players[1] = p2;
        players[2] = p3;
        players[3] = p4;
        current = Marble.BLACK;
        turnCount = 0;
    }

    /**
     * Initialises the game.
     * 
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
     * 
     * @requires board != null
     */
    public void play() {
        System.out.println(board.toString());
        while (!gameOver()) {
            getCurrentPlayer().makeMove(board);
            if (playerAmount != 4) {
                for (Player p : players) {
                    System.out.println(p.getName() + "'s points: " + p.getPoints());
                }
            } else {
                System.out.println("Team " + players[0].getName() + " & " + players[1].getName() 
                        + "'s points: " + (players[0].getPoints() + players[1].getPoints()));
                System.out.println("Team " + players[2].getName() + " & " + players[3].getName() 
                        + "'s points: " + (players[2].getPoints() + players[3].getPoints()));
            }
            System.out.println(board.toString());
            current = current.next(playerAmount);
            turnCount++;
        }

    }

    /**
     * Makes a player forfeit the game.
     * This results in an instant win in case of 2 and 4 player games.
     * @param player who wants to forfeit
     */
    public void playerForfeit(String player) {
        Player pl = null;
        for (Player p : players) {
            if (p.getName().equals(player)) {
                pl = p;
            }
        }
        if (playerAmount == 2) {
            for (Player p : players) {
                if (pl.getMarble().next(2) == p.getMarble()) {
                    p.setPoints(6);
                }
            }
        } else if (playerAmount == 3) {
            Player[] newPlayers = new Player[2];
            int current = 0;
            for (Player p : players) {
                if (p != pl) {
                    newPlayers[current] = p;
                    current++;
                }
            }
            board.removeMarbles(pl);
            players = newPlayers;
        } else {
            for (Player p : players) {
                if (pl.getMarble().next(4) == p.getMarble()) {
                    p.setPoints(6);
                }
            }
        }
    }

    /**
     * Checks if the game is over (there is a winner or 96 turns have passed).
     * 
     * @requires players.length >= 2 && players.length <= 4
     * @ensures total team points >= 6 => gameOver
     * @return game finished or not
     */
    public boolean gameOver() {
        return (getWinner() != null || turnCount >= 96);
    }

    /**
     * Returns the player amount of this game.
     * 
     * @return playerAmount
     */
    public int getPlayerAmount() {
        return playerAmount;
    }

    public Player[] getPlayers() {
        return players;
    }
    
    /**
     * Returns the player who is now in turn.
     * 
     * @return Player whose turn it is right now
     */
    public Player getCurrentPlayer() {
        for (Player p : players) {
            if (p.getMarble() == current) {
                return p;
            }
        }
        return null;
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Returns the winner of the game.
     * 
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

    public Player getTeamMate(String name) {
        if (playerAmount == 4) {
            int index = -1;
            for (int i = 0; i < 4; i++) {
                if (players[i].getName().equals(name)) {
                    index = i;
                    break;
                }
            }
            if (index == 0) {
                return players[1];
            } else if (index == 1) {
                return players[0];
            } else if (index == 2) {
                return players[3];
            } else if (index == 3) {
                return players[2];
            }
        }
        return null;
    }
    
    /**
     * Prints the result of the game to the standard output (winner/winners/draw).
     * 
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
                            System.out.println("Team " + p.getName() + " and " + ps.getName() + " won!");
                            return;
                        }
                    }
                }
            }
            System.out.println("96 turns have passed. It's a draw!");
        }
    }
}
