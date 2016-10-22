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

	List<Integer> winningSpaces(BoardModel state) {
		int p1Count = 0;
		int p2Count = 0;
		List<Integer> result = new ArrayList<Integer>();
		List<Point> ws = new ArrayList<Point>(state.kLength);
		for (int i = 0; i < state.width; ++i) {
			for (int j = 0; j < state.height; ++j) {
				// if the space previous is either not the same as current,
				// empty, or OOB
				// while the next thing is the same AND not OOB
				// increment contiguous count
				// if count greater than k, return the winner
				// returns on first winning sequence found
				// searches to the right and up

				if (state.pieces[i][j] == 0) {
					if (state.gravity)
						break;// go to next column
					else
						continue;// move up
				}

				if (i - 1 < 0 || state.pieces[i - 1][j] != state.pieces[i][j]) { // horizontal
					int count = 1;
					while (i + count < state.width && state.pieces[i][j] == state.pieces[i + count][j]) {
						++count;
						if (count >= state.kLength) {
							for (int k = 0; k < state.kLength; ++k)
								ws.add(new Point(i + k, j));
							if (state.pieces[i][j] == 1)
								++p1Count;
							else if(state.pieces[i][j] == 2)
								++p2Count;
							ws.clear();
						}
					}
				}

				if (i - 1 < 0 || j - 1 < 0 || state.pieces[i - 1][j - 1] != state.pieces[i][j]) { // diagonal
																						// up,
																						// (j-1<0)
																						// needed
																						// to
																						// avoid
																						// OOB
					int count = 1;
					while (i + count < state.width && j + count < state.height && state.pieces[i][j] == state.pieces[i + count][j + count]) {
						++count;
						if (count >= state.kLength) {
							for (int k = 0; k < state.kLength; ++k)
								ws.add(new Point(i + k, j + k));
							if (state.pieces[i][j] == 1)
								++p1Count;
							else if(state.pieces[i][j] == 2)
								++p2Count;
							ws.clear();
						}
					}
				}

				if (i - 1 < 0 || j + 1 >= state.height || state.pieces[i - 1][j + 1] != state.pieces[i][j]) { // diagonal
																							// down,
																							// (j+1>=height)
																							// needed
																							// to
																							// avoid
																							// OOB
					int count = 1;
					while (i + count < state.width && j - count >= 0 && state.pieces[i][j] == state.pieces[i + count][j - count]) {
						++count;
						if (count >= state.kLength) {
							for (int k = 0; k < state.kLength; ++k)
								ws.add(new Point(i + k, j - k));
							if (state.pieces[i][j] == 1)
								++p1Count;
							else if(state.pieces[i][j] == 2)
								++p2Count;
							ws.clear();
						}
					}
				}

				if (j - 1 < 0 || state.pieces[i][j - 1] != state.pieces[i][j]) { // vertical
					int count = 1;
					while (j + count < state.height && state.pieces[i][j] == state.pieces[i][j + count]) {
						++count;
						if (count >= state.kLength) {
							for (int k = 0; k < state.kLength; ++k)
								ws.add(new Point(i, j + k));
							if (state.pieces[i][j] == 1)
								++p1Count;
							else if(state.pieces[i][j] == 2)
								++p2Count;
							ws.clear();
						}
					}
				}
			}
		}
		result.add(p1Count);
		result.add(p2Count);
		return result;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
