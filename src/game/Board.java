package game;

import exceptions.OffBoardException;

public class Board {
    public static final int size = 61;
    public static final String SPACE = "  ";
    public static final String LINE = "|";
    public static final String ENTER = "\n";
    public static final String LBORDER = "/";
    public static final String RBORDER = "\\";
    public static final String UBORDER = 
            "_________________";
    public static final String BBORDER = 
            " 1   2   3   4   5";
    char[] horizontal = "ABCDEFGHI".toCharArray();
    int[] diagonal = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private Marble[] fields;
    public int players;

    /**
     * Constructor of the class.
     * 
     * @param players amount of players
     * @ensures fields != null
     */
    public Board(int players) {
        fields = new Marble[size];
        this.players = players;
        try {
            reset();
        } catch (OffBoardException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a deep copy of the board.
     * 
     * @ensures new object (not this object)
     * @ensures the values of all fields of the copy match the ones of this board
     * @return copy of this board
     * @throws OffBoardException (can't happen because hard coded)
     */
    public Board deepCopy() throws OffBoardException {
        Board copy = new Board(players);
        for (int i = 0; i < size; i++) {
            copy.setField(i, this.getMarble(i));
        }
        return copy;
    }
    
    /**
     * Removes all the marbles of a player (upon forfeit).
     * @param player whose marbles need to be removed
     */
    public void removeMarbles(Player player) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == player.getMarble()) {
                fields[i] = Marble.EMPTY;
            }
        }
    }

    /**
     * Calculates how many marbles a player has on the board.
     * 
     * @param marble the marble of the player
     * @return amount of marbles of this player that have not been pushed off
     * @throws OffBoardException if index is invalid
     */
    public int getNRofMarbles(Marble marble) throws OffBoardException {
        int j = 0;
        for (int i = 0; i < 61; i++) {
            if (getMarble(i) == marble) {
                j++;
            }
        }
        return j;
    }

    /**
     * Converts a combination of horizontal and diagonal coordinate to index.
     * 
     * @param hor horizontal coordinate
     * @param dia diagonal coordinate
     * @ensures result == -1 || fields[result] != null
     * @return index for hor,dia combination
     * @throws OffBoardException if hor,dia combination is not a valid index on the
     *                           board
     */
    public int getIndex(char hor, int dia) throws OffBoardException {
        if (!isValidField(hor, dia)) {
            throw new OffBoardException("Invalid combination of horizontal and diagonal coordinate");
        }
        int index = new String(horizontal).indexOf(hor);
        int result = 0;
        if (index <= 4) {
            result = dia;
            for (int i = 0; i < index; i++) {
                result += 5 + i;
            }
        } else {
            result = size - (9 - dia);
            for (int i = index; i < horizontal.length - 1; i++) {
                result -= 9 - (i - 3);
            }
        }
        return result - 1;
    }

    /**
     * Converts an index to a combination of a horizontal and diagonal coordinate.
     * 
     * @param i index
     * @return String with coordinates for index i
     * @throws OffBoardException when index is invalid
     */
    public String getCoords(int i) throws OffBoardException {
        if (i < 5) {
            return "A," + (i + 1);
        } else if (i < 11) {
            return "B," + (i - 4);
        } else if (i < 18) {
            return "C," + (i - 10);
        } else if (i < 26) {
            return "D," + (i - 17);
        } else if (i < 35) {
            return "E," + (i - 26 + 1);
        } else if (i < 43) {
            return "F," + (i - 35 + 2);
        } else if (i < 50) {
            return "G," + (i - 43 + 3);
        } else if (i < 56) {
            return "H," + (i - 50 + 4);
        } else if (i < 61) {
            return "I," + (i - 56 + 5);
        } else {
            throw new OffBoardException("Invalid index");
        }
    }

    /**
     * Returns the marble on the field given.
     * 
     * @param index of which to get the marble
     * @return marble if valid index, null if invalid
     * @throws OffBoardException if index is invalid
     */
    public Marble getMarble(int index) {
        if (index >= 0 && index < 61) {
            return fields[index];
        } else {
            return null;
        }
    }

