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
		board = new BoardModel(9, 7, 5, false);
		board = board.placePiece(new Point(3,2), (byte) 1);
//		board = board.placePiece(new Point(3,6), (byte) 1);
//		board = board.placePiece(new Point(3,4), (byte) 1);
//		board = board.placePiece(new Point(3,3), (byte) 1);
//		board = board.placePiece(new Point(3,5), (byte) 1);
		
		
		ai = new GroupAI((byte) 2, board);
	}

	@Test
	public void winningSpacesTest() {

		System.out.println(board.toString());
		System.out.println(ai.waysToWin(board));
		System.out.println(ai.heuristic(board));
//		assertTrue(ai.waysToWin(board).get(0) < ai.waysToWin(board).get(1));
	}

}
