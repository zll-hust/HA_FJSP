package com.zll.FJSP.NeighbourSearch2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.zll.FJSP.Data.Operation;
import com.zll.FJSP.Data.Problem;
import com.zll.FJSP.Data.Solution;

public class RTS {

    int MAXITERATIONS;
    int CYCLEMAX = 30;

    int[][] tabuList;
    LinkedList<ArrayList<ArrayList<Operation>>> savingList = new LinkedList<ArrayList<ArrayList<Operation>>>();
    ArrayList<ArrayList<Operation>> optimalSolution;
    int cycleMoveAve = 0;
    int optimalMakespan;
    int TabuMaxLength = 40;
    int iterations = 0;
    Problem p;

    public RTS(ArrayList<ArrayList<Operation>> inisol, Problem input,int maxTSIterSize) {
        MAXITERATIONS = maxTSIterSize;
        TSSolution newState;
        ArrayList<ArrayList<Operation>> state;
        int newMakespan, lastUpdate = 0;
        iterations = 0;

        // number of machines
        p = input;
        tabuList = new int[p.getMachineCount()][p.getTotalOperationCount()];
        for(int[] list: tabuList)
            Arrays.fill(list, 0);

        // find initial state and set it as optimal
        state = inisol;
        optimalSolution = state;
        optimalMakespan = calculateMakespan(state);

        savingList.add(state);

        while (iterations < MAXITERATIONS) {

            // find best neighbor state not in tabu list
            if ((newState = Neighbour1(state)) == null)
                break;

            state = newState.schedule;

            // calculate makespan of new state
            newMakespan = calculateMakespan(state);

            // update optimal state found so far
            if (newMakespan < optimalMakespan) {
                optimalMakespan = newMakespan;
                optimalSolution = state;
            }
/*

            // find when state has been visited in the past CYCLEMAX iterations
            int lastiteration;
            lastiteration = checkSavingList(state, iterations);

            // if it's less than CYCLEMAX past iterations increase tabu list size
            if (lastiteration <= CYCLEMAX) {

                double mean = (double) (cycleMoveAve * (iterations) + lastiteration) / (iterations + 1);
                cycleMoveAve = (int) mean;

                modifyTLLength(1);

                lastUpdate = iterations;
            } else if (iterations - lastUpdate > cycleMoveAve) { // if tabu list hasn't been changed for the past
                // cycleMoveAve iterations decrease tabu list size
                modifyTLLength(-1);
                lastUpdate = iterations;
            }

            // if savingList is full make room for new state and add it
            if (savingList.size() == CYCLEMAX)
                savingList.removeFirst();
            savingList.add(state);

*/
            iterations++;
        }

    }

    /**
     * takes the current state and output the neighbor state with the lowest
     * makespan that it's not in the tabu list
     */
    public TSSolution Neighbour1(ArrayList<ArrayList<Operation>> currentState) {
        ArrayList<Operation> criticalPath = new ArrayList<Operation>();
        TSSolution newState = new TSSolution();
        TSSolution bestState = null;
        Operation lastop = null;
        int lowestMakespan = Integer.MAX_VALUE;
        int lastendtime = 0;

        // set lastop to the operation with the last end time among the last operation
        // of each machine
        for (ArrayList<Operation> r : currentState) { // for each machine
            if (r.size() != 0 && r.get(r.size() - 1).endTime > lastendtime) { // last operation for each machine
                lastendtime = r.get(r.size() - 1).endTime;
                lastop = r.get(r.size() - 1);
            }
        }

        // find one critical path
        findCriticalPath(lastop.startTime, currentState, criticalPath, lastop);
        criticalPath.add(lastop);

        // if two adjacent operations in critical path have the same machine then
        // critical block -> possible neighbor
        for (int i = 0; i < criticalPath.size() - 1; i++) {
            if (criticalPath.get(i).machineNo == criticalPath.get(i + 1).machineNo
                    && criticalPath.get(i).jobNo != criticalPath.get(i + 1).jobNo) {
                // build new state swapping two operations
                newState.schedule = replan(cloneState(currentState), cloneOperation(criticalPath.get(i)),
                        cloneOperation(criticalPath.get(i + 1)));
                // 标记交换的位置
                newState.machine = criticalPath.get(i).machineNo;
                newState.pos1 = i;
                newState.pos2 = i + 1;
                newState.cost = calculateMakespan(newState.schedule);
                // set bestState to the state with the lowest makespan that is not in tabu list
                if (lowestMakespan > newState.cost && !checkTabuList(newState)) {
                    lowestMakespan = newState.cost;
                    bestState = newState;
                    tabuList[newState.machine][newState.pos1] = iterations + TabuMaxLength;
                    tabuList[newState.machine][newState.pos2] = iterations + TabuMaxLength;
                }
            }
        }

        return bestState;
    }

