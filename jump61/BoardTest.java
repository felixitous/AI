package jump61;
import org.junit.Test;
import java.io.Writer;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import static jump61.Color.*;

/** Junit Tests for Board
 *  @author Felix Liu
 */

public class BoardTest {

    /** Sets up the MutableBoard to be altered. */
    private MutableBoard mtboard;

    /** Sets up game. */
    private Game tGame;

    /** Object that will be altered to test
     *  private, protected fields. */
    private Object value;

    /** function that helps build the size of the
     *  MutableBoard. */
    void setupMboard(int input) {
        mtboard = new MutableBoard(input);
    }

    /** function that sets up a game for use. */
    void setupMGboard(int input) {
        Writer output = new OutputStreamWriter(System.out);
        Game game = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
        mtboard = new MutableBoard(game, input);
    }

    /** builds the fields. */
    void fieldSetup(String input) {
        try {
            Field field = Board.class.getDeclaredField(input);
            field.setAccessible(true);
            value = field.get(mtboard);
        } catch (IllegalAccessException|NoSuchFieldException e) {
            System.err.println("error");
            System.exit(1);
        }
    }

    /** Tests the construction of the size of the
     *  gameboard. */
    @Test
    public void test1() {
        setupMboard(6);
        fieldSetup("gameboard");
        String[][] actual = (String[][]) value;
        assertEquals(6, actual.length);
    }

    /** Tests if the size() is working as intended
     */
    @Test
    public void test2() {
        setupMboard(6);
        assertEquals(6, mtboard.size());
    }

    /** Tests the set move function. */
    @Test
    public void test3() {
        setupMboard(6);
        mtboard.setMoves(2);
        assertEquals("setting moves"
            + " is incorrect.",
            2, mtboard.numMoves());
    }

    /** Tests the set functionality
     *  of the program. */
    @Test
    public void test4() {
        setupMboard(6);
        mtboard.set(1, 1, 1, RED);
        assertEquals("wrong number"
            + " of spots. ",
            1, mtboard.spots(1, 1));
        assertEquals("wrong color",
            RED, mtboard.color(1, 1));
    }

    /** Tests the add-spot functionality. */
    @Test
    public void test5() {
        setupMGboard(6);
        mtboard.addSpot(BLUE, 1, 1);
        assertEquals("correct number of dots",
            1, mtboard.spots(1, 1));
    }

    /** Tests the jump functionality. */
    @Test
    public void test6() {
        setupMGboard(6);
        mtboard.set(1, 1, 2, RED);
        mtboard.set(1, 2, 3, RED);
        mtboard.set(2, 1, 3, RED);
        mtboard.addSpot(RED, 1, 1);
        assertEquals(1, mtboard.spots(1, 1));
        assertEquals(2, mtboard.spots(1, 2));
        assertEquals(2, mtboard.spots(2, 1));
        assertEquals(2, mtboard.spots(2, 2));
        assertEquals(1, mtboard.spots(1, 3));
        assertEquals(1, mtboard.spots(3, 1));
    }

    /** Tests the functionality of undo. */
    @Test
    public void test7() {
        setupMGboard(6);
        mtboard.addSpot(RED, 1, 1);
        mtboard.addSpot(BLUE, 3, 1);
        mtboard.undo();
        assertEquals(1, mtboard.spots(1, 1));
        assertEquals(0, mtboard.spots(3, 1));
    }

    /** Tests the functionality of clear. */
    @Test
    public void test8() {
        setupMGboard(6);
        mtboard.addSpot(RED, 1, 1);
        mtboard.set(1, 5, 3, BLUE);
        mtboard.clear(6);
        fieldSetup("gameboard");
        String[][] actual = (String[][]) value;
        for (String[] items : actual) {
            String[] temp = items;
            for (String things : temp) {
                assertEquals(null, things);
            }
        }
    }

    /** Testing if undoInfo is getting the right
     *  info and if strRemove is actually removing
     *  the last of the undoInfo list. Mostly need
     *  this to work for AI. */
    @Test
    @SuppressWarnings("unchecked")
    public void test9() {
        setupMGboard(6);
        mtboard.addSpot(RED, 1, 1);
        mtboard.addSpot(RED, 2, 1);
        mtboard.historian();
        fieldSetup("undoInfo");
        ArrayList<ArrayList<String>> temp =
            (ArrayList<ArrayList<String>>) value;
        assertEquals(3, temp.size());
        mtboard.strRemove();
        fieldSetup("undoInfo");
        temp = (ArrayList<ArrayList<String>>) value;
        assertEquals(2, temp.size());
    }
}
