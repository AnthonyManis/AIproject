import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import connectK.BoardModel;

public class GroupAITest {
	
	BoardModel board;
	GroupAI ai;
	
	@Before
	public void setUp() {
		board = new BoardModel(9, 7, 5, false);
		board = board.placePiece(new Point( 4, 2), (byte) 1);
		board = board.placePiece(new Point( 3, 6), (byte) 2);
		board = board.placePiece(new Point( 3, 2), (byte) 1);
//		board = board.placePiece(new Point( 5, 6), (byte) 2);
//		board = board.placePiece(new Point( 5, 2), (byte) 1);
//		board = board.placePiece(new Point( 2, 2), (byte) 2);
//		board = board.placePiece(new Point( 3, 6), (byte) 1);
//		board = board.placePiece(new Point( 6, 2), (byte) 2);
//		board = board.placePiece(new Point( 4, 3), (byte) 1);
//		board = board.placePiece(new Point( 0, 0), (byte) 2);
//		board = board.placePiece(new Point( 3, 4), (byte) 1);
//		board = board.placePiece(new Point( 2, 5), (byte) 2);
//		board = board.placePiece(new Point( 5, 4), (byte) 1);
//		board = board.placePiece(new Point( 2, 1), (byte) 2);
//		board = board.placePiece(new Point( 3, 3), (byte) 1);
//		board = board.placePiece(new Point( 3, 5), (byte) 2);
//		board = board.placePiece(new Point( 2, 4), (byte) 1);
//		board = board.placePiece(new Point( 1, 5), (byte) 2);
//		board = board.placePiece(new Point( 4, 4), (byte) 1);
//		board = board.placePiece(new Point( 0, 1), (byte) 2);
//		board = board.placePiece(new Point( 6, 4), (byte) 1);
		
		ai = new GroupAI((byte) 2, board);
	}

	@Test
	public void winningSpacesTest() {

		System.out.println(board.toString());
		System.out.println(ai.s.waysToWin(board));
		System.out.println(ai.s.heuristic(board));
//		System.out.println(ai.search(board, 3, (byte)2));
//		System.out.println(ai.bestPoint.x + ", " + ai.bestPoint.y);
//		assertTrue(ai.waysToWin(board).get(0) < ai.waysToWin(board).get(1));
	}

}
