package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exceptions.OffBoardException;

public class SmartAI {

	public String determineMove(Board board, Marble marble) {
		List<Integer> myMarbles = new ArrayList<Integer>();
		myMarbles = board.getMyMarbles(marble);
		
		//check if you can win the game
		Board copy = null;
        try {
            copy = board.deepCopy();
        } catch (OffBoardException e) {
           // Hard coded, so won't happen
        }
		
		//makes a random move
        Random rand = new Random();
		int m1 = rand.nextInt(myMarbles.size());
		int m2 = rand.nextInt(myMarbles.size());
		int d = (int) (6.00 * Math.random());
		Direction dir = null;
		if(d == 0) {
			dir = Direction.TOP_LEFT;
		}
		else if(d == 1) {
			dir = Direction.TOP_RIGHT;
		}
		else if(d == 2) {
			dir = Direction.RIGHT;
		}
		else if(d == 3) {
			dir = Direction.BOTTOM_RIGHT;
		}
		else if(d == 4) {
			dir = Direction.BOTTOM_LEFT;
		}
		else if(d == 5) {
			dir = Direction.LEFT;
		}
		
		String marble1 = null;
		String marble2 = null;
        try {
            marble1 = board.getCoords(m1);
            marble2 = board.getCoords(m2);
        } catch (OffBoardException e) {
           e.printStackTrace();
        }
		
		String p1name = "pc";
		Player p1 = new ComputerPlayer(p1name, Marble.WHITE);
		String move = marble1 + ";" + marble2 + ";" + dir;
		
		//checks if the move proposed is valid, if not it makes a new move
		try {
            if (board.isValidMove(p1, move) == false) {
            	determineMove(board, marble);
            }
        } catch (OffBoardException e) {
            determineMove(board,marble);
        }
		return move;
	}
	
}
