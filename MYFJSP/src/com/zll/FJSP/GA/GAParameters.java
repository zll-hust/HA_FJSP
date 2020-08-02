package com.zll.FJSP.GA;
/**
* Description:遗传算法所需参数
* @author zll-hust E-mail:zh20010728@126.com
* @date 创建时间：2020年5月29日 下午2:40:11
*/
public class GAParameters {
	//GA parameter
	private double crossoverRate = 0.8;// there are 90 percent posibility for an individual to crossover
	private double mutationRate = 0.10;// there are 5 percent posibility for an individual to crossover
	private int loopCount = 500;// to caculate 5 times,one time for 200 crossover or mutation
	private int populationCount = 400;// there are 15 individuals for a generation
	private int iteratorCount = 200;// iterator for 200 time for each loop
	private int maxNoImprove = 20;
	private int timeLimit = -1;// no time limit
	private double gsRate = 0.6;// global search rate
	private double lsRate = 0.3;// local search rate
	private double rsRate = 0.1;// random search rate
	
	public int getLoopCount() {
		return loopCount;
	}

	public double getGsRate() {
		return gsRate;
	}

	public double getLsRate() {
		return lsRate;
	}

	public double getRsRate() {
		return rsRate;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getPopulationCount() {
		return populationCount;
	}

	public int getIteratorCount() {
		return iteratorCount;
	}

	// Random random=new Random();
	public double getCrossoverRate() {
		return crossoverRate;
	}

	public double getMutationRate() {
		return mutationRate;
	}

}