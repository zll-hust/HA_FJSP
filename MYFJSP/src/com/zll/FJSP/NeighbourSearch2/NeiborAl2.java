package com.zll.FJSP.NeighbourSearch2;

import java.util.ArrayList;

import com.zll.FJSP.Data.Operation;
import com.zll.FJSP.Data.Solution;

/**
* Description:
* @author zll-hust E-mail:zh20010728@126.com
* @date 创建时间：2020年7月20日 上午9:10:57
*/
public class NeiborAl2 {
	public static Solution search(Solution sol, int maxTSIterSize) {
		ArrayList<ArrayList<Operation>> iniSol = toTSSol(sol);
		//run reactive tabu search to find best possible solution
		RTS rts = new RTS(iniSol, sol.problem, maxTSIterSize);
		Solution newSol = rts.getBest(sol);

		//print solution and its makespan
//		System.out.println("Best solution found:");
//		System.out.println("with makespan: " + rts.optimalMakespan);
		
		return newSol; // newSol
	}

	public static ArrayList<ArrayList<Operation>> toTSSol(Solution sol){
		ArrayList<ArrayList<Operation>> iniSol = new ArrayList<ArrayList<Operation>>();
		sol.getMachineMatrix();
		Operation[][] operationMatrix = sol.machineMatrix;
		for (int i = 0; i < operationMatrix.length; i++) {
        	iniSol.add(new ArrayList<Operation>());
            for (int j = 0; j < operationMatrix[i].length; j++)
				iniSol.get(i).add(new Operation(operationMatrix[i][j].machineNo, operationMatrix[i][j].jobNo, operationMatrix[i][j].startTime ,operationMatrix[i][j].endTime, operationMatrix[i][j].task));
        }
		return iniSol;
	}
}