    /**
     * return a cloned state of currentState
     */
    public ArrayList<ArrayList<Operation>> cloneState(ArrayList<ArrayList<Operation>> currentState) {
        ArrayList<ArrayList<Operation>> clonestate = new ArrayList<ArrayList<Operation>>();
        for (int j = 0; j < currentState.size(); j++) {
            clonestate.add(new ArrayList<Operation>());
            for (int k = 0; k < currentState.get(j).size(); k++)
                clonestate.get(j).add(cloneOperation(currentState.get(j).get(k)));
        }
        return clonestate;
    }

    /**
     * return a cloned operation of op
     */
    public Operation cloneOperation(Operation op) {
        Operation newop = new Operation(op);
        return newop;
    }

    /**
     * @param state initial state
     * @param op1   first operation to swap
     * @param op2   second operation to swap
     * @return a neighbor of state with op1 and op2 swapped
     */
    public ArrayList<ArrayList<Operation>> replan(ArrayList<ArrayList<Operation>> state, Operation op1,
                                                  Operation op2) {
//        if ((op1.job == 0 && op1.task == 0) || (op2.job == 0 && op2.task == 0))
//            System.out.println();
        ArrayList<ArrayList<Operation>> newState = new ArrayList<ArrayList<Operation>>();
        ArrayList<Operation> lateOperations = new ArrayList<Operation>();
        int i, timeswap = op1.endTime;

        for (i = 0; i < state.size(); i++) { // for each machine
            newState.add(new ArrayList<Operation>()); // initialize machine for newState
            for (int j = 0; j < state.get(i).size(); j++) { // for each operation
                // every op ending before swap remains in its previous position, the others are
                // putted into lateOperation list
                if (state.get(i).get(j).endTime <= timeswap && !state.get(i).get(j).equals(op1)
                        && !state.get(i).get(j).equals(op2)) {
                    newState.get(i).add(state.get(i).get(j));
                } else if (!state.get(i).get(j).equals(op1) && !state.get(i).get(j).equals(op2)) { // others ops
                    lateOperations.add(state.get(i).get(j)); // add every other operations to this list
                }
            }
        }

        // swap op1 with op2 and insert them into newState
        op2.startTime = 0;
        // find latest end time for operations already inserted into newState that are
        // of the same job
        for (ArrayList<Operation> r : newState) { // for each machine in newState
            for (Operation o : r) { // for each operation already inserted into newState
                if (o.jobNo == op2.jobNo && o.endTime >= op2.startTime)
                    op2.startTime = o.endTime;
            }
        }
        // find minimum start time considering free slots in machine scheduling
        for (i = 0; i < newState.get(op2.machineNo).size(); i++) {

            if (newState.get(op2.machineNo).get(i).endTime >= op2.startTime) {

                if ((newState.get(op2.machineNo).get(i).startTime - op2.startTime < op2.span))
                    op2.startTime = newState.get(op2.machineNo).get(i).endTime;
                else
                    break;

            }
        }
        op2.endTime = op2.startTime + op2.span;
        op1.startTime = op2.endTime;
        op1.endTime = op1.startTime + op1.span;
        newState.get(op2.machineNo).add(op2);// form same robot
        newState.get(op1.machineNo).add(op1);

        // sort operations in lateOperations based on start time
        lateOperations.sort((o1, o2) -> ((Integer) o1.startTime).compareTo(o2.startTime));

        // insert into newSolution all operations saved in lateOperations in the best
        // possible way
        for (Operation op : lateOperations) { // for each operation in lateOperations
            op.startTime = 0;
            // find latest end time for operations already inserted into newState that are
            // of the same job
            for (ArrayList<Operation> r : newState) { // for each machine in newState
                for (Operation o : r) { // for each operation already inserted into newState
                    if (o.jobNo == op.jobNo && o.endTime >= op.startTime)
                        op.startTime = o.endTime;
                }
            }
            // find minimum start time considering free slots in machine scheduling
            for (i = 0; i < newState.get(op.machineNo).size(); i++) {
                if (newState.get(op.machineNo).get(i).endTime >= op.startTime) {

                    if ((newState.get(op.machineNo).get(i).startTime - op.startTime < op.span))
                        op.startTime = newState.get(op.machineNo).get(i).endTime;
                    else
                        break;
                }
            }
            op.endTime = op.startTime + op.span;
            newState.get(op.machineNo).add(op);
            newState.get(op.machineNo).sort((o1, o2) -> ((Integer) o1.startTime).compareTo(o2.startTime));
        }

        return newState;
    }

