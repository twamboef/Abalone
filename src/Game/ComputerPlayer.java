package Game;

public class ComputerPlayer extends Player{

	public ComputerPlayer(String name, Marble marble) {
		super(name, marble);
	
	}
	
	@Override
	public String determineMove(Board board) {
		return Strategy.determineMove(board, marble);
	}
}