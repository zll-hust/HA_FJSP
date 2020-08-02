package com.zll.FJSP.GA;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.zll.FJSP.Data.Problem;
import com.zll.FJSP.Data.Operation;

/**
 * Cette classe met en place l'algorithme de recherche local Tabu search.
 * 
 * @author Tehema
 *
 */
public class TabuSearch1 {

	private LinkedList<Chromosome> tabuList;
	private Chromosome currentSolution;
	private Problem input;
	private Operation[][] operationMatrix;
	private Random r;

	public TabuSearch1(Problem input, Random r) {
		this.input = input;

		this.operationMatrix = new Operation[input.getJobCount()][];
		for (int i = 0; i < operationMatrix.length; i++) {
			operationMatrix[i] = new Operation[input.getOperationCountArr()[i]];
			for (int j = 0; j < operationMatrix[i].length; j++)
				operationMatrix[i][j] = new Operation();
		}

		this.r = r;
	}

	public Chromosome TabuSearch(int maxTSIterSize, int TSLength, Chromosome chromosome, double bestFitness) {
		int TSIter = 0;

		tabuList = new LinkedList<>();
		currentSolution = chromosome;

		while (TSIter < maxTSIterSize) {

			LinkedList<Chromosome> neighborhood = neighborhoodSolution(currentSolution);
			Collections.sort(neighborhood, Collections.reverseOrder());
			
			for(int i = 0; i < neighborhood.size(); i++){
				if(currentSolution.fitness > bestFitness || !tabuList.contains(currentSolution)) {
					currentSolution = neighborhood.get(i);
				}
			}

			if (tabuList.size() > TSLength)
				tabuList.removeFirst();
			tabuList.add(currentSolution);
			
			TSIter++;
		}

		return currentSolution;
	}

	private LinkedList<Chromosome> neighborhoodSolution(Chromosome chromosome) {
		LinkedList<Chromosome> neighborhood = new LinkedList<>();
		LinkedList<int[]> OSs = new LinkedList<>();
		LinkedList<int[]> MSs = new LinkedList<>();
		CaculateFitness c = new CaculateFitness();
		ChromosomeOperation chromOps = new ChromosomeOperation(r, input);
		
		for (int i = 0; i < chromosome.gene_OS.length - 1; i += 2) {
			for (int j = i + 1; j < chromosome.gene_OS.length; j += 2) {
				if(r.nextDouble() < 0.5)
					OSs.add(swap(chromosome.gene_OS, i, j));
			}
		}

		for (int i = 0; i < chromosome.gene_MS.length; i++) {
			if(r.nextDouble() < 0.5){
				int[] MS = chromosome.gene_MS.clone();
				MSs.add(chromOps.machineSeqMutation(MS));
			}
		}

		for (int[] OS : OSs) {
			Chromosome newChrom = new Chromosome(OS, chromosome.gene_MS.clone(), chromosome.r);
			newChrom.fitness = 1.0 / c.evaluate(newChrom, input, operationMatrix);
			neighborhood.add(newChrom);
		}

		for (int[] MS : MSs) {
			Chromosome newChrom = new Chromosome(chromosome.gene_OS.clone(), MS, chromosome.r);
			newChrom.fitness = 1.0 / c.evaluate(newChrom, input, operationMatrix);
			neighborhood.add(newChrom);
		}

		return neighborhood;
	}

	private static int[] swap(int[] OS, int i, int j) {
		int[] ret = OS.clone();
		ret[i] = OS[j];
		ret[j] = OS[i];
		return ret;
	}

}
