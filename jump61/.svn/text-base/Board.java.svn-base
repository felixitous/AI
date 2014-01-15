package jump61;

import java.util.Formatter;
import java.util.Arrays;
import java.util.ArrayList;

import static jump61.Color.*;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered 0 - size()-1, in
 *  row 2 numbered size() - 2*size() - 1, etc.
 *  @author Felix Liu
 */
abstract class Board {

    /** Holds the values for the undo. */
    protected ArrayList<ArrayList<String>> undoInfo
        = new ArrayList<ArrayList<String>>();

    /** 2D Arrays that make up the game board. */
    protected String[][] gameboard;

    /** the ability to get undoinfo and returns an
     *  ArrayList. */
    ArrayList<ArrayList<String>> getUndoInfo() {
        return undoInfo;
    }

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        unsupported("clear");
    }

    /** Return the number of rows and of columns of THIS. */
    abstract int size();

    /** Returns the number of spots in the square at row R, column C,
     *  1 <= R, C <= size (). */
    abstract int spots(int r, int c);

    /** Returns the color of the square at row R, column C,
     *  1 <= R, C <= size(). */
    abstract Color color(int r, int c);

    /** Returns the total number of moves made (red makes the odd moves,
     *  blue the even ones). */
    abstract int numMoves();

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }


    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Color player, int r, int c) {
        if (color(r, c) == WHITE || player == color(r, c)) {
            return true;
        }
        return false;
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Color player, int r, int c) {
        unsupported("addSpot");
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Clear the undo
     *  history. */
    void set(int r, int c, int num, Color player) {
        unsupported("set");
    }

    /** Set the current number of moves to N.  Clear the undo history. */
    void setMoves(int n) {
        unsupported("setMoves");
    }

    /** Undo the effects one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        unsupported("undo");
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        String emptySpace = "    ";
        String borders = "===";
        String builder = "";
        for (int i = 0; i < gameboard.length; i++) {
            String temp = Arrays.toString(gameboard[i]);
            temp = temp.replaceAll("\\[|\\]|\\,", "");
            temp = temp.replaceAll("null", "--");
            builder += emptySpace + temp + "\n";
        }
        out.format("%s%n%s%s%n", borders, builder, borders);
        return out.toString();
    }

    /** Returns an external rendition of me, suitable for
     *  human-readable textual display.  This is distinct from the dumped
     *  representation (returned by toString). */
    public String toDisplayString() {
        StringBuilder out = new StringBuilder(toString());
        System.out.println("accessing the function toDisplayString");
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int total = 0;
        if (exists(r + 1, c)) {
            total++;
        }
        if (exists(r, c + 1)) {
            total++;
        }
        if (exists(r - 1, c)) {
            total++;
        }
        if (exists(r, c - 1)) {
            total++;
        }
        return total;
    }

    /** Indicate fatal error: OP is unsupported operation. */
    private void unsupported(String op) {
        String msg = String.format("'%s' operation not supported", op);
        throw new UnsupportedOperationException(msg);
    }

    /** The length of an end of line on this system. */
    private static final int NL_LENGTH =
        System.getProperty("line.separator").length();

}
