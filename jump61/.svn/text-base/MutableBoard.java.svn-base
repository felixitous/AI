package jump61;

import java.util.ArrayList;
import static jump61.Color.*;

/** A Jump61 board state.
 *  @author Felix Liu
 */

class MutableBoard extends Board {

    /** Total combined number of moves by both sides. */
    protected int _moves;

    /** Convenience variable: size of board (squares along one edge). */
    private int _N;

    /** variable that holds the game. */
    private Game _game;

    /** variable that determines if need to clear for undo. */
    private boolean undoMode = false;

    /** A board that takes in the the GAME and the
     *  integer N. */
    MutableBoard(Game game, int N) {
        _N = N;
        gameboard = new String[N][N];
        _game = game;
    }

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _N = N;
        gameboard = new String[N][N];
    }

    /** Special clear that takes in an integer N and
     *  performs the specified functions and resets
     *  the gameboard. */
    void clear(int N) {
        _N = N;
        gameboard = new String[N][N];
        if (!undoMode) {
            _moves = 0;
        }
    }

    /** returns the integer total amount of that particular COLOR
     *  for the gameboard. */
    int howMany(Color color) {
        int total = 0;
        for (int i = 0; i < _N; i++) {
            for (int k = 0; k < _N; k++) {
                if (color == color(i + 1, k + 1)) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Saves the board into an ArrayList for the undo option. */
    void historian() {
        ArrayList<String> actions = new ArrayList<String>();
        for (int i = 0; i < _N; i++) {
            for (int k = 0; k < _N; k++) {
                if (gameboard[i][k] != null) {
                    actions.add((i + 1) + " " + (k + 1) + " "
                        + gameboard[i][k].charAt(0) + " "
                        + gameboard[i][k].charAt(1));
                }
            }
        }
        undoInfo.add(actions);
    }

    /** removes the last value of the undoInfo for the
     *  purpose of strategy. */
    void strRemove() {
        undoInfo.remove(undoInfo.size() - 1);
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        if (gameboard[r - 1][c - 1] == null) {
            return 0;
        } else {
            String temp = gameboard[r - 1][c - 1];
            String charC = Character.toString(temp.charAt(0));
            int tempint = Integer.parseInt(charC);
            return tempint;
        }
    }

    @Override
    Color color(int r, int c) {
        if (gameboard[r - 1][c - 1] == null) {
            return WHITE;
        } else {
            String temp = gameboard[r - 1][c - 1];
            if (temp.charAt(1) == 'r') {
                return RED;
            } else {
                return BLUE;
            }
        }
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        if (color(r, c) == WHITE || player == color(r, c)) {
            historian();
            int temp = spots(r, c);
            temp++;
            String input = Integer.toString(temp)
                + Character.toString(player.toString().charAt(0));
            gameboard[r - 1][c - 1] = input;
            setMoves(numMoves() + 1);
            jump(r, c);
        } else {
            System.err.println("cannot place here");
        }
    }

    @Override
    void set(int r, int c, int num, Color player) {
        if (exists(r, c)) {
            if (num != 0) {
                String input = Integer.toString(num)
                    + Character.toString(player.toString().charAt(0));
                gameboard[r - 1][c - 1] = input;
            } else if (num == 0) {
                gameboard[r - 1][c - 1] = null;
            }
        } else {
            System.err.println("square doesn't exist");
        }
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
    }


    @Override
    void undo() {
        if (undoInfo.size() != 0) {
            undoMode = true;
            ArrayList<String> hold = undoInfo.get(undoInfo.size() - 1);
            undoInfo.remove(undoInfo.size() - 1);
            clear(_N);
            for (int i = 0; i < hold.size(); i++) {
                int var1, var2, var3;
                var1 = var2 = var3 = 0;
                String[] setStuff = hold.get(i).split("\\s+");
                var1 = Integer.parseInt(setStuff[0]);
                var2 = Integer.parseInt(setStuff[1]);
                var3 = Integer.parseInt(setStuff[2]);
                set(var1, var2, var3, _game.colorConvert(setStuff[3]));
            }
            setMoves(numMoves() - 1);
            undoMode = false;
        } else {
            System.out.println("nothing to undo");
        }
    }

    /** Takes in integer R, integer C, color ATSPOT, color ORIGINAL.
     *  Allocates the dots, a complement to the jump function. */
    private void contagion(int r, int c, Color atSpot, Color original) {
        int temp = spots(r, c);
        temp++;
        String input = Integer.toString(temp)
            + Character.toString(original.toString().charAt(0));
        gameboard[r - 1][c - 1] = input;
    }

    /** Takes in integer R, integer C, and integer AMOUNT.
     *  Redefines the amount of points in the original jump square. */
    private void exclusion(int r, int c, int amount) {
        int temp = spots(r, c);
        temp -= amount;
        String alter = gameboard[r - 1][c - 1];
        String input = Integer.toString(temp)
            + Character.toString(alter.charAt(1));
        gameboard[r - 1][c - 1] = input;
    }


    /** Takes in row R, and column C.
     *  Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int r, int c) {
        _game.checkForWin();
        if (exists(r, c) && !_game.getWinner()) {
            if (spots(r, c) <= neighbors(r, c)) {
                return;
            } else {
                int counter = 0;
                if (exists(r + 1, c)) {
                    counter++;
                    contagion(r + 1, c, color(r + 1, c), color(r, c));
                }
                if (exists(r, c + 1)) {
                    counter++;
                    contagion(r, c + 1, color(r, c + 1), color(r, c));
                }
                if (exists(r - 1, c)) {
                    counter++;
                    contagion(r - 1, c, color(r - 1, c), color(r, c));
                }
                if (exists(r, c - 1)) {
                    counter++;
                    contagion(r, c - 1, color(r, c - 1), color(r, c));
                }
                exclusion(r, c, counter);
                jump(r + 1, c);
                jump(r, c + 1);
                jump(r - 1, c);
                jump(r, c - 1);
            }
        }
    }

}
