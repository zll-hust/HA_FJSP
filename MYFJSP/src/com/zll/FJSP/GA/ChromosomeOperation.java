package com.zll.FJSP.GA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.zll.FJSP.Data.Problem;

/**
 * Description:遗传算法-算子部分，包括：selection， cross， mutation。
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月30日 上午1:02:42
 */
public class ChromosomeOperation {

    Random r;
    Problem input;

    public ChromosomeOperation(Random r, Problem input) {
        this.r = r;
        this.input = input;
    }

    public Chromosome[] Selection(Chromosome[] parents, double pr) {
        int popNr = parents.length;
        Chromosome[] children = new Chromosome[popNr];

        // 选择策略一：精英选择。
        int num = (int) (pr * popNr);
        ArrayList<Chromosome> p = new ArrayList<>();
        Collections.addAll(p, parents);
        Collections.sort(p);
        for (int i = 0; i < num; i++) {
            children[i] = p.get(i);
        }

        // 选择策略二：锦标赛选择。
        for (int i = num; i < popNr; i++) {
            int n1 = r.nextInt(popNr);
            int n2 = r.nextInt(popNr);
            if (parents[n1].fitness < parents[n2].fitness)
                children[i] = parents[n2];
            else
                children[i] = parents[n1];
        }

//        // 选择策略三：轮盘赌选择。
//        double[] prob = new double[popNr];
//        double sum = 0.0d;
//
//        for (Chromosome i : parents)
//            sum += i.fitness;
//
//        prob[0] = parents[0].fitness / sum;
//        for (int i = 1; i < parents.length; i++) {
//            prob[i] = prob[i - 1] + parents[i].fitness / sum;
//        }
//
//        for (int i = num; i < popNr; i++) {
//            double rand_num = r.nextDouble();
//            for (int j = 0; j < parents.length; j++) {
//                if (prob[j] > rand_num) {
//                    children[i] = parents[j];
//                    break;
//                }
//            }
//        }

        return children;
    }

    // 轮盘赌选择父代个体
    public Chromosome[] Selection2(Chromosome[] parents) {
        int len = parents.length;
        double[] prob = new double[len];
        double sum = 0.0d;
        Chromosome[] chromosomes = new Chromosome[len];

        for (Chromosome i : parents) {
            sum += i.fitness;
        }

        prob[0] = parents[0].fitness / sum;
        for (int i = 1; i < parents.length; i++) {
            prob[i] = prob[i - 1] + parents[i].fitness / sum;
        }

        for (int i = 0; i < len; i++) {
            double rand_num = r.nextDouble();
            for (int j = 0; j < parents.length; j++) {
                if (prob[j] > rand_num) {
                    chromosomes[i] = parents[j];
                    break;
                }
            }
        }

        return chromosomes;
    }


    public void Crossover(Chromosome c1, Chromosome c2) {
        if (r.nextDouble() < 0.5) {
            operSeqCrossoverPOX(c1.gene_OS, c2.gene_OS);
        } else {
            operSeqCrossoverJBX(c1.gene_OS, c2.gene_OS);
        }

        machineSeqCrossover(c1.gene_MS, c2.gene_MS);
    }

    public void operSeqCrossoverZLL(int o1[], int o2[]) {
        // ZLL自己尝试的cross方法
        int len = o1.length;
        int jobCount = input.getJobCount();
        int[] operationCountArr = input.getOperationCountArr();

        int[] p1 = new int[len];
        int[] p2 = new int[len];
        System.arraycopy(o1, 0, p1, 0, len);
        System.arraycopy(o2, 0, p2, 0, len);

        // 生成随机的交叉起点和终点
        int start = r.nextInt(len);
        int end = r.nextInt(len);
//		start = 3;
//		end = 4;
        int temp = 0;
        if (start > end) {
            temp = start;
            start = end;
            end = temp;
        }

        // 统计
        int[] operCount = new int[jobCount];// 在标记区域内每个工件已经执行的工序数
        Arrays.fill(operCount, 0);
        for (int i = start; i <= end; i++)
            operCount[p1[i]]++;

        // 赋值
        int index = 0;// p2的下标,同时作用于p1的赋值
        for (int i = 0; i < len; i++) {
            if (i >= start && i <= end) {
                o1[i] = p1[i];
            } else {
                while (true) {
                    if (operCount[p2[index]] < operationCountArr[p2[index]]) {
                        o1[i] = p2[index];
                        operCount[p2[index]]++;
                        break;
                    } else {
                        o2[index] = p2[index];
                        index++;
                    }
                }
                o2[index] = p1[i];
                index++;
            }
        }

    }

    public void operSeqCrossoverPOX(int o1[], int o2[]) {

        // 随机分配工件集
        int jobCount = input.getJobCount();
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < jobCount; i++) {
            temp.add(i);
        }
        Collections.shuffle(temp);

        int len1 = r.nextInt(jobCount);
        List<Integer> jobSet1 = temp.subList(0, len1);
        List<Integer> jobSet2 = temp.subList(len1, jobCount);

//		List<Integer> jobSet1 = new ArrayList<>();
//		jobSet1.add(1);
//		List<Integer> jobSet2 = new ArrayList<>();
//		jobSet2.add(0);
//		jobSet2.add(2);

