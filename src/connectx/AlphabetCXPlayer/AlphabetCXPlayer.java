package connectx.AlphabetCXPlayer;

import connectx.CXBoard;
import connectx.CXCellState;
import connectx.CXGameState;
import connectx.CXPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AlphabetCXPlayer implements CXPlayer {
    public static final double MILLISECOND_IN_SECOND = 1000.0;
    private int M;
    private int N;
    private int X;
    private double timeoutInSeconds;
    private int maxDepth;
    private boolean isFirst;
    private String playerName;
    private long start;
    private final Map<String, Integer> tokensScoreCache = new LinkedHashMap<>(16,
            0.75f,
            true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return size() > 10000; // Limita la dimensione della cache a 10000 voci
        }
    };

    public void initPlayer(int M, int N, int X, boolean first, int timeoutInSecs) {
        this.M = M;
        this.N = N;
        this.X = X;
        this.isFirst = first;
        this.timeoutInSeconds = timeoutInSecs;
        this.playerName = "AlphabetCXPlayer";
        this.maxDepth = 7; // Imposta il limite di profondità a 7
    }

    public int selectColumn(CXBoard B) {
        long startTime = System.currentTimeMillis();
        long endTime = (long) (startTime + (timeoutInSeconds * MILLISECOND_IN_SECOND)); // Tempo massimo in millisecondi

        start = startTime;

        List<Integer> availableColumns = getAvailableColumns(B);
        int selectedColumn = availableColumns.get(0);

        int maxDepthReached = 0; // Profondità massima raggiunta finora

        // Itera attraverso i livelli di profondità fino a raggiungere maxDepth
        for (int depth = 1; depth <= maxDepth; depth++) {
            int maxScore = Integer.MIN_VALUE;

            // Itera attraverso le colonne disponibili
            for (int col : availableColumns) {
                CXBoard simulatedBoard = B.copy();
                simulatedBoard.markColumn(col);

                try {
                    checkTime();
                    int score = alphabetSearch(simulatedBoard, depth,
                            Integer.MIN_VALUE, Integer.MAX_VALUE,
                            false, 1, endTime);
                    if (score > maxScore) {
                        maxScore = score;
                        selectedColumn = col;
                    }
                } catch (TimeoutException e) {
                    // Il tempo è scaduto, usa la miglior mossa trovata finora
                    return selectedColumn;
                }
            }

            // Se il tempo è scaduto, esci dal ciclo e considera il livello di profondità massimo raggiunto finora
            if (System.currentTimeMillis() >= endTime) {
                maxDepthReached = depth - 1;
                break;
            } else {
                maxDepthReached = depth; // Aggiorna la profondità massima raggiunta
            }
        }

        // Se la prima profondità non è stata esplorata completamente, usa l'approfondimento iterativo
        if (maxDepthReached < maxDepth) {
            // Continua l'approfondimento iterativo finché c'è tempo disponibile
            for (int depth = maxDepthReached + 1; depth <= maxDepth; depth++) {
                int maxScore = Integer.MIN_VALUE;

                for (int col : availableColumns) {
                    CXBoard simulatedBoard = B.copy();
                    simulatedBoard.markColumn(col);

                    try {
                        checkTime();
                        int score = alphabetSearch(simulatedBoard, depth,
                                Integer.MIN_VALUE, Integer.MAX_VALUE,
                                false, 1, endTime);
                        if (score > maxScore) {
                            maxScore = score;
                            selectedColumn = col;
                        }
                    } catch (TimeoutException e) {
                        return selectedColumn;
                    }
                }
                // Se il tempo è scaduto, esci dal ciclo e considera la profondità massima raggiunta finora
                if (System.currentTimeMillis() >= endTime) {
                    break;
                }
            }
        }
        return selectedColumn;
    }

    public String playerName() {
        return playerName;
    }

    private void checkTime() throws TimeoutException {
        if ((System.currentTimeMillis() - start) / MILLISECOND_IN_SECOND >= timeoutInSeconds * (99.0 / 100.0))
            throw new TimeoutException();
    }

    // Restituisce le colonne disponibili in un dato stato del campo di gioco
    private List<Integer> getAvailableColumns(CXBoard B) {
        List<Integer> availableColumns = new ArrayList<>();
        for (int col = 0; col < N; col++) {
            if (!B.fullColumn(col)) {
                availableColumns.add(col);
            }
        }
        return availableColumns;
    }

    // Implementa l'algoritmo di ricerca con potatura alpha-beta
    private int alphabetSearch(CXBoard B, int depth, int alpha, int beta,
                               boolean maximizingPlayer, int currentDepth,
                               long endTime) throws TimeoutException {
        checkTime();
        if (depth == 0 || B.gameState() != CXGameState.OPEN || System.currentTimeMillis() >= endTime) {
            return evaluateBoard(B);
        }

        List<Integer> availableColumns = getAvailableColumns(B);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : availableColumns) {
                CXBoard simulatedBoard = B.copy();
                simulatedBoard.markColumn(col);

                int eval = alphabetSearch(simulatedBoard, depth - 1, alpha, beta,
                        false, currentDepth + 1, endTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }

                if (System.currentTimeMillis() >= endTime) {
                    throw new TimeoutException();
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : availableColumns) {
                CXBoard simulatedBoard = B.copy();
                simulatedBoard.markColumn(col);

                int eval = alphabetSearch(simulatedBoard, depth - 1, alpha, beta,
                        true, currentDepth + 1, endTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }

                if (System.currentTimeMillis() >= endTime) {
                    throw new TimeoutException();
                }
            }
            return minEval;
        }
    }

    // Responsabile del punteggio per un dato stato del campo di gioco
    private int evaluateBoard(CXBoard B) {
        int player1Score = calculateScore(B, CXCellState.P1);
        int player2Score = calculateScore(B, CXCellState.P2);
        int player1DefenseScore = calculateDefenseScore(B, CXCellState.P1);
        int player2DefenseScore = calculateDefenseScore(B, CXCellState.P2);
        int player1PositionScore = calculatePositionScore(B, CXCellState.P1);
        int player2PositionScore = calculatePositionScore(B, CXCellState.P2);

        // Memorizza nella cache il punteggio per l'attuale configurazione della scheda
        String boardKey = B.toString();
        if (tokensScoreCache.containsKey(boardKey)) {
            return tokensScoreCache.get(boardKey);
        }

        int playerScore;
        if (isFirst) {
            // Assegna pesi a ciascun fattore in base alla strategia di gioco
            int scoreWeight = 1;
            int defenseWeight = 2;
            int positionWeight = 3;

            playerScore = (scoreWeight * (player1Score - player2Score)) +
                    (defenseWeight * (player2DefenseScore - player1DefenseScore)) +
                    (positionWeight * (player1PositionScore - player2PositionScore));
        } else {
            // Assegna pesi a ciascun fattore in base alla strategia di gioco
            int scoreWeight = 1;
            int defenseWeight = 3;
            int positionWeight = 2;

            playerScore = (scoreWeight * (player2Score - player1Score)) +
                    (defenseWeight * (player1DefenseScore - player2DefenseScore)) +
                    (positionWeight * (player2PositionScore - player1PositionScore));
        }
        tokensScoreCache.put(boardKey, playerScore);

        return playerScore;
    }

    private int calculatePositionScore(CXBoard B, CXCellState player) {
        int score = 0;

        // Assegna punteggi più alti alle posizioni chiave sul tabellone
        if (B.cellState(0, N / 2) == player) {
            score += 10; // Top center position
        }
        if (B.cellState(M - 1, N / 2) == player) {
            score += 10; // Bottom center position
        }
        if (B.cellState(M / 2, 0) == player) {
            score += 5; // Left center position
        }
        if (B.cellState(M / 2, N - 1) == player) {
            score += 5; // Right center position
        }
        return score;
    }

    // Calcola il punteggio per righe, colonne e diagonali in cui l'avversario ha allineato i gettoni
    private int calculateDefenseScore(CXBoard B, CXCellState opponent) {
        int score = 0;
        for (int row = 0; row < M; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokens = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row, col + k) == opponent) {
                        tokens++;
                    }
                }
                if (tokens == X - 1) {
                    score += 3; // Incrementa il punteggio per una potenziale mossa difensiva
                }
            }
        }

        for (int col = 0; col < N; col++) {
            for (int row = 0; row <= M - X; row++) {
                int tokens = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col) == opponent) {
                        tokens++;
                    }
                }
                if (tokens == X - 1) {
                    score += 3; // Incrementa il punteggio per una potenziale mossa difensiva
                }
            }
        }

        for (int row = 0; row <= M - X; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokens = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col + k) == opponent) {
                        tokens++;
                    }
                }
                if (tokens == X - 1) {
                    score += 3; // Incrementa il punteggio per una potenziale mossa difensiva
                }
            }
        }

        for (int row = 0; row <= M - X; row++) {
            for (int col = N - 1; col >= X - 1; col--) {
                int tokens = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col - k) == opponent) {
                        tokens++;
                    }
                }
                if (tokens == X - 1) {
                    score += 3; // Incrementa il punteggio per una potenziale mossa difensiva
                }
            }
        }
        return score;
    }

    // Calcola il punteggio per righe, colonne e diagonali in cui il giocatore ha allineato i gettoni
    private int calculateScore(CXBoard B, CXCellState player) {
        int score = 0;
        for (int row = 0; row < M; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokensInRow = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row, col + k) == player) {
                        tokensInRow++;
                    }
                }
                score += evaluateTokens(tokensInRow, row, col);
            }
        }

        for (int col = 0; col < N; col++) {
            for (int row = 0; row <= M - X; row++) {
                int tokensInCol = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col) == player) {
                        tokensInCol++;
                    }
                }
                score += evaluateTokens(tokensInCol, row, col);
            }
        }

        for (int row = 0; row <= M - X; row++) {
            for (int col = 0; col <= N - X; col++) {
                int tokensInDiagonal1 = 0;
                int tokensInDiagonal2 = 0;
                for (int k = 0; k < X; k++) {
                    if (B.cellState(row + k, col + k) == player) {
                        tokensInDiagonal1++;
                    }
                    if (B.cellState(row + k, col + X - 1 - k) == player) {
                        tokensInDiagonal2++;
                    }
                }
                score += evaluateTokens(tokensInDiagonal1, row, col);
                score += evaluateTokens(tokensInDiagonal2, row, col);
            }
        }

        return score;
    }

    // Responsabile di valutare lo score di uno stato del campo di gioco
    private int evaluateTokens(int tokens, int row, int col) {
        int score = 0;
        if (tokens == X) {
            // Condizione di vittoria
            score = 1000;
        } else if (tokens == X - 1) {
            // Potenziale condizione di vittoria
            score = 100;
        } else if (tokens > 0) {
            // Allineamento parziale
            score = 10;
        }

        // Punteggio bonus per gettoni più vicini al centro del tabellone
        int centerRow = M / 2;
        int centerCol = N / 2;
        int distance = Math.abs(row - centerRow) + Math.abs(col - centerCol);
        score -= distance;

        return score;
    }
}