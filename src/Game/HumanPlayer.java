package Game;

import java.util.Scanner;

public class HumanPlayer extends Player {
	Scanner scanner;
	
	public HumanPlayer(String name, Marble marble) {
		super(name,marble);
		scanner = new Scanner(System.in);
	}
	
	public String makeGoodFormat(String input) {
		String[] marblesplit = input.split(",");
		try {
			Integer.parseInt(marblesplit[0]);
		}
		catch (NumberFormatException e) {
			input = marblesplit[1] + "," + marblesplit[0];
		}
		return input;
	}
	
	public String makeGoodInput(Board board, String input) {
		String[] marblesplit = input.split(",");
		String result = input;
		while (marblesplit.length != 2) {
			System.out.println("> Please try again.\n  Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
			result = makeGoodFormat(scanner.nextLine().toUpperCase());
			marblesplit = result.split(",");
		}
		while (true) {
			try {		
				while (board.getMarble(marblesplit[1].charAt(0),Integer.parseInt(marblesplit[0])) != getMarble()) {
					System.out.println("> This is not one of your marbles, please try again");
					result = makeGoodFormat(scanner.nextLine().toUpperCase());
					marblesplit = result.split(",");
				}
				break;
			}
			catch (Exception e) {
				System.out.println("> Invalid input, please try again.\n  Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
				result = makeGoodFormat(scanner.nextLine());
				marblesplit = result.split(",");
			}
		}
		return result;
	}
	
	public String determineMove(Board board) {
		System.out.println("> " + getName() + " (" + getMarble().toString() + "),\n  what is (one of) the outer marble(s) you want to move?\n  e.g. (A,1 or 1,A) ");
		String marble1;
		while(true) {
			try {
				System.out.println();
				marble1 = makeGoodFormat(scanner.nextLine().toUpperCase());
				break;
			}
			catch (ArrayIndexOutOfBoundsException e) {
			}
			System.out.println("> Please try again.\n   Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n   e.g. A,1 or 1,A");
		}
		marble1 = makeGoodInput(board,marble1);
		System.out.println("> What is the other outer marble you want to move?\n  If you only want to move 1 marble, enter the same one again");
		String marble2;
		while(true) {
			try {
				marble2 = makeGoodFormat(scanner.nextLine().toUpperCase());
				break;
			}
			catch (ArrayIndexOutOfBoundsException e) {
			}
			System.out.println("> Please try again.\n  Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
		}
		marble2 = makeGoodInput(board,marble2);
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<6; i++) {
			sb.append("\n" + i + ": " + Direction.values()[i]);
		}
		sb.append("\n");
		int dir;
		while (true) {
			try {
				System.out.println("> In which direction do you want to move?");
				System.out.println(sb.toString());
				dir = Integer.parseInt(scanner.nextLine());
				if (!(dir < 0 || dir > 5)) break;
			}
			catch (NumberFormatException e) {
			}	
			System.out.println("\nPlease enter a number between 0 and 5\n");
		}
		String move = marble1 + ";" + marble2 + ";" + dir;
		move = makeLeadingFirst(board,move);
		boolean valid = isValidMove(board,move);
		if (!valid) {
			System.out.println("Invalid move, please try again\n");
			move = determineMove(board);
		}
		
		return move;
	}
}