        // 分配
        int len = o1.length;

        int[] p1 = new int[len];
        int[] p2 = new int[len];
        System.arraycopy(o1, 0, p1, 0, len);
        System.arraycopy(o2, 0, p2, 0, len);
        Arrays.fill(o1, -1);
        Arrays.fill(o2, -1);
        for (int i = 0; i < len; i++) {
            if (jobSet1.contains(p1[i])) {
                o1[i] = p1[i];
                p1[i] = -1;
            }
            if (jobSet1.contains(p2[i])) {
                o2[i] = p2[i];
                p2[i] = -1;
            }
        }

        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < len; i++) {
            if (o2[i] == -1) {
                while (p1[index1] == -1) {
                    index1++;
                }
                o2[i] = p1[index1];
                index1++;
            }
            if (o1[i] == -1) {
                while (p2[index2] == -1) {
                    index2++;
                }
                o1[i] = p2[index2];
                index2++;
            }
        }

    }

    public void operSeqCrossoverJBX(int o1[], int o2[]) {
        // 随机分配工件集
        int jobCount = input.getJobCount();
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < jobCount; i++) {
            temp.add(i);
        }
        Collections.shuffle(temp);

        int len1 = r.nextInt(jobCount);
        List<Integer> jobSet1 = temp.subList(0, len1);
        List<Integer> jobSet2 = temp.subList(len1, jobCount);

//		List<Integer> jobSet1 = new ArrayList<>();
//		jobSet1.add(1);
//		List<Integer> jobSet2 = new ArrayList<>();
//		jobSet2.add(0);
//		jobSet2.add(2);

        // 分配
        int len = o1.length;

        int[] p1 = new int[len];
        int[] p2 = new int[len];
        System.arraycopy(o1, 0, p1, 0, len);
        System.arraycopy(o2, 0, p2, 0, len);
        Arrays.fill(o1, -1);
        Arrays.fill(o2, -1);
        for (int i = 0; i < len; i++) {
            if (jobSet1.contains(p1[i])) {
                o1[i] = p1[i];
            } else {
                p1[i] = -1;
            }
            if (jobSet2.contains(p2[i])) {
                o2[i] = p2[i];
            } else {
                p2[i] = -1;
            }
        }


        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < len; i++) {
            if (o2[i] == -1) {
                while (p1[index1] == -1) {
                    index1++;
                }
                o2[i] = p1[index1];
                index1++;
            }
            if (o1[i] == -1) {
                while (p2[index2] == -1) {
                    index2++;
                }
                o1[i] = p2[index2];
                index2++;
            }
        }

    }

    public void machineSeqCrossover(int m1[], int m2[]) {
        int len = m1.length;

        int[] p1 = new int[len];
        int[] p2 = new int[len];
        System.arraycopy(m1, 0, p1, 0, len);
        System.arraycopy(m2, 0, p2, 0, len);

        // 生成随机的交叉起点和终点
        int start = r.nextInt(len);
        int end = r.nextInt(len);

//		start = 2;
//		end = 5;

        int temp = 0;
        if (start > end) {
            temp = start;
            start = end;
            end = temp;
        }

        for (int i = start; i < end; i++) {
            m1[i] = p2[i];
            m2[i] = p1[i];
        }
    }

    public void Mutation(Chromosome chromosome) {
        double posibility = r.nextDouble();
        if (posibility < 0.5) {
            operSeqMutationSwap(chromosome.gene_OS);
        } else {
            operSeqMutationNeighbor(chromosome.gene_OS);
        }

        machineSeqMutation(chromosome.gene_MS);
    }

    public void operSeqMutationSwap(int[] os) {
        int len = os.length;
        // 随机查找两点
        double posibility = r.nextDouble();
        if (posibility < 0.5) {
            int posa = r.nextInt(len);
            int posb = r.nextInt(len);
            while (posa == posb)
                posb = r.nextInt(len);
            int temp;
            if (posa > posb) {
                temp = posa;
                posa = posb;
                posb = temp;
            }

            temp = os[posa];
            os[posa] = os[posb];
            os[posb] = temp;
        }
    }

    public void operSeqMutationNeighbor(int[] os) {
        int len = os.length;
        int pos1 = r.nextInt(len);
        int pos2 = r.nextInt(len);
        while (os[pos1] == os[pos2])
            pos2 = r.nextInt(len);
        int pos3 = r.nextInt(len);
        while (os[pos3] == os[pos2] || os[pos3] == os[pos1])
            pos3 = r.nextInt(len);

        ArrayList<Integer> li = new ArrayList<>();
        li.add(os[pos1]);
        li.add(os[pos2]);
        li.add(os[pos3]);
        Collections.shuffle(li);

        os[pos1] = li.get(0);
        os[pos2] = li.get(1);
        os[pos3] = li.get(2);
    }

    public int[] machineSeqMutation(int[] ms) {
        int[] machineCountArr = input.getMachineCountArr();
        for (int i = 0; i < ms.length / 2; i++) {
            int pos = r.nextInt(ms.length);
            ms[pos] = r.nextInt(machineCountArr[pos]) + 1;
        }

        return ms;
    }
}