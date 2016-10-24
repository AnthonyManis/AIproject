import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import connectK.BoardModel;

public class GroupAITest {
	
	BoardModel board;
	GroupAI ai;
	
	@Before
	public void setUp() {
		board = new BoardModel(10, 10, 4, false);
		
//		board = board.placePiece(new Point(6,0), (byte) 1);
//		board = board.placePiece(new Point(7,0), (byte) 1);
		board = board.placePiece(new Point(8,0), (byte) 1);
		board = board.placePiece(new Point(9,0), (byte) 1);
		
		board = board.placePiece(new Point(9,9), (byte) 2);
		board = board.placePiece(new Point(8,9), (byte) 2);
		board = board.placePiece(new Point(7,9), (byte) 2);
//		board = board.placePiece(new Point(9,8), (byte) 2);
//		board = board.placePiece(new Point(8,8), (byte) 2);
		
		ai = new GroupAI((byte) 1, board);
	}

	@Test
	public void winningSpacesTest() {

		System.out.println(board.toString());
		System.out.println(ai.waysToWin(board));
		System.out.println(ai.heuristic(board, (byte) 1));
//		assertTrue(ai.waysToWin(board).get(0) < ai.waysToWin(board).get(1));
	}

}
