package game;

public enum Marble {
    EMPTY, BLACK, WHITE, BLUE, RED;

    /**
     * Returns the marble for the next turn.
     * 
     * @param players amount of players in the game
     * @return marble for next turn
     */
    public Marble next(int players) {
        if (players == 2) {
            if (this == BLACK) {
                return WHITE;
            } else {
                return BLACK;
            }
        } else if (players == 3) {
            if (this == BLACK) {
                return BLUE;
            } else if (this == BLUE) {
                return WHITE;
            } else {
                return BLACK;
            }
        } else {
            if (this == WHITE) {
                return RED;
            } else if (this == RED) {
                return BLACK;
            } else if (this == BLACK) {
                return BLUE;
            } else {
                return WHITE;
            }
        }
    }
}
