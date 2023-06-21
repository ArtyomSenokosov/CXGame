package connectx.MCTS.MCTreeCopy;

import connectx.CXBoard;

public class TreeCopy {
    NodeCopy root;

    public TreeCopy(CXBoard board) {
        root = new NodeCopy(board);
    }

    public TreeCopy(int M, int N, int X) {
        root = new NodeCopy(M,N,X);
    }

    public TreeCopy(NodeCopy pRoot) {
        root = pRoot;
    }

    public NodeCopy getRoot() {
        return root;
    }

    public void setRoot(NodeCopy pRoot) {
        root = pRoot;
    }

    public void addChild(NodeCopy parent, NodeCopy child) {
        parent.getChildren().add(child);
    }
}
