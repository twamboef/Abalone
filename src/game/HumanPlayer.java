package game;

import exceptions.OffBoardException;
import java.util.Scanner;

public class HumanPlayer extends Player {
    Scanner scanner;

    public HumanPlayer(String name, Marble marble) {
        super(name, marble);
        scanner = new Scanner(System.in);
    }

    /**
     * Makes sure the input consists of an integer and a character with a comma
     * between them (order doesn't matter).
     * 
     * @param board game board
     * @param input input first input try
     * @return coordinate consisting of an integer and a character
     */
    public String makeGoodInput(Board board, String input) {
        String[] marblesplit = input.split(",");
        String result = input;
        while (marblesplit.length != 2 || marblesplit[0].equals("") || marblesplit[1].equals("")) {
            System.out.println(
                    "> Please try again.\n  " + "Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
            result = scanner.nextLine().toUpperCase();
            marblesplit = result.split(",");
        }
        result = makeGoodFormat(board, result).toUpperCase();
        boolean hasHorizontal = false;
        while (true) {
            marblesplit = result.split(",");
            for (int i = 0; i < board.horizontal.length; i++) {
                if (marblesplit[1].charAt(0) == board.horizontal[i]) {
                    hasHorizontal = true;
                }
            }
            if (!hasHorizontal) {
                System.out.println(">Invalid input, please try again.\n  "
                        + "Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
                result = makeGoodInput(board, scanner.nextLine());
            } else {
                break;
            }
        }
        while (true) {
            try {
                Marble marble;
                while ((marble = board.getMarble(marblesplit[1].charAt(0),
                        Integer.parseInt(marblesplit[0]))) != getMarble()) {
                    if (board.getPlayers() == 4) {
                        if (marble == getMarble().next(4).next(4)) {
                            break;
                        }
                    }
                    System.out.println("> This is not one of your marbles, please try again");
                    result = makeGoodInput(board, scanner.nextLine().toUpperCase());
                    marblesplit = result.split(",");
                }
                break;
            } catch (Exception e) {
                System.out.println("> Invalid input, please try again.\n  "
                        + "Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
                result = makeGoodInput(board, scanner.nextLine());
                marblesplit = result.split(",");
            }
        }
        return result;
    }

    /**
     * Asks the user to input two coordinates and a Direction.
     */
    public String determineMove(Board board) {
        System.out.println("> " + getName() + " (" + getMarble().toString() + "),\n  "
                + "what is (one of) the outer marble(s) you want to move?\n  e.g. (A,1 or 1,A) ");
        final String marble1 = makeGoodInput(board, scanner.nextLine().toUpperCase());
        System.out.println("> What is the other outer marble you want to move?\n  "
                + "If you only want to move 1 marble, enter the same one again");
        final String marble2 = makeGoodInput(board, scanner.nextLine().toUpperCase());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append("\n" + i + ": " + Direction.values()[i]);
        }
        sb.append("\n");
        int dir;
        while (true) {
            try {
                System.out.println("> In which direction do you want to move?");
                System.out.println(sb.toString());
                dir = Integer.parseInt(scanner.nextLine());
                if (!(dir < 0 || dir > 5)) {
                    break;
                }
            } catch (NumberFormatException e) {
                // error occurs if no integer is entered, enters while loop again
            }
            System.out.println("\nPlease enter a number between 0 and 5\n");
        }
        String move = marble1 + ";" + marble2 + ";" + dir;
        try {
            move = makeLeadingFirst(board, move);
        } catch (OffBoardException e) {
            // board.isValidMove() will fix this
        }
        boolean valid = false;
        try {
            valid = board.isValidMove(this, move);
        } catch (OffBoardException e) {
            // will return false in case of exceptions
        }
        if (!valid) {
            System.out.println("Invalid move, please try again\n");
            move = determineMove(board);
        }
        return move;
    }
}
