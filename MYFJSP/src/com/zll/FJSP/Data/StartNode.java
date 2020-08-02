package com.zll.FJSP.Data;

import java.util.ArrayList;

public class StartNode extends Node{
    // dummy node 0: start node
    public StartNode() {
        super();
        this.machine = -1;
        this.task = 1;
        this.job = -1;
        this.cost = 0;
        this.startTime = 0;
        this.tailTime = -1;
        this.SuccList = new ArrayList<>();
    }

    public StartNode(StartNode start) {
        super();
        this.machine = start.machine;
        this.task = start.task;
        this.job = start.job;
        this.cost = start.cost;
        this.startTime = start.startTime;
        this.tailTime = start.tailTime;
        this.SuccList = new ArrayList<>();
    }

    private ArrayList<Arc> SuccList;

    public void addArc(Node next) {
        addArc(next, 'J');
    }

    public void addArc(Node next, char type) { // add in two side
        next.addPred(this, 'J');
        for(Arc a: SuccList){
            if(a.getNext() == next && a.getType() == type)
                return;
        }
        SuccList.add(new Arc(next, 'J'));
    }

    public void delArc(Node next, char type) { // delete in two side
        for(Arc a: SuccList){
            if(a.getNext() == next && a.getType() == type){ //?
                SuccList.remove(a);
                break;
            }
        }

        if (type == 'M')
            next.getPredList()[0] = null;
        else if (type == 'J')
            next.getPredList()[1] = null;
    }

    public Arc[] getSuccList() {
        return (Arc[]) SuccList.toArray(new Arc[SuccList.size()]);
    }
}
