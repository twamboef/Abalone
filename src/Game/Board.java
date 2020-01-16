package Game;

public class Board {
	public static final int size = 61;
	public static final String SPACE = "  ";
	public static final String LINE = "|";
	public static final String ENTER = "\n";
	public static final String LBORDER = "/";
	public static final String RBORDER = "\\";
	public static final String BORDER = "+---+---+---+---+";	
	char[] horizontal = "ABCDEFGHI".toCharArray();
	int[] diagonal = {1,2,3,4,5,6,7,8,9};
	private Marble[] fields;
	public int players;
	
	public Board(int players) {
		fields = new Marble[size];
		this.players = players;
		reset();
	}
	/**
	 * @ensures valid index returned
	 * @returns index if valid, -1 if invalid
	 * @param players
	 */
	public int getIndex(char hor, int dia) {
		if (!isValidField(hor,dia)) return -1;
		int index = new String(horizontal).indexOf(hor);
		int result = 0;
		if (index <= 4) {
			result = dia;
			for (int i=0; i < index; i++) {
				result += 5 + i;
			}
		}
		else {
			result = size-(9-dia); //61-(13-6-2)=56 56-(5-1)-(5)=56
			for (int i=index; i < horizontal.length-1; i++) {
				result -= 9 - (i-3);
			}
		}
		return result-1;
	}
	
	public String getCoords(int i) {
		if (i < 6) return "A," + i;
		else if (i < 12) return "B," + (i-5);
		else if (i < 19) return "C," + (i-11);
		else if (i < 27) return "D," + (i-18);
		else if (i < 36) return "E," + (i-26+1);
		else if (i < 44) return "F," + (i-35+2);
		else if (i < 51) return "G," + (i-43+3);
		else if (i < 57) return "H," + (i-50+4);
		else return "I," + (i-56+5);
	}
	
	public Marble getMarble(int index) {
		return fields[index];
	}
	
	public Marble getMarble(char hor, int dia) {
		return fields[getIndex(hor,dia)];
	}
	
	public String getRep(Marble m) {
		if (m == Marble.WHITE) return "WHI";
		else if (m == Marble.BLACK) return "BLK";
		else if (m == Marble.BLUE) return "BLU";
		else if (m == Marble.RED) return "RED";
		else return "   ";
	}
	
	public int getPlayers() {
		return players;
	}
	
	public void setPlayers(int players) {
		this.players = players;
	}
	
	public void setField(int i, Marble m) {
		fields[i] = m;
	}
	
	public void setField(char hor, int dia, Marble m) {
		fields[getIndex(hor,dia)] = m;
	}
	
