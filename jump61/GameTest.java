package jump61;
import org.junit.Test;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import static jump61.Color.*;

/** Junit Tests for Game
 *  @author Felix Liu
 */

public class GameTest {

    /** Sets up the MutableBoard to be altered. */
    private MutableBoard mtboard;

    /** Sets up game. */
    private Game tGame;

    /** Object that will be altered to test
     *  private, protected fields. */
    private Object value;

    /** builds the fields. Takes in the string INPUT */
    void fieldSetup(String input) {
        try {
            Field field = Game.class.getDeclaredField(input);
            field.setAccessible(true);
            value = field.get(tGame);
        } catch (IllegalAccessException|NoSuchFieldException e) {
            System.err.println("error");
            System.exit(1);
        }
    }

    /** method invoking. */
    void methodSetup(String input) {
        try {
            Method method = Game.class.getDeclaredMethod(input);
            method.setAccessible(true);
            method.invoke(tGame);
        } catch (NoSuchMethodException|InvocationTargetException
            |IllegalAccessException e) {
            System.err.println("error");
            System.exit(1);
        }
    }

    /** function that sets up a game. */
    void setupGame(int input) {
        Writer output = new OutputStreamWriter(System.out);
        tGame = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
        mtboard = new MutableBoard(tGame, input);
    }

    /** Checking if when restartGame is accessed,
     *  if the game actually sets the playing to true. */
    @Test
    public void test1() {
        setupGame(1);
        mtboard.addSpot(RED, 1, 1);
        methodSetup("restartGame");
        fieldSetup("_playing");
        boolean temp = (boolean) value;
        assertEquals("_playing not changed.", true, temp);
    }

    /** Make sure clear turns off the game. */
    @Test
    public void test2() {
        setupGame(6);
        mtboard.addSpot(RED, 2, 2);
        methodSetup("restartGame");
        methodSetup("clear");
        fieldSetup("_playing");
        boolean temp = (boolean) value;
        assertEquals("_playing doesn't turn off.", false, temp);
    }

    /** Making sure colorCheck is working correctly. */
    @Test
    public void test3() {
        setupGame(6);
        boolean temp = tGame.colorCheck("r");
        assertEquals("stating non-valid color for a valid color",
            true, temp);
        temp = tGame.colorCheck("b");
        assertEquals("stating non-valid color for a valid color",
            true, temp);
        temp = tGame.colorCheck("x");
        assertEquals("stating non-valid color for a valid color",
            false, temp);
    }

    /** Making sure that colorConvert is working correctly. */
    @Test
    public void test4() {
        setupGame(6);
        Color temp = tGame.colorConvert("r");
        assertEquals("color not given back correctly", RED, temp);
        temp = tGame.colorConvert("b");
        assertEquals("color not given back correctly", BLUE, temp);
        temp = tGame.colorConvert("random input");
        assertEquals("color not given back correctly", WHITE, temp);
    }
}
