package Game;

public abstract class Player {
	private String name;
	private Marble marble;
	private int points = 0;
	
	public Player(String name, Marble marble) {
		this.name = name;
		this.marble = marble;
	}
	
	public String getName() {
		return name;
	}
	
	public Marble getMarble() {
		return marble;
	}
	
	public int getPoints() {
		return points;
	}
	
	public abstract String determineMove(Board board);
	
	public int marbleTo(Board board, char hor, int dia, Direction dir) {
		int hori = (new String(board.horizontal)).indexOf(hor);
		int result = -1;
		try {
			if (dir == Direction.TOP_LEFT) result = board.getIndex(board.horizontal[hori+1], dia);
			else if (dir == Direction.TOP_RIGHT) result = board.getIndex(board.horizontal[hori+1], dia+1);
			else if (dir == Direction.RIGHT) result = board.getIndex(board.horizontal[hori], dia+1);
			else if (dir == Direction.BOTTOM_RIGHT) result = board.getIndex(board.horizontal[hori-1], dia);
			else if (dir == Direction.BOTTOM_LEFT) result = board.getIndex(board.horizontal[hori-1], dia-1);
			else result = board.getIndex(board.horizontal[hori], dia-1);
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
		return result;
	}
	
	public int marbleTo(Board board, int i, Direction dir) {
		String[] coords = board.getCoords(i).split(",");
		return marbleTo(board, coords[0].charAt(0), Integer.parseInt(coords[1]), dir);
	}
	/**
	 * 
	 * @param board
	 * @param move with first coord, last coord and direction
	 * @requires String move of form (e.g.) "5,A;5,B;1"
	 * @return move is inLine or not
	 */
	public boolean isInLine(Board board, String move) {
		String[] movesplit = move.split(";");
		String[] first = movesplit[0].split(",");
		String[] last = movesplit[1].split(",");
		char firsthor = first[1].charAt(0);
		char lasthor = last[1].charAt(0);
		int firsthori = (new String(board.horizontal)).indexOf(first[1]);
		int firstdiai = Integer.parseInt(first[0])-1;
		int	lasthori = (new String(board.horizontal)).indexOf(last[1]);
		int lastdiai = Integer.parseInt(last[0])-1;
		int dirvalue = Integer.parseInt(movesplit[2]);
		Direction dir = Direction.values()[dirvalue];
		
		if (firsthori == lasthori && firstdiai == lastdiai) return false;
		return marbleTo(board,firsthor,firstdiai,dir) == board.getIndex(lasthor, lastdiai) ||
				marbleTo(board, marbleTo(board,firsthor,firstdiai,dir),dir) == board.getIndex(lasthor, lastdiai) ||
				marbleTo(board,lasthor,lastdiai,dir) == board.getIndex(firsthor, firstdiai) ||
				marbleTo(board, marbleTo(board,lasthor,lastdiai,dir),dir) == board.getIndex(firsthor, firstdiai);
	}
	
	public String makeLeadingFirst(Board board, String move) {
		String[] movesplit = move.split(";");
		String[] first = movesplit[0].split(",");
		String[] last = movesplit[1].split(",");
		char firsthor = first[1].charAt(0);
		char lasthor = last[1].charAt(0);
		int firstdiai = Integer.parseInt(first[0])-1;
		int lastdiai = Integer.parseInt(last[0])-1;
		int dirvalue = Integer.parseInt(movesplit[2]);
		Direction dir = Direction.values()[dirvalue];
		if (marbleTo(board,firsthor,firstdiai,dir) == board.getIndex(lasthor, lastdiai)
				|| marbleTo(board,marbleTo(board,firsthor,firstdiai,dir),dir) == board.getIndex(lasthor, lastdiai)) {
			move = movesplit[1] + ";" + movesplit[0] + ";" + movesplit[2];
		}
		return move;
	}
	
	public boolean isValidMove(Board board, String move) {
		int tempscore = getPoints();
		Board copy = board.deepCopy();
		String[] movesplit = move.split(";");
		String[] first, last;
		char firsthor ,lasthor;
		int firstdiai,lastdiai,dirvalue;
		Direction dir;
		try {
			first = movesplit[0].split(",");
			last = movesplit[1].split(",");
			firsthor = first[1].charAt(0);
			lasthor = last[1].charAt(0);
			firstdiai = Integer.parseInt(first[0]);
			lastdiai = Integer.parseInt(last[0]);
			dirvalue = Integer.parseInt(movesplit[2]);
			dir = Direction.values()[dirvalue];
		}
		catch (Exception e) {
			return false;
		}
		try {
			boolean hasThree = false;
			int ball2 = -1;
			Marble teamMate = marble.next(4).next(4);
			for (int i = 0; i<6 && !hasThree; i++) { //test if moving three balls
				if (marbleTo(board, (ball2 = marbleTo(board,lasthor,lastdiai,Direction.values()[i])),Direction.values()[i]) == board.getIndex(firsthor,firstdiai))
					hasThree = true;
			}
			if (copy.getMarble(firsthor,firstdiai) != marble) {
				if (copy.getPlayers() != 4) return false;
				else if (copy.getMarble(firsthor,firstdiai) != teamMate) return false;
			}
			if (copy.getMarble(lasthor, lastdiai) != marble) {
				if (copy.getPlayers() != 4) return false;
				else if (copy.getMarble(lasthor,lastdiai) != teamMate) return false;//this part checks whether all selected
			}																		//marbles are of the player's team
			if (copy.getPlayers() == 4 && copy.getMarble(firsthor,firstdiai) != marble
					&& copy.getMarble(lasthor,lastdiai) != marble) {
				if (!hasThree) return false;
				else if (copy.getMarble(ball2) != marble) return false;//1+ of the moving marbles has to be player's marble
			}
			if (hasThree) {
				if (copy.getMarble(ball2) != marble) {
					if (copy.getPlayers() != 4) return false;
					else if (copy.getMarble(ball2) != teamMate) return false;
				}
			}
			if (isInLine(board,move)) {
				if (board.getMarble(lasthor, lastdiai) != marble) return false;
				if (board.getMarble(marbleTo(board, firsthor, firstdiai, dir)) == Marble.EMPTY) return true;
				if (board.getMarble(marbleTo(board, firsthor, firstdiai, dir)) == marble) return false;
				if (copy.getPlayers() == 4) {
					if (board.getMarble(marbleTo(board, firsthor, firstdiai, dir)) == teamMate) return false;
				}
				int upTwoi = marbleTo(board,marbleTo(board,firsthor,firstdiai,dir),dir);
				Marble upTwo = board.getMarble(upTwoi);
				Marble upThree = board.getMarble(marbleTo(board,upTwoi,dir));
				if (upTwo != null && upTwo != Marble.EMPTY) {
					if (!hasThree) return false;
					if (upThree != null && upThree != Marble.EMPTY) return false;
				}
				if (hasThree) {
					if (upTwo == marble) return false;
					if (copy.getPlayers() == 4 && upTwo == teamMate) return false;
				}
			}
			else {
				if (copy.getMarble(marbleTo(copy,firsthor,firstdiai,dir)) != Marble.EMPTY
						|| copy.getMarble(marbleTo(copy,lasthor,lastdiai,dir)) != Marble.EMPTY) return false;
				if (hasThree && copy.getMarble(marbleTo(copy,ball2,dir)) != Marble.EMPTY) return false;
			}
			setFields(copy,move);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		finally {
			points = tempscore;
		}
		return true;
	}
	
	public void setFields(Board board, String move) {//e.g. 5,A;5,B;1
		String[] movesplit = move.split(";");
		String[] first = movesplit[0].split(",");
		String[] last = movesplit[1].split(",");
		Marble toMarble = Marble.EMPTY;
		char firsthor = first[1].charAt(0);
		char lasthor = last[1].charAt(0);
		int firstdiai = Integer.parseInt(first[0]);
		int lastdiai = Integer.parseInt(last[0]);
		int dirvalue = Integer.parseInt(movesplit[2]);
		Direction dir = Direction.values()[dirvalue];
		int ball2 = -1;
		boolean hasThree = false;
		for (int j = 0; j<6 && !hasThree; j++) {
			if (marbleTo(board, (ball2 = marbleTo(board,firsthor,firstdiai,Direction.values()[j])),Direction.values()[j]) == board.getIndex(lasthor,lastdiai)) {
				hasThree = true;
			}
		}
		if (isInLine(board,move)) {
			int upTwo = 100, upThree = 100;
			int toMarblei;
			if ((toMarble = board.getMarble((toMarblei = marbleTo(board,firsthor,firstdiai,dir)))) == Marble.EMPTY) {//first marble is leading
				board.setField(toMarblei, marble);
				board.setField(board.getIndex(lasthor,lastdiai), Marble.EMPTY);
			}
			else if (toMarble != marble) { //if bumping into another player
				upTwo = marbleTo(board,toMarblei,dir);
				upThree = marbleTo(board,upTwo,dir);
				if (hasThree) {
					if (upTwo == -1 || upThree == -1) points++; //point for pushing marble off 
				}
				else {
					if (upTwo == -1) points++; //point for pushing marble off
				}
				board.setField(toMarblei, marble);
				board.setField(board.getIndex(lasthor,lastdiai), Marble.EMPTY);
				if (upTwo != -1) {
					if (upThree != -1) board.setField(upThree, board.getMarble(marbleTo(board,toMarblei,dir)));
					board.setField(upTwo, toMarble);
				}
			}
		}
		else {
			board.setField(marbleTo(board,firsthor,firstdiai,dir), marble);
			board.setField(firsthor, firstdiai, Marble.EMPTY);
			board.setField(marbleTo(board,lasthor,lastdiai,dir), marble);
			board.setField(lasthor, lastdiai, Marble.EMPTY);
			if (hasThree) {
				board.setField(marbleTo(board,ball2,dir), marble);
				board.setField(ball2, Marble.EMPTY);
			}
		}
	}
	
	public void makeMove(Board board) {
		String move = determineMove(board);
		setFields(board,move);
	}
}
