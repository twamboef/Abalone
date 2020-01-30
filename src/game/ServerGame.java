package game;

import exceptions.OffBoardException;

public class ServerGame extends Game implements Runnable {
    public Object moveHappened = new Object();

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
    public void run() {
        try {
            board.reset();
        } catch (OffBoardException e) {
            e.printStackTrace();
        }
        play();
    }

    @Override
    public void play() {
        while (!gameOver() && getTurnCount() < 96) {
            synchronized (moveHappened) {
                try {
                    moveHappened.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            current = current.next(playerAmount);
            turnCount = getTurnCount() + 1;
        }
    }

    /**
     * Returns a string with the result of this game.
     * @return winner/winners/draw
     */
    public String getResult() {
        for (Player p : players) {
            if (board.getPlayers() != 4 && p.getPoints() >= 6) {
                return p.getName() + " won!";
            } else if (board.getPlayers() == 4) {
                for (Player ps : players) {
                    if (ps.getMarble() == p.getMarble().next(4).next(4)) {
                        if (p.getPoints() + ps.getPoints() >= 6) {
                            return "Team " + p.getName() + " & " + ps.getName() + " won!";
                        }
                    }
                }
            }
        }
        return "96 turns have passed. It's a draw!";
    }
}
