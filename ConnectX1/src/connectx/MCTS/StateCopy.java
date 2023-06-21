package connectx.MCTS;

import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXCellState;

import java.util.ArrayList;
import java.util.List;

//State of a node in a Monte Carlo Search Tree.
public class StateCopy {

    private CXBoard cxBoard;
    //  private int playerID;
    private int visitCount;
    private double winScore;

    private boolean isFirst;

    //Constructors:
    public StateCopy(int M, int N, int X) {
        cxBoard = new CXBoard(M, N, X);
    }

    public StateCopy(CXBoard board) {
        cxBoard = board;
    }

    public StateCopy(StateCopy state) {
        state = new StateCopy(cxBoard.M, cxBoard.N, cxBoard.X);
    }

    public boolean getFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    CXBoard getCXBoard() {
        return cxBoard;
    }

    void setCXBoard(CXBoard board) {
        cxBoard = board;
    }

    //противник
    boolean getOpponent() {
        return !isFirst; //Assuming player ID' are 1 and 2 only
    }

    //ответ
    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int pVisitCount) {
        visitCount = pVisitCount;
    }

    double getWinScore() {
        return winScore;
    }

    void setWinScore(int pWinScore) {
        winScore = pWinScore;
    }


    public List<CXCell> getEmptyPositions(CXBoard B) {
        List<CXCell> emptyPositions = new ArrayList<>();
        //iterate through columns. There's only one move per column available at most
        // итерация по столбцам. В каждой колонке доступно не более одного хода
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j < B.N; j++) {
                if (B.cellState(i, j).equals(CXCellState.FREE)) {
                    emptyPositions.add(new CXCell(i, j, CXCellState.FREE));
                }
            }
        }
        return emptyPositions;
    }


    public List<StateCopy> getAllPossibleStates() {
        List<StateCopy> possibleStates = new ArrayList<>();
        List<CXCell> availablePositions = getEmptyPositions(cxBoard);
        for (CXCell p : availablePositions) {
            StateCopy newState = new StateCopy(cxBoard);
            newState.setFirst(!isFirst);
            newState.getCXBoard().markColumn(p.i);//          getBoard().performMove(newState.getPlayerID(), p.getX()); //TODO: may need debugging ...
            possibleStates.add(newState);
        }
        return possibleStates;
    }

    void incrementVisit() {
        visitCount++;
    }

    void addScore(double pScore) {
        if (winScore != Integer.MIN_VALUE) {
            winScore += pScore;
        }
    }

    void randomPlay() {
        List<CXCell> availablePositions = getEmptyPositions(cxBoard);
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * totalPossibilities);
        cxBoard.markColumn(availablePositions.get(selectRandom).i);//TODO: may need debugging...
    }

    void togglePlayer() {
        isFirst = !isFirst;
    }
}