    /**
     * Returns the marble of the field given. Uses getMarble(int index) and
     * getIndex(hor, dia)
     * 
     * @param hor Horizontal coordinate (A-I)
     * @param dia Diagonal coordinate (1-9)
     * @return marble if valid index, null if invalid
     */
    public Marble getMarble(char hor, int dia) {
        try {
            return getMarble(getIndex(hor, dia));
        } catch (OffBoardException e) {
            return null;
        }
    }

    /**
     * Returns representation of marbles and empty field.
     * 
     * @param m Marble of which to get representation
     * @ensures result != null
     * @return 3-letter representation of marble if field not empty, " " if field is
     *         empty
     */
    public String getRep(Marble m) {
        if (m == Marble.WHITE) {
            return "WHI";
        } else if (m == Marble.BLACK) {
            return "BLK";
        } else if (m == Marble.BLUE) {
            return "BLU";
        } else if (m == Marble.RED) {
            return "RED";
        } else {
            return "   ";
        }
    }

    /**
     * Returns the amount of players on this board.
     * 
     * @return players
     */
    public int getPlayers() {
        return players;
    }

    /**
     * Sets the amount of players for this board.
     * 
     * @param players new amount of players for this board
     */
    public void setPlayers(int players) {
        this.players = players;
    }

    /**
     * Changes the marble of field i.
     * 
     * @requires i to be a valid index
     * @param i index of the field
     * @param m new marble for this field
     * @throws OffBoardException if index i is invalid
     * @ensures field i is set to marble m
     */
    public void setField(int i, Marble m) throws OffBoardException {
        try {
            fields[i] = m;
        } catch (IndexOutOfBoundsException e) {
            throw new OffBoardException("Invalid index");
        }
    }

    /**
     * Changes the marble of field (hor,dia).
     * 
     * @requires (hor,dia) to be a valid coordinate
     * @param hor Horizontal coordinate (A-I)
     * @param dia Diagonal coordinate (1-9)
     * @param m   new marble for this field
     * @throws OffBoardException if index i is invalid
     * @ensures field (hor,dia) is set to marble m
     */
    public void setField(char hor, int dia, Marble m) throws OffBoardException {
        fields[getIndex(hor, dia)] = m;
    }

