package connectx.MCTS;

import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXPlayer;
import connectx.MCTS.MCTree.Node;
import connectx.MCTS.MCTreeCopy.NodeCopy;
import connectx.MCTS.MCTreeCopy.TreeCopy;

import java.util.List;

public class MCTS implements CXPlayer {
    private boolean first;
    private int timeout;
    private int M;
    private int N;
    private int K;

    private long START;

    private CXGameState cxGameState;
    private static final int WIN_SCORE = 10;

    public MCTS() {
    }

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        this.first = first;
        this.timeout = timeout_in_secs;
        this.M = M;
        this.N = N;
        this.K = K;
    }

    @Override
    public int selectColumn(CXBoard B) {
        long start = System.currentTimeMillis();
        long end = start + 1000000000000000000L * timeout + 1000000000000000000L * timeout;

        TreeCopy tree = new TreeCopy(B.copy());
        NodeCopy rootNode = tree.getRoot();
        rootNode.getState().setCXBoard(B);
        rootNode.getState().setFirst(!first);//opponentID

        while (System.currentTimeMillis() < end) {
            //Selection:
            NodeCopy promisingNode = selectPromisingNode(rootNode);

            //Expansion://Расширение:
            if (promisingNode.getState().getCXBoard().gameState() == CXGameState.OPEN) {//B.IN_PROGRESS //проверка статуса на полу
                expandNode(promisingNode);
            }

            //Simulation:
            NodeCopy nodeToExplore = promisingNode;
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            CXGameState playoutResult = simulateRandomPlayout(nodeToExplore);

            //Update:
            //    backPropagation(nodeToExplore, playoutResult);
        }

        NodeCopy winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        //For debugging purposes:
      /*  if (winnerNode.getState().getBoard().equals(pBoard)) {
            System.out.println("ERROR! MCTS DIDN'T WORK");
        } else {
            System.out.println("MCTS moved: ");
        }*/


        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (B.cellState(i, j) != winnerNode.getState().getCXBoard().cellState(i, j)) {
                    System.out.println("AAAAAAAAAAA " + j);
                    return j;
                }
            }
        }

        return -1;
        // return winnerNode.getState().getCXBoard();
    }

    @Override
    public String playerName() {
        return "MCTS";
    }

    private NodeCopy selectPromisingNode(NodeCopy pRootNode) {
        NodeCopy node = pRootNode;
        while (node.getChildren().size() != 0) {
            node = UCTCopy.findBestNodeWithUCT(node);
        }
        return node;
    }

    //развернуть узел
    private void expandNode(NodeCopy pNode) {
        List<StateCopy> possibleStates = pNode.getState().getAllPossibleStates();
        for (StateCopy s : possibleStates) {
            NodeCopy newNode = new NodeCopy(s);
            newNode.setParent(pNode);
            newNode.getState().setFirst(pNode.getState().getOpponent());
            pNode.getChildren().add(newNode);
        }
    }

    private CXGameState simulateRandomPlayout(NodeCopy pNode) {
        NodeCopy temp = new NodeCopy(pNode);
        StateCopy tempState = temp.getState();
        CXGameState boardStatus = tempState.getCXBoard().gameState();

     /*   if (boardStatus == opponentID) {
            temp.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }*/
        while (boardStatus == CXGameState.OPEN) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getCXBoard().gameState();
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
