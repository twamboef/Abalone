package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Game.Board;
import Game.HumanPlayer;
import Game.Marble;
import Game.Player;

class MakeMoveTest {
	Player player1, player2;
	Board board2, testpushboard;
	
	@BeforeEach
	void setUp() {
		board2 = new Board(2);
		testpushboard = new Board(0);
		player1 = new HumanPlayer("Twan",Marble.WHITE);
		player2 = new HumanPlayer("Tim", Marble.BLACK);
	}

	@Test
	void testIsValidMove() {
		assertFalse(player1.isValidMove(board2, "5,I;5,I;2"),"Enemy marble");
		assertFalse(player1.isValidMove(board2, "1,E;1,E;2"),"Empty field");
		assertFalse(player1.isValidMove(board2, "3,C;5,C;3"),"Move into own marbles");
		assertFalse(player1.isValidMove(board2, "1,A;1,A;5"),"Move off field");
		
		assertTrue(player1.isValidMove(board2, "3,C;5,C;1"),"Broadside move");
		assertTrue(player1.isValidMove(board2, "3,C;3,A;0"),"InLine move to empty");
		
		
		testpushboard.setField(1, Marble.WHITE);
		testpushboard.setField(2, Marble.WHITE);
		testpushboard.setField(3, Marble.BLACK);
		testpushboard.setField(4, Marble.BLACK);
		
		assertFalse(player1.isValidMove(testpushboard, "3,A;2,A;2"),"2 marbles push 2");
		
		testpushboard.setField(0, Marble.WHITE);
		
		assertTrue(player1.isValidMove(testpushboard, "3,A;1,A;2"),"3 marbles push 2");
		
		testpushboard.setField(3, Marble.WHITE);
		
		assertTrue(player1.isValidMove(testpushboard, "4,A;3,A;2"),"2 marbles push 1");
		assertFalse(player1.isValidMove(testpushboard, "4,A;4,A;2"),"1 marble pushes 1");
		
		testpushboard.setPlayers(4);
		testpushboard.setField(3, Marble.BLUE);
		
		assertFalse(player1.isValidMove(testpushboard,"3,A;1,A;2"),"cant push own team");
		
		testpushboard.setField(1, Marble.BLACK);
		testpushboard.setField(4, Marble.BLUE);
		
		assertTrue(player1.isValidMove(testpushboard,"3,A;1,A;2"),"teamup push");
	}
	
	@Test
	void testPoints() {
		testpushboard.setField(0, Marble.WHITE);
		testpushboard.setField(1, Marble.WHITE);
		testpushboard.setField(2, Marble.WHITE);
		testpushboard.setField(3, Marble.BLACK);
		testpushboard.setField(4, Marble.BLACK);
		
		assertEquals(player1.getPoints(),0,"0 points at start");
		
		player1.setFields(testpushboard,"3,A;1,A;2");
		assertEquals(player1.getPoints(),1, "1 point after pushing off one");
		
		player1.setFields(testpushboard,"4,A;2,A;2");
		assertEquals(player1.getPoints(),2, "2 points after pushing off two");
		
		testpushboard.setField(0, Marble.WHITE);
		testpushboard.setField(1, Marble.WHITE);
		testpushboard.setField(2, Marble.WHITE);
		testpushboard.setField(3, Marble.RED);
		testpushboard.setField(4, Marble.BLUE);
		player1.setFields(testpushboard,"3,A;1,A;2");
		
		assertEquals(player1.getPoints(),3, "point after pushing marble off by using another team's marble");
	}
}
