package Game;

import java.util.Scanner;

public class Abalone {
	
	public static void main(String[] args) {
		int players;
		String player1 = "",player2 = "",player3 = "",player4="";
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("How many players?");
			try {
				players = Integer.parseInt(scanner.nextLine());
				if (players >= 2 && players <= 4) break;
			}
			catch (NumberFormatException e) {
			}
			System.out.println("Please enter an integer from 2 to 4");
		}
		System.out.println("Player 1, what is your name?");
		player1 = scanner.nextLine();
		System.out.println("Player 2, what is your name?");
		player2 = scanner.nextLine();
		if (players > 2) {
			System.out.println("Player 3, what is your name?");
			player3 = scanner.nextLine();
		}
		if (players > 3) {
			System.out.println("Player 4, what is your name?");
			player4 = scanner.nextLine();
		}
		Player p1,p2,p3,p4;
		Game game;
		p1 = new HumanPlayer(player1,Marble.WHITE);
		p2 = new HumanPlayer(player2,Marble.BLACK);
		p3 = new HumanPlayer(player3, Marble.BLUE);
		p4 = new HumanPlayer(player4, Marble.BLUE);
		if (players == 4) {
			p2 = new HumanPlayer(player2,Marble.RED);
			p3 = new HumanPlayer(player3, Marble.BLACK);
		}		
		game = new Game(p1,p2);
		if (players == 3) game = new Game(p1,p2,p3);
		else if (players == 4) game = new Game(p1,p2,p3,p4);
		game.start();
		scanner.close();
		
	}
}
