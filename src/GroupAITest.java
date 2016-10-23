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
		board = board.placePiece(new Point(0,0), (byte) 1);
		board = board.placePiece(new Point(1,0), (byte) 1);
		board = board.placePiece(new Point(2,0), (byte) 1);
		board = board.placePiece(new Point(3,0), (byte) 1);
		board = board.placePiece(new Point(1,1), (byte) 1);
		
		board = board.placePiece(new Point(9,9), (byte) 2);
		board = board.placePiece(new Point(9,8), (byte) 2);
		board = board.placePiece(new Point(8,8), (byte) 2);
		System.out.println(board.toString());
		
		ai = new GroupAI((byte) 1, board);
	}

	@Test
	public void winningSpacesTest() {
		System.out.println(ai.winningSpaces(board));
	}

}
