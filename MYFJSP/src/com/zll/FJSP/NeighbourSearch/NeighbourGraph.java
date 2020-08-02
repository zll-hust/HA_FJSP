package com.zll.FJSP.NeighbourSearch;

import com.zll.FJSP.Data.*;

/**
 * Description:邻域图
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月09日 下午07:27:14
 */
public class NeighbourGraph extends Graph {
    public int nodeId;
    public int machineId;
    public int fitness;

    public NeighbourGraph(int machineNr, Problem problem) {
        super(machineNr, problem);
    }

    public NeighbourGraph(Graph g) {
        super(g.getNodeList().length, g.problem);
        // add new nodes
        for (int i = 0; i < this.getNodeList().length; i++) {
            for (Node n : g.getNodeList()[i]) {
                Node tmp = new Node(n); // Copying all the nodes
                addNode(tmp);
            }
        }
        this.start = new StartNode(g.getStart());
        this.end = new EndNode(g.getEnd());

        // copy arcs
        for (int i = 0; i < this.getNodeList().length; i++) {
            for (Node n : g.getNodeList()[i]) {// Copying all the arc ( all nodes needs to be created first )
                Node nEq = findEquivalent(n);
                for (Arc a : n.getSuccList()) {
                    if (a == null) continue;
                    Node seq = findEquivalent(a.getNext());
                    if (seq != null && nEq != null)
                        nEq.addArc(seq, a.getType());
                }
            }
        }

        for (Arc a : g.getStart().getSuccList()) {
            Node seq = findEquivalent(a.getNext());
            start.addArc(seq, a.getType());
        }

        // copy critical path
        if (g.getCriticalPath() != null){
            this.criticalPath = new Path();
            for (Node n : g.getCriticalPath().getPath()) {
                Node seq = findEquivalent(n);
                if(seq != null)
                    this.criticalPath.addNode(seq);
            }
        }

        this.cost = g.cost;
        this.fitness = g.cost;
    }

    public Node findEquivalent(Node n) {
        if (getStart().getID().equals(n.getID())) {
            return getStart();
        } else if (getEnd().getID().equals(n.getID())) {
            return getEnd();
        } else if (n.getMachine() == -3) {
            return null;
        } else {
            for (Node cn : this.getNodeList()[n.getMachine()])
                if (cn.getID().equals(n.getID()))
                    return cn;
        }

        return null;
    }

    public DeleteNeighbourGraph deleteMArcs(Node n) {
        DeleteNeighbourGraph new_g = new DeleteNeighbourGraph(this);
        Node new_n = new_g.findEquivalent(n);
        new_g.deleteNode = new_n;

        Arc machineArc = new_n.getSuccList()[0];
        if (machineArc == null) {
            new_g.SMDeleteNode = null;
        } else {
            new_g.SMDeleteNode = machineArc.getNext();
            new_n.delArc(new_g.SMDeleteNode, 'M'); // Deleting machine constrain after V
        }

        machineArc = new_n.getPredList()[0];
        if (machineArc == null) {
            new_g.PMDeleteNode = null;
        } else {
            new_g.PMDeleteNode = machineArc.getNext();
            new_g.PMDeleteNode.delArc(new_n, 'M'); // Deleting machine constrain after V
        }

        new_g.PJDeleteNode = new_n.getPredList()[1].getNext();
        new_g.SJDeleteNode = new_n.getSuccList()[1].getNext();
        new_n.delArc(new_g.SJDeleteNode, 'J');
        new_g.PJDeleteNode.delArc(new_n, 'J');

        new_g.nodeList[new_n.getMachine()].remove(new_n);
        new_n.setCost(new_n.getMachine());
        new_n.setMachine(-3); // -3 is the symbol of delete node, don't change it

        // add arc for deleteNode before and after
        if (new_g.PMDeleteNode != null && new_g.SMDeleteNode != null)
            new_g.PMDeleteNode.addArc(new_g.SMDeleteNode, 'M');

        // update starting time and tail time for insertNode v
        new_g.s_v_ = new_g.PJDeleteNode.getStartTime() + new_g.PJDeleteNode.getCost();
        new_g.t_v_ = new_g.SJDeleteNode.getTailTime() + new_g.SJDeleteNode.getCost();

        return new_g;
    }

}
