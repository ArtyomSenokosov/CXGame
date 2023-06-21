package connectx;

import java.util.LinkedList;
import java.util.TreeSet;

public class CXBoard {

    public final int M;
    public final int N;
    public final int X;


    // grid for the board
    protected CXCellState[][] B;

    // Marked Cells stack (used to undo)
    protected LinkedList<CXCell> MC;


    // First free row position
    protected int RP[];


    // Availabe (not full) columns
    protected TreeSet<Integer> AC;

    // we define characters for players (PR for Red, PY for Yellow)
    private final CXCellState[] Player = {CXCellState.P1, CXCellState.P2};

    // currentPlayer plays next move
    protected int currentPlayer;


    // game state
    protected CXGameState gameState;

    public CXBoard(int M, int N, int X) throws IllegalArgumentException {
        if (M <= 0)
            throw new IllegalArgumentException("M cannot be smaller than 1");
        if (N <= 0)
            throw new IllegalArgumentException("N cannot be smaller than 1");
        if (X <= 0)
            throw new IllegalArgumentException("X cannot be smaller than 1");

        this.M = M;
        this.N = N;
        this.X = X;

        B = new CXCellState[M][N];
        MC = new LinkedList<CXCell>();
        RP = new int[N];
        AC = new TreeSet<Integer>();
        reset();
    }


    //Resetta la tabella di gioco
    public void reset() {
        currentPlayer = 0;
        gameState = CXGameState.OPEN;
        initBoard();
        initDataStructures();
    }

    // Sets to free all board cells
    private void initBoard() {
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                B[i][j] = CXCellState.FREE;
    }

    //Resets the marked cells list and other data structures
    private void initDataStructures() {
        this.MC.clear();
        this.AC.clear();
        for (int j = 0; j < N; j++) {
            RP[j] = M - 1;
            AC.add(j);
        }
    }

    //Stato della cella i,j della matrice
    public CXCellState cellState(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || i >= M || j < 0 || j >= N)
            throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " are out of matrix bounds");
        else
            return B[i][j];
    }

    //Ritorna true se la colonna col e` piena
    public boolean fullColumn(int col) {
        return col < 0 || col >= N || RP[col] == -1;
    }

    //Ritorna l’ultima mossa effettuata
    public CXCell getLastMove() {
        if (MC.size() == 0)
            return null;
        else
            return MC.peekLast();
    }

    //Ritorna lo stato del gioco (WIN1, WIN2, DRAW, OPEN)
    public CXGameState gameState() {
        return gameState;
    }

    //Giocatore a cui tocca la prossima mossa
    public int currentPlayer() {
        return currentPlayer;
    }

    //Numero di celle ancora libere nella matrice
    public int numOfFreeCells() {
        return M * N - MC.size();
    }

    //Numero di celle gia` occupate nella matrice
    public int numOfMarkedCells() {
        return MC.size();
    }

    //Il giocatore corrente gioca sulla colonna indicata
    public CXGameState markColumn(int col) throws IndexOutOfBoundsException, IllegalStateException {
        if (gameState != CXGameState.OPEN) { // Game already ended
            throw new IllegalStateException("Game ended!");
        } else if (!(0 <= col && col < N)) { // Column index out of matrix bounds
            throw new IndexOutOfBoundsException("Index " + col + " out of matrix bounds\n" + "Column must be between 0 and " + (N - 1));
        } else if (RP[col] == -1) {          // Column full
            throw new IllegalStateException("Column " + col + " is full.");
        } else {
            int row = RP[col]--;
            if (RP[col] == -1) AC.remove(col);
            B[row][col] = Player[currentPlayer];
            CXCell newc = new CXCell(row, col, Player[currentPlayer]);
            MC.add(newc); // Add move to the history

            currentPlayer = (currentPlayer + 1) % 2;

            if (isWinningMove(row, col))
                gameState = B[row][col] == CXCellState.P1 ? CXGameState.WINP1 : CXGameState.WINP2;
            else if (MC.size() == M * N)
                gameState = CXGameState.DRAW;

            return gameState;
        }
    }

    //Elimina l’ultima mossa giocata
    public void unmarkColumn() throws IllegalStateException {
        if (MC.size() == 0) {
            throw new IllegalStateException("No move to undo");
        } else {
            CXCell oldc = MC.removeLast();

            B[oldc.i][oldc.j] = CXCellState.FREE;
            RP[oldc.j]++;
            if (RP[oldc.j] == 0) AC.add(oldc.j);

            currentPlayer = (currentPlayer + 1) % 2;
            gameState = CXGameState.OPEN;
        }
    }

    //Ritorna la lista di mosse gia` giocate, in ordine
    public CXCell[] getMarkedCells() {
        return MC.toArray(new CXCell[MC.size()]);
    }

    //Ritorna la lista di colonne non ancora piene
    public Integer[] getAvailableColumns() {
        return AC.toArray(new Integer[AC.size()]);
    }

    //Ritorna la matrice di gioco attuale
    public CXCellState[][] getBoard() {
        CXCellState[][] C = new CXCellState[M][N];

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C[i][j] = B[i][j];

        return C;
    }

    //Crea una copia dell’oggetto connectx.CXBoard
    public CXBoard copy() {
        CXBoard C = new CXBoard(M, N, X);
        for (CXCell c : this.getMarkedCells())
            C.markColumn(c.j);
        return C;
    }


    // Check winning state from cell i, j
    private boolean isWinningMove(int i, int j) {
        CXCellState s = B[i][j];
        int n;

        // Useless pedantic check
        if (s == CXCellState.FREE)
            return false;

        // Horizontal check
        n = 1;
        // backward check
        for (int k = 1; j - k >= 0 && B[i][j - k] == s; k++) n++;
        // forward check
        for (int k = 1; j + k < N && B[i][j + k] == s; k++) n++;
        if (n >= X) return true;

        // Vertical check
        n = 1;
        for (int k = 1; i + k < M && B[i + k][j] == s; k++) n++;
        if (n >= X) return true;

        // Diagonal check
        n = 1;
        // backward check
        for (int k = 1; i - k >= 0 && j - k >= 0 && B[i - k][j - k] == s; k++) n++;
        // forward check
        for (int k = 1; i + k < M && j + k < N && B[i + k][j + k] == s; k++) n++;
        if (n >= X) return true;

        // Anti-diagonal check
        n = 1;
        // backward check
        for (int k = 1; i - k >= 0 && j + k < N && B[i - k][j + k] == s; k++) n++;
        // forward check
        for (int k = 1; i + k < M && j - k >= 0 && B[i + k][j - k] == s; k++) n++;
        if (n >= X) return true;

        return false;
    }
}
