import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Random;

public class GroupAI extends CKPlayer {

    private Point bestPoint = new Point(); // (i, j)

	public GroupAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "GroupAI";
	}

	@Override
	public Point getMove(BoardModel state) {
		Random randomGenerator = new Random();

		while (state.hasMovesLeft()){

			int random_i = randomGenerator.nextInt(state.getWidth());
			int random_j = randomGenerator.nextInt(state.getHeight());
			if(state.getSpace(random_i, random_j) == 0)
				return new Point(random_i,random_j);
		}
		return null;
	}

	public int heuristic(BoardModel state){
		return state.height;
	}

	public byte nextPlayer(byte p) {
		return (byte) p == (byte) 1 ? (byte) 2 : (byte) 1;
	}

	// player can be 1 or 2
	public int search(BoardModel state, int depth, byte move) {
	    // base case
	    if ( depth == 0 ) {
	        return heuristic(state);
	    }

	    int bestValue = 0;
	    for ( int i  = 0 ; i  < state.getHeight(); i++) {
	        for (int j = 0 ; j < state.getWidth(); j++ ) {
	            if (state.getSpace(i, j) == 0) {
	            	Point p = new Point(i,j);
	                int value = search(state.placePiece(p, move), depth-1, nextPlayer(move));
	                if ( move == 1 ) {
	                    if ( value > bestValue){
	                        bestValue = value;
	                        bestPoint.x = i;
	                        bestPoint.y = j;
	                    }
	                }
	                else {
	                    if ( value < bestValue) {
	                        bestValue = value;
	                        bestPoint.x = i;
	                        bestPoint.y = j;
	                    }
	                }
	            }

	        }
	    }
	    return bestValue;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
