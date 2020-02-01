package client;

import exceptions.OffBoardException;
import exceptions.ServerUnavailableException;
import game.ClientPlayer;
import game.ComputerPlayer;
import game.Marble;
import game.Player;
import game.ServerGame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import protocol.ProtocolMessages;

public class ServerListener implements Runnable {
    private BufferedReader in;
    private Client client;
    private ClientTui tui;
    private Socket sock;

    /**
     * Constructor for ServerListener.
     * 
     * @param sock   socket
     * @param client this ServerListener is for
     * @param tui    Textual User Interface
     */
    public ServerListener(Socket sock, Client client, ClientTui tui) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.sock = sock;
            this.tui = tui;
            this.client = client;
        } catch (IOException e) {
            shutDown();
        }

    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                message = readLineFromServer();
                handleServerMessage(message);
            } catch (IOException | ServerUnavailableException e) {
                tui.showMessage(e.getMessage());
                shutDown();
                break;
            }
        }
    }

    /**
     * Reads and returns one line from the server.
     * 
     * @return the line sent by the server.
     * @throws ServerUnavailableException if IO errors occur.
     */
    public String readLineFromServer() throws ServerUnavailableException {
        if (in != null) {
            try {
                // Read and return answer from Server
                String answer = in.readLine();
                if (answer == null) {
                    throw new ServerUnavailableException("Could not read from server.");
                }
                return answer;
            } catch (IOException e) {
                throw new ServerUnavailableException("Could not read from server.");
            }
        } else {
            throw new ServerUnavailableException("Could not read from server.");
        }
    }

    private void handleServerMessage(String input) throws IOException {
        String[] sinput = input.split(";");
        String command = sinput[0];
        String parm1 = null;
        if (sinput.length > 1) {
            parm1 = sinput[1];
        }
        tui.showMessage("\n");
        switch (command) {
            case ProtocolMessages.CONNECT:
                if (parm1.equals("200")) {
                    client.connected = true;
                }
                synchronized (sock) {
                    sock.notifyAll();
                }
                break;
            case ProtocolMessages.CREATE:
                if (parm1.equals("200")) {
                    tui.showMessage("Successfully created lobby!");
                } else {
                    tui.showMessage("Failed to create lobby");
                }
                break;
            case ProtocolMessages.LISTL:
                if (parm1.equals("200")) {
                    StringBuilder sb = new StringBuilder();
                    String[] lsplit;
                    for (int i = 2; i < sinput.length; i++) {
                        lsplit = sinput[i].split(",");
                        if (lsplit.length != 3) {
                            break;
                        }
                        sb.append(
                                (i - 1) + ") " + lsplit[0] + "    Size: " + lsplit[1] 
                                        + "    Joined: " + lsplit[2] + "\n");
                    }
                    if (sb.toString().equals("")) {
                        sb.append("There are no lobbies yet!\n");
                    }
                    sb.insert(0, "Available lobbies:\n");
                    tui.showMessage(sb.toString());
                } else {
                    tui.showMessage("Failed to retrieve lobbies");
                }
                break;
            case ProtocolMessages.JOIN:
                if (parm1.equals("200")) {
                    tui.showMessage("Successfully joined lobby!\n");
                } else {
                    tui.showMessage("Failed to join lobby");
                }
                break;
            case ProtocolMessages.LEAVE:
                if (parm1.equals("200")) {
                    tui.showMessage("Sucessfully left the lobby");
                } else {
                    tui.showMessage("Failed to leave lobby");
                }
                break;
            case ProtocolMessages.READY:
                int parmi = Integer.parseInt(parm1);
                if (parm1.equals("200")) {
                    tui.showMessage("You are now ready");
                } else if (parmi > 4) {
                    tui.showMessage("Failed to ready up");
                } else if (parmi != 1) {
                    tui.showMessage(parmi + " players are now ready");
                } else {
                    tui.showMessage(parmi + " player is now ready");
                }
                break;
            case ProtocolMessages.UNREADY:
                parmi = Integer.parseInt(parm1);
                if (parm1.equals("200")) {
                    tui.showMessage("You are not ready anymore");
                } else if (parmi > 3) {
                    tui.showMessage("Failed to ready up");
                } else if (parmi != 1) {
                    tui.showMessage(parmi + " players are now ready");
                } else {
                    tui.showMessage(parmi + " player is now ready");
                }
                break;
            case ProtocolMessages.CHANGE:
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < sinput.length; i++) {
                    sb.append(sinput[i] + "\n");
                }
                sb.insert(0, "Players in lobby:\n");
                tui.showMessage(sb.toString());
                break;
            case ProtocolMessages.START:
                client.createGame(input);
                if (tui instanceof BotClientTui) {
                    if (parm1.equals(client.getName())) {
                        ((BotClientTui) tui).computer = new ComputerPlayer(client.getName(), Marble.BLACK);
                    } else if (sinput[2].equals(client.getName())) {
                        ((BotClientTui) tui).computer = new ComputerPlayer(client.getName(), Marble.WHITE);
                        if (client.getGame().getPlayerAmount() == 3) {
                            ((BotClientTui) tui).computer.setMarble(Marble.BLUE);
                        }
                    } else if (sinput[3].equals(client.getName())) {
                        ((BotClientTui) tui).computer = new ComputerPlayer(client.getName(), Marble.BLUE);
                        if (client.getGame().getPlayerAmount() == 3) {
                            ((BotClientTui) tui).computer.setMarble(Marble.WHITE);
                        }
                    } else {
                        ((BotClientTui) tui).computer = new ComputerPlayer(client.getName(), Marble.RED);
                    }
                }
                new Thread(((ServerGame) client.getGame())).start();
                tui.showMessage("Game started!");
                if (client.getGame().getCurrentPlayer().getName().equals(client.getName())) {
                    tui.showMessage(client.getGame().getBoard().toString());
                    tui.showMessage(
                            "\nIt's your turn first. Your color is " + client.getGame().getCurrentPlayer().getMarble());
                    if (tui instanceof BotClientTui) {
                        try {
                            ((BotClientTui) tui).doMove();
                        } catch (ServerUnavailableException e1) {
                            shutDown();
                        }
                    } else {
                        tui.showMessage("To make a move, type MOVE");
                    }
                } else {
                    tui.showMessage("Waiting until it's your turn...");
                }
                break;
            case ProtocolMessages.MOVE:
                try {
                    if (parm1.equals("200")) {
                        tui.showMessage("Move accepted by server");
                    }
                    parmi = Integer.parseInt(parm1);
                    if (parmi > 200) {
                        tui.showMessage("Move rejected by server");
                        if (tui instanceof BotClientTui && client.getGame() != null) {
                            try {
                                ((BotClientTui) tui).doMove();
                            } catch (ServerUnavailableException e1) {
                                shutDown();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    try {
                        ((ClientPlayer) client.getGame().getCurrentPlayer()).makeMove(
                                client.getGame().getBoard(), sinput[2]
                                + ProtocolMessages.DELIMITER + sinput[3] + ProtocolMessages.DELIMITER + sinput[4]);
                    } catch (OffBoardException e1) {
                        e1.printStackTrace();
                    }
                    synchronized (((ServerGame) client.getGame()).moveHappened) {
                        ((ServerGame) client.getGame()).moveHappened.notifyAll();
                    }
                    Player[] players = client.getGame().getPlayers();
                    if (players.length != 4) {
                        for (Player p : players) {
                            tui.showMessage(p.getName() + "'s points: " + p.getPoints());
                        }
                    } else {
                        tui.showMessage("Team " + players[0].getName() + " & " + players[1].getName() + "'s points: "
                                + (players[0].getPoints() + players[1].getPoints()));
                        tui.showMessage("Team " + players[2].getName() + " & " + players[3].getName() + "'s points: "
                                + (players[2].getPoints() + players[3].getPoints()));
                    }
                    tui.showMessage(client.getGame().getBoard().toString());
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    if (client.getGame().getCurrentPlayer().getName().equals(client.getName())) {
                        tui.showMessage("It's your turn! Your color is " 
                                + client.getGame().getCurrentPlayer().getMarble());
                        if (tui instanceof BotClientTui) {
                            try {
                                ((BotClientTui) tui).doMove();
                            } catch (ServerUnavailableException e1) {
                                shutDown();
                            }
                        } else {
                            tui.showMessage("To make a move, type MOVE");
                        }
                    } else {
                        tui.showMessage("Waiting until it's your turn...");
                    }
                }
                break;
            case ProtocolMessages.FINISH:
                tui.showMessage(((ServerGame) client.getGame()).getResult());
                tui.showMessage("\n\nPlease enter a command.");
                tui.showMessage("For help, type HELP");
                client.clearGame();
                break;
            case ProtocolMessages.DEFEAT:
                tui.showMessage(parm1 + " has been defeated!");
                break;
            case ProtocolMessages.FORFEIT:
                if (parm1.equals("200")) {
                    tui.showMessage("Forfeit successful");
                }
                parmi = Integer.parseInt(parm1);
                if (parmi > 200) {
                    tui.showMessage("Forfeit failed");
                }
                break;
            case ProtocolMessages.LISTP:
                if (parm1.equals("200")) {
                    sb = new StringBuilder();
                    for (int i = 2; i < sinput.length; i++) {
                        sb.append(sinput[i] + "\n");
                    }
                    sb.insert(0, "Players on server:\n");
                    tui.showMessage(sb.toString());
                } else {
                    tui.showMessage("Failed to retrieve player list");
                }
                break;
            case ProtocolMessages.CHALL:
                if (parm1.equals("200")) {
                    tui.showMessage("Challenge sent successfully");
                }
                try {
                    parmi = Integer.parseInt(parm1);
                    if (parmi > 200) {
                        tui.showMessage("Challenge invite failed");
                    }
                } catch (NumberFormatException e) {
                    tui.showMessage("You've been challenged by " + parm1 + "!\n");
                    tui.showMessage("To accept, type \"CHALLENGE_ACCEPT\"!");
                }
                break;
            case ProtocolMessages.CHALLACC:
                if (parm1.equals("200")) {
                    tui.showMessage("Challenge accepted");
                }
                try {
                    parmi = Integer.parseInt(parm1);
                    if (parmi > 200) {
                        tui.showMessage("Challenge accept failed");
                    }
                } catch (NumberFormatException e) {
                    tui.showMessage(parm1 + " accepted your challenge!");
                }
                break;
            case ProtocolMessages.PM:
                if (parm1.equals("200")) {
                    tui.showMessage("PM sent successfully");
                } else {
                    tui.showMessage("PM failed");
                }
                break;
            case ProtocolMessages.PMRECV:
                tui.showMessage("[" + parm1 + "]: " + sinput[2]);
                break;
            case ProtocolMessages.LMSG:
                if (parm1.equals("200")) {
                    tui.showMessage("Lobby Message sent successfully");
                } else {
                    tui.showMessage("Lobby Message failed");
                }
                break;
            case ProtocolMessages.LMSGRECV:
                tui.showMessage("[LOBBY " + parm1 + "]: " + sinput[2]);
                break;
            case ProtocolMessages.LEADERBOARD:
                if (parm1.equals("200")) {
                    sb = new StringBuilder();
                    for (int i = 2; i < sinput.length; i++) {
                        String[] lsplit = sinput[i].split(",");
                        if (lsplit.length != 2) {
                            break;
                        }
                        if (Integer.parseInt(lsplit[1]) == 1) {
                            sb.append(lsplit[0] + ": 1 point\n");
                        } else {
                            sb.append(lsplit[0] + ": " + lsplit[1] + " points\n");
                        }
                    }
                    sb.insert(0, "Leaderboard:\n");
                    tui.showMessage(sb.toString());
                } else {
                    tui.showMessage("Failed to retrieve leaderboard");
                }
                break;
            default:
                // do nothing if server sends invalid message
                break;
        }
        synchronized (tui.ack) {
            tui.ack.notifyAll();
        }
    }

    private void shutDown() {
        tui.showMessage("Server closed, shutting down");
        try {
            tui.shutDown();
            client.shutDown();
            in.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
