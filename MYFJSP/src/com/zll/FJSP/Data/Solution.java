package com.zll.FJSP.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Random;

import com.zll.FJSP.GA.Chromosome;

/**
 * Description:
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月30日 下午6:11:48
 */
public class Solution {
    public Operation[][] operationMatrix; // job i operation j
    public Chromosome chromosome;
    public Problem problem;
    public Operation[][] machineMatrix; // machine i operation j
    public double cost;
    public double algrithmTimeCost;
    public Random r;

    public Solution() {

    }

    public Solution(Problem p, Random r) {
        this.problem = p;
        this.r = r;
    }

    public Solution(Operation[][] operationMatrix, Chromosome chromosome, Problem problem, double cost) {
        this.operationMatrix = new Operation[operationMatrix.length][];
        for (int i = 0; i < operationMatrix.length; i++) {
            this.operationMatrix[i] = new Operation[operationMatrix[i].length];
            for (int j = 0; j < operationMatrix[i].length; j++) {
                this.operationMatrix[i][j] = new Operation(operationMatrix[i][j]);
            }
        }

        this.problem = problem;
        this.chromosome = new Chromosome(chromosome);
        this.r = chromosome.r;
        this.cost = cost;
    }

    public void printToConsole() {
        for (int i = 0; i < operationMatrix.length; i++) {
            for (int j = 0; j < operationMatrix[i].length; j++) {
                System.out.print("Machine:" + (operationMatrix[i][j].machineNo + 1) + "|Job:" + (i + 1) + "|Operation:"
                        + (j + 1));
                System.out.println("|time(" + (operationMatrix[i][j].machineNo + 1) + "," + (i + 1) + ")="
                        + (operationMatrix[i][j].endTime - operationMatrix[i][j].startTime) + "|start time:"
                        + (operationMatrix[i][j].startTime + 1) + "|end time:" + operationMatrix[i][j].endTime);
            }
        }
    }

