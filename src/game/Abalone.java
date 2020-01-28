package game;

import java.util.Scanner;

public class Abalone {
    /**
     * Main class for initialising an Abalone game.
     */
    public static void main(String[] args) {
        int players;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("How many players?");
            try {
                players = Integer.parseInt(scanner.nextLine());
                if (players >= 2 && players <= 4) {
                    break;
                }
            } catch (NumberFormatException e) {
                // If something else than an integer was entered
            }
            System.out.println("Please enter an integer from 2 to 4");
        }
        System.out.println("Player 1, what is your name?");
        final String player1 = scanner.nextLine();
        System.out.println("Player 2, what is your name?");
        String player2 = scanner.nextLine();
        String player3 = "";
        if (players > 2) {
            System.out.println("Player 3, what is your name?");
            player3 = scanner.nextLine();
        }
        String player4 = "";
        if (players > 3) {
            System.out.println("Player 4, what is your name?");
            player4 = scanner.nextLine();
        }
        Player p1;
        Player p2;
        Player p3;
        Player p4;
        Game game;
        if (player1.equals("-BOT")) {
        	p1 = new ComputerPlayer(player1, Marble.BLACK);
        }
        else {
            p1 = new HumanPlayer(player1, Marble.BLACK);
        }
        if (player2.equals("-BOT")) {
        	p2 = new ComputerPlayer(player2, Marble.WHITE);
        }
        else {
            p2 = new HumanPlayer(player2, Marble.WHITE);

        }
        game = new Game(p1, p2);
        if (players == 3) {
        	p2.setMarble(Marble.BLUE);
        	if (player3.equals("-BOT")) {
        		p3 = new ComputerPlayer(player3, Marble.WHITE);
        	}
        	else {
        		 p3 = new HumanPlayer(player3, Marble.WHITE);
        	}
            game = new Game(p1, p2, p3);
        } else if (players == 4) {
            if (player3.equals("-BOT")) {
            	p3 = new ComputerPlayer(player3, Marble.BLUE);
            }
            else {
                p3 = new HumanPlayer(player3, Marble.BLUE);
            }
            if (player4.equals("-BOT")) {
            	p4 = new ComputerPlayer(player4, Marble.RED);
            }
            else {
                p4 = new HumanPlayer(player4, Marble.RED);
            }
            game = new Game(p1, p2, p3, p4);
        }
        game.start();
        scanner.close();
    }
}
