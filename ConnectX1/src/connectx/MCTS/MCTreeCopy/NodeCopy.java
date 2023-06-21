package connectx.MCTS.MCTreeCopy;

import connectx.CXBoard;
import connectx.MCTS.StateCopy;

import java.util.ArrayList;
import java.util.List;

//Node in a MCTS tree.
public class NodeCopy {
    StateCopy state;
    NodeCopy parent;
    List<NodeCopy> children;

    public NodeCopy() {
    }

    public NodeCopy(CXBoard board) {
        state = new StateCopy(board);
        children = new ArrayList<>();
    }


    public NodeCopy(int M, int N, int X) {
        state = new StateCopy(M, N, X);
        // children = new ArrayList<>();
    }

    public NodeCopy(StateCopy pState) {
        state = pState;
        children = new ArrayList<>();
    }

    public NodeCopy(StateCopy pState, NodeCopy pParent, List<NodeCopy> pChildren) {
        state = pState;
        parent = pParent;
        children = pChildren;
    }

    public NodeCopy(NodeCopy pNode) {
        children = new ArrayList<>();
        state = new StateCopy(pNode.getState());
        if (pNode.getParent() != null) {
            parent = pNode.getParent();
        }
        List<NodeCopy> children = pNode.getChildren();
        for (NodeCopy child : children) {
            children.add(new NodeCopy(child));
        }
    }

    public StateCopy getState() {
        return state;
    }

    public void setState(StateCopy pState) {
        state = pState;
    }

    public NodeCopy getParent() {
        return parent;
    }

    public void setParent(NodeCopy pParent) {
        parent = pParent;
    }

    public List<NodeCopy> getChildren() {
        return children;
    }

    public void setChildren(List<NodeCopy> pChildren) {
        children = pChildren;
    }

    public NodeCopy getRandomChild() {
        int nbOfPossibleMoves = children.size();
        int selectRandom = (int) (Math.random() * nbOfPossibleMoves);
        return children.get(selectRandom);
    }

    //TODO: don't really understand why highest visit count --> max score.
    /* public Node getChildWithMaxScore() {
        return Collections.max(children, Comparator.comparing(c -> {
            return c.getState().getVisitCount();
        }));
    }*/

    public NodeCopy getChildWithMaxScore() {
        NodeCopy maxChild = null;
        int maxScore = Integer.MIN_VALUE;

        for (NodeCopy child : children) {
            int score = child.getState().getVisitCount();
            if (score > maxScore) {
                maxScore = score;
                maxChild = child;
            }
        }

        return maxChild;
    }
}