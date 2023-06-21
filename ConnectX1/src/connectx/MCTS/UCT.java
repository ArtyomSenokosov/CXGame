package connectx.MCTS;

import connectx.MCTS.MCTree.Node;

import java.util.Collections;
import java.util.Comparator;

public class UCT {
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisitCount){
        if(nodeVisitCount==0){
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore/ (double) nodeVisitCount) + 1.41*Math.sqrt(Math.log(totalVisit)/(double) nodeVisitCount);
    }

    static Node findBestNodeWithUCT(Node pNode){
        int parentVisitCount = pNode.getState().getVisitCount();
        return Collections.max(
                pNode.getChildren(),
                Comparator.comparing(c-> uctValue(parentVisitCount, c.getState().getWinScore(), c.getState().getVisitCount()))
        );
    }

}
