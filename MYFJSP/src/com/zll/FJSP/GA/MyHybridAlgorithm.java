package com.zll.FJSP.GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.zll.FJSP.Data.Job;
import com.zll.FJSP.Data.Problem;
import com.zll.FJSP.Data.Operation;
import com.zll.FJSP.Data.Solution;
import com.zll.FJSP.NeighbourSearch2.NeiborAl2;


/**
 * Description:遗传算法
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月28日 下午2:58:10
 */
public class MyHybridAlgorithm {
    private Problem input;
    private Operation[][] operationMatrix;
    private Random r;

    private final int popSize = 50;// population size 400
    private final double pr = 0.10;// Reproduction probability
    private final double pc = 0.80;// Crossover probability
    private final double pm = 0.10;// Mutation probability

    private final int maxT = 9;// tabu list length
    private final int maxTabuLimit = 100;// maxTSIterSize = maxTabuLimit * (Gen / maxGen)
    private final double pt = 0.05;// tabu probability

    private final double pp = 0.30;// perturbation probability

    private final int maxGen = 200;// iterator for 200 time for each loop
    private final int maxStagnantStep = 30;// max iterator no improve
    private final int timeLimit = -1;// no time limit

    public MyHybridAlgorithm(Problem input) {
        this.input = input;
        this.operationMatrix = new Operation[input.getJobCount()][];

        for (int i = 0; i < operationMatrix.length; i++) {
            operationMatrix[i] = new Operation[input.getOperationCountArr()[i]];
            for (int j = 0; j < operationMatrix[i].length; j++)
                operationMatrix[i][j] = new Operation();
        }

        this.r = new Random();
//		this.r.setSeed(1);
    }

    /**
     * the whole logic of the flexible job shop sheduling problem
     */
    public Solution solve() {
        int jobCount = input.getJobCount();
        CaculateFitness c = new CaculateFitness();
        ChromosomeOperation chromOps = new ChromosomeOperation(r, input);
//        TabuSearch1 tabu = new TabuSearch1(input, r);

        // 初始化工件类entries
        int[][] operationToIndex = input.getOperationToIndex();
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

        long startTime = System.currentTimeMillis();// 算法开始

        // 随机生成初始种群
        Chromosome[] parents = new Chromosome[this.popSize];// 染色体
        for (int i = 0; i < this.popSize; i++) {
            parents[i] = new Chromosome(jobs, r);
            parents[i].fitness = 1.0 / c.evaluate(parents[i], input, operationMatrix);
        }

        Chromosome[] children = new Chromosome[this.popSize];
        for (int i = 0; i < this.popSize; i++) {
            children[i] = new Chromosome(parents[i]);
        }

        // 获取最优子代
        double maxFitness = Double.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < this.popSize; i++) {
            if (maxFitness < parents[i].fitness) {
                index = i;
                maxFitness = parents[i].fitness;
            }
        }
        Chromosome best = new Chromosome(parents[index]);
        Chromosome currentBest = new Chromosome(parents[index]);

        int noImprove = 0;
        int gen = 0;
        while (gen < this.maxGen) {

            // 陷入局部最优时进行扰动:取部分精英个体后随机生成新个体
            if (gen - noImprove > this.maxStagnantStep) {
//                break;

                int num = (int) (pp * popSize);
                ArrayList<Chromosome> p = new ArrayList<>();
                Collections.addAll(p, parents);
                Collections.sort(p);
                for (int i = 0; i < num; i++)
                    parents[i] = p.get(i);
                for (int i = num; i < this.popSize; i++) {
                    parents[i] = new Chromosome(jobs, r);
                    parents[i].fitness = 1.0 / c.evaluate(parents[i], input, operationMatrix);
                }

                noImprove = gen;
            }

            // 选择 selection
            children = chromOps.Selection(parents, pr);

            // 交叉 cross
            for (int i = 0; i < this.popSize; i += 2) {
                if (r.nextDouble() < this.pc) {
//					int fatherIndex = r.nextInt(popSize);
//					int motherIndex = r.nextInt(popSize);
//					while (fatherIndex == motherIndex)
//						motherIndex = r.nextInt(popSize);
                    int fatherIndex = i;
                    int motherIndex = i + 1;
                    chromOps.Crossover(children[fatherIndex], children[motherIndex]);
                }
            }

            // 变异 mutation
            for (int i = 0; i < this.popSize; i++) {
                if (r.nextDouble() < this.pm) {
                    chromOps.Mutation(children[i]);
                }
            }

            // update fitness
//            for (int i = 0; i < this.popSize; i++){
//                children[i].fitness = 1.0 / c.evaluate(children[i], input, operationMatrix);
//                parents[i] = new Chromosome(children[i]);
//            }
            for (int i = 0; i < this.popSize; i++) {
                children[i].fitness = 1.0 / c.evaluate(children[i], input, operationMatrix);
                int maxTSIterSize = (int) (maxGen * ((float) gen / (float) maxGen));
                Solution sol = new Solution(operationMatrix, children[i], input, 1.0 / children[i].fitness);

                // TS1
                sol = NeiborAl2.search(sol, maxTSIterSize);

                // TS2
//                sol = NeighbourAlgorithms.neighbourSearch(sol);

                parents[i] = sol.toChromosome();
            }

            // get best chromosome
            currentBest = getBest(parents);

//            ArrayList<Chromosome> p = new ArrayList<>();
//            Collections.addAll(p, children);
//            Collections.sort(p);
//            currentBest = p.get(0);
//
//             tabu search
//            int tabuNr = popSize - (int) (pt * popSize);
//            for (int i = 0; i < tabuNr; i++) {
//                parents[i] = new Chromosome(children[i]);
//            }
//            for (int i = tabuNr; i < popSize; i++) {
//                int maxTSIterSize = (int) (maxGen * ((float) gen / (float) maxGen));
//                parents[i] = new Chromosome(tabu.TabuSearch(maxTSIterSize, 5, children[i], best.fitness));
//            }


            if (best.fitness < currentBest.fitness) {
                best = new Chromosome(currentBest);
                noImprove = gen;
                System.out.println("In " + gen + " generation, find new best fitness is:" + currentBest.fitness);
            }
            System.out.println(" After " + gen + " generation, the best fitness is:" + best.fitness);

            gen++;
        }
        Solution bestSolution = new Solution(operationMatrix, best, input, c.evaluate(best, input, operationMatrix));
        System.out.println();
        System.out.println(" After " + gen + " generation, the best schedule cost is:" + bestSolution.cost);

        long endTime = System.currentTimeMillis();// 算法开始
        System.out.println(" 算法时间花费：" + (endTime - startTime) / 1000.0 + "s");
        bestSolution.algrithmTimeCost = (endTime - startTime) / 1000.0;

        return bestSolution;
    }

    private Chromosome getBest(Chromosome[] parents) {
        double maxFitness = Double.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < this.popSize; i++) {
            if (maxFitness < parents[i].fitness) {
                index = i;
                maxFitness = parents[i].fitness;
            }
        }
        return new Chromosome(parents[index]);
    }
}