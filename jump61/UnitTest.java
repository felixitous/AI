package jump61;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/** Runs all the UnitTests and gives detailed explanation
 *  on the process of everything.
 *  @author Felix Liu
 */
public class UnitTest {

    /** Main class that doesn't actually take in ARGS but simply runs and
     *  prints the process of everything accordingly. */
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(UnitTestSuite.class);
        System.out.println("Total of "
                            + result.getRunCount() + " test(s) processed");
        System.out.println("Program runs at "
                            + result.getRunTime() + " milliseconds.\n");
        if (result.wasSuccessful()) {
            System.out.println("Great Job!!!");
        } else {
            System.out.println("Aww dude, its okay, just keep trying :)\n"
                                + "Hilfinger got nothing on you\n");
        }
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}
