package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;
import java.util.Random;

import static jump61.Color.*;
import static jump61.GameException.error;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Felix Liu
 */

class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(this, Defaults.BOARD_SIZE);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)$|^|\\p{Blank}");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
    }


    /** Returns the mutableboard for alterations. */
    MutableBoard getmBoard() {
        return _board;
    }

    /** Returns the move needed to be player. */
    int[] getMove() {
        return _move;
    }


    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        _playing = false;
        while (true) {
            setDefault();
            if (waitOrNot()) {
                promptForNext();
                if (_inp.hasNextLine()) {
                    inst = _inp.nextLine();
                } else {
                    break;
                }
                readExecuteCommand();
            } else {
                plExecute();
            }
            checkForWin();
            if (_quitter) {
                _out.println("hope the game was enjoyable! :)");
                _out.flush();
                break;
            }
            if (_winner && !testing) {
                announceWinner();
            }
            _out.flush();
        }
        return 0;
    }

    /** Print function used in the AI or Computer and takes in
     *  color COLOR, integer ROW, and integer COLUMN to create
     *  the correct print statement. */
    void printAI(Color color, int row, int column) {
        String temp = color.toCapitalizedString()
            + " " + "moves" + " " + row + " " + column + "." + "\n";
        _out.print(temp);
        _out.flush();
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Get the winner for the game board. Return the boolean _WINNER. */
    boolean getWinner() {
        return _winner;
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    void checkForWin() {
        int counter = 0;
        List<String> temp1 = new ArrayList<String>();
        temp1 = Arrays.asList(_board.toString().split("\\s+"));
        ArrayList<String> temp = new ArrayList<String>(temp1);
        temp.remove(0);
        temp.remove(temp.size() - 1);
        Character compare = temp.get(0).charAt(1);
        for (int i = 0; i < temp.size(); i++) {
            Character current = temp.get(i).charAt(1);
            if (compare.equals(current)
                && colorCheck(compare.toString())) {
                counter++;
            }
        }
        if (counter == _board.size() * _board.size()) {
            _winner = true;
            _winningColor = colorConvert(compare.toString());
        }
    }

    /** Takes in the string INPUT.
     *  Calls on MutableBoard to perform a one
     *  step back undo. */
    private void undo(String input) {
        if (input.equals("")) {
            _board.undo();
        } else {
            try {
                int n = Integer.parseInt(input);
                for (int i = 0; i < n; i++) {
                    _board.undo();
                }
            } catch (NumberFormatException e) {
                System.err.println("not a valid input for undo");
            }
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _playing = false;
        setWinner();
        _out.print(_winningColor.toCapitalizedString() + " wins." + "\n");
        _out.flush();
    }

    /** setting up players initially. */
    private void setDefault() {
        if (_begin) {
            player1 = new HumanPlayer(this, RED);
            player2 = new Computer(this, BLUE);
            _begin = false;
        }
    }

    /** Takes in the integer N and decides whether there is
     *  a need to look at user input. Returns a boolean TRUE or FALSE.*/
    private boolean waitOrNot() {
        if (_playing) {
            if (player1 instanceof Computer
                    && !(_board.numMoves() % 2 == 0)) {
                return false;
            }
            if (player2 instanceof Computer
                    && _board.numMoves() % 2 == 0) {
                return false;
            }
        }
        return true;
    }

    /** Takes in string TYPE and string COLOR.
     *  Processes the input and decides how to allocate the sets to
     *  which specific player, takes in the input from the nextLine
     *  waiting in the play function. */
    private void humanOrDroid(String type, String color) {
        try {
            Color colorH = Color.parseColor(color);
            _playing = false;
            if (player1.getColor() == colorH) {
                electrify(type, colorH, player1);
            } else if (player2.getColor() == colorH) {
                electrify(type, colorH, player2);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("not a valid color selection");
        }
    }

    /** Takes in string TYPE, string COLOR and player PLAYER.
     *  The actual decision on making the certain player either
     *  a machine or a person. */
    private void electrify(String type, Color color, Player player) {
        if (type.equals("auto")) {
            if (player == player1) {
                player1 = new Computer(this, color);
            } else {
                player2 = new Computer(this, color);
            }
        } else {
            if (player == player1) {
                player1 = new HumanPlayer(this, color);
            } else {
                player2 = new HumanPlayer(this, color);
            }
        }
    }


    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        int temp = _board.size();
        _board.clear(temp);
        _playing = false;
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.print(_board);
        _out.flush();
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Takes in the string N.
     *  Stop any current game and set the move number to N. */
    private void setMoveNumber(String n) {
        int temp = 0;
        try {
            temp = Integer.parseInt(n);
        } catch (NumberFormatException e) {
            System.err.println("invalid entry");
        }
        _playing = false;
        _board.setMoves(temp);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Checks if COLOR its a valid color. Return a boolean. */
    boolean colorCheck(String color) {
        switch (color) {
        case "r":
            return true;
        case "b":
            return true;
        default:
            return false;
        }
    }

    /** Converts the INPUT to a valid color. Return the enum COLOR. */
    Color colorConvert(String input) {
        switch (input) {
        case "r":
            return RED;
        case "b":
            return BLUE;
        default:
            return WHITE;
        }
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(String r, String c, String spots, String color) {
        int temp1, temp2, temp3;
        temp1 = temp2 = temp3 = 0;
        try {
            temp1 = Integer.parseInt(r);
            temp2 = Integer.parseInt(c);
            temp3 = Integer.parseInt(spots);
        } catch (NumberFormatException e) {
            System.err.println("make sure row, columns,"
                + " and spots are numbers");
        }
        _playing = false;
        if (temp3 <= _board.neighbors(temp1, temp2) && colorCheck(color)) {
            _board.set(temp1, temp2, temp3, colorConvert(color));
        } else {
            System.err.println("not a recognizable color");
        }
    }

    /** Takes in the string N.
     *  Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(String n) {
        int temp = 0;
        try {
            temp = Integer.parseInt(n);
        } catch (NumberFormatException e) {
            System.out.println("invalid entry");
        }
        _playing = false;
        if (temp != 0) {
            _board.clear(temp);
        } else {
            System.out.println("not a applicable size");
        }
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        _playing = true;
        if (_board.numMoves() == 0) {
            _board.setMoves(1);
        }
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            throw error("move %d %d out of bounds", r, c);
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Returns a color (player) name from _inp: either RED or BLUE.
     *  Throws an exception if not present. */
    private Color readColor() {
        return Color.parseColor(_inp.next("[rR][eE][dD]|[Bb][Ll][Uu][Ee]"));
    }

    /** Delegates the things needed to be done to the designated player. */
    private void plExecute() {
        if (_playing) {
            if (_board.numMoves() % 2 == 0) {
                player2.makeMove();
            } else {
                player1.makeMove();
            }
        } else {
            System.err.println("no game has been started");
        }
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        recipe = inst.trim().split("\\s+|\\#.*");
        try {
            try {
                for (int i = 0; i < 2; i++) {
                    _move[i] = Integer.parseInt(recipe[i]);
                }
                plExecute();
            } catch (ArrayIndexOutOfBoundsException e) {
                if (recipe.length != 0) {
                    System.err.println("make sure two values are inputted");
                }
            }
        } catch (NumberFormatException e) {
            try {
                Integer.parseInt(recipe[0]);
                System.err.println("cannot run such input command");
            } catch (NumberFormatException er) {
                executeCommand(recipe[0]);
            }
        }
    }

    /** Takes in the integer INPUT and helps executeCommand process
     *  erroneous inputs. Returns a string. */
    private String filter(int input) {
        String tempS;
        try {
            tempS = recipe[input];
            return tempS;
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    private void executeCommand(String cmnd) {
        switch (cmnd) {
        case "": case "\r\n":
            return;
        case "dump":
            dump();
            break;
        case "help":
            help();
            break;
        case "set":
            setSpots(filter(1), filter(2), filter(3), filter(4));
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            _quitter = true;
            break;
        case "size":
            setSize(filter(1));
            break;
        case "move":
            setMoveNumber(filter(1));
            break;
        case "clear":
            clear();
            break;
        case "undo":
            undo(filter(1));
            break;
        case "auto":
            humanOrDroid(filter(0), filter(1));
            break;
        case "manual":
            humanOrDroid(filter(0), filter(1));
            break;
        default:
            System.err.println("not a valid command!");
        }
    }

    /** Print a prompt. */
    private void promptForNext() {
        if (_playing) {
            if (_board.numMoves() % 2 == 0) {
                _prompter.print(player2.getColor());
            } else {
                _prompter.print(player1.getColor());
            }
        }
        _prompter.print("> ");
        _prompter.flush();
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Able for AI to set when testing is wanted. */
    void setTesting() {
        if (testing) {
            testing = false;
        } else {
            testing = true;
        }
    }

    /** Able to manually bypass winner for AI. */
    void setWinner() {
        if (_winner) {
            _winner = false;
        } else {
            _winner = true;
        }
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    private final MutableBoard _board;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

    /** Holds the command input for process with readExecuteCommand. */
    private String inst;

    /** Determines if the program should exit. */
    private boolean _quitter = false;

    /** Determines if there is a winner. */
    private boolean _winner = false;

    /** Helps to do the beginning set or sets up for default call. */
    private boolean _begin = true;

    /** The winning color to be printed. */
    private Color _winningColor;

    /** Allows AI to bypass winning announcement. */
    private boolean testing = false;

    /** Temporarily holds the command to be inputted in the program. */
    private String[] recipe;

    /** variable to be set to player1. */
    private Player player1;

    /** variable to be set to player2. */
    private Player player2;

   /** Used to return a move entered from the console.  Allocated
    *  here to avoid allocations. */
    private final int[] _move = new int[2];
}
