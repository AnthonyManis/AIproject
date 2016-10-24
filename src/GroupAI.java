import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
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

		while (state.hasMovesLeft()){
			search(state, 3, player);
			return new Point(bestPoint.x, bestPoint.y);
		}
		return null;
	}

	// Only use heuristic on boards where it is the opponent's turn to play.
	public int heuristic(BoardModel state){
		ArrayList<ArrayList<Integer>> ret = waysToWin(state);
		int total = 0, player1total = 0, player2total = 0;
		int player1biggest = 0, player2biggest = 0;
		int k = state.kLength;

		// Player 1 has won on this board, or plays next and wins.
		if ( ret.get(0).get(k) > 0 || (player == 2 && ret.get(0).get(k-1) > 0) ){
			player1total = 1000000;
		}
		// Player 2 has won on this board, or plays next and wins.
		else if ( ret.get(1).get(k) > 0 || (player == 1 && ret.get(1).get(k-1) > 0) ){
			player2total = 1000000;
		}
		else {
			// Find how close each player is to winning.
			for (int i = 1 ; i < k-1 ; i++) {
				if ( ret.get(0).get(i) > 0 )
					player1biggest = i;
				if ( ret.get(1).get(i) > 0 )
					player2biggest = i;
			}

			if ( player == 1 ) {
				if ( player1biggest <= player2biggest ){
					player2total += ret.get(1).get(player2biggest);
				}
				else {
					player1total += ret.get(0).get(player1biggest);
				}
			}
			else {
				if ( player2biggest <= player1biggest ) {
					player1total += ret.get(0).get(player1biggest);
				}
				else {
					player2total += ret.get(0).get(player2biggest);
				}
			}
		}

		total = player1total - player2total;
		// player field in the method calling search aka US
		if (player == 1) 
			return total;
		else
			return 0 - total;
	}

	public byte nextPlayer(byte p) {
		return (byte) p == (byte) 1 ? (byte) 2 : (byte) 1;
	}

	// move can be 1 or 2
	public int search(BoardModel state, int depth, byte move) {
		// base case
		if ( depth == 0 ) {
			return heuristic(state);
		}

		int bestValue = 0;
		boolean isBVSet = false;
		for ( int i  = 0 ; i  < state.getWidth() ; i++) {
			for (int j = 0 ; j < state.getHeight(); j++ ) {
				if (state.getSpace(i, j) == 0) {
					Point p = new Point(i,j);
					int value = search(state.placePiece(p, move), depth-1, nextPlayer(move));
					if ( isBVSet == false ) {
						bestValue = value;
						bestPoint.x = i;
						bestPoint.y = j;
						isBVSet = true;
					}
					if ( move == player ) {
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

	// Returns the number of turns needed to win by each player for this board
	// (player1, player2)
	public ArrayList<ArrayList<Integer>> waysToWin(BoardModel state) {
		ArrayList<Integer> p1totals = new ArrayList<Integer>(state.kLength+1);
		ArrayList<Integer> p2totals = new ArrayList<Integer>(state.kLength+1);
		for (int i = 0 ; i <= state.kLength ; i++) {
			p1totals.add(0);
			p2totals.add(0);
		}
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		// Horizontal windows
		for (int j = 0 ; j < state.height ; j++) {
			int player1 = 0, player2 = 0;
			int windowStart= 0, windowSize = 0;
			for (int i = 0 ; i < state.width ; i++) {
				if ( windowSize < state.kLength )
					windowSize++;

				if ( state.getSpace(i, j) == 1 )
					player1++;
				else if ( state.getSpace(i, j) == 2)
					player2++;

				// Scoring
				if ( windowSize == state.kLength ) {
					if ( player1 == 0 ) {
						int t = p2totals.get(player2) + 1;
						p2totals.set(player2, t);
					}						
					if ( player2 == 0 ) {
						int t = p1totals.get(player1) + 1;
						p1totals.set(player1, t);
					}

					// Adjust window
					if ( state.getSpace(windowStart, j) == 1 )
						player1--;
					else if ( state.getSpace(windowStart, j) == 2 )
						player2--;
					windowSize--;
					windowStart++;
				}
			}
		}

		// Vertical windows
		for (int i = 0 ; i < state.width ; i++) {
			int player1 = 0, player2 = 0;
			int windowStart = 0, windowSize = 0;
			for (int j = 0 ; j < state.height ; j++) {
				if ( windowSize < state.kLength ) {
					windowSize++;
				}

				if ( state.getSpace(i, j) == 1 )
					player1++;
				else if ( state.getSpace(i, j) == 2 )
					player2++;

				// Scoring
				if ( windowSize == state.kLength ) {
					if ( player1 == 0 ) {
						int t = p2totals.get(player2) + 1;
						p2totals.set(player2, t);
					}						
					if ( player2 == 0 ) {
						int t = p1totals.get(player1) + 1;
						p1totals.set(player1, t);
					}

					// Adjust window
					if ( state.getSpace(i, windowStart) == 1 )
						player1--;
					if ( state.getSpace(i, windowStart) == 2 )
						player2--;
					windowSize--;
					windowStart++;
				}
			}
		}

		// Diagonal up windows
		for (int j = 0 ; j < state.height ; j++) {
			int player1 = 0, player2 = 0;
			int windowSize = 0, windowStart = 0;
			for (int i = 0 ; i < state.width ; i++) {

				// Skipping a lot of stuff that we don't need
				if ( i != 0 && j != 0 )
					break;

				int count = 0;
				windowSize = 0;
				windowStart = 0;
				player1 = 0;
				player2 = 0;
				while (i + count < state.width && j + count < state.height ) {
					if ( windowSize == state.kLength )
						windowStart++;

					if ( windowSize < state.kLength) {
						windowSize++;
					}

					if ( state.getSpace(i + count, j + count) == 1 )
						player1++;
					else if ( state.getSpace(i + count, j + count) == 2 )
						player2++;


					// Scoring
					if ( windowSize == state.kLength ) {
						if ( player1 == 0 ) {
							int t = p2totals.get(player2) + 1;
							p2totals.set(player2, t);
						}						
						if ( player2 == 0 ) {
							int t = p1totals.get(player1) + 1;
							p1totals.set(player1, t);
						}

						// Adjust window
						if ( state.getSpace(i + windowStart, j + windowStart) == 1 )
							player1--;
						if ( state.getSpace(i + windowStart, j + windowStart) == 2 )
							player2--;
					}
					count++;
				}
			}
		}

		// Diagonal down windows
		for (int j = 0 ; j < state.height ; j++) {
			int player1 = 0, player2 = 0;
			int windowSize = 0, windowStart = 0;
			for (int i = 0 ; i < state.width ; i++) {

				// Skipping a lot of stuff that we don't need
				if ( i != 0 && j != state.height-1 )
					break;

				int count = 0;
				windowSize = 0;
				windowStart = 0;
				player1 = 0;
				player2 = 0;
				while (i + count < state.width && j - count >=0) {
					if ( windowSize == state.kLength )
						windowStart++;

					if ( windowSize < state.kLength) {
						windowSize++;
					}

					if ( state.getSpace(i + count, j - count) == 1 )
						player1++;
					else if ( state.getSpace(i + count, j - count) == 2 )
						player2++;


					// Scoring
					if ( windowSize == state.kLength ) {
						if ( player1 == 0 ) {
							int t = p2totals.get(player2) + 1;
							p2totals.set(player2, t);
						}						
						if ( player2 == 0 ) {
							int t = p1totals.get(player1) + 1;
							p1totals.set(player1, t);
						}

						// Adjust window
						if ( state.getSpace(i + windowStart, j - windowStart) == 1 )
							player1--;
						if ( state.getSpace(i + windowStart, j - windowStart) == 2 )
							player2--;
					}
					count++;
				}
			}
		}

		result.add(p1totals);
		result.add(p2totals);
		return result;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
