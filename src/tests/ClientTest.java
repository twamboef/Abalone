package tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import client.Client;
import exceptions.ExitProgram;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class ClientTest {
    //DEZE TEST MOET ANDERS WANT ER IS USER INPUT NODIG
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    private static final String PLAYER1 = "Twam";
    private static final String PLAYER2 = "Tin";

    private static Client client;

    @BeforeAll
    public static void setUpClient() throws IOException {
        client = new Client();
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
    void testClient() throws ExitProgram, IOException {
        client.start();
        assertThat(outContent.toString(), containsString("Starting abalone client..."));
        outContent.reset();
    }

}
