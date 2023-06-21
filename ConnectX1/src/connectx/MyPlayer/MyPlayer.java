package connectx.MyPlayer;

import connectx.CXBoard;
import connectx.CXCellState;
import connectx.CXGameState;
import connectx.CXPlayer;

public class MyPlayer implements CXPlayer {
    private boolean first;
    private Integer startingDepth = 10;
    private int TIMEOUT;
    private long START;
    private int M;
    private int N;
    private int K;

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.TIMEOUT = timeout_in_secs;
    }

    @Override
    public int selectColumn(CXBoard B) {
        // Get the available columns
        Integer[] availableColumns = B.getAvailableColumns();

        // Perform the Minimax algorithm to find the best move
        int bestMove = Integer.MIN_VALUE;
        int bestColumn = -1;

        for (int col : availableColumns) {
            // Make a copy of the board and simulate a move
            CXBoard copyBoard = B.copy();
            copyBoard.markColumn(col);

            // Evaluate the move using the Minimax algorithm
            int moveValue = minimax(copyBoard, K, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Update the best move if necessary
            if (moveValue > bestMove) {
                bestMove = moveValue;
                bestColumn = col;
            }
        }

        return bestColumn;
    }

    private int minimax(CXBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.gameState() != CXGameState.OPEN) {
            // Reached the maximum depth or the game has ended, evaluate the board state
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            Integer[] availableColumns = board.getAvailableColumns();

            for (int col : availableColumns) {
                // Make a copy of the board and simulate a move
                CXBoard copyBoard = board.copy();
                copyBoard.markColumn(col);

                // Recursive call to the minimax function
                int eval = minimax(copyBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    // Beta cutoff, prune the remaining branches
                    break;
                }
            }

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            Integer[] availableColumns = board.getAvailableColumns();

            for (int col : availableColumns) {
                // Make a copy of the board and simulate a move
                CXBoard copyBoard = board.copy();
                copyBoard.markColumn(col);

                // Recursive call to the minimax function
                int eval = minimax(copyBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    // Alpha cutoff, prune the remaining branches
                    break;
                }
            }

            return minEval;
        }
    }

    private int evaluateBoard(CXBoard board) {
        // Evaluate the board state based on the number of marked cells in a winning configuration
        int score = 0;

        // Evaluate rows
        for (int i = 0; i < M; i++) {
            for (int j = 0; j <= N - K; j++) {
                int countPlayer1 = 0;
                int countPlayer2 = 0;

                for (int k = 0; k < K; k++) {
                    CXCellState cellState = board.cellState(i, j + k);
                    if (cellState == CXCellState.P1) {
                        countPlayer1++;
                    } else if (cellState == CXCellState.P2) {
                        countPlayer2++;
                    }
                }

                if (countPlayer1 > 0 && countPlayer2 == 0) {
                    score += Math.pow(10, countPlayer1 - 1);
                } else if (countPlayer2 > 0 && countPlayer1 == 0) {
                    score -= Math.pow(10, countPlayer2 - 1);
                }
            }
        }

        // Evaluate columns
        for (int j = 0; j < N; j++) {
            for (int i = 0; i <= M - K; i++) {
                int countPlayer1 = 0;
                int countPlayer2 = 0;

                for (int k = 0; k < K; k++) {
                    CXCellState cellState = board.cellState(i + k, j);
                    if (cellState == CXCellState.P1) {
                        countPlayer1++;
                    } else if (cellState == CXCellState.P2) {
                        countPlayer2++;
                    }
                }

                if (countPlayer1 > 0 && countPlayer2 == 0) {
                    score += Math.pow(10, countPlayer1 - 1);
                } else if (countPlayer2 > 0 && countPlayer1 == 0) {
                    score -= Math.pow(10, countPlayer2 - 1);
                }
            }
        }

        // Evaluate diagonals (top-left to bottom-right)
        for (int i = 0; i <= M - K; i++) {
            for (int j = 0; j <= N - K; j++) {
                int countPlayer1 = 0;
                int countPlayer2 = 0;

                for (int k = 0; k < K; k++) {
                    CXCellState cellState = board.cellState(i + k, j + k);
                    if (cellState == CXCellState.P1) {
                        countPlayer1++;
                    } else if (cellState == CXCellState.P2) {
                        countPlayer2++;
                    }
                }

                if (countPlayer1 > 0 && countPlayer2 == 0) {
                    score += Math.pow(10, countPlayer1 - 1);
                } else if (countPlayer2 > 0 && countPlayer1 == 0) {
                    score -= Math.pow(10, countPlayer2 - 1);
                }
            }
        }

        // Evaluate diagonals (top-right to bottom-left)
        for (int i = 0; i <= M - K; i++) {
            for (int j = K - 1; j < N; j++) {
                int countPlayer1 = 0;
                int countPlayer2 = 0;

                for (int k = 0; k < K; k++) {
                    CXCellState cellState = board.cellState(i + k, j - k);
                    if (cellState == CXCellState.P1) {
                        countPlayer1++;
                    } else if (cellState == CXCellState.P2) {
                        countPlayer2++;
                    }
                }

                if (countPlayer1 > 0 && countPlayer2 == 0) {
                    score += Math.pow(10, countPlayer1 - 1);
                } else if (countPlayer2 > 0 && countPlayer1 == 0) {
                    score -= Math.pow(10, countPlayer2 - 1);
                }
            }
        }

        return score;
    }

    @Override
    public String playerName() {
        return "MyPlayer";
    }
}
