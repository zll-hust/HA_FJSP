package com.zll.FJSP.GA;

import java.util.ArrayList;
import java.util.Arrays;

import com.zll.FJSP.Data.Problem;
import com.zll.FJSP.Data.Operation;

public class CaculateFitness {

	/**
	 * @param MyProblem the problem description which has been arranged
	 * @return int[2] machineNoAndTimeArr machine index and time cost
	 */
	public static int[] getMachineNoAndTime(Problem input, int MS[], int jobNo, int operationNo) {
		int[][] proDesMatrix = input.getProDesMatrix();
		int operationToIndex[][] = input.getOperationToIndex();
		int tempCount = 0;
		int totaloperNo = operationToIndex[jobNo][operationNo];// 工序编号
		int machineTimeArr[] = proDesMatrix[totaloperNo];// 工序在备选机器上的加工时间
		int index = 0;
		int count = MS[totaloperNo];// 工序对应的机器编号（备选机器）

		while (tempCount < count) {
			if (machineTimeArr[index] != 0)// 如果对应加工时间为0，代表无法加工，跳过
				tempCount++;
			index++;
		}
		index--;
		
		int[] machineNoAndTimeArr = new int[2];
		machineNoAndTimeArr[0] = index;

//		if(index == -1){
//			System.out.println("totaloperNo " + totaloperNo + " index " + index);
//			for(int a: MS)
//				System.out.print(a + " ");
//			System.out.println();
//		}

		machineNoAndTimeArr[1] = proDesMatrix[totaloperNo][index];
		return machineNoAndTimeArr;
	}

	/**
	 * @param operationMatrix the operation description of the scheduling problem
	 */
	public static void initOperationMatrix(Operation[][] operationMatrix) {
		int i = 0, j = 0;
		for (i = 0; i < operationMatrix.length; i++) {
			for (j = 0; j < operationMatrix[i].length; j++)
				operationMatrix[i][j].initOperation();
		}
	}

	public class Time {
		int start;
		int end;
		int type;// 0为工作,1为空闲。

		Time(int s, int e, int t) {
			this.start = s;
			this.end = e;
			this.type = t;
		}
	}

	/**
	 * 计算一条染色体（一个可行的调度）所耗费的最大时间
	 * 
	 * @param dna    the dna array,an element represents a procedure of a job
	 * @param length the DNA array length
	 * @param input  the time and order information of the problem
	 * @return the fitness of a sheduling
	 */
	public int evaluate(Chromosome chromosome, Problem input, Operation[][] operationMatrix) {
		int jobCount = input.getJobCount();
		int machineCount = input.getMachineCount();
		initOperationMatrix(operationMatrix);

		int[] operNoOfEachJob = new int[jobCount];// 当前处理到工件的工序No
		Arrays.fill(operNoOfEachJob, 0);

		ArrayList<Time> machTimes[] = new ArrayList[machineCount];// 机器的时间段
		for (int i = 0; i < machineCount; i++) {
			machTimes[i] = new ArrayList<>();
			machTimes[i].add(new Time(0, Integer.MAX_VALUE, 0));
		}

		int jobNo = 0;
		int operNo = 0;
		int operationTime = 0;
		int machineNo = 0;
		int machineNoAndTimeArr[] = new int[2];

		for (int i = 0; i < chromosome.gene_OS.length; i++) {
			jobNo = chromosome.gene_OS[i];// 工件名
			operNo = operNoOfEachJob[jobNo]++;// 当前工件操作所在的工序数
			
			//找到这道工序对应的机器编号以及加工时间
			machineNoAndTimeArr = getMachineNoAndTime(input, chromosome.gene_MS, jobNo, operNo);
			machineNo = machineNoAndTimeArr[0];
			operationTime = machineNoAndTimeArr[1];

//			System.out.println("i=" + i + ",JobNo " + jobNo + ",OperNo " + operNo + ",machineNo " + machineNo
//					+ ",operationTime" + operationTime);

			// 如果是第一个，允许最早开始时间为0
			if (operNo == 0) {
				operationMatrix[jobNo][operNo].aStartTime = 0;
			} else {
				operationMatrix[jobNo][operNo].aStartTime = operationMatrix[jobNo][operNo - 1].endTime;
			}
			operationMatrix[jobNo][operNo].machineNo = machineNo;
			operationMatrix[jobNo][operNo].jobNo = jobNo;
			operationMatrix[jobNo][operNo].task = operNo;
			
			for (int j = 0; j < machTimes[machineNo].size(); j++) {
				int start = Math.max(operationMatrix[jobNo][operNo].aStartTime, machTimes[machineNo].get(j).start);
				int end = start + operationTime;
				// 对机器空闲的时间段，若可以加工，则加工，否则判断下一个空闲时间段
				if (machTimes[machineNo].get(j).type == 0 && end <= machTimes[machineNo].get(j).end) {
					// 设置工序开始结束时间
					operationMatrix[jobNo][operNo].startTime = start;
					operationMatrix[jobNo][operNo].endTime = end;
					// 更新机器时间段
					ArrayList<Time> t = new ArrayList<>();
					if (operationMatrix[jobNo][operNo].aStartTime > machTimes[machineNo].get(j).start) {
						t.add(new Time(machTimes[machineNo].get(j).start, operationMatrix[jobNo][operNo].aStartTime, 0));
						t.add(new Time(operationMatrix[jobNo][operNo].aStartTime, end, 1));
					} else {
						t.add(new Time(machTimes[machineNo].get(j).start, end, 1));
					}
					if (end < machTimes[machineNo].get(j).end) {
						t.add(new Time(end, machTimes[machineNo].get(j).end, 0));
					}
					machTimes[machineNo].remove(j);
					machTimes[machineNo].addAll(j, t);
					
				
//					System.out.println("startTime "+operationMatrix[jobNo][operNo].startTime+
//							",endTime "+operationMatrix[jobNo][operNo].endTime);
					
					break;
				}
			}

		}
		
		int longestTime = 0;
		for(int i = 0; i < machineCount; i++)
			longestTime = Math.max(machTimes[i].get(machTimes[i].size() - 1).start , longestTime);
		
		return longestTime;
	}

