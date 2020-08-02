package com.zll.FJSP.Data;
/**
 * Description:作业车间车间问题中的所需参数
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年5月28日 下午2:29:10
 */
public class Problem {
	//Problem describe
	private int[] machineCountArr;// how many machines can choose for every operation
	private int[] operationCountArr;// how many operations for every job
	private int proDesMatrix[][];// the machine no and time for every operation 第i道工序在第j台机器上的时间
	private int machineCount; // total machine count
	private int jobCount; // total job count
	private int maxOperationCount = 0;// the max operation count for the whole job
	private int totalOperationCount = 0;// the total operation count for the whole job
	private int[][] operationToIndex;// the index of some operation of some job 第i个工件第j道工序对应的index

	public int[] getMachineCountArr() {
		return machineCountArr;
	}

	public void setMachineCountArr(int[] machineCountArr) {
		this.machineCountArr = machineCountArr;
	}

//	public Random getRandom()
//	{
//		return random;
//	}

	public int[] getOperationCountArr() {
		return operationCountArr;
	}

	public void setOperationCountArr(int[] operationCountArr) {
		this.operationCountArr = operationCountArr;
	}

	public int[][] getOperationToIndex() {
		return operationToIndex;
	}

	public void setOperationToIndex(int[][] operationToIndex) {
		this.operationToIndex = operationToIndex;
	}

	public int getMaxOperationCount() {
		return maxOperationCount;
	}

	public void setMaxOperationCount(int maxOperationCount) {
		this.maxOperationCount = maxOperationCount;
	}

	public int[][] getProDesMatrix() {
		return proDesMatrix;
	}

	public void setProDesMatrix(int[][] proDesMatrix) {
		this.proDesMatrix = proDesMatrix;
	}

	public int getMachineCount() {
		return machineCount;
	}

	public void setMachineCount(int machineCount) {
		this.machineCount = machineCount;
	}

	public int getJobCount() {
		return jobCount;
	}

	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}

	public int getTotalOperationCount() {
		return totalOperationCount;
	}

	public void setTotalOperationCount(int totalOperationCount) {
		this.totalOperationCount = totalOperationCount;
	}
}
