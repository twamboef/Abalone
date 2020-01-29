package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import exceptions.OffBoardException;
import exceptions.ServerUnavailableException;
import game.ServerGame;
import protocol.ProtocolMessages;

public class ServerListener implements Runnable {
    private BufferedReader in;
    private Client client;
    private ClientTUI TUI;
    private Socket sock;

    public ServerListener(Socket sock, Client client, ClientTUI TUI) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.sock = sock;
            this.TUI = TUI;
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
                TUI.showMessage(e.getMessage());
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
        TUI.showMessage("\n");
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
                TUI.showMessage("Successfully created lobby!");
                TUI.showMessage("Players in lobby:\n" + client.getName());
            } else {
                TUI.showMessage("Failed to create lobby");
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
                            (i - 1) + ") " + lsplit[0] + "    Size: " + lsplit[1] + "    Joined: " + lsplit[2] + "\n");
                }
                if (sb.toString().equals("")) {
                    sb.append("There are no lobbies yet!\n");
                }
                sb.insert(0, "Available lobbies:\n");
                TUI.showMessage(sb.toString());
            } else {
                TUI.showMessage("Failed to retrieve lobbies");
            }
            break;
        case ProtocolMessages.JOIN:
            if (parm1.equals("200")) {
                StringBuilder sb = new StringBuilder();
                sb.append("Successfully joined lobby!\n\n");
                sb.append("Players in lobby:\n");
                for (int i = 2; i < sinput.length; i++) {
                    sb.append(sinput[i] + "\n");
                }
                TUI.showMessage(sb.toString());
            } else {
                TUI.showMessage("Failed to join lobby");
            }
            break;
        case ProtocolMessages.LEAVE:
            if (parm1.equals("200")) {
                TUI.showMessage("Sucessfully left the lobby");
            } else {
                TUI.showMessage("Failed to leave lobby");
            }
            break;
        case ProtocolMessages.READY:
            int parmi = Integer.parseInt(parm1);
            if (parm1.equals("200")) {
                TUI.showMessage("You are now ready");
            } else if (parmi > 4) {
                TUI.showMessage("Failed to ready up");
            } else if (parmi != 1) {
                TUI.showMessage(parmi + " players are now ready");
            } else {
                TUI.showMessage(parmi + " player is now ready");
            }
            break;
        case ProtocolMessages.UNREADY:
            parmi = Integer.parseInt(parm1);
            if (parm1.equals("200")) {
                TUI.showMessage("You are not ready anymore");
            } else if (parmi > 3) {
                TUI.showMessage("Failed to ready up");
            } else if (parmi != 1) {
                TUI.showMessage(parmi + " players are now ready");
            } else {
                TUI.showMessage(parmi + " player is now ready");
            }
            break;
        case ProtocolMessages.CHANGE:
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < sinput.length; i++) {
                sb.append(sinput[i] + "\n");
            }
            sb.insert(0, "Players in lobby:\n");
            TUI.showMessage(sb.toString());
            break;
        case ProtocolMessages.START:
            TUI.showMessage(client.getGame().getBoard().toString());
            client.createGame(input);
            client.getGame().start();
            break;
        case ProtocolMessages.MOVE:
            try {
                if (parm1.equals("200")) {
                    TUI.showMessage("Move accepted by server");
                }
                parmi = Integer.parseInt(parm1);
                if (parmi > 200) {
                    TUI.showMessage("Move rejected by server");
                }
            } catch (NumberFormatException e) {
                TUI.showMessage(client.getGame().getBoard().toString());
                String move = sinput[2] + ProtocolMessages.DELIMITER
                        + sinput[3] + ProtocolMessages.DELIMITER + sinput[4];
                TUI.showMessage(parm1 + " made move" + move);
                    synchronized (((ServerGame) client.getGame()).moveHappened) {
                        ((ServerGame) client.getGame()).moveHappened.notifyAll();
                    }
            }
            break;
        case ProtocolMessages.FINISH:
            if (parm1.equals("")) {
                TUI.showMessage("Game finished! It's a draw!");
            } else if (client.getGame().getPlayerAmount() < 4) {
                TUI.showMessage("Game finished! Winner: " + parm1);
            } else {
                TUI.showMessage("Game finished! Winners: " + parm1 
                        + " and " + client.getGame().getTeamMate(parm1).getName());
            }
            client.clearGame();
            break;
        case ProtocolMessages.DEFEAT:
            TUI.showMessage(parm1 + " has been defeated!");
            break;
        case ProtocolMessages.FORFEIT:
            TUI.showMessage("Player " + parm1 + " has forfeited");
            client.getGame().playerForfeit(parm1);
            break;
        case ProtocolMessages.LISTP:
            if (parm1.equals("200")) {
                sb = new StringBuilder();
                for (int i = 2; i < sinput.length; i++) {
                    sb.append(sinput[i] + "\n");
                }
                sb.insert(0, "Players on server:\n");
                TUI.showMessage(sb.toString());
            } else {
                TUI.showMessage("Failed to retrieve player list");
            }
            break;
        case ProtocolMessages.CHALL:
            if (parm1.equals("200")) {
                TUI.showMessage("Challenge sent successfully");
            }
            try {
                parmi = Integer.parseInt(parm1);
                if (parmi > 200) {
                    TUI.showMessage("Challenge invite failed");
                }
            } catch (NumberFormatException e) {
                TUI.showMessage("You've been challenged by " + parm1 + "!\n");
                TUI.showMessage("To accept, type \"CHALLENGE_ACCEPT\"!");
            }
            break;
        case ProtocolMessages.CHALLACC:
            if (parm1.equals("200")) {
                TUI.showMessage("Challenge accepted");
            }
            try {
                parmi = Integer.parseInt(parm1);
                if (parmi > 200) {
                    TUI.showMessage("Challenge accept failed");
                }
            } catch (NumberFormatException e) {
                TUI.showMessage(parm1 + " accepted your challenge!");
            }
            break;
        case ProtocolMessages.PM:
            if (parm1.equals("200")) {
                TUI.showMessage("PM sent successfully");
            } else {
                TUI.showMessage("PM failed");
            }
            break;
        case ProtocolMessages.PMRECV:
            TUI.showMessage("[" + parm1 + "]: " + sinput[2]);
            break;
        case ProtocolMessages.LMSG:
            if (parm1.equals("200")) {
                TUI.showMessage("Lobby Message sent successfully");
            } else {
                TUI.showMessage("Lobby Message failed");
            }
            break;
        case ProtocolMessages.LMSGRECV:
            TUI.showMessage("[LOBBY " + parm1 + "]: " + sinput[2]);
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
                TUI.showMessage(sb.toString());
            } else {
                TUI.showMessage("Failed to retrieve leaderboard");
            }
        }
        synchronized (TUI.Ack) {
            TUI.Ack.notifyAll();
        }
    }

    private void shutDown() {
        TUI.showMessage("Server closed, shutting down");
        try {
            TUI.shutDown();
            client.shutDown();
            in.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
