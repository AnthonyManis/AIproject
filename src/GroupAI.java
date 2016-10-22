import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

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

	List<Point> winningSpaces() {
		List<Point> ws = new ArrayList<Point>(kLength);
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				// if the space previous is either not the same as current,
				// empty, or OOB
				// while the next thing is the same AND not OOB
				// increment contiguous count
				// if count greater than k, return the winner
				// returns on first winning sequence found
				// searches to the right and up

				if (pieces[i][j] == 0) {
					if (gravity)
						break;// go to next column
					else
						continue;// move up
				}

				if (i - 1 < 0 || pieces[i - 1][j] != pieces[i][j]) { // horizontal
					int count = 1;
					while (i + count < width && pieces[i][j] == pieces[i + count][j]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j));
							return ws;
						}
					}
				}

				if (i - 1 < 0 || j - 1 < 0 || pieces[i - 1][j - 1] != pieces[i][j]) { // diagonal
																						// up,
																						// (j-1<0)
																						// needed
																						// to
																						// avoid
																						// OOB
					int count = 1;
					while (i + count < width && j + count < height && pieces[i][j] == pieces[i + count][j + count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j + k));
							return ws;
						}
					}
				}

				if (i - 1 < 0 || j + 1 >= height || pieces[i - 1][j + 1] != pieces[i][j]) { // diagonal
																							// down,
																							// (j+1>=height)
																							// needed
																							// to
																							// avoid
																							// OOB
					int count = 1;
					while (i + count < width && j - count >= 0 && pieces[i][j] == pieces[i + count][j - count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j - k));
							return ws;
						}
					}
				}

				if (j - 1 < 0 || pieces[i][j - 1] != pieces[i][j]) { // vertical
					int count = 1;
					while (j + count < height && pieces[i][j] == pieces[i][j + count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i, j + k));
							return ws;
						}
					}
				}
			}
		}
		return ws;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
