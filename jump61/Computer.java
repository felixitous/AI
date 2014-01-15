package jump61;

import java.util.ArrayList;
import java.lang.Math.*;
import static jump61.Color.*;

/** An automated Player.
 *  @author Felix Liu
 */
class Computer extends Player {

    /** Counter which helps makeMove() cycle through
     *  moves. */
    private int counter = 0;

    /** An ArrayList that holds moves. */
    private ArrayList<ArrayList<String>> instructions
        = new ArrayList<ArrayList<String>>();

    /** A separate array that handles smaller code or when needed
     *  to run a set of instructions. */
    private int[] tempCo = new int[2];

    /** Integer which holds the maximum choice according to the
     *  mutable gameboard. */
    private int limit = 0;

    /** Holds the status or the object of the current game. */
    private Game gameC;

    /** Heuristic Tracker. */
    private int heurTrack = 9;

    /** Board object which holds the current status of the board,
     *  easy access to further methods. */
    private MutableBoard mboard;

    /** original depth. */
    private int _depth = 0;

    /** current state legal moves. */
    private ArrayList<String> allMoves;

    /** point at the ArrayList. */
    private int point;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    Computer(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        gameC = getGame();
        mboard = getmBoard();
        limit = mboard.size();
        instructions = mboard.getUndoInfo();
        initP();
        gameC.printAI(getColor(), tempCo[0], tempCo[1]);
        mboard.addSpot(getColor(), tempCo[0], tempCo[1]);
        counter++;
    }

    /** Takes in the integer SIZE and returns a integer depth
     *  Allocates different depths so no insanely searching
     *  depth for a proper move even though moves in the beginning
     *  don't really matter where placed. */
    int depthPartition(int size) {
        if (size > (5 * 5)) {
            return 5;
        } else if (size > (4 * 4)) {
            return 6;
        } else if (size > (3 * 3)) {
            return 7;
        } else {
            return 8;
        }
    }

    /** Takes in integer SIZE and integer DEPTH, returns
     *  the altered depth for correct processing.
     *  Changes how much the depth goes down to avoid huge tasking
     *  data analysis. If initial checks aren't enough, takes
     *  in TIME for a better check. */
    int depthChange(int size, int depth, long time) {
        if (System.currentTimeMillis() > time) {
            depth = 1;
        } else if (size > (5 * 5) && depth > 4) {
            depth = 4;
        } else if (size > (4 * 4) && depth > 5) {
            depth = 5;
        } else if (size > (3 * 3) && depth > 6) {
            depth = 6;
        }
        depth -= 1;
        return depth;
    }

    /** initialization of the trees. */
    void initP() {
        mboard.historian();
        instructions = mboard.getUndoInfo();
        allMoves = legalMoves(mboard, getColor());
        gameC.setTesting();
        _depth = depthPartition(allMoves.size());
        long start = System.currentTimeMillis();
        long end = start + 5 * Defaults.CONVERTER;
        int test = decisionMaker(mboard, _depth, getColor(), Integer.MIN_VALUE,
                Integer.MAX_VALUE, end);
        gameC.setTesting();
        String[] random = allMoves.get(point).split("\\s+");
        setTemp(random[0], random[1]);
        mboard.strRemove();
    }