    public void printToTxt() {
        StringBuilder jobNoBuilder = new StringBuilder();
        StringBuilder machineNoBuilder = new StringBuilder();
        StringBuilder operationNoBuilder = new StringBuilder();
        StringBuilder startTimeBuilder = new StringBuilder();
        StringBuilder endTimeBuilder = new StringBuilder();

        for (int i = 0; i < operationMatrix.length; i++) {
            for (int j = 0; j < operationMatrix[i].length; j++) {
                jobNoBuilder.append((i + 1) + " ");
                operationNoBuilder.append((j + 1) + " ");
                machineNoBuilder.append((operationMatrix[i][j].machineNo + 1) + " ");
                startTimeBuilder.append(operationMatrix[i][j].startTime + " ");
                endTimeBuilder.append(operationMatrix[i][j].endTime + " ");
            }
        }

        File file = new File("operationInfo.txt");// draw the picture which will
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("工件号:" + jobNoBuilder.toString() + "\r\n");
            fw.write("工序号:" + operationNoBuilder.toString() + "\r\n");
            fw.write("机器号:" + machineNoBuilder.toString() + "\r\n");
            fw.write("开始时间:" + startTimeBuilder.toString() + "\r\n");
            fw.write("持续时间:" + endTimeBuilder.toString() + "\r\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMachineMatrix() {
        int machineCount = problem.getMachineCount();
        ArrayList<Operation> matrix[] = new ArrayList[machineCount];
        for (int i = 0; i < machineCount; i++)
            matrix[i] = new ArrayList<Operation>();

        for (int i = 0; i < operationMatrix.length; i++) {
            for (int j = 0; j < operationMatrix[i].length; j++) {
                Operation tempOperation = operationMatrix[i][j];
                ArrayList<Operation> machine = matrix[tempOperation.machineNo];
                int k;
                if (machine.size() == 0 || tempOperation.endTime <= machine.get(0).startTime)
                    machine.add(0, new Operation(tempOperation));
                else {
                    for (k = 0; k < machine.size() - 1; k++)
                        if (machine.get(k).endTime <= tempOperation.startTime && machine.get(k + 1).startTime >= tempOperation.endTime)
                            break;
                    machine.add(k + 1, new Operation(tempOperation));
                }
            }
        }

        this.machineMatrix = new Operation[machineCount][];
        for (int i = 0; i < machineMatrix.length; i++) {
//            machineMatrix[i] = (Operation[])matrix[i].toArray(new Operation[matrix[i].size()]);
            machineMatrix[i] = new Operation[matrix[i].size()];
            for (int j = 0; j < machineMatrix[i].length; j++) {
                machineMatrix[i][j] = matrix[i].get(j);
            }
        }
    }

    public void printSchedPicInConsole() {
        // 控制台输出图形
        int start = 0, end = 0, machineNo = 0;
        String flagString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Operation tempOperation = null;
        int j = 0, p = 0, q = 0, i = 0;
        char ch = 'a';
        int machineCount = problem.getMachineCount();
        int colums = 2000;
        char sheduleMatrix[][] = new char[machineCount][colums];
        for (i = 0; i < machineCount; i++)
            Arrays.fill(sheduleMatrix[i], ' ');
        machineNo = 0;
        int jobCount = problem.getJobCount();
        int operCount = problem.getMaxOperationCount();

        System.out.println();
        System.out.println(" Gantt for this schedule:");
        for (p = 0; p < jobCount; p++) {
            ch = flagString.charAt(p);// 每一个工件对应一种字符
            System.out.print(" job" + (p + 1) + ":" + ch + ";");
            for (q = 0; q < operationMatrix[p].length; q++) {
                tempOperation = operationMatrix[p][q];
                start = tempOperation.startTime;
                end = tempOperation.endTime;
                machineNo = tempOperation.machineNo;
                if (machineNo == -1)
                    continue;
                for (j = start; j < end; j++)
                    sheduleMatrix[machineNo][j] = ch;
            }
        }

        testSchedule(sheduleMatrix);

        // 绘制甘特图
        Formatter formatter = new Formatter(System.out);
        System.out.println();
        for (i = 0; i < machineCount; i++) {
            formatter.format(" Machine " + (i + 1) + ":");
            for (j = 0; j < colums; j++)
                System.out.print(sheduleMatrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    private void testSchedule(char[][] sheduleMatrix) {
        String flagString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        boolean flag = true;
        outer:
        for (int i = 0; i < operationMatrix.length; i++) {
            for (int j = 0; j < operationMatrix[i].length; j++) {
                Operation o = this.operationMatrix[i][j];
                if (o.jobNo == -1)
                    continue;
                char ch = flagString.charAt(o.jobNo);// 每一个工件对应一种字符
                for (int k = o.startTime; k < o.endTime; k++) {
                    if (sheduleMatrix[o.machineNo][k] != ch) {
                        flag = false;
                        break outer;
                    }
                }
            }
        }
        if (!flag)
            System.err.println("===========!规划错误！===========");
    }

    public Chromosome toChromosome() {
        chromosome = new Chromosome(r);
        chromosome.gene_MS = new int[problem.getTotalOperationCount()];
        chromosome.gene_OS = new int[problem.getTotalOperationCount()];

        ArrayList<Operation> state = new ArrayList<Operation>();
        for(Operation[] ops :operationMatrix)
            for(Operation o :ops)
                state.add(o);

        state.sort((o1, o2) -> ((Integer) o1.startTime).compareTo(o2.startTime));
        for(int i = 0 ; i < chromosome.gene_MS.length; i++){
            Operation o = state.get(i);
            chromosome.gene_OS[i] = o.jobNo;
            int m = 0;
            for(int j = 0; j <= o.machineNo; j++)
                if(problem.getProDesMatrix()[problem.getOperationToIndex()[o.jobNo][o.task]][j] != 0) m++;
            chromosome.gene_MS[problem.getOperationToIndex()[o.jobNo][o.task]] = m;
        }

        chromosome.fitness = 1.0 / cost;
        return chromosome;
    }

    public boolean checkSolution() {
        boolean[][] exist = new boolean[problem.getJobCount()][];

        for (int i = 0; i < operationMatrix.length; i++) {
            exist[i] = new boolean[problem.getOperationCountArr()[i]];
            Arrays.fill(exist[i], false);
            exist[operationMatrix[i][0].jobNo][operationMatrix[i][0].task] = true;

            if (operationMatrix[i][0].endTime - operationMatrix[i][0].startTime != problem.getProDesMatrix()[problem.getOperationToIndex()[i][0]][operationMatrix[i][0].machineNo]) {
                System.out.println("工序对应加工时间错误！:" + "job " + i + " task " + 0);
                System.out.println("true time:" + problem.getProDesMatrix()[problem.getOperationToIndex()[i][0]][operationMatrix[i][0].machineNo]);
                return false;
            }
            for (int j = 1; j < problem.getOperationCountArr()[i]; j++) {
                Operation o = operationMatrix[i][j];
                if (o.endTime - o.startTime != problem.getProDesMatrix()[problem.getOperationToIndex()[i][j]][o.machineNo]) {
                    System.out.println("工序对应加工时间错误！:" + "job " + i + " task " + j);
                    return false;
                }
                Operation before = operationMatrix[i][j - 1];
                if (before.endTime > o.startTime) {
                    System.out.println("同一工件对应工序顺序错误！");
                    return false;
                }
                exist[o.jobNo][o.task] = true;
            }
        }

        this.getMachineMatrix();
        for (int i = 0; i < problem.getMachineCount(); i++) {
            for (int j = 1; j < machineMatrix[i].length; j++) {
                if (machineMatrix[i][j - 1].endTime > machineMatrix[i][j].startTime) {
                    System.out.println("同一机器工序加工时间错误！");
                    return false;
                }
            }
        }

        for (int i = 0; i < operationMatrix.length; i++) {
            for (int j = 1; j < problem.getOperationCountArr()[i]; j++) {
                if (!exist[i][j]) {
                    System.out.println("存在未完成的工件！" + "job" + i + " task" +j + ";");
                    return false;
                }
            }
        }

        int longest = Integer.MIN_VALUE;
        for (Operation[] o : operationMatrix)
            if (o[o.length - 1].endTime > longest) longest = o[o.length - 1].endTime;

        if (longest != cost) {
            System.out.println("解计算错误！");
            return false;
        }

        System.out.println("该解正确且可行！");
        return true;
    }
}