package connectx.AlphabetCXPlayer;

import connectx.CXBoard;
import connectx.CXCellState;
import connectx.CXGameState;
import connectx.CXPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlphabetCXPlayer implements CXPlayer {
    private int M;
    private int N;
    private int X;
    private int timeoutInSeconds;

    private boolean first;

    private String playerName;

    private Map<String, Integer> tokensScoreCache = new HashMap<>();

    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.X = X;
        this.first = first;
        this.timeoutInSeconds = timeout_in_secs;
        this.playerName = "AlphabetCXPlayer";
    }

    public int selectColumn(CXBoard B) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (timeoutInSeconds * 1000L); // Tempo massimo in millisecondi

        List<Integer> availableColumns = getAvailableColumns(B);
        int maxScore = Integer.MIN_VALUE;
        int selectedColumn = availableColumns.get(0);

        for (int col : availableColumns) {
            CXBoard simulatedBoard = B.copy();
            simulatedBoard.markColumn(col);

            int score = alphabetSearch(simulatedBoard, X, Integer.MIN_VALUE, Integer.MAX_VALUE, false, 1, endTime);
            if (score > maxScore) {
                maxScore = score;
                selectedColumn = col;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break; // Interrompe il ciclo se il tempo limite è scaduto
            }
        }

        return selectedColumn;
    }

    private List<Integer> getAvailableColumns(CXBoard B) {
        List<Integer> availableColumns = new ArrayList<>();
        for (int col = 0; col < N; col++) {
            if (!B.fullColumn(col)) {
                availableColumns.add(col);
            }
        }
        return availableColumns;
    }

    private int alphabetSearch(CXBoard B, int depth, int alpha, int beta, boolean maximizingPlayer, int currentDepth, long endTime) {
        if (depth == 0 || B.gameState() != CXGameState.OPEN || System.currentTimeMillis() >= endTime) {
            return evaluateBoard(B);
        }

        List<Integer> availableColumns = getAvailableColumns(B);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : availableColumns) {
                CXBoard simulatedBoard = B.copy();
                simulatedBoard.markColumn(col);

                int eval = alphabetSearch(simulatedBoard, depth - 1, alpha, beta, false, currentDepth + 1, endTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : availableColumns) {
                CXBoard simulatedBoard = B.copy();
                simulatedBoard.markColumn(col);

                int eval = alphabetSearch(simulatedBoard, depth - 1, alpha, beta, true, currentDepth + 1, endTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }


    private int evaluateBoard(CXBoard B) {
        int player1Score = calculateScore(B, CXCellState.P1);
        int player2Score = calculateScore(B, CXCellState.P2);

        // Cache the score for the current board configuration
        String boardKey = B.toString();
        if (tokensScoreCache.containsKey(boardKey)) {
            return tokensScoreCache.get(boardKey);
        }

        // Calculate the score for the current player
        int playerScore;
        if (first) {
            playerScore = player1Score - player2Score;
        } else {
            playerScore = player2Score - player1Score;
        }

        // Store the score in the cache
        tokensScoreCache.put(boardKey, playerScore);

        return playerScore;
    }

    private int calculateDefenseScore(CXBoard B, CXCellState opponent) {
        int score = 0;

        // Calcola il punteggio per le righe, colonne e diagonali in cui l'avversario ha gettoni allineati
        for (int row = 0; row < M; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokensInRow = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row, col + k) == opponent) {
                        tokensInRow++;
                    }
                }
                score -= evaluateTokens(tokensInRow, row, col);
            }
        }

        for (int col = 0; col < N; col++) {
            for (int row = 0; row <= M - X; row++) {
                int tokensInCol = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col) == opponent) {
                        tokensInCol++;
                    }
                }
                score -= evaluateTokens(tokensInCol, row, col);
            }
        }

        for (int row = 0; row <= M - X; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokensInDiagonal = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col + k) == opponent) {
                        tokensInDiagonal++;
                    }
                }
                score -= evaluateTokens(tokensInDiagonal, row, col);
            }
        }

        for (int row = 0; row <= M - X; row++) {
            for (int col = X - 1; col < N; col++) {
                int tokensInDiagonal = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col - k) == opponent) {
                        tokensInDiagonal++;
                    }
                }
                score -= evaluateTokens(tokensInDiagonal, row, col);
            }
        }

        return score;
    }

    private int calculateScore(CXBoard B, CXCellState player) {
        int score = 0;
        int streak = 0;

        // Check horizontal streaks
        for (int row = 0; row < M; row++) {
            for (int col = 0; col <= N - X; col++) {
                for (int i = 0; i < X; i++) {
                    if (B.cellState(row, col + i) == player) {
                        streak++;
                    } else {
                        streak = 0;
                        break;
                    }
                }
                score += streak;
            }
        }

        // Check vertical streaks
        for (int col = 0; col < N; col++) {
            for (int row = 0; row <= M - X; row++) {
                for (int i = 0; i < X; i++) {
                    if (B.cellState(row + i, col) == player) {
                        streak++;
                    } else {
                        streak = 0;
                        break;
                    }
                }
                score += streak;
            }
        }

        // Check diagonal streaks (top-left to bottom-right)
        for (int row = 0; row <= M - X; row++) {
            for (int col = 0; col <= N - X; col++) {
                for (int i = 0; i < X; i++) {
                    if (B.cellState(row + i, col + i) == player) {
                        streak++;
                    } else {
                        streak = 0;
                        break;
                    }
                }
                score += streak;
            }
        }

        // Check diagonal streaks (top-right to bottom-left)
        for (int row = 0; row <= M - X; row++) {
            for (int col = X - 1; col < N; col++) {
                for (int i = 0; i < X; i++) {
                    if (B.cellState(row + i, col - i) == player) {
                        streak++;
                    } else {
                        streak = 0;
                        break;
                    }
                }
                score += streak;
            }
        }

        return score;
    }

    private int evaluateTokens(int tokens, int row, int col) {
        // Assegna un punteggio in base al numero di gettoni allineati e alla posizione delle caselle
        if (tokens == X) {
            return 100; // Se tutti i gettoni sono allineati, punteggio massimo
        } else if (tokens == X - 1) {
            return 10; // Se manca un solo gettone per allinearli, punteggio alto
        } else if (tokens >= 2) {
            // Assegna un punteggio basato sulla posizione delle caselle
            if (row == M / 2 && col == N / 2) {
                return 5; // Casella centrale, punteggio medio-alto
            } else if (row == 0 || row == M - 1 || col == 0 || col == N - 1) {
                return 3; // Caselle sui bordi, punteggio medio
            } else {
                return 1; // Caselle interne, punteggio base
            }
        }

        return 0; // Nessun gettone allineato, punteggio nullo
    }


    public String playerName() {
        return playerName;
    }
}