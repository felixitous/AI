AI
==

For our database class, this was our third project, based on creating a game and creating a computer player which a human could play against. In order to make a decent AI, the algorithm needed to look four steps in advance or go down a minimax tree, 4 levels deep. However, I wanted to make a better AI by creating a AI that looked eight steps in advance which could also fit in the 15 second time limit requirement put on by the specifications of the project. The code below is a snippet from the Computer.java file or my implementation of the minimax tree with alpha-beta pruning.

``` java
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
```


