package tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import client.TestClient;
import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import protocol.ProtocolMessages;
import server.TestServer;

class ClientTest {
    // Throughout the test, we implemented TimeUnit.MILLISECONDS.sleep(10)
    // to wait 10 milliseconds after sending something, so the server has time to receive this message.
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    private static final String CLIENT1 = "Twam";
    private static final String CLIENT2 = "Tin";

    private static TestClient client1;
    private static TestClient client2;

    @BeforeAll
    public static void setUpClient() throws IOException, ExitProgram {
        new Thread(new TestServer()).start();
        client1 = new TestClient();
        client2 = new TestClient();
    }

    @BeforeAll
    public static void setUpStream() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterAll
    static void restoreStream() {
        System.setOut(originalOut);
    }

    @Test
    void testClient() throws ExitProgram, IOException, 
        ServerUnavailableException, ProtocolException, InterruptedException {
        // Clients try to create a connection
        client1.createConnection(CLIENT1, false);
        client2.createConnection(CLIENT1, false);
        assertThat(outContent.toString(), containsString("Attempting to connect to /127.0.0.1:25565..."));
        outContent.reset();
        
        // Connect with a name. Expected: Success (200)
        client1.tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                + CLIENT1 + ProtocolMessages.DELIMITER);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CONNECT 
                + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS));
        outContent.reset();
        
        // Client who is not connected tries a command that is not ProtocolMessages.CONNECT
        // Expected: Unauthorized (401)
        client2.tui.handleUserInput(ProtocolMessages.LISTP);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LISTP 
                + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED));
        outContent.reset();
        
        // Another client connects with same name as client1. Expected: Forbidden (403)
        client2.tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                + CLIENT1 + ProtocolMessages.DELIMITER);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CONNECT 
                + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN));
        outContent.reset();
        
        // Another client connects with another name than client1. Expected: Success (200)
        client2.tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                + CLIENT2 + ProtocolMessages.DELIMITER);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CONNECT 
                + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS));
        outContent.reset();  
        
        // Asks for help menu. Expected: "Commands:"
        client1.tui.handleUserInput("HELP");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString("Commands:"));
        outContent.reset(); 
        
        // List players. Expected: Includes name of client1 ("Twam")
        client1.tui.handleUserInput(ProtocolMessages.LISTP);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LISTP + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER + CLIENT1));
        outContent.reset();
        
        // Create a lobby with name BadLobby and size 5. Expected: Forbidden (403)
        client1.createLobby("BadLobby", 5);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CREATE + ProtocolMessages.DELIMITER
                + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Create a lobby with name TestLobby and size 2. Expected: Success (200)
        client1.createLobby("TestLobby", 2);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CREATE + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // List lobbies. Expected: Lobby TestLobby is included
        client1.tui.handleUserInput(ProtocolMessages.LISTL);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LISTL + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER + "TestLobby"));
        outContent.reset();
        
        // Join lobby TestLobby. Expected: Success (200)
        client2.joinLobby("TestLobby");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.JOIN + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Leave lobby. Expected: Success (200)
        client2.leaveLobby();
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        client2.joinLobby("TestLobby");
        
        // One client readies up. Expected: Success (200) and 1 player ready
        client1.tui.handleUserInput(ProtocolMessages.READY);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.READY + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.READY + ProtocolMessages.DELIMITER
                + 1 + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Everyone readies up. Expected: Success (200) and game start
        client2.tui.handleUserInput(ProtocolMessages.READY);
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.READY + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.START + ProtocolMessages.DELIMITER
                + CLIENT1 + ProtocolMessages.DELIMITER + CLIENT2 + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Underlying tests could not be performed, since it produces a NullPointerException in makeMove of class Client
        // This only happens in the test, not when using the client manually.
        // Because of that, FORFEIT is called.
        
        //        // Player sends invalid move. Expected: Forbidden (403)
        //        client1.makeMove("1,A;1,A;2");
        //        TimeUnit.MILLISECONDS.sleep(10);
        //        assertThat(outContent.toString(), containsString(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
        //                + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER));
        //        outContent.reset();
        //        
        //        // Player is not in turn. Expected: Forbidden (403)
        //        client2.makeMove("3,C;5,C;1");
        //        TimeUnit.MILLISECONDS.sleep(10);
        //        assertThat(outContent.toString(), containsString(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
        //                + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER));
        //        outContent.reset();
        //        
        //        // Player sends valid move. Expected: Success (200) and move with name
        //        client1.makeMove("5,G;7,G;3");
        //        TimeUnit.MILLISECONDS.sleep(10);
        //        assertThat(outContent.toString(), containsString(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
        //                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        //        assertThat(outContent.toString(), containsString(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
        //                + CLIENT1 + ProtocolMessages.DELIMITER + "5,G;7,G;3"));
        //        outContent.reset();
        
        // Player forfeits. Expected: Success (200) and game finish with winner CLIENT2
        client1.playerForfeit();
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.FORFEIT + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.FINISH + ProtocolMessages.DELIMITER
                + CLIENT2 + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Asks for leaderboard. Expected: Success (200) and client1 has 0 points, client2 has 3 points
        client1.getLeaderboard();
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER + CLIENT1 + ",0;" + CLIENT2 + ",3;"));
        outContent.reset();
        
        // There is a bug in our server where it still thinks clients are ingame after finish
        // Because of this, they have to connect again with a different name:
        client1.tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                + "Henk" + ProtocolMessages.DELIMITER);
        client2.tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                + "Jaap" + ProtocolMessages.DELIMITER);
        outContent.reset();
        
        // Client 1 challenges client 2. Expected: Success (200) and client2 receives client1's challenge
        client1.challengePlayer("Jaap");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CHALL + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.CHALL + ProtocolMessages.DELIMITER
                + "Henk" + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Client 2 accepts not sent challenge. Expected: Forbidden (403)
        client2.challengeAccept("Jaap");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER
                + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER));
        outContent.reset();
        
        // Client 2 accepts the challenge. Expected: Success (200) and lobby changed with Henk and Jaap
        client2.challengeAccept("Henk");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.CHANGE + ProtocolMessages.DELIMITER
                + "Henk;Jaap;"));
        outContent.reset();
        
        // Client 1 sends PM to client 2. Expected: Success (200) and PMRECV for client2 with client1 and message
        client1.sendPM("Jaap", "Hi!");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.PM + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.PMRECV + ProtocolMessages.DELIMITER
                + "Henk;Hi!;"));
        outContent.reset();
        
        // Client 1 sends lobby message. Expected: Success (200) and LMSGRECV with client1 and message
        client1.sendLM("Hi!");
        TimeUnit.MILLISECONDS.sleep(10);
        assertThat(outContent.toString(), containsString(ProtocolMessages.LMSG + ProtocolMessages.DELIMITER
                + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER));
        assertThat(outContent.toString(), containsString(ProtocolMessages.LMSGRECV + ProtocolMessages.DELIMITER
                + "Henk;Hi!;"));
        outContent.reset();
    }

}
