package connectx.MCTSPlayer;

import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MonteCarloCXPlayer implements CXPlayer {
    private boolean isFirstPlayer;
    private int M;
    private int N;
    private int K;

    public void initPlayer(int M, int N, int K, boolean isFirstPlayer, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.isFirstPlayer = isFirstPlayer;
    }

    public int selectColumn(CXBoard board) {
        TreeNode rootNode = new TreeNode(null, -1, board.copy(), isFirstPlayer);
        long endTime = System.currentTimeMillis() + 10000;  // Set the end time for MCTS to 1 second from now

        while (System.currentTimeMillis() < endTime) {
            TreeNode node = selectNode(rootNode);
            if (node.board.gameState() == CXGameState.OPEN) {
                expandNode(node);
            }
            TreeNode randomNode = getRandomChild(node);
            SimulationResult result = simulateRandomPlayout(randomNode);
            backpropagate(randomNode, result);
        }

        TreeNode bestChild = rootNode.getBestChild();
        return bestChild.move;
    }

    private TreeNode selectNode(TreeNode rootNode) {
        TreeNode node = rootNode;
        while (!node.untriedMoves.isEmpty() && node.childNodes.size() > 0) {
            if (node.untriedMoves.size() > 0) {
                return expandNode(node);
            } else {
                node = node.selectBestChild();
            }
        }
        return node;
    }

    private TreeNode expandNode(TreeNode node) {
        if (node.untriedMoves.isEmpty()) {
            return new TreeNode(node, -1, node.board.copy(), !node.isFirstPlayer);
        }

        int randomMove = node.untriedMoves.remove(new Random().nextInt(node.untriedMoves.size()));
        CXBoard newBoard = node.board.copy();
        newBoard.markColumn(randomMove);
        TreeNode newNode = new TreeNode(node, randomMove, newBoard, !node.isFirstPlayer);
        node.childNodes.add(newNode);
        return newNode;
    }

    private TreeNode getRandomChild(TreeNode node) {
        Random random = new Random();
        int randomIndex = random.nextInt(node.childNodes.size());
        return node.childNodes.get(randomIndex);
    }

    private SimulationResult simulateRandomPlayout(TreeNode node) {
        CXBoard board = node.board.copy();
        boolean isPlayerA = node.isFirstPlayer;
        Integer[] moves = board.getAvailableColumns();

        while (board.gameState() == CXGameState.OPEN) {
            Random random = new Random();
            int randomMove = moves[random.nextInt(moves.length)];
            board.markColumn(randomMove);
            isPlayerA = !isPlayerA;
            moves = board.getAvailableColumns();
        }

        if (board.gameState() == CXGameState.WINP1) {
            return new SimulationResult(isPlayerA ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        } else if (board.gameState() == CXGameState.WINP2) {
            return new SimulationResult(isPlayerA ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        } else {
            return new SimulationResult(0);
        }
    }

    private void backpropagate(TreeNode node, SimulationResult result) {
        while (node != null) {
            node.visits++;
            node.score += result.score;
            node = node.parent;
        }
    }

    private static class TreeNode {
        private final TreeNode parent;
        private final int move;
        private final CXBoard board;
        private final boolean isFirstPlayer;
        private int visits;
        private double score;
        private final List<Integer> untriedMoves;
        private final List<TreeNode> childNodes;

        private TreeNode(TreeNode parent, int move, CXBoard board, boolean isFirstPlayer) {
            this.parent = parent;
            this.move = move;
            this.board = board;
            this.isFirstPlayer = isFirstPlayer;
            this.visits = 0;
            this.score = 0;
            this.untriedMoves = new ArrayList<>();
            this.untriedMoves.addAll(Arrays.asList(board.getAvailableColumns()));
            this.childNodes = new ArrayList<>();
        }

        private TreeNode selectBestChild() {
            TreeNode bestChild = null;
            double bestValue = Double.MIN_VALUE;

            for (TreeNode child : childNodes) {
                double uctValue = child.score / child.visits + Math.sqrt(2 * Math.log(visits) / child.visits);
                if (uctValue > bestValue) {
                    bestChild = child;
                    bestValue = uctValue;
                }
            }

            return bestChild;
        }

        private TreeNode getBestChild() {
            TreeNode bestChild = null;
            double bestValue = Double.MIN_VALUE;

            for (TreeNode child : childNodes) {
                if (child.visits > bestValue) {
                    bestChild = child;
                    bestValue = child.visits;
                }
            }

            return bestChild;
        }
    }

    private static class SimulationResult {
        private final double score;

        private SimulationResult(double score) {
            this.score = score;
        }
    }

    @Override
    public String playerName() {
        return "MonteCarloCXPlayer";
    }
}

