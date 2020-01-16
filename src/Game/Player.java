package Game;

public abstract class Player {
	private String name;
	private Marble marble;
	
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
	
	public abstract String determineMove(Board board);
	
	public int marbleTo(Board board, char hor, int dia, Direction dir) {
		int hori = (new String(board.horizontal)).indexOf(hor);
		if (dir == Direction.TOP_LEFT) return board.getIndex(board.horizontal[hori+1], dia);
		else if (dir == Direction.TOP_RIGHT) return board.getIndex(board.horizontal[hori+1], dia+1);
		else if (dir == Direction.RIGHT) return board.getIndex(board.horizontal[hori], dia+1);
		else if (dir == Direction.BOTTOM_RIGHT) return board.getIndex(board.horizontal[hori-1], dia);
		else if (dir == Direction.BOTTOM_LEFT) return board.getIndex(board.horizontal[hori-1], dia-1);
		else return board.getIndex(board.horizontal[hori], dia-1);
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
	
	public void makeMove(Board board) {
		String move = determineMove(board); //e.g. 5,A;5,B;1
		String[] movesplit = move.split(";");
		String[] first = movesplit[0].split(",");
		String[] last = movesplit[1].split(",");
		
		char firsthor = first[1].charAt(0);
		char lasthor = last[1].charAt(0);
		int firsthori = (new String(board.horizontal)).indexOf(first[1]);
		int firstdiai = Integer.parseInt(first[0]);
		int	lasthori = (new String(board.horizontal)).indexOf(last[1]);
		int lastdiai = Integer.parseInt(last[0]);
		int dirvalue = Integer.parseInt(movesplit[2]);
		Direction dir = Direction.values()[dirvalue];
		if (isInLine(board,move)) {
			int i;
			if (board.getMarble((i = marbleTo(board,firsthor,firstdiai,dir))) == Marble.EMPTY) {
			board.setField(i, marble);
			board.setField(board.getIndex(lasthor,lastdiai), Marble.EMPTY);
			}
			else {
				board.setField(marbleTo(board,lasthor,lastdiai,dir), marble);
				board.setField(board.getIndex(firsthor, firstdiai), Marble.EMPTY);
			}
		}
		else {
			boolean hasThree = false;
			int ball2 = -1;
			for (int i = 0; i<6 && !hasThree; i++) {
				if (marbleTo(board, (ball2 = marbleTo(board,firsthor,firstdiai,Direction.values()[i])),Direction.values()[i]) == board.getIndex(lasthor,lastdiai)) {
					hasThree = true;
				}
			}
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
}
