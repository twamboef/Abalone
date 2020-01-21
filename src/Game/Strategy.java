package Game;

public interface Strategy {
	
	public abstract String determineMove(Board board, Marble marble);
	
}