    /**
     * Checks whether hor is a valid horizontal coordinate.
     * 
     * @param hor horizontal coordinate
     * @return boolean if hor is a valid horizontal coordinate
     */
    public boolean isValidHorizontal(char hor) {
        for (int i = 0; i < horizontal.length; i++) {
            if (horizontal[i] == hor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether dia is a valid diagonal coordinate.
     * 
     * @param hor horizontal coordinate (needed because each row has a different
     *            amount of valid diagonals)
     * @param dia diagonal coordinate
     * @return boolean if dia is a valid diagonal coordinate
     */
    public boolean isValidDiagonal(char hor, int dia) {
        int index = new String(horizontal).indexOf(hor);
        if (index == -1) {
            return false;
        } else if (index <= 4) {
            return dia <= index + 5;
        } else {
            return dia >= index - 3 && dia <= 9;
        }
    }

    /**
     * Checks whether the index represents a field on the board.
     * 
     * @param index of a field
     * @return boolean if valid field
     */
    public boolean isValidField(int index) {
        return index < size && index >= 0;
    }

    /**
     * Checks whether the coordinates represent a field on the board.
     * 
     * @param hor horizontal coordinate
     * @param dia diagonal coordinate
     * @return boolean if valid field
     */
    public boolean isValidField(char hor, int dia) {
        return isValidHorizontal(hor) && isValidDiagonal(hor, dia);
    }

    /**
     * Checks whether the index represents an empty field on the board.
     * 
     * @requires isValidField(index)
     * @param index of a field
     * @return boolean if empty field
     * @throws OffBoardException if index is invalid
     */
    public boolean isEmptyField(int index) throws OffBoardException {
        return getMarble(index) == Marble.EMPTY;
    }

    /**
     * Checks whether the coordinates represent an empty field on the board.
     * 
     * @requires isValidField(hor,dia)
     * @param hor horizontal coordinate
     * @param dia diagonal coordinate
     * @return boolean if empty field
     */
    public boolean isEmptyField(char hor, int dia) {
        return getMarble(hor, dia) == Marble.EMPTY;
    }

    /**
     * Checks whether the move is valid on this board.
     * 
     * @param player to check it for
     * @param move   to check for validity
     * @return boolean if valid move
     * @throws OffBoardException if getMarble() throws this exception
     */
    public boolean isValidMove(Player player, String move) throws OffBoardException {
        int tempscore = player.getPoints();
        Marble marble = player.getMarble();
        Board copy = deepCopy();
        String[] movesplit = move.split(";");
        String[] first;
        String[] last;
        char firsthor;
        char lasthor;
        int firstdiai;
        int lastdiai;
        int dirvalue;
        Direction dir;
        try {
            first = movesplit[0].split(",");
            last = movesplit[1].split(",");
            firsthor = first[1].charAt(0);
            lasthor = last[1].charAt(0);
            firstdiai = Integer.parseInt(first[0]);
            lastdiai = Integer.parseInt(last[0]);
            dirvalue = Integer.parseInt(movesplit[2]);
            dir = Direction.values()[dirvalue];
        } catch (Exception e) {
            return false;
        }
        boolean hasThree = false;
        try {
            int ball2 = -1;
            Marble teamMate = marble.next(4).next(4);
            for (int i = 0; i < 6 && !hasThree; i++) { // test if moving three balls
                if (player.marbleTo(copy, (ball2 = player.marbleTo(copy, lasthor, lastdiai, Direction.values()[i])),
                        Direction.values()[i]) == getIndex(firsthor, firstdiai)) {
                    hasThree = true;
                }
            }
            if (copy.getMarble(firsthor, firstdiai) != marble) {
                if (copy.getPlayers() != 4) {
                    return false;
                } else if (copy.getMarble(firsthor, firstdiai) != teamMate) {
                    return false;
                }
            }
            if (copy.getMarble(lasthor, lastdiai) != marble) { // copy part checks whether all
                if (copy.getPlayers() != 4) { // selected marbles are of the player's team
                    return false;
                } else if (copy.getMarble(lasthor, lastdiai) != teamMate) {
                    return false;
                }
            }
            if (copy.getPlayers() == 4 && copy.getMarble(firsthor, firstdiai) != marble
                    && copy.getMarble(lasthor, lastdiai) != marble) {
                if (!hasThree) {
                    return false;
                } else if (copy.getMarble(ball2) != marble) {
                    return false;// 1+ of the moving marbles has to be player's marble
                }
            }
            if (hasThree) {
                if (copy.getMarble(ball2) != marble) {
                    if (copy.getPlayers() != 4) {
                        return false;
                    } else if (copy.getMarble(ball2) != teamMate) {
                        return false;
                    }
                }
            }
            if (player.isInLine(copy, move)) {
                if (getMarble(lasthor, lastdiai) != marble) {
                    return false;
                }
                if (getMarble(player.marbleTo(copy, firsthor, firstdiai, dir)) == Marble.EMPTY) {
                    return true;
                }
                if (getMarble(player.marbleTo(copy, firsthor, firstdiai, dir)) == marble) {
                    return false;
                }
                if (copy.getPlayers() == 4 && getMarble(player.marbleTo(copy, firsthor, firstdiai, dir)) == teamMate) {
                    return false;
                }
            }
            int upTwoi = player.marbleTo(copy, player.marbleTo(copy, firsthor, firstdiai, dir), dir);
            Marble upTwo = getMarble(upTwoi);
            Marble upThree = getMarble(player.marbleTo(copy, upTwoi, dir));
            if (upTwo != null && upTwo != Marble.EMPTY) {
                if (!hasThree) {
                    return false;
                }
                if (upThree != null && upThree != Marble.EMPTY) {
                    return false;
                }
            }
            if (hasThree) {
                if (upTwo == marble) {
                    return false;
                }
                if (copy.getPlayers() == 4 && upTwo == teamMate) {
                    return false;
                }
            } else {
                if (copy.getMarble(player.marbleTo(copy, firsthor, firstdiai, dir)) != Marble.EMPTY
                        || copy.getMarble(player.marbleTo(copy, lasthor, lastdiai, dir)) != Marble.EMPTY) {
                    return false;
                }
                if (hasThree && copy.getMarble(player.marbleTo(copy, ball2, dir)) != Marble.EMPTY) {
                    return false;
                }
            }
            player.setFields(copy, move);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        } finally {
            player.setPoints(tempscore);
        }
        return true;
    }

    /**
     * Resets the board for the amount of players. If the amount of players is not
     * in the range of 2-4, all fields are set to empty
     * 
     * @throws OffBoardException if getIndex throws this exception
     */
    public void reset() throws OffBoardException {
        if (players == 2) {
            for (int i = 0; i < 11; i++) {
                fields[i] = Marble.WHITE;
            }
            for (int i = 13; i < 16; i++) {
                fields[i] = Marble.WHITE;
            }
            for (int i = 45; i < 48; i++) {
                fields[i] = Marble.BLACK;
            }
            for (int i = 50; i < 61; i++) {
                fields[i] = Marble.BLACK;
            }
        } else if (players == 3) {
            for (int i = 0; i < 11; i++) {
                fields[i] = Marble.BLUE;
            }
            fields[18] = Marble.WHITE;
            fields[25] = Marble.BLACK;
            for (int i = 4; i < horizontal.length; i++) {
                fields[getIndex(horizontal[i], i - 3)] = Marble.WHITE;
                fields[getIndex(horizontal[i], i - 2)] = Marble.WHITE;
                fields[getIndex(horizontal[i], 8)] = Marble.BLACK;
                fields[getIndex(horizontal[i], 9)] = Marble.BLACK;
            }
        } else if (players == 4) {
            for (int i = 1; i < 5; i++) {
                fields[i] = Marble.BLUE;
            }
            fields[5] = Marble.WHITE;
            for (int i = 7; i < 10; i++) {
                fields[i] = Marble.BLUE;
            }
            fields[11] = Marble.WHITE;
            fields[12] = Marble.WHITE;
            fields[14] = Marble.BLUE;
            fields[15] = Marble.BLUE;
            for (int i = 18; i < 21; i++) {
                fields[i] = Marble.WHITE;
            }
            for (int i = 26; i < 29; i++) {
                fields[i] = Marble.WHITE;
            }
            for (int i = 32; i < 35; i++) {
                fields[i] = Marble.BLACK;
            }
            for (int i = 40; i < 43; i++) {
                fields[i] = Marble.BLACK;
            }
            fields[45] = Marble.RED;
            fields[46] = Marble.RED;
            fields[48] = Marble.BLACK;
            fields[49] = Marble.BLACK;
            for (int i = 51; i < 54; i++) {
                fields[i] = Marble.RED;
            }
            fields[55] = Marble.BLACK;
            for (int i = 56; i < 60; i++) {
                fields[i] = Marble.RED;
            }
        }
        for (int i = 0; i < size; i++) {
            if (fields[i] == null) {
                fields[i] = Marble.EMPTY;
            }
        }
    }

    /**
     * Gives a textual representation of the board. (Uses unicode, so might appear a
     * little off depending on your font)
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int max;
        String line = "";
        for (int i = 0; i < 6; i++) {
            sb.append(SPACE);
        }
        sb.append(BBORDER);
        for (int i = 0; i < 9; i++) {
            line = "";
            for (int j = 0; j < Math.abs(i - 4); j++) {
                line += SPACE;
            }
            line += horizontal[i] + " ";
            if (i < 4) {
                line += RBORDER;
            } else if (i == 4) {
                line += LINE;
            } else {
                line += LBORDER;
            }
            for (int j = 0; j < (max = 9 - Math.abs(i - 4)); j++) {
                line += getRep(getMarble(index));
                index++;
                if (j < max - 1) {
                    line += LINE;
                }
            }
            if (i < 4) {
                line += LBORDER + " " + (i + 6);
            } else if (i == 4) {
                line += LINE;
            } else {
                line += RBORDER;
            }
            line += ENTER;
            sb.insert(0, line);
        }
        line = "";
        for (int i = 0; i < 6; i++) {
            line += SPACE;
        }
        line += UBORDER + ENTER;
        sb.insert(0, line);
        return sb.toString();
    }
}