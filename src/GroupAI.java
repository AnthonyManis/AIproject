import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;


public class GroupAI extends CKPlayer {

	final int WIN_VALUE = (int) 1.0e9;
	final int DRAW_VALUE = (int) 1.0e9 - 1;
	final int LOSE_VALUE = (int) -1.0e9;
	final int INF = WIN_VALUE + 100;
	final byte PLAYER1 = 1;
	final byte PLAYER2 = 2;

	public Point bestPoint = new Point(); // (i, j)

	public GroupAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "GroupAI";
		bestPoint.x = 0;
		bestPoint.y = 0;
	}

	@Override
	public Point getMove(BoardModel state) {

		while (state.hasMovesLeft()){
			System.out.println("best " + search(state, 4, player));
			return new Point(bestPoint.x, bestPoint.y);
		}
		return null;
	}


	public int heuristic(BoardModel state) {
		int result = 0;
		int winner = state.winner();
		if ( winner != -1 ) {
			if ( winner == 1 ) {
				result =  WIN_VALUE;
			}
			if ( winner == 2) {
				result = LOSE_VALUE;
			}
			if ( winner == 0) {
				result = DRAW_VALUE;
			}
			if ( player == PLAYER2 ) {
				result *= -1;
			}
			return result;
		}




		ArrayList<ArrayList<Integer>> cont = waysToWin(state);
		int player1total = 0, player2total = 0;
		for ( int i = 1 ; i < state.kLength ; i++ ) {
			int weight = (int) Math.pow(10, i-1);
			player1total += cont.get(1).get(i) * weight;
			player2total += cont.get(2).get(i) * weight;
		}

		result = player1total - player2total;
		if (player == PLAYER2) {
			result *= -1;
		}
		return result;
	}


	public byte nextPlayer(byte p) {
		return (byte) p == (byte) 1 ? (byte) 2 : (byte) 1;
	}

	public int search(BoardModel state, int depth, byte move) {
		int k = state.kLength;
		int v = 0;
		int bestV = 0;
		int alpha = -INF, beta = INF;
		boolean validMoveFound = false;
		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point p = new Point(i,j);
				if (state.getSpace(p) == 0) {
					v = minSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta);
					alpha = Math.max(alpha, v);
					// fallback in case we don't find anything we like
					if ( !validMoveFound ) {
						validMoveFound = true;
						bestV = v;
						bestPoint.x = p.x;
						bestPoint.y = p.y;
					}
					
					if ( v > bestV ) {
						bestV = v;
						bestPoint.x = p.x;
						bestPoint.y = p.y;
					}
				}
			}
		}
		return v;
	}

	public int maxSearch(BoardModel state, int depth, byte move, int alpha, int beta) {
		if ( depth == 0 ) {
			return heuristic(state);
		}

		int h = heuristic(state);
		if ( h == DRAW_VALUE || h == WIN_VALUE || h == LOSE_VALUE ) {
			return h;
		}

		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point p = new Point(i,j);
				if (state.getSpace(p) == 0) {
					alpha = Math.max(alpha, minSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta) );
					if (alpha >= beta) {
						return INF;
					}
				}
			}
		}
		return alpha;
	}

	public int minSearch(BoardModel state, int depth, byte move, int alpha, int beta) {
		if ( depth == 0 ) {
			return heuristic(state);
		}

		int h = heuristic(state);
		if ( h == DRAW_VALUE || h == WIN_VALUE || h == LOSE_VALUE ) {
			return h;
		}

		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point p = new Point(i,j);
				if (state.getSpace(p) == 0) {
					beta = Math.min(beta, maxSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta) );
					if (alpha >= beta) {
						return -INF;
					}
				}
			}
		}
		return beta;
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
		result.add(null);
		result.add(p1totals);
		result.add(p2totals);
		return result;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
