package connectx.MCTS;

import connectx.MCTS.Connect4.Board;
import connectx.MCTS.Connect4.Position;

import java.util.ArrayList;
import java.util.List;

//State of a node in a Monte Carlo Search Tree.
public class State {
    private Board board;
    //какой игрок 1 или 2
    private int playerID;
    private int visitCount;
    private double winScore;

    //Constructors:
    public State(){
        board = new Board();
    }

    public State(State pState) {
        board = new Board(pState.getBoard());
        playerID = pState.getPlayerID();
        visitCount = pState.getVisitCount();
        winScore = pState.getWinScore();
    }

    public State(Board pBoard){
        board = new Board(pBoard);
    }

    Board getBoard(){
        return board;
    }

    void setBoard(Board pBoard){
        board = pBoard;
    }

    int getPlayerID(){
        return playerID;
    }

    void setPlayerID(int pPlayerID){
        playerID = pPlayerID;
    }

    int getOpponent(){
        return 3-playerID; //Assuming player ID' are 1 and 2 only
    }

    public int getVisitCount(){
        return visitCount;
    }

    public void setVisitCount(int pVisitCount){
        visitCount = pVisitCount;
    }

    double getWinScore(){
        return winScore;
    }

    void setWinScore(int pWinScore){
        winScore = pWinScore;
    }

    public List<State> getAllPossibleStates(){
        List<State> possibleStates = new ArrayList<>();
        List<Position> availablePositions = board.getEmptyPositions();
        for(Position p: availablePositions){
            State newState = new State(board);
            newState.setPlayerID(3-playerID);
            newState.getBoard().performMove(newState.getPlayerID(),p.getX()); //TODO: may need debugging ...
            possibleStates.add(newState);
        }
        return possibleStates;
    }

    void incrementVisit(){
        visitCount++;
    }

    void addScore(double pScore){
        if(winScore!=Integer.MIN_VALUE){
            winScore += pScore;
        }
    }

    void randomPlay(){
        List<Position> availablePositions= board.getEmptyPositions();
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random()*totalPossibilities);
        board.performMove(playerID,availablePositions.get(selectRandom).getX()); //TODO: may need debugging...
    }

    void togglePlayer(){
        playerID = 3-playerID;
    }
}