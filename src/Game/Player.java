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
	
	public void setPoints(int points) {
		this.points = points;
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
