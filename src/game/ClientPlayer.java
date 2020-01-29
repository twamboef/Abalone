package game;

import exceptions.OffBoardException;

public class ClientPlayer extends Player {

    public ClientPlayer(String name, Marble marble) {
        super(name, marble);
    }

    @Override
    public String determineMove(Board board) {
        return null;
    }
    
    public void makeMove(Board board, String move) throws OffBoardException {
        setFields(board, move);
    }
}
