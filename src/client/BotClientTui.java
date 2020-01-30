package client;

import exceptions.ServerUnavailableException;
import game.ComputerPlayer;
import java.io.IOException;

public class BotClientTui extends ClientTui {
    protected ComputerPlayer computer;

    public BotClientTui(Client cl) throws IOException {
        super(cl);
    }

    @Override
    public void doMove() throws ServerUnavailableException {
        client.makeMove(computer.determineMove(client.getGame().getBoard()));
    }
}
