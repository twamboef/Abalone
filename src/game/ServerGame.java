package game;

public class ServerGame extends Game {
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
    public void play() {
        while (!gameOver() && turnCount < 96) {
            synchronized(moveHappened) {
                try {
                    moveHappened.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            turnCount++;
        }
    }
}
