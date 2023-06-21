package connectx.L0;

import connectx.CXBoard;
import connectx.CXPlayer;

import java.util.Random;

/**
 * Totally random software player.
 */
public class L0 implements CXPlayer {
	private Random rand;

	/* Default empty constructor */
	public L0() {
	}

	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());
	}

	/* Selects a random column */
	public int selectColumn(CXBoard B) {
		Integer[] L = B.getAvailableColumns();
		return L[rand.nextInt(L.length)];
	}

	public String playerName() {
		return "L0";
	}
}
	
	

