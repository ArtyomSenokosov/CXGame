package connectx.MCTS.Connect4;

import connectx.MCTS.MonteCarloSearchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Board {
    //CXCellState[][] B
    int[][] boardValues;
     //кол-во ходов совершонных
     int totalMoves;

     public static final int DEFAULT_ROW_SIZE = 6;
     public static final int DEFAULT_COL_SIZE = 7;







    //CXGameState
     public static final int PLAYER1 = 1;
     public static final int PLAYER2 = 2;

     public static final int IN_PROGRESS = -1;
     public static final int DRAW = 0;











     //Constructors.
     //из-за того что дефолтные значения массива есть, он может создать поле без входных данных
     public Board() {
         boardValues = new int[DEFAULT_ROW_SIZE][DEFAULT_COL_SIZE];
     }

     public Board(Board pBoard) {
         int boardRowSize = pBoard.getBoardValues().length;
         int boardColSize = pBoard.getBoardValues()[0].length;
         boardValues = new int[boardRowSize][boardColSize];
         int[][] boardToCopy = pBoard.getBoardValues();
         for (int i = 0; i < boardRowSize; i++) {
             for (int j = 0; j < boardColSize; j++) {
                 this.boardValues[i][j] = boardToCopy[i][j];
             }
         }
     }














     //Checks if we can place a disk in the column provided.
     //Проверяем, можем ли мы поместить диск в указанную колонку.
     //fullColumn
     public boolean isValidMove(int col) {
         return (col >= 0 && col < boardValues[0].length && boardValues[0][col] == 0);
     }

     //Drop the disk into one of the columns.
     // Перетащите диск в один из столбцов.
     //markColumn
     public void performMove(int playerID, int col) {
         if (isValidMove(col)) {
             int diskHeight = boardValues.length - 1;
             while (boardValues[diskHeight][col] != 0) diskHeight--;
             boardValues[diskHeight][col] = playerID;
             totalMoves++;
         }
     }

     //Get&Set board values.
     public int[][] getBoardValues() {
         return this.boardValues;
     }

     //Check the current status of the game: In progress, a draw or a  win for either player 1 or 2?
     //Проверить текущий статус игры: Идет, ничья или выигрыш для игрока 1 или 2?
     //isWinningMove
     //gameState
     public int checkStatus() {
         int height = boardValues.length;
         int width = boardValues[0].length;

         //Check horizontal:
         for (int row = 0; row < height; row++) {
             for (int col = 0; col < width - 3; col++) {
                 int element = boardValues[row][col];
                 if (element != 0) {
                     if (element == boardValues[row][col + 1] && element == boardValues[row][col + 2] && element == boardValues[row][col + 3]) {
                         return element;
                     }
                 }
             }
         }

         //Check vertical:
         for (int row = 0; row < height - 3; row++) {
             for (int col = 0; col < width; col++) {
                 int element = boardValues[row][col];
                 if (element != 0) {
                     if (element == boardValues[row + 1][col] && element == boardValues[row + 2][col] && element == boardValues[row + 3][col]) {
                         return element;
                     }
                 }
             }
         }

         //Check \ diagonal:
         for (int row = 0; row < height - 3; row++) {
             for (int col = 0; col < width - 3; col++) {
                 int element = boardValues[row][col];
                 if (element != 0) {
                     if (element == boardValues[row + 1][col + 1] && element == boardValues[row + 2][col + 2] && element == boardValues[row + 3][col + 3]) {
                         return element;
                     }
                 }
             }
         }

         //Check / diagonal:
         for (int row = 0; row < height - 3; row++) {
             for (int col = 3; col < width; col++) {
                 int element = boardValues[row][col];
                 if (element != 0) {
                     if (element == boardValues[row + 1][col - 1] && element == boardValues[row + 2][col - 2] && element == boardValues[row + 3][col - 3]) {
                         return element;
                     }
                 }
             }
         }

         if (getEmptyPositions().size() > 0) return IN_PROGRESS;
         else return DRAW;
     }


     //Retrieve all empty positions on the board. TODO: return all available ones, or the empty ones?
     //Получить все пустые позиции на доске. TODO: вернуть все доступные или пустые?
     //getAvailableColumns
     public List<Position> getEmptyPositions() {
         List<Position> emptyPositions = new ArrayList<>();
         //iterate through columns. There's only one move per column available at most
         for (int i = 0; i < boardValues[0].length; i++) {
             int diskHeight = boardValues.length - 1;
             while (boardValues[diskHeight][i] != 0) {
                 diskHeight--;
                 if (diskHeight < 0) break;
             }
             if (diskHeight < 0) continue;
             emptyPositions.add(new Position(i, diskHeight));
         }
         return emptyPositions;
     }

     //Print method:
     public void printBoard() {
         int row = this.boardValues.length;
         int col = this.boardValues[0].length;
         for (int i = 0; i < row; i++) {
             for (int j = 0; j < col; j++) {
                 System.out.print(boardValues[i][j] + " ");
             }
             System.out.println();

         }
     }

     public boolean equals(Board b) {
         int[][] pBoardValues = b.getBoardValues();
         if (boardValues.length != pBoardValues.length || boardValues[0].length != pBoardValues[0].length) return false;
         for (int i = 0; i < boardValues.length; i++) {
             for (int j = 0; j < boardValues[0].length; j++) {
                 if (boardValues[i][j] != pBoardValues[i][j]) return false;
             }
         }
         return true;
     }

public static void main(String[]args){
        Board b1 = new Board();
        int totalMoves = DEFAULT_COL_SIZE * DEFAULT_ROW_SIZE;
        MonteCarloSearchTree mcts = new MonteCarloSearchTree();

        /*
        b1.performMove(PLAYER2,1);

        b1.performMove(PLAYER1,2);
        b1.performMove(PLAYER1,2);
        b1.performMove(PLAYER2,2);
        b1.performMove(PLAYER1,2);
        b1.performMove(PLAYER1,2);

        b1.performMove(PLAYER1,3);
        b1.performMove(PLAYER1,3);
        b1.performMove(PLAYER2,3);
        b1.performMove(PLAYER2,3);

        b1.performMove(PLAYER2,4);
        b1.performMove(PLAYER2,4);
        b1.performMove(PLAYER2,4);
        b1.performMove(PLAYER1,4);

        b1.performMove(PLAYER1,5);
        b1.performMove(PLAYER2,5);
        b1.performMove(PLAYER1,5);

        b1.printBoard();
        b1 = mcts.findNextMove(b1,PLAYER2);
        b1.printBoard();
        System.out.print("\n"+b1.checkStatus());
        */

       for(int i=1;i<totalMoves +1;i++){
        if(b1.checkStatus()!=-1)break;

        //Player 2: AI
        if(i%2==0){
        b1=mcts.findNextMove(b1,PLAYER2);
        }
        //Player 1: For now, the user.
        else{
        Scanner sc=new Scanner(System.in);    //System.in is a standard input stream
        boolean validMove=false;
        int column;
        do{
        System.out.print("Enter your move: ");
        column=sc.nextInt();
        validMove=b1.isValidMove(column);
        if(!validMove){
        System.out.println("\nNot a valid move! Please try again: ");
        }
        }while(!validMove);
        b1.performMove(PLAYER1,column);
        }

        b1.printBoard();
        System.out.println();
        }
        System.out.println("Winner is: "+b1.checkStatus());
        }

        }
