import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import connectK.BoardModel;
import connectK.CKPlayer;


public class GroupAI extends CKPlayer {

	final int WIN_VALUE = (int) 1.0e9;
	final int DRAW_VALUE = (int) 1.0e9 - 1;
	final int LOSE_VALUE = (int) -1.0e9;
	final int INF = WIN_VALUE + 100;
	final byte PLAYER1 = 1;
	final byte PLAYER2 = 2;

	public Point bestPoint;
	public int runDepth;

	public HashMap<BoardModel, Point> map;

	public GroupAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "GroupAI";
		bestPoint = new Point();
		map = new HashMap<BoardModel, Point>();
	}

	@Override
	public Point getMove(BoardModel state) {

		while (state.hasMovesLeft()){
			try {
				search(state, 4, player, 0);
//				System.out.println("best " + search(state, 4, player, 0));
			}
			catch (TimeoutException e) {
				return bestPoint;
			}
			return bestPoint;
		}
		return null;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		long marginTime = 1000L;
		long endTime = System.currentTimeMillis() + deadline - marginTime;

		// Call search with a deadline
		runDepth = 1;
		try {
			while (System.currentTimeMillis() < endTime) {
				search(state, runDepth, player, endTime);
//				System.out.println("best " + search(state, runDepth, player, endTime));
				runDepth++;
			}
		}
		catch (TimeoutException e) {
			System.out.println("runDepth " + (runDepth-1));
			return bestPoint;
		}


		return bestPoint;
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

	public int search(BoardModel state, int depth, byte move, long deadline) throws TimeoutException {
		int v = 0;
		int bestV = 0;
		int alpha = -INF, beta = INF;
		boolean validMoveFound = false;
		Point nonFinalBestPoint = null;

		// Create a normal queue, starting with previous best move if present
		// Then adding every other move
		Queue<Point> stateQ = new LinkedList<Point>();
		if ( map.get(state) != null ) {
			stateQ.add(map.get(state));
		}
		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point p = new Point(i,j);
				if (state.getSpace(p) == 0)
					stateQ.add(p);
					if(state.gravity)
						break;				
			}
		}
		// Iterate through the queue
		while (!stateQ.isEmpty()) {
			Point p = stateQ.poll();
			// Before the recursive call, check for the deadline
			if ( deadline != 0 && System.currentTimeMillis() >= deadline ) {
				throw new TimeoutException();
			}
			v = minSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta, deadline);
			alpha = Math.max(alpha, v);
			// fallback in case we don't find anything we like
			if ( !validMoveFound ) {
				validMoveFound = true;
				bestV = v;
				nonFinalBestPoint = p;
			}

			if ( v > bestV ) {
				bestV = v;
				nonFinalBestPoint = p;
			}
		}
		if ( nonFinalBestPoint != null ) {
			bestPoint = nonFinalBestPoint;
			map.put(state, bestPoint);
		}
		return v;
	}

	public int maxSearch(BoardModel state, int depth, byte move, int alpha, int beta, long deadline) throws TimeoutException {
		if ( depth == 0 ) {
			return heuristic(state);
		}

		int h = heuristic(state);
		if ( h == DRAW_VALUE || h == WIN_VALUE || h == LOSE_VALUE ) {
			return h;
		}

		// Create a normal queue, starting with previous best move if present
		// Then adding every other move
		Queue<Point> stateQ = new LinkedList<Point>();
		if ( map.get(state) != null ) {
			stateQ.add(map.get(state));
		}
		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point newPoint = new Point(i,j);
				if (newPoint != map.get(state))
					stateQ.add(newPoint);
			}
		}
		// Iterate through the queue
		Point bestAction = null;
		while (!stateQ.isEmpty()) {
			Point p = stateQ.poll();
			if (state.getSpace(p) == 0) {
				// Before the recursive call, check for the deadline
				if ( deadline != 0 && System.currentTimeMillis() >= deadline ) {
					throw new TimeoutException();
				}
				int v = minSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta, deadline);
				if ( v > alpha ) {
					alpha = v;
					bestAction = p;
				}
				if (alpha >= beta) {
					if ( bestAction != null) {
						map.put(state, bestAction);
					}
					return INF;
				}
			}
		}

		if ( bestAction != null) {
			map.put(state, bestAction);
		}
		return alpha;
	}

	public int minSearch(BoardModel state, int depth, byte move, int alpha, int beta, long deadline) throws TimeoutException {
		if ( depth == 0 ) {
			return heuristic(state);
		}

		int h = heuristic(state);
		if ( h == DRAW_VALUE || h == WIN_VALUE || h == LOSE_VALUE ) {
			return h;
		}

		// Create a normal queue, starting with previous best move if present
		// Then adding every other move
		Queue<Point> stateQ = new LinkedList<Point>();
		if ( map.get(state) != null ) {
			stateQ.add(map.get(state));
		}
		for (int i = 0 ; i < state.width ; i++) {
			for (int j = 0 ; j < state.height ; j++) {
				Point newPoint = new Point(i, j);
				if (newPoint != map.get(state))
					stateQ.add(newPoint);
			}
		}
		// Iterate through the queue
		Point bestAction = null;
		while (!stateQ.isEmpty()) {
			Point p = stateQ.poll();
			if (state.getSpace(p) == 0) {
				// Before the recursive call, check for the deadline
				if ( deadline != 0 && System.currentTimeMillis() >= deadline ) {
					throw new TimeoutException();
				}
				int v = maxSearch(state.placePiece(p, move), depth-1, nextPlayer(move), alpha, beta, deadline);
				if ( v < beta) {
					beta = v;
					bestAction = p;
				}
				if (alpha >= beta) {
					if ( bestAction != null) {
						map.put(state, bestAction);
					}
					return -INF;
				}
			}
		}

		if ( bestAction != null) {
			map.put(state, bestAction);
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

}
