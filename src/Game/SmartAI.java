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
		List<Integer> middle = new ArrayList<Integer>();
		middle.add(22);
		middle.add(23);
		middle.add(30);
		middle.add(31);
		middle.add(32);
		middle.add(39);
		middle.add(40);
		empty.retainAll(middle);
		
		
		return null;
	}
}