	public boolean isValidHorizontal(char hor) {
		for (int i=0; i < horizontal.length; i++) {
			if (horizontal[i] == hor) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidDiagonal(char hor, int dia) {
		int index = new String(horizontal).indexOf(hor);
		if (index == -1) {
			return false;
		}
		else if (index <= 4) {
			return dia <= index+5;
		}
		else {
			return dia >= index - 3 && dia <= 9;
		}
	}
	
	public boolean isValidField(char hor, int dia) {
		return isValidHorizontal(hor) && isValidDiagonal(hor, dia);
	}
	
	public boolean isValidField(int index) {
		return index <= size && index >= 0;
	}
	
	public boolean isEmptyField(int index) {
		return fields[index] == Marble.EMPTY;
	}
	
	public boolean isEmptyField(char hor, int dia) {
		return fields[getIndex(hor,dia)] == Marble.EMPTY;
	}
	
//	public boolean gameOver() {
//		//TODO players gain points when pushing off other marbles
//		//with 2/3 players, any player needs 6 points. with 4 players, black+white needs 6 points or red+blue needs 6 points.
//	}
	
	public void reset() {
		if (players == 2) {
			for (int i = 0; i<11;i++) {
				fields[i] = Marble.WHITE;
			}
			for (int i = 13; i<16; i++) {
				fields[i] = Marble.WHITE;
			}
			for (int i = 45; i<48; i++) {
				fields[i] = Marble.BLACK;
			}
			for (int i = 50; i < 61; i++) {
				fields[i] = Marble.BLACK;
			}
			for (int i=0; i < size; i++) {
				if (fields[i] == null) fields[i] = Marble.EMPTY;
			}
		}
		else if (players == 3) {
			for (int i = 0; i<11;i++) {
				fields[i] = Marble.BLUE;
			}
			fields[18] = Marble.WHITE;
			fields[25] = Marble.BLACK;
			for (int i = 4; i < horizontal.length; i++) {
				fields[getIndex(horizontal[i], i-3)] = Marble.WHITE;
				fields[getIndex(horizontal[i], i-2)] = Marble.WHITE;
				fields[getIndex(horizontal[i], 8)] = Marble.BLACK;
				fields[getIndex(horizontal[i], 9)] = Marble.BLACK;
			}
			for (int i=0; i < size; i++) {
				if (fields[i] == null) fields[i] = Marble.EMPTY;
			}
		}
		else if (players == 4) {
			for (int i=1; i<5; i++) {
				fields[i] = Marble.BLUE;
			}
			fields[5] = Marble.WHITE;
			for (int i=7; i<10; i++) {
				fields[i] = Marble.BLUE;
			}
			fields[11] = Marble.WHITE;
			fields[12] = Marble.WHITE;
			fields[14] = Marble.BLUE;
			fields[15] = Marble.BLUE;
			for (int i = 18; i < 21; i++) {
				fields[i] = Marble.WHITE;
			}
			for (int i = 26; i < 29; i++) {
				fields[i] = Marble.WHITE;
			}
			for (int i = 32; i < 35; i++) {
				fields[i] = Marble.BLACK;
			}
			for (int i = 40; i < 43; i++) {
				fields[i] = Marble.BLACK;
			}
			fields[45] = Marble.RED;
			fields[46] = Marble.RED;
			fields[48] = Marble.BLACK;
			fields[49] = Marble.BLACK;
			for (int i = 51; i < 54; i++) {
				fields[i] = Marble.RED;
			}
			fields[55] = Marble.BLACK;
			for (int i = 56; i < 60; i++) {
				fields[i] = Marble.RED;
			}
			for (int i=0; i < size; i++) {
				if (fields[i] == null) fields[i] = Marble.EMPTY;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<5; i++) {
			sb.append(SPACE);
		}
		sb.append(BORDER + ENTER);
		for (int i=0; i<4; i++) {//ROW 1
			sb.append(SPACE);
		}
		sb.append(LBORDER);
		for (int i=56; i<61; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 60) sb.append(LINE);
		}
		sb.append(RBORDER + ENTER);
		for (int i=0; i<3; i++) {//ROW 2
			sb.append(SPACE);
		}
		sb.append(LBORDER);
		for (int i=50; i<56; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 55) sb.append(LINE);
		}
		sb.append(RBORDER + ENTER + SPACE + SPACE + LBORDER);//ROW 3
		for (int i=43; i<50; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 49) sb.append(LINE);
		}
		sb.append(RBORDER + ENTER + SPACE + LBORDER);//ROW 4
		for (int i=35; i<43; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 42) sb.append(LINE);
		}
		sb.append(RBORDER + ENTER + LINE);//ROW 5
		for (int i=26; i<35; i++) {
			sb.append(getRep(getMarble(i)));
			sb.append(LINE);
		}
		sb.append(ENTER + SPACE + RBORDER);//ROW 6
		for (int i=18; i<26; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 25) sb.append(LINE);
		}
		sb.append(LBORDER + ENTER + SPACE + SPACE + RBORDER);//ROW 7
		for (int i=11; i<18; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 17) sb.append(LINE);
		}
		sb.append(LBORDER + ENTER);//ROW 8
		for (int i=0; i<3; i++) {
			sb.append(SPACE);
		}
		sb.append(RBORDER);
		for (int i=5; i<11; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 10) sb.append(LINE);
		}
		sb.append(LBORDER + ENTER);
		for (int i=0; i<4; i++) {//ROW 9
			sb.append(SPACE);
		}
		sb.append(RBORDER);
		for (int i=0; i<5; i++) {
			sb.append(getRep(getMarble(i)));
			if (i < 4) sb.append(LINE);
		}
		sb.append(LBORDER + ENTER);
		for (int i=0; i<5; i++) {
			sb.append(SPACE);
		}
		sb.append(BORDER);
		return sb.toString();
	}
	public static void main(String[] args) {
		Board board2 = new Board(2);
		Board board3 = new Board(3);
		Board board4 = new Board(4);
		System.out.println(board2.toString() + "\n\n");
		System.out.println(board3.toString() + "\n\n");
		System.out.println(board4.toString());
	}
}