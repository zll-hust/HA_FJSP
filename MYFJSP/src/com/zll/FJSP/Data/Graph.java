package com.zll.FJSP.Data;

import com.zll.FJSP.NeighbourSearch.NeighbourGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Description:图
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月09日 下午07:19:24
 */
public class Graph implements Comparable<Graph> {
    // solution graph
    protected ArrayList<Node> nodeList[];
    protected StartNode start;
    protected EndNode end;
    protected Path criticalPath;
    public Problem problem;
    public int cost;

    public Graph() {
    }

    ;

    public Graph(int machineNr, Problem problem) {
        this.start = new StartNode();
        this.end = new EndNode();
        nodeList = new ArrayList[machineNr];
        for (int i = 0; i < nodeList.length; i++)
            nodeList[i] = new ArrayList<>();
        this.problem = problem;
    }

    public Path evaluatePath(Node current, Node end) { // DFS
        boolean flag = false;
        Path currentPath;
        Path chosenPath = null;

        if (current.equals(end)) {
            chosenPath = new Path();
            chosenPath.addNode(current);
            return chosenPath;
        }

        for (Arc arc : current.getSuccList()) {
            try {
                if (arc == null) continue;
                currentPath = evaluatePath(arc.getNext(), end);
                if (currentPath == null)
                    continue; // Path not found
                chosenPath = (chosenPath == null ? currentPath
                        : (chosenPath.getCost() > currentPath.getCost()) ? chosenPath : currentPath); // Choose between different successors
            } catch (StackOverflowError e) {
                System.out.println("stackoverflow!");
                flag = true;
            }
        }

        if (chosenPath != null)
            chosenPath.addNode(current);

        if (flag) {
            System.out.println("current:" + current + " end" + end);
            System.out.println("current succeed:");
            for (Arc a : current.getSuccList())
                System.out.println(a.getNext());
            System.out.println("end pred:");
            for (Arc a : end.getPredList())
                System.out.println(a.getNext());
        }


        return chosenPath;
    }


    public int evaluateCost() {
        this.cost = evaluateCost(start, end);
        return this.cost;
    }

    public int evaluateCost(Node before, Node after) {
        Path tmp = evaluatePath(before, after);
        Collections.reverse(tmp.getPath());
        criticalPath = tmp;
        return criticalPath.getCost();
    }

    public void updateStartTime() { // Bellman-ford algorithm
        int vNum = this.problem.getTotalOperationCount() + 2; // 顶点数
        int[] distance = new int[vNum]; // 存放start点到该点的距离


        for (int i = 0; i < this.getNodeList().length; i++) {
            for (Node n : this.getNodeList()[i]) {
                n.setStartTime(-1);
                n.setTailTime(-1);
            }
        }
        this.getEnd().setStartTime(-1);
        this.getStart().setStartTime(0);
        for (Arc a : start.getSuccList())
            a.getNext().setStartTime(0);

        for (int i = 0; i < vNum - 1; i++) {
            for (int j = 0; j < this.getNodeList().length; j++) {
                for (Node n1 : this.getNodeList()[j]) {
                    for (Arc a : n1.getSuccList()) {
                        if (a == null) continue;
                        Node n2 = a.getNext();
                        int cost = n1.getCost();
                        if (n2.getStartTime() < n1.getStartTime() + cost)
                            n2.setStartTime(n1.getStartTime() + cost);
                    }
                }
            }
        }

        for (int i = 0; i < this.getNodeList().length; i++)
            for (Node n : this.getNodeList()[i])
                n.setTailTime(this.cost - n.getStartTime() - n.getCost());
    }


    public StartNode getStart() {
        return start;
    }

    public EndNode getEnd() {
        return end;
    }

    public Path getCriticalPath() {
        return this.criticalPath;
    }

    public void addNode(Node node) {
        nodeList[node.getMachine()].add(node);
    }

    public ArrayList<Node>[] getNodeList() {
        return nodeList;
    }

    public int compareTo(Graph o) {
        return o.cost - this.cost;
    }
}
