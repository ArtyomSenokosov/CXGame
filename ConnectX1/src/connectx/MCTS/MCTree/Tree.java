package connectx.MCTS.MCTree;

public class Tree {
    Node root;

    public Tree(){
        root = new Node();
    }

    public Tree(Node pRoot){
        root = pRoot;
    }

    public Node getRoot(){
        return root;
    }

    public void setRoot(Node pRoot){
        root = pRoot;
    }

    public void addChild(Node parent, Node child){
        parent.getChildren().add(child);
    }
}