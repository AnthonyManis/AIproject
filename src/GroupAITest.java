import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import connectK.BoardModel;

public class GroupAITest {
	
	BoardModel board;
	GroupAI ai;
	
	@Before
	public void setUp() {
		board = new BoardModel(10, 10, 4, false);
//		board = board.placePiece(new Point(4,0), (byte) 1);
//		board = board.placePiece(new Point(5,0), (byte) 1);
//		board = board.placePiece(new Point(6,0), (byte) 1);
		board = board.placePiece(new Point(7,0), (byte) 2);
		board = board.placePiece(new Point(8,0), (byte) 1);
		board = board.placePiece(new Point(9,0), (byte) 1);
		
		board = board.placePiece(new Point(9,9), (byte) 2);
		board = board.placePiece(new Point(9,8), (byte) 2);
		board = board.placePiece(new Point(9,7), (byte) 2);
		board = board.placePiece(new Point(8,8), (byte) 2);
		board = board.placePiece(new Point(7,7), (byte) 2);
		board = board.placePiece(new Point(6,6), (byte) 2);
		System.out.println(board.toString());
		
		ai = new GroupAI((byte) 1, board);
	}

	@Test
	public void winningSpacesTest() {
		List<Integer> f = new ArrayList<Integer>();
		f.add(4);
		f.add(3);
		System.out.println(ai.turnsToWin(board));
		assertEquals(f, ai.turnsToWin(board));
	}

}
