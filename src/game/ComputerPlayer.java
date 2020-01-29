package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import exceptions.OffBoardException;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String name, Marble marble) {
        super(name, marble);
    }
    
    /**
     * Makes sure the coordinate is in the format INTEGER, CHARACTER.
     * 
     * @param input coordinate
     * @return correctly formatted coordinate
     */
    public String makeGoodFormat(Board board, String input) {
        String[] marblesplit = input.split(",");
        while (marblesplit.length != 2 || marblesplit[0].equals("") || marblesplit[1].equals("")) {
        	input = makeGoodInput(board, input);
        }
        try {
            Integer.parseInt(marblesplit[0]);
        } catch (NumberFormatException e) {
            input = marblesplit[1] + "," + marblesplit[0];
        }
        return input;
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
            //result = scanner.nextLine().toUpperCase();
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
                 //result = makeGoodInput(board, scanner.nextLine());
    		}
    		else {
    			break;
    		}
       	}
        while (true) {
            try {
            	Marble marble;
                while ((marble = board.getMarble(marblesplit[1].charAt(0), Integer.parseInt(marblesplit[0]))) != getMarble()) {
                    if (board.getPlayers() == 4) {
                    	if (marble == getMarble().next(4).next(4)) {
                    		break;
                    	}
                    }
   	            	System.out.println("> This is not one of your marbles, please try again");
       	         //   result = makeGoodInput(board, scanner.nextLine().toUpperCase());
        	           marblesplit = result.split(",");
                }
                break;
            } catch (Exception e) {
                System.out.println("> Invalid input, please try again.\n  "
                        + "Format: CHARACTER,INTEGER or INTEGER,CHARACTER\n  e.g. A,1 or 1,A");
                //result = makeGoodInput(board, scanner.nextLine());
                marblesplit = result.split(",");
            }
        }
        return result;
       	}


    
    @Override
    public String determineMove(Board board) {
    	List<Integer> myMarbles = new ArrayList<Integer>();
		myMarbles = board.getMyMarbles(marble);
		
		//make a list of possible moves
        List<String> myMoves = new ArrayList<String>();
        Random rand = new Random();
        Iterator<Integer> iterator= myMarbles.iterator();
        Iterator<Integer> iterator2 = myMarbles.iterator();
        String marble1 = "";
        String marble2 = "";
        
        while(iterator.hasNext()) {
        	int m1 = iterator.next();
        	while(iterator2.hasNext()) {
        		int m2 = iterator2.next();
        		try {
					marble1 = board.getCoords(m1);
				} catch (OffBoardException e) {
					e.printStackTrace();
				}
        		try {
					marble2 = board.getCoords(m2);
				} catch (OffBoardException e) {
					e.printStackTrace();
				}
        		for(int dir = 0; dir < 6; dir++) {
        			String move = makeGoodFormat(board,marble1) + ";" + makeGoodFormat(board, marble2) + ";" + dir;
        			
        			try {
						if(board.isValidMove(this, move)) {
							myMoves.add(move);
						}
					} catch (OffBoardException e) {
					}
        		}
        	}
        }
        //randomly choose one of the possible moves
        return myMoves.get(rand.nextInt(myMoves.size()));
    }
    
}