    /** Takes in board BOARD and color COLOR.
     *  Prints the ArrayList of legal moves of the board.
     *  Returns an ArrayList TEMP. */
    ArrayList<String> legalMoves(MutableBoard board, Color color) {
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < board.size(); i++) {
            for (int k = 0; k < board.size(); k++) {
                if (mboard.isLegal(color, i + 1, k + 1)) {
                    String b = (i + 1) + " " + (k + 1);
                    temp.add(b);
                }
            }
        }
        return temp;
    }


    /** The minimax tree which supports alpha-beta pruning. Takes in
     *  board BOARD, integer DEPTH, color COLOR, integer ALPHA, and integer
     *  BETA to create the best possible scenario for the computer. Returns
     *  the integer ALPHA or BETA. Keeps check of TIME for hidden big
     *  data at recursive branches. */
    private int decisionMaker(MutableBoard board, int depth, Color color,
            int alpha, int beta, long time) {
        if (depth == 0) {
            return valueAtPoint(board, color);
        }
        if (color == getColor()) {
            beta = Integer.MAX_VALUE;
            ArrayList<String> aMoves = legalMoves(board, color);
            for (int i = 0; i < aMoves.size(); i++) {
                String[] exe = aMoves.get(i).split("\\s+");
                board.addSpot(color, Integer.parseInt(exe[0]),
                    Integer.parseInt(exe[1]));
                if (gameC.getWinner()) {
                    gameC.setWinner();
                }
                int chdepth = depthChange(aMoves.size(), depth, time);
                int val = decisionMaker(board, chdepth, color.opposite(),
                        alpha, beta, time);
                int oldbeta = beta;
                beta = Math.min(beta, val);
                if (_depth == depth && oldbeta >= beta) {
                    point = i;
                }
                if (beta <= alpha) {
                    board.undo();
                    return beta;
                }
                board.undo();
            }
            return beta;
        } else {
            alpha = Integer.MIN_VALUE;
            ArrayList<String> aMoves = legalMoves(board, color);
            for (int i = 0; i < aMoves.size(); i++) {
                String[] exe = aMoves.get(i).split("\\s+");
                board.addSpot(color, Integer.parseInt(exe[0]),
                    Integer.parseInt(exe[1]));
                if (gameC.getWinner()) {
                    gameC.setWinner();
                }
                int chdepth = depthChange(aMoves.size(), depth, time);
                int val = decisionMaker(board, chdepth, color.opposite(),
                        alpha, beta, time);
                alpha = Math.max(alpha, val);
                if (alpha >= beta) {
                    board.undo();
                    return alpha;
                }
                board.undo();
            }
            return alpha;
        }
    }


    /** Takes in mutableboard BOARD and color COLOR
     *  returns the heuristic value for the particular node. */
    private int valueAtPoint(MutableBoard board, Color color) {
        return board.howMany(color);
    }


    /** Looks at the board for better processing. Helps to determine
     *  what possible moves are possible? */
    private void modulator() {
        cycleInitialStep();
    }

    /** Helps cycle through the initialStep to see which
     *  spots on the gameboard can the initial step actually
     *  be placed. */
    private void cycleInitialStep() {
        while (true) {
            if (mboard.isLegal(getColor(), 1, 1)) {
                setTemp(1, 1);
                break;
            } else if (mboard.isLegal(getColor(), 1, limit)) {
                setTemp(1, limit);
                break;
            } else if (mboard.isLegal(getColor(), limit, 1)) {
                setTemp(limit, 1);
                break;
            } else if (mboard.isLegal(getColor(), limit, limit)) {
                setTemp(limit, limit);
                break;
            }
            break;
        }
    }

    /** Takes in ROW and COL. Sets the moves for the current position. */
    private void setTemp(int row, int col) {
        tempCo[0] = row;
        tempCo[1] = col;
    }

    /** Takes in ROW and COL. Sets the move for Strings. */
    private void setTemp(String row, String col) {
        tempCo[0] = Integer.parseInt(row);
        tempCo[1] = Integer.parseInt(col);
    }

    /** Functions below analyze the information in a particular instruction. */
    /** Takes in string INPUT for row analysis. Returns an integer. */
    private int row(String input) {
        String[] temp = input.split("\\s+");
        return Integer.parseInt(temp[0]);
    }

    /** Takes in string INPUT for column analysis. Returns an integer. */
    private int col(String input) {
        String[] temp = input.split("\\s+");
        return Integer.parseInt(temp[1]);
    }

    /** Takes in string INPUT for spot analysis. Returns an integer. */
    private int spot(String input) {
        String[] temp = input.split("\\s+");
        return Integer.parseInt(temp[2]);
    }

    /** Takes in string INPUT for color analysis. Returns an integer. */
    private Color color(String input) {
        String[] temp = input.split("\\s+");
        return gameC.colorConvert(temp[3]);
    }
    /** end the function tools. */

}
