package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import game.Direction;
import protocol.ProtocolMessages;

public class ClientTui implements ClientView {
    public Object Ack = new Object();
    protected Client client;
    private BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

    private boolean invalidCommand;
    
    public ClientTui(Client cl) throws IOException {
        this.client = cl;
    }

    public void start() throws ProtocolException, ExitProgram, InterruptedException {
        while (true) {
            invalidCommand = false;
            showMessage("\nPlease enter a command.");
            showMessage("For help, type HELP");
            try {
                String line;
                handleUserInput((line = systemIn.readLine().toUpperCase()));
                    if (!line.equals("HELP") && !invalidCommand) {
                    synchronized(Ack) {
                        Ack.wait();
                    }
                }
            } catch (ServerUnavailableException | IOException e) {
                throw new ExitProgram("Server closed");
            }
        }
    }
    
    public void handleUserInput(String command) throws ServerUnavailableException, ProtocolException {
        if (command.contains(ProtocolMessages.CONNECT)) {
            client.connect(command.split(";")[1]);
            return;
        }
        switch (command) {
        case "HELP":
            showMessage(printHelpMenu());
            break;
        case ProtocolMessages.CREATE:
            String lobbyname = getString("What name should the lobby get?");
            int size = getInt("How many players? (2-4)");
            while (size < 2 || size > 4) {
                size = getInt("Please enter an integer between 2 and 4");
            }
            client.createLobby(lobbyname, size);
            break;
        case ProtocolMessages.LISTL:
            client.getLobbyList();
            break;
        case ProtocolMessages.JOIN:
            client.joinLobby(getString("Which lobby would you like to join?"));
            break;
        case ProtocolMessages.LEAVE:
            client.leaveLobby();
            break;
        case ProtocolMessages.READY:
            client.doReady();
            break;
        case ProtocolMessages.UNREADY:
            client.doUnready();
            break;
        case ProtocolMessages.MOVE:
            doMove();
            break;
        case ProtocolMessages.FORFEIT:
            client.playerForfeit();
            break;
        case ProtocolMessages.LISTP:
            client.getServerList();
            break;
        case ProtocolMessages.CHALL:
            client.challengePlayer(getString("Who do you want to challenge?"));
            break;
        case ProtocolMessages.CHALLACC:
            client.challengeAccept(getString("Whose challenge do you want to accept?"));
            break;
        case ProtocolMessages.PM:
            String name;
            client.sendPM((name = getString("Who would you like to PM?")), getString("What do you want to send to " + name + "?"));
            break;
        case ProtocolMessages.LMSG:
            client.sendLM(getString("What do you want to send to your lobby?"));
            break;
        case ProtocolMessages.LEADERBOARD:
            client.getLeaderboard();
            break;
        default:
            showMessage("Invalid command");
            invalidCommand = true;
            break;
        }
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public InetAddress getIP() {
        try {
            return InetAddress.getByName(getString("Please enter an IP"));
        } catch (UnknownHostException e) {
            showMessage("Invalid IP, please try again");
            return getIP();
        }
    }

    public String getString(String question) {
        showMessage(question);
        String line = "";
        try {
            line = systemIn.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public int getInt(String question) {
        int i = -1;
        try {
            i = Integer.parseInt(getString(question));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public Boolean getBoolean(String question) {
        String line;
        if ((line = getString(question)).equals("y")) {
            return true;
        }
        if (line.equals("n")) {
            return false;
        } else {
            showMessage("Please enter y/n");
            return getBoolean(question);
        }
    }
    
    public void doMove() throws ServerUnavailableException {
        StringBuilder sb = new StringBuilder();
        sb.append("In which direction?\n");
        for (int i = 0; i < 6; i++) {
            sb.append("\n" + i + ": " + Direction.values()[i]);
        }
        sb.append("\n");
        client.makeMove(getString("What is one of the outer marbles you want to move?") + ProtocolMessages.DELIMITER
                + getString("What is the other outer marble you want to move?") + ProtocolMessages.DELIMITER
                + getInt(sb.toString()));
    }

    public String printHelpMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nCommands:\n");
        sb.append(ProtocolMessages.CREATE + "\n");
        sb.append(ProtocolMessages.LISTL + "\n");
        sb.append(ProtocolMessages.JOIN + "\n");
        sb.append(ProtocolMessages.LEAVE + "\n");
        sb.append(ProtocolMessages.READY + "\n");
        sb.append(ProtocolMessages.UNREADY + "\n");
        sb.append(ProtocolMessages.MOVE + "\n");
        sb.append(ProtocolMessages.FORFEIT + "\n");
        sb.append(ProtocolMessages.LISTP + "\n");
        sb.append(ProtocolMessages.CHALL + "\n");
        sb.append(ProtocolMessages.CHALLACC + "\n");
        sb.append(ProtocolMessages.PM + "\n");
        sb.append(ProtocolMessages.LMSG + "\n");
        sb.append(ProtocolMessages.LEADERBOARD);
        return sb.toString();
    }

    public void shutDown() throws IOException {
        systemIn.close();
    }
}