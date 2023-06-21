package connectx.MCTS.Connect4;

//Position on the board:
public class Position {
    int x;
    int y;

    //Constructor
    public Position(int pX, int pY){
        this.x = pX;
        this.y = pY;
    }

    //Getters and Setters
    public int getX(){
        return this.x;
    }
    public void setX(int pX){
        this.x = pX;
    }
    public int getY(){
        return this.y;
    }
    public void setY(int pY){
        this.y = pY;
    }
}
