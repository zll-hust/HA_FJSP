package com.zll.FJSP.Data;

import java.util.ArrayList;

public class EndNode extends Node {
    // dummy node *: end node
    public EndNode() {
        super();
        this.machine = -1;
        this.task = 2;
        this.job = -1;
        this.cost = 0;
        this.startTime = -1;
        this.tailTime = 0;
        this.PredList = new ArrayList<>();
    }

    public EndNode(EndNode start) {
        super();
        this.machine = start.machine;
        this.task = start.task;
        this.job = start.job;
        this.cost = start.cost;
        this.startTime = start.startTime;
        this.tailTime = start.tailTime;
        this.PredList = new ArrayList<>();
    }

    private ArrayList<Arc> PredList;

    // add in two side
    protected void addPred(Node before) {
        for (Arc a : PredList) {
            if (a.getNext() == before)
                return;
        }
        PredList.add(new Arc(before, 'J'));
    }

    public void delArc(Node next, char type) { // delete in two side
        for (Arc a : PredList) {
            if (a.getNext() == next && a.getType() == type) { //?
                PredList.remove(a);
                break;
            }
        }
    }

    public Arc[] getPredList() {
        return (Arc[]) PredList.toArray(new Arc[PredList.size()]);
    }
}