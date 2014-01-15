package jump61;


/** A Player that gets its moves from manual input.
 *  @author Felix Liu
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board mboard = getmBoard();
        int[] move = getMove();
        mboard.addSpot(getColor(), move[0], move[1]);
    }

}
