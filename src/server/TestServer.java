package server;

import exceptions.ExitProgram;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer extends Server {

    public TestServer() {
        super();
    }
    
    @Override
    public void run() {
        boolean openNewSocket = true;
        while (openNewSocket) {
            try {
                setupForTest();
                while (true) {
                    Socket sock = ssock.accept();
                    ClientHandler handler = new ClientHandler(sock, this, "TestClient");
                    new Thread(handler).start();
                    clients.add(handler);
                }
            } catch (ExitProgram e) {
                openNewSocket = false;
            } catch (IOException e) {
                System.out.println("A server IO error occurred: " + e.getMessage());
                if (!view.getBoolean("Do you want to open a new socket?")) {
                    openNewSocket = false;
                }
            }
        }
        view.showMessage("Already miss you!");
    }

    /**
     * Sets up the server for the test, i.e. doesn't ask for port
     * 
     * @throws ExitProgram when user indicates to exit the program.
     */
    public void setupForTest() throws ExitProgram {
        ssock = null;
        while (ssock == null) {
            int port = 25565;
            try { // try to open a new ServerSocket
                view.showMessage("Attempting to open a socket on port " + port + "...");
                ssock = new ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"));
                view.showMessage("Server started at port " + port);
            } catch (IOException e) {
                view.showMessage("ERROR: cannot create a socket on port " + port + ".");
                if (!view.getBoolean("Do you want to try again?")) {
                    throw new ExitProgram("User indicated to exit the program.");
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new TestServer();
        new Thread(server).start();
    }
}
