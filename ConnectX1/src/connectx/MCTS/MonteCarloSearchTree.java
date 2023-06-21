package connectx.MCTS;

import connectx.MCTS.Connect4.Board;
import connectx.MCTS.MCTree.Node;
import connectx.MCTS.MCTree.Tree;

import java.util.List;

public class MonteCarloSearchTree {
    private static final int WIN_SCORE = 10;
    private int level;
    private int opponentID;

    public MonteCarloSearchTree() {
        level = 3;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int pLevel) {
        level = pLevel;
    }

    //получить миллисекунды с текущего уровня
    private int getMillisForCurrentLevel() {
        return 2 * (level - 1) + 1;
    }

    public Board findNextMove(Board pBoard, int pPlayerID) {
        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();

        opponentID = 3 - pPlayerID;
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(pBoard);
        rootNode.getState().setPlayerID(opponentID);

        while (System.currentTimeMillis() < end) {
            //Selection:
            Node promisingNode = selectPromisingNode(rootNode);

            //Expansion://Расширение:
            if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS) {
                expandNode(promisingNode);
            }

            //Simulation:
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);

            //Update:
            backPropagation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        //For debugging purposes:
        if (winnerNode.getState().getBoard().equals(pBoard)) {
            System.out.println("ERROR! MCTS DIDN'T WORK");
        } else {
            System.out.println("MCTS moved: ");
        }

        for (int i = 0; i < Board.DEFAULT_ROW_SIZE; i++) {
            for (int j = 0; j < Board.DEFAULT_COL_SIZE; j++) {
                if (pBoard.getBoardValues()[i][j] != winnerNode.getState().getBoard().getBoardValues()[i][j]) {
                    System.out.println("AAAAAAAAAAA " + j);
                }
            }
        }


        return winnerNode.getState().getBoard();
    }

    private Node selectPromisingNode(Node pRootNode) {
        Node node = pRootNode;
        while (node.getChildren().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    //развернуть узел
    private void expandNode(Node pNode) {
        List<State> possibleStates = pNode.getState().getAllPossibleStates();
        for (State s : possibleStates) {
            Node newNode = new Node(s);
            newNode.setParent(pNode);
            newNode.getState().setPlayerID(pNode.getState().getOpponent());
            pNode.getChildren().add(newNode);
        }
    }

    private int simulateRandomPlayout(Node pNode) {
        Node temp = new Node(pNode);
        State tempState = temp.getState();
        int boardStatus = tempState.getBoard().checkStatus();

        if (boardStatus == opponentID) {
            temp.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().checkStatus();
        }

        return boardStatus;
    }

    private void backPropagation(Node pNodeToExplore, int playerID) {
        Node temp = pNodeToExplore;
        while (temp != null) {
            temp.getState().incrementVisit();
            if (temp.getState().getPlayerID() == playerID) {
                temp.getState().addScore(WIN_SCORE);
            }
            temp = temp.getParent();
        }
    }
}
