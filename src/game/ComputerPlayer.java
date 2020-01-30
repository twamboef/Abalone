package game;

import exceptions.OffBoardException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String name, Marble marble) {
        super(name, marble);
    }

    /**
     * Makes sure the coordinate is in the format INTEGER, CHARACTER.
     * 
     * @param input coordinate
     * @return correctly formatted coordinate
     */
    public String makeGoodFormat(Board board, String input) {
        String[] marblesplit = input.split(",");
        try {
            Integer.parseInt(marblesplit[0]);
        } catch (NumberFormatException e) {
            input = marblesplit[1] + "," + marblesplit[0];
        }
        return input;
    }

    @Override
    public String determineMove(Board board) {
        List<Integer> myMarbles = new ArrayList<Integer>();
        myMarbles = board.getMyMarbles(marble);

        // make a list of possible moves
        List<String> myMoves = new ArrayList<String>();
        Random rand = new Random();
        Iterator<Integer> iterator = myMarbles.iterator();
        Iterator<Integer> iterator2;
        String marble1 = "";
        String marble2 = "";
        while (iterator.hasNext()) {
            iterator2 = myMarbles.iterator();
            int m1 = iterator.next();
            while (iterator2.hasNext()) {
                int m2 = iterator2.next();
                try {
                    marble1 = board.getCoords(m1);
                } catch (OffBoardException e) {
                    e.printStackTrace();
                }
                try {
                    marble2 = board.getCoords(m2);
                } catch (OffBoardException e) {
                    e.printStackTrace();
                }
                for (int dir = 0; dir < 6; dir++) {
                    String move = makeGoodFormat(board, marble1) + ";" + makeGoodFormat(board, marble2) + ";" + dir;
                    try {
                        if (board.isValidMove(this, move)) {
                            myMoves.add(move);
                        }
                    } catch (OffBoardException e) {
                        // can't happen because isValidMove is first called
                    }
                }
            }
        }
        // randomly choose one of the possible moves
        return myMoves.get(rand.nextInt(myMoves.size()));
    }

}