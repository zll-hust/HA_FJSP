package com.zll.FJSP.NeighbourSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.zll.FJSP.Data.*;

/**
 * Description:禁忌搜索算法
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月15日 下午11:47:44
 */
public class TabuSearch2 {
    public int tabuIterations = 100; // 禁忌搜索迭代次数
    public int[][] TabuList; // 禁忌表
    public NeighbourGraph initialGraph; // 初始解
    private Problem p;

    public TabuSearch2(NeighbourGraph g) {
        this.initialGraph = g;
        this.p = g.problem;
        this.TabuList = new int[p.getTotalOperationCount()][p.getMachineCount()];
        for (int[] list : TabuList)
            Arrays.fill(list, 0);
    }

    public Solution tabuSearch() {
        NeighbourGraph currentGraph = new NeighbourGraph(initialGraph); // the solution in each iteration
        currentGraph.evaluateCost();
        NeighbourGraph bestGraph = null;
        int bestCost = currentGraph.cost;

        long startTime = System.currentTimeMillis();// 算法开始

        // start tabu search
        for (int i = 0; i < this.tabuIterations; i++) {

            NeighbourGraph currentBestGraph = null; // best in each iteration
            int currentBestCost = Integer.MAX_VALUE;
            int nodeId = -1;
            int machineId = -1;

            for(int j = 0; j < currentGraph.getNodeList().length; j++)
                Collections.sort(currentGraph.getNodeList()[j]);

            // find best move
            ArrayList<Node> criticalPath = currentGraph.getCriticalPath().getPath();
            for (int j = 1; j < criticalPath.size() - 1; j++) {// skip StartNode and EndNode
                Node deleteNode = criticalPath.get(j);

                // delete
                int deleteMachine = deleteNode.getMachine();
                DeleteNeighbourGraph deleteGraph = currentGraph.deleteMArcs(deleteNode);

                // get the best move of optimal k-insertions
                for (int k = 0; k < currentGraph.problem.getMachineCount(); k++) {

                    if(k == deleteMachine)
                        continue;

                    if (p.getProDesMatrix()[p.getOperationToIndex()[deleteNode.job][deleteNode.task]][k] == 0) // skip unuseful machine
                        continue;

                    // insert
                    NeighbourGraph bestKInsertGraph = deleteGraph.insertNodeBest(k, deleteMachine);
                    int cost = bestKInsertGraph.evaluateCost();

                    nodeId = p.getOperationToIndex()[deleteNode.job][deleteNode.task];
                    machineId = k;
                    if (bestKInsertGraph != null && (cost < bestCost || this.TabuList[nodeId][machineId] <= i) && cost <= currentBestCost) {
                        currentBestGraph = new NeighbourGraph(bestKInsertGraph);
                        currentBestCost = cost;
                    }
                }
            }

//            System.out.println("Tabu iteration " + i + " - New cost : " + currentBestCost);

            if (currentBestCost < bestCost) {
                bestGraph = new NeighbourGraph(currentBestGraph);
                bestCost = currentBestCost;
//                System.out.println("In " + i + " generation, find new best fitness is:" + bestCost);
            }

            if (currentBestGraph != null) {
                currentGraph = new NeighbourGraph(currentBestGraph);
//                currentGraph.evaluateCost();
                currentGraph.updateStartTime();
                TabuList[nodeId][machineId] = i + criticalPath.size() + p.getMachineCountArr()[nodeId];
            }
        }

        long endTime = System.currentTimeMillis();
//        System.out.println(" TS time cost is：" + (endTime - startTime) / 1000.0 + "s");

        Solution sol = null;

        if (bestGraph == null)
            sol = NeighbourAlgorithms.toSolution(initialGraph);
        else {
            bestGraph.evaluateCost();
            bestGraph.updateStartTime();
            sol = NeighbourAlgorithms.toSolution(bestGraph);
        }

        sol.algrithmTimeCost = (endTime - startTime) / 1000.0;

        return sol;
    }
}