    /**
     * recursive function to find a critical path in the schedule state
     */
    public boolean findCriticalPath(int time, ArrayList<ArrayList<Operation>> state,
                                    ArrayList<Operation> criticalPath, Operation op) {
        // if time == 0 means that I have found a critical path whose first operation is
        // op
        if (time == 0) {
            return true;
        }

        for (ArrayList<Operation> r : state) { // for each machine
            for (Operation o : r) { // for each operation
                if (o.endTime == time) {// if operation is adjacent to op
                    if (findCriticalPath(o.startTime, state, criticalPath, o)) { // go down another level
                        criticalPath.add(o); // add operation if critical path has been found
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * return how many passed iterations newState has been visited; if newState is
     * not in savingList return CYCLEMAX+1
     */
    public int checkSavingList(ArrayList<ArrayList<Operation>> newState, int currentIteration) {
        boolean equal;

        for (int k = savingList.size() - 1; k >= 0; k--) { // for each state saved in savingList
            equal = true;
            for (int i = 0; i < newState.size(); i++) { // for each machine in newSolution
                for (int j = 0; j < newState.get(i).size(); j++) { // for each operation in a machine
                    if (newState.get(i).size() == savingList.get(k).get(i).size() && (!newState.get(i).get(j).equals(savingList.get(k).get(i).get(j))
                            || newState.get(i).get(j).startTime != savingList.get(k).get(i).get(j).startTime)) {
                        equal = false;
                    }
                }
            }
            if (equal) {
                return savingList.size() - k;
            }
        }

        return CYCLEMAX + 1;
    }

    /**
     * return true if newState is in tabuList else return false
     */
    public boolean checkTabuList(TSSolution newState) {
        boolean equal;
        if(newState.cost < optimalMakespan)
            return true;
        if(tabuList[newState.machine][newState.pos1] + TabuMaxLength > iterations && tabuList[newState.machine][newState.pos2] + TabuMaxLength > iterations)
            return false;
        return true;
    }

    /**
     * return the makespan of newState
     */
    public int calculateMakespan(ArrayList<ArrayList<Operation>> newState) {
        int solution = 0;

        for (ArrayList<Operation> r : newState) {
            if (r.size() != 0 && solution < r.get(r.size() - 1).endTime)
                solution = r.get(r.size() - 1).endTime;
        }

        return solution;
    }

    /**
     * modify the length of the tabu list
     */
    public void modifyTLLength(double alpha) {
        TabuMaxLength += alpha;
        if (TabuMaxLength < 1)
            TabuMaxLength = 1;
    }

    public Solution getBest(Solution inisol) {
        Solution sol = new Solution(inisol.problem, inisol.r);

        sol.operationMatrix = new Operation[sol.problem.getJobCount()][];
        for (int i = 0; i < sol.operationMatrix.length; i++)
            sol.operationMatrix[i] = new Operation[sol.problem.getOperationCountArr()[i]];

        for (int i = 0; i < optimalSolution.size(); i++) {
            for (int j = 0; j < optimalSolution.get(i).size(); j++)
                sol.operationMatrix[optimalSolution.get(i).get(j).jobNo][optimalSolution.get(i).get(j).task] = new com.zll.FJSP.Data.Operation(optimalSolution.get(i).get(j));
        }

        sol.cost = optimalMakespan;
        return sol;
    }

    @Override
    public String toString() {
        String result = "";

        for (int i = 0; i < optimalSolution.size(); i++) {
            // result += "robot " + i;
            for (int j = 0; j < optimalSolution.get(i).size(); j++) {
                result += " " + optimalSolution.get(i).get(j).id;
                result += " s:" + optimalSolution.get(i).get(j).startTime;
                result += " e:" + optimalSolution.get(i).get(j).endTime + " ";
            }
            result += "\n";
        }

        return result;
    }

}
