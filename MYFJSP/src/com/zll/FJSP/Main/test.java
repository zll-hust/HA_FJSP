package com.zll.FJSP.Main;

import com.zll.FJSP.Data.*;
import com.zll.FJSP.GA.CaculateFitness;
import com.zll.FJSP.GA.Chromosome;
import com.zll.FJSP.NeighbourSearch2.NeiborAl2;

import java.io.File;
import java.util.Random;

public class test {
    public static void main(String[] args) {
        String instances = "Mk01.txt";
        Input in = new Input(new File("./input/" + instances));// 输入算例
        Problem input = in.getProblemDesFromFile();
        // 初始化工件类entries
        int[][] operationToIndex = input.getOperationToIndex();
        int jobCount = input.getJobCount();
        Job[] jobs = new Job[jobCount];
        for (int i = 0; i < jobCount; i++) {
            int index = i;// 工件编号
            int opsNr = input.getOperationCountArr()[i];// 工件工序数
            int[] opsIndex = operationToIndex[i];// 工件工序对应的index
            int[] opsMacNr = new int[opsNr];// 工序对应备选机器数
            for (int j = 0; j < opsNr; j++) {
                opsMacNr[j] = input.getMachineCountArr()[opsIndex[j]];
            }
            jobs[i] = new Job(index, opsNr, opsIndex, opsMacNr);
        }
        Chromosome cc = new Chromosome(jobs, new Random());
        Operation[][] operationMatrix = new Operation[input.getJobCount()][];
        for (int i = 0; i < operationMatrix.length; i++) {
            operationMatrix[i] = new Operation[input.getOperationCountArr()[i]];
            for (int j = 0; j < operationMatrix[i].length; j++)
                operationMatrix[i][j] = new Operation();
        }
        CaculateFitness c = new CaculateFitness();
        cc.fitness = 1.0 / c.evaluate(cc, input, operationMatrix);
        Solution sol = new Solution(operationMatrix, cc, input, 1.0 / cc.fitness);
        Chromosome pp = sol.toChromosome();
        Solution sol2 = new Solution(operationMatrix, pp, input, 1.0 / pp.fitness);
        Solution sol3 = NeiborAl2.search(sol, 200);
        Chromosome c3 = sol3.toChromosome();

        sol.printSchedPicInConsole();
        sol2.printSchedPicInConsole();
        sol3.printSchedPicInConsole();
        System.out.println();
    }
}
