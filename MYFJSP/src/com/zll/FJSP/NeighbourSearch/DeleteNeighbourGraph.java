package com.zll.FJSP.NeighbourSearch;

import com.zll.FJSP.Data.EndNode;
import com.zll.FJSP.Data.Node;

import java.util.ArrayList;

/**
 * Description:删除节点后的邻域图
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月10日 下午11:50:46
 */
public class DeleteNeighbourGraph extends NeighbourGraph {
    public Node deleteNode;
    public Node PMDeleteNode;
    public Node SMDeleteNode;
    public Node PJDeleteNode;
    public Node SJDeleteNode;
    int s_v_; // starting time for delete node
    int t_v_; // tailing time for delete node

    public DeleteNeighbourGraph(NeighbourGraph ng) {
        super(ng);
        this.deleteNode = null;
        this.PMDeleteNode = null;
        this.SMDeleteNode = null;
        this.PJDeleteNode = null;
        this.SJDeleteNode = null;
    }

    public NeighbourGraph insertNodeBest(int insertMachine, int deleteMachine) {
        // find L sequence and R sequence
        int[] posLandR = new int[2]; // first one is position of L, second one is R (包括端点)
        ArrayList<Node> Qk = nodeList[insertMachine];
        findLandR(posLandR, Qk);

//        for (int i = 0; i < Qk.size(); i++)
//            System.out.println("nodes: " + " start: " + Qk.get(i).getStartTime() + " tail:" + Qk.get(i).getTailTime() + " cost:" + Qk.get(i).getCost());
//        System.out.println("L:" + posLandR[0] + "  R" + posLandR[1]);
//
//        System.out.println("L:");
//        System.out.println("t_v_: " + t_v_);
//        for (int i = 0; i <= posLandR[0]; i++)
//            System.out.println("node time: " + Qk.get(i).getTailTime() + " " + Qk.get(i).getCost());
//        System.out.println("R：");
//        System.out.println("s_v_: " + s_v_);
//        for (int i = posLandR[1]; i < Qk.size(); i++)
//            System.out.println("node time: " + Qk.get(i).getStartTime() + " " + Qk.get(i).getCost());
//        System.out.println("=====");

        // insert node between L and R
        NeighbourGraph new_g = insertNode(posLandR, insertMachine, deleteMachine, Qk);
        return new_g;
    }

    private void findLandR(int[] posLandR, ArrayList<Node> Qk) { // find L sequence and R sequence using binary search
        if (Qk.size() == 0) {
            posLandR[0] = posLandR[1] = 0;
            return;
        }
        // find L
        int low = 0;
        int high = Qk.size() - 1;
        int mid = -1;
        while (low <= high) {
            mid = (low + high) / 2;
            if (this.t_v_ > Qk.get(mid).getTailTime() + Qk.get(mid).getCost()) {
                high = mid - 1;
            } else if (this.t_v_ < Qk.get(mid).getTailTime() + Qk.get(mid).getCost()) {
                low = mid + 1;
            } else {
//                System.out.println("findL");
                break;
            }
        }

        if (this.t_v_ == Qk.get(mid).getTailTime() + Qk.get(mid).getCost())
            posLandR[0] = mid - 1;
        else
            posLandR[0] = mid;

        // find R
        low = 0;
        high = Qk.size() - 1;
        mid = -1;
        while (low <= high) {
            mid = (low + high) / 2;
            if (this.s_v_ < Qk.get(mid).getStartTime() + Qk.get(mid).getCost()) {
                high = mid - 1;
            } else if (this.s_v_ > Qk.get(mid).getStartTime() + Qk.get(mid).getCost()) {
                low = mid + 1;
            } else {
//                System.out.println("findR");
                break;
            }
        }

        // my binary search will set the position after if don't equal
        // so set the position - 1 if equal
        if (this.s_v_ == Qk.get(mid).getStartTime() + Qk.get(mid).getCost())
            posLandR[1] = mid + 1;
        else
            posLandR[1] = mid;
    }

