package client;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import protocol.ProtocolMessages;

public class TestClient extends Client {

    public TestClient() throws IOException {
        super();
    }

    /**
     * Starts a connection and TUI. Uses parameters for createConnection().
     * 
     * @param name     of a client
     * @param computer computerplayer or not
     * @throws ExitProgram if no connection is established
     * @throws IOException if createConnection throws this
     */
    public void start(String name, boolean computer) throws ExitProgram, IOException {
        try {
            Thread t;
            tui.showMessage("Starting Abalone client...");
            while (true) {
                t = null;
                createConnection(name, computer);
                tui.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + name
                        + ProtocolMessages.DELIMITER + ProtocolMessages.DELIMITER);
                t = new Thread(sl);
                t.start();
                synchronized (serverSocket) {
                    serverSocket.wait();
                }
                if (connected) {
                    tui.showMessage("Successfully connected!");
                    break;
                }
                tui.showMessage("Failed to connect, please try again\n");
            }
            tui.start();
        } catch (ExitProgram e) {
            tui.showMessage("Disconnected.");
            return;
        } catch (ServerUnavailableException e) {
            // do nothing
        } catch (InterruptedException e) {
            // do nothing
        } catch (ProtocolException e) {
            // do nothing
        }
        throw new ExitProgram("Exiting program");
    }

    /**
     * Creates a connection to the server using IP 127.0.0.1 (localhost). Port
     * 25565. Uses name and boolean as parameters so that the test can use it.
     * 
     * @param name     of a client
     * @param computer computerplayer or not
     * @throws IOException if clearConnection() throws this
     * @throws ExitProgram if connection is not established
     */
    public void createConnection(String name, boolean computer) throws IOException, ExitProgram {
        clearConnection();
        while (serverSocket == null) {
            this.name = name;
            if (computer) {
                tui = new BotClientTui(this);
                name = "BOT-" + name;
            }
            String host = "127.0.0.1";
            int port = 25565;
            try {
                InetAddress addr = InetAddress.getByName(host);
                tui.showMessage("Attempting to connect to " + addr + ":" + port + "...");
                serverSocket = new Socket(addr, port);
                out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                sl = new ServerListener(serverSocket, this, tui);
            } catch (IOException e) {
                throw new ExitProgram("Exiting program");
            }
        }
    }
}
