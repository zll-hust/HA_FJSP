package com.zll.FJSP.Data;

import java.util.ArrayList;

/**
 * Description:邻域图的节点
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月09日 下午07:13:27
 */
public class Node implements Comparable<Node> {
    protected int machine;
    public int job;
    public int task;
    protected int startTime;
    protected int tailTime;
    protected int cost;
    private Arc[] SuccList;// the first arc is machine arc, second arc is job arc
    private Arc[] PredList;

    public Node() {
    }

    public Node(int job, int task, int machine, int cost) {
        this.machine = machine;
        this.task = task;
        this.job = job;
        this.cost = cost;
        this.startTime = -1;
        this.tailTime = -1;
        SuccList = new Arc[2];
        PredList = new Arc[2];
    }

    public Node(int job, int task, int machine, int cost, int startTime, int tailTime) {
        this.machine = machine;
        this.task = task;
        this.job = job;
        this.cost = cost;
        this.startTime = startTime;
        this.tailTime = tailTime;
        SuccList = new Arc[2];
        PredList = new Arc[2];
    }

    public Node(Node n) {
        this.machine = n.machine;
        this.task = n.task;
        this.job = n.job;
        this.cost = n.cost;
        this.startTime = n.startTime;
        this.tailTime = n.tailTime;
        SuccList = new Arc[2];
        PredList = new Arc[2];
    }

    public void addArc(Node next, char type) { // add in two side
        if (type == 'M')
            SuccList[0] = new Arc(next, type);
        else if (type == 'J' && SuccList != null)
            SuccList[1] = new Arc(next, type);
        else{
            ((StartNode)this).addArc(next, type);
            return;
        }


        if (next instanceof EndNode) {
            next = (EndNode) next;
            ((EndNode) next).addPred(this);
        } else
            next.addPred(this, type);
    }

    protected void addPred(Node before, char type) {
        if (type == 'M')
            PredList[0] = new Arc(before, type);
        else if (type == 'J')
            PredList[1] = new Arc(before, type);
    }

    public void delArc(Node next, char type) { // delete in two side
        if (type == 'M')
            SuccList[0] = null;
        else if (type == 'J')
            SuccList[1] = null;

        if (next != null) {
            if (type == 'M')
                next.PredList[0] = null;
            else if (type == 'J' && next.PredList != null)
                next.PredList[1] = null;
            else if(next instanceof  EndNode){
                ((EndNode)next).delArc(this, 'J');
            }
        }
    }

    public String getID() {
        return "ID" + this.job + this.task; // Should find another way
    }

    public int getMachine() {
        return this.machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int time) {
        this.startTime = time;
    }

    public int getTailTime() {
        return tailTime;
    }

    public void setTailTime(int time) {
        this.tailTime = time;
    }

    public Arc[] getSuccList() {
        return SuccList;
    }

    public Arc[] getPredList() {
        return PredList;
    }

    public String toString() {
        if (this.machine == -1 && this.task == 1)
            return "Node:Start";
        if (this.machine == -1 && this.task == 2)
            return "Node:End";
        return "Node: M" + (this.machine + 1) + "J" + (this.job + 1) + "T" + (this.task + 1) + "C" + this.cost;
    }

    public int compareTo(Node o) {
        return this.getStartTime() - o.getStartTime();
    }
}