	/**
	 * 计算一条染色体（一个可行的调度）所耗费的最大时间
	 * 
	 * @param dna    the dna array,an element represents a procedure of a job
	 * @param length the DNA array length
	 * @param input  the time and order information of the problem
	 * @return the fitness of a sheduling
	 */
	public static int evaluate1(Chromosome chromosome, Problem input, Operation[][] operationMatrix) {
		int jobCount = input.getJobCount();
		int machineCount = input.getMachineCount();
		initOperationMatrix(operationMatrix);

		int span = -1;
		int[] operNoOfEachJob = new int[jobCount];// 当前处理到工件的工序No
		Arrays.fill(operNoOfEachJob, 0);

		int[] machFreeTime = new int[machineCount];// 机器最早空闲时间
		Arrays.fill(machFreeTime, 0);

		int jobNo = 0;
		int operNo = 0;
		int operationTime = 0;
		int machineNo = 0;
		int machineNoAndTimeArr[] = new int[2];

		for (int i = 0; i < chromosome.gene_OS.length; i++) {
			jobNo = chromosome.gene_OS[i];// 工件名
			operNo = operNoOfEachJob[jobNo]++;// 当前工件操作所在的工序数

			machineNoAndTimeArr = getMachineNoAndTime(input, chromosome.gene_MS, jobNo, operNo);
			machineNo = machineNoAndTimeArr[0];
			operationTime = machineNoAndTimeArr[1];

//			System.out.println("i=" + i + ",JobNo " + jobNo + ",OperNo " + operNo + ",machineNo " + machineNo
//					+ ",operationTime" + operationTime);

			if (operNo == 0) {
				// 如果是第一个，开始时间
				operationMatrix[jobNo][operNo].jobNo = jobNo;
				operationMatrix[jobNo][operNo].machineNo = machineNo;
				operationMatrix[jobNo][operNo].task = operNo;
				operationMatrix[jobNo][operNo].startTime = machFreeTime[machineNo];
				operationMatrix[jobNo][operNo].endTime = operationMatrix[jobNo][operNo].startTime + operationTime;
			} else {
				operationMatrix[jobNo][operNo].jobNo = jobNo;
				operationMatrix[jobNo][operNo].machineNo = machineNo;
				operationMatrix[jobNo][operNo].task = operNo;
				operationMatrix[jobNo][operNo].startTime = Math.max(operationMatrix[jobNo][operNo - 1].endTime,
						machFreeTime[machineNo]);
				operationMatrix[jobNo][operNo].endTime = operationMatrix[jobNo][operNo].startTime + operationTime;
			}

			machFreeTime[machineNo] = operationMatrix[jobNo][operNo].endTime;
			if (operationMatrix[jobNo][operNo].endTime > span) {
				span = operationMatrix[jobNo][operNo].endTime;
			}
		}

		return span;
	}

}