    private NeighbourGraph insertNode(int[] posLandR, int insertMachine, int deleteMachine, ArrayList<Node> Qk) {

        NeighbourGraph new_g = new NeighbourGraph(this);
        int posL = posLandR[0];
        int posR = posLandR[1];
        int operationIndex = this.problem.getOperationToIndex()[this.deleteNode.job][this.deleteNode.task];
        int newCost = this.problem.getProDesMatrix()[operationIndex][insertMachine];

        // copy deleteNode
        Node insertNode = new Node(deleteNode);
        new_g.findEquivalent(PJDeleteNode).addArc(insertNode, 'J');
        insertNode.addArc(new_g.findEquivalent(SJDeleteNode), 'J');


        insertNode.setCost(newCost);
        insertNode.setMachine(insertMachine);

        if (Qk.size() == 0) {
            new_g.getNodeList()[insertMachine].add(0, insertNode);
            new_g.fitness = newCost + s_v_ + t_v_;
        } else if (Qk.size() == 1) {
            Node oriNode = new_g.findEquivalent(Qk.get(0));
            if (oriNode.job == insertNode.job) { // 防止出现环
                if (oriNode.task > insertNode.task) {
                    insertNode.addArc(oriNode, 'M');
                    new_g.getNodeList()[insertMachine].add(0, insertNode);
                    new_g.fitness = newCost + s_v_ + oriNode.getCost() + oriNode.getTailTime();
                } else {
                    oriNode.addArc(insertNode, 'M');
                    new_g.getNodeList()[insertMachine].add(1, insertNode);
                    new_g.fitness = newCost + oriNode.getTailTime() + oriNode.getCost() + t_v_;
                }
            } else {
                if (Math.random() < 0.5) {
                    insertNode.addArc(oriNode, 'M');
                    new_g.getNodeList()[insertMachine].add(0, insertNode);
                    new_g.fitness = newCost + s_v_ + oriNode.getCost() + oriNode.getTailTime();
                } else {
                    oriNode.addArc(insertNode, 'M');
                    new_g.getNodeList()[insertMachine].add(1, insertNode);
                    new_g.fitness = newCost + oriNode.getTailTime() + oriNode.getCost() + t_v_;
                }
            }
        } else {
            int bestPos = -1; // will insert at the initial position of bestPos
            int bestFitness = Integer.MAX_VALUE;

            if (posL < posR) { // if L && R = null, insert any position is optimal
                bestPos = posL + 1;
            } else { // if L && R != null
                int fitness = Integer.MAX_VALUE; //upper Bound Of the LongestPath

                // pos pi(0)
                bestPos = posR;
                bestFitness = this.s_v_ + Qk.get(posR).getCost() + Qk.get(posR).getTailTime();
                // pos pi(l)
                fitness = Qk.get(posL).getStartTime() + Qk.get(posL).getCost() + this.t_v_;
                if (fitness < bestFitness) {
                    bestPos = posL + 1;
                    bestFitness = fitness;
                }
                // pos pi(1) to pi(l - 1)
                for (int pos = posR + 1; pos <= posL; pos++) { // pos - posR = i
                    fitness = Qk.get(pos - 1).getStartTime() + Qk.get(pos - 1).getCost() + Qk.get(pos).getCost() + Qk.get(pos).getTailTime();
                    if (fitness < bestFitness) {
                        bestPos = pos;
                        bestFitness = fitness;
                    }
                }
            }

            if (bestPos == 0) {
                Node insertAfter = new_g.findEquivalent(Qk.get(bestPos));
                insertNode.addArc(insertAfter, 'M');
            } else if (bestPos == Qk.size()) {
                Node insertBefore = new_g.findEquivalent(Qk.get(bestPos - 1));
                insertBefore.addArc(insertNode, 'M');
            } else {
                Node insertBefore = new_g.findEquivalent(Qk.get(bestPos - 1));
                Node insertAfter = new_g.findEquivalent(Qk.get(bestPos));
                insertBefore.delArc(insertAfter, 'M');
                insertBefore.addArc(insertNode, 'M');
                insertNode.addArc(insertAfter, 'M');
            }


            new_g.getNodeList()[insertMachine].add(bestPos, insertNode);
            new_g.fitness = newCost + bestFitness;
        }

        return new_g;
    }
}