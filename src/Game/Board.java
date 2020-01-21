package Game;

public class Board {
	public static final int size = 61;
	public static final String SPACE = "  ";
	public static final String LINE = "|";
	public static final String ENTER = "\n";
	public static final String LBORDER = "/";
	public static final String RBORDER = "\\";
	public static final String UBORDER = 
			"\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F\uFF3F";
	public static final String BBORDER = 
			"\u203E1\u203E\u203E\u203E2\u203E\u203E\u203E3\u203E\u203E\u203E4\u203E\u203E\u203E5";	
	char[] horizontal = "ABCDEFGHI".toCharArray();
	int[] diagonal = {1,2,3,4,5,6,7,8,9};
	private Marble[] fields;
	public int players;
	
	public Board(int players) {
		fields = new Marble[size];
		this.players = players;
		reset();
	}
	
	public Board deepCopy() {
		Board copy = new Board(players);
		for (int i=0; i<size; i++) copy.setField(i, this.getMarble(i));
		return copy;
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
			result = size-(9-dia);
			for (int i=index; i < horizontal.length-1; i++) {
				result -= 9 - (i-3);
			}
		}
		return result-1;
	}
	
	public String getCoords(int i) {
		if (i < 5) return "A," + (i+1);
		else if (i < 11) return "B," + (i-4);
		else if (i < 18) return "C," + (i-10);
		else if (i < 26) return "D," + (i-17);
		else if (i < 35) return "E," + (i-26+1);
		else if (i < 43) return "F," + (i-35+2);
		else if (i < 50) return "G," + (i-43+3);
		else if (i < 56) return "H," + (i-50+4);
		else return "I," + (i-56+5);
	}
	
	public Marble getMarble(int index) {
		return (index != -1) ? fields[index] : null;
	}
	
	public Marble getMarble(char hor, int dia) {
		return getMarble(getIndex(hor, dia));
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
		else {
			for (int i=0; i < size; i++) {
				fields[i] = Marble.EMPTY;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int max;
		String line = "";
		for (int i=0; i<6; i++) {
			sb.append(SPACE);
		}
		sb.append(BBORDER);
		for (int i=0; i < 9; i++) {
			line = "";
			for (int j=0; j < Math.abs(i-4); j++) line += SPACE;
			line += horizontal[i] + " ";
			if (i<4) line += RBORDER;
			else if (i == 4) line += LINE;
			else line += LBORDER;
			for (int j=0; j < (max = 9 - Math.abs(i-4)); j++) {
				line += getRep(getMarble(index));
				index++;
				if (j < max - 1) line += LINE;
			}
			if (i<4) line += LBORDER + " " + (i+6);
			else if (i == 4) line += LINE;
			else line += RBORDER;
			line += ENTER;
			sb.insert(0,line);
		}
		line = "";
		for (int i=0; i<15; i++) {
			line += SPACE;
		}
		line += UBORDER + ENTER;
		sb.insert(0, line);
		return sb.toString();
	}	
}