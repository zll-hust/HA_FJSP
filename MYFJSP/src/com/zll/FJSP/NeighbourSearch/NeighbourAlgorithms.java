package com.zll.FJSP.NeighbourSearch;

import com.zll.FJSP.Data.*;
import com.zll.FJSP.GA.Chromosome;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Description:邻域搜索算法
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月10日 下午10:04:42
 */
public abstract class NeighbourAlgorithms {

    public static Solution neighbourSearch(Solution iniSolution) {
        Graph g = toGraph(iniSolution);
        NeighbourGraph ng = new NeighbourGraph(g);
        TabuSearch2 tabu = new TabuSearch2(ng);
        Solution sol = tabu.tabuSearch();
        return iniSolution;//sol
    }

    // k-insertions
    public static NeighbourGraph neighbourf1(NeighbourGraph g) {
        NeighbourGraph newGraph;
        DeleteNeighbourGraph deleteGraph;
        int bestCost = Integer.MAX_VALUE;
        NeighbourGraph bestGraph = null;

        for (ArrayList<Node> Qk : g.getNodeList())
            Collections.sort(Qk);

        ArrayList<Node> criticalPath = g.getCriticalPath().getPath();
        for (int j = 2; j < criticalPath.size() - 2; j++) {// skip first one and last one
            Node n = criticalPath.get(j);

            int deleteMachine = n.getMachine();
            deleteGraph = g.deleteMArcs(n);

            for (int m = 0; m < g.problem.getMachineCount(); m++) {
                newGraph = deleteGraph.insertNodeBest(m, deleteMachine);
                int cost = newGraph.evaluateCost();
//                    System.out.println("cost:" + cost);
                if (newGraph != null && cost < bestCost) {
                    bestGraph = g;
                    bestCost = cost;
                }
            }
        }

        return bestGraph;
    }

    public static Graph toGraph(Solution sol) {
        Problem pro = sol.problem;
        Graph g = new Graph(pro.getMachineCount(), sol.problem);
        Node[][] nodes = new Node[pro.getJobCount()][]; // job i operation j
        StartNode start = g.getStart();
        EndNode end = g.getEnd();
        start.setStartTime(0);
        start.setTailTime((int) sol.cost);
        end.setStartTime((int) sol.cost);
        end.setTailTime(0);

        // add nodes and precedence arc
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node[pro.getOperationCountArr()[i]];
            nodes[i][0] = new Node(sol.operationMatrix[i][0].jobNo, sol.operationMatrix[i][0].task, sol.operationMatrix[i][0].machineNo, sol.operationMatrix[i][0].endTime - sol.operationMatrix[i][0].startTime, sol.operationMatrix[i][0].startTime, (int) (sol.cost - sol.operationMatrix[i][0].endTime));
            start.addArc(nodes[i][0]);
            for (int j = 1; j < nodes[i].length; j++) {
                nodes[i][j] = new Node(sol.operationMatrix[i][j].jobNo, sol.operationMatrix[i][j].task, sol.operationMatrix[i][j].machineNo, sol.operationMatrix[i][j].endTime - sol.operationMatrix[i][j].startTime, sol.operationMatrix[i][j].startTime, (int) (sol.cost - sol.operationMatrix[i][j].endTime));
                nodes[i][j - 1].addArc(nodes[i][j], 'J');
            }
            nodes[i][nodes[i].length - 1].addArc(end, 'J');
        }

        // add machine arc
        sol.getMachineMatrix();
        Operation[][] machineMatrix = sol.machineMatrix;
        for (int i = 0; i < machineMatrix.length; i++) {
            for (int j = 1; j < machineMatrix[i].length; j++) {
                Operation temp = machineMatrix[i][j - 1];
                Operation next = machineMatrix[i][j];
                nodes[temp.jobNo][temp.task].addArc(nodes[next.jobNo][next.task], 'M');
                // add nodes to graph as starting order
                g.addNode(nodes[temp.jobNo][temp.task]);
            }
            if(machineMatrix[i].length > 0){
                Operation o = machineMatrix[i][machineMatrix[i].length - 1];
                g.addNode(nodes[o.jobNo][o.task]);
            }
        }

        g.cost = (int) sol.cost;
        return g;
    }

    public static Chromosome toChrom(Graph graph) {
        //TODO
        return null;
    }

    public static Solution toSolution(Graph graph) {
        Solution sol = new Solution();
        Problem pro = graph.problem;
        Operation[][] operationMatrix = new Operation[pro.getJobCount()][];

        for (int i = 0; i < operationMatrix.length; i++) {
            operationMatrix[i] = new Operation[pro.getOperationCountArr()[i]];
            for (int j = 0; j < operationMatrix[i].length; j++)
                operationMatrix[i][j] = new Operation();
        }

        for (int i = 0; i < graph.getNodeList().length; i++) {
            for (Node n : graph.getNodeList()[i]) {
                Operation o = operationMatrix[n.job][n.task];
                o.jobNo = n.job;
                o.task = n.task;
                o.startTime = n.getStartTime();
                o.endTime = n.getStartTime() + n.getCost();
                o.machineNo = n.getMachine();
            }
        }

        sol.operationMatrix = operationMatrix;
        sol.problem = pro;
        sol.cost = graph.cost;

        return sol;
    }
}