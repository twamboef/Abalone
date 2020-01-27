package game;

public class ComputerPlayer extends Player {
    private Strategy strategy;

    public ComputerPlayer(String name, Marble marble) {
        super(name, marble);

    }

    @Override
    public String determineMove(Board board) {
        return strategy.determineMove(board, marble);
    }
}