package Game;

import java.util.ArrayList;
import java.util.List;

public class SmartAI implements Strategy {

	@Override
	public String determineMove(Board board, Marble marble) {
		List<Integer> empty = new ArrayList<Integer>();
		for (int i = 0; i < 61; i ++) {
			if (board.isEmptyField(i)) {
				empty.add(i);
			}
		}
		Board copy = board.deepCopy();
		
		//make a random move 
		int marble1 = empty.get((int) (Math.random()*empty.size()));
		int marble2 = empty.get((int) (Math.random()*empty.size()));
		int dir = empty.get((int) (Math.random());
		
		
		String move = marble1 + ";" + marble2 + ";" + dir;
		return move;
		
		
		if (isValidMove(copy, move) {
			
		}
	}
}
