package project6;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class Scheduler {
	/*
	 * Scheduler
	 * Reads jobs from file
	 * Creates minimum schedule
	 */
	
	public static void main(String[] args) {
		String dirName = "data";
		File[] files = getFilesInFolder(dirName);
		int choice = 0;
		Scanner inputScanner = new Scanner(System.in);		
		
		while (true) {			
			System.out.println("\n***********************************************");
			System.out.println("Select file number. Enter 0 to Exit.");
			printFiles(files);
			
			// TODO: catch first line
			//???while (!inputScanner.hasNextLine()) inputScanner.nextLine();

			try {
				choice = Integer.parseInt(inputScanner.nextLine());   // Get selection

				if (choice >= 1 && choice <= files.length) {
					
					long startTime = System.nanoTime();
					
					if (choice >= 1 && choice <= files.length) {
						// File, populationSize, numGenerations, mutationRate
						//scheduleJobs(files[choice - 1], 50, 500, 0.01);
						scheduleWOC(files[choice - 1], 50, 500, 0.01);
					}
					
					long stopTime = System.nanoTime();
					System.out.println("Duration:  " + (stopTime - startTime));
					
				} else if (choice == 0) break;	
			} catch (NumberFormatException e) {}	
		}

		inputScanner.close();
	}
	
	// Schedule and aggregate using Wisdom of Crowds
	private static void scheduleWOC(File file, int populationSize, int numGenerations, double mutationRate) {
		Problem problem = getProblemFromFile(file);
		int numAdded, popIdx;   // Counter for number of individuals added, and an index for adding individuals
		int numGARuns = 10, topNumIndividuals = 3;   // Number of GA's run and top number selected
		int crowdSize = numGARuns * topNumIndividuals;
		int maxRowIdx, maxColIdx, maxIdx, cityNum;
		int numElite = Math.max(1, (int) (populationSize * 0.1));
		int[][] edgeCounterMatrix = new int[problem.getNumJobs()][problem.getNumJobs()];   // Upper triangular matrix counting occurrences of job pairs
		double totalMakespan = 0, shortestMakespan = 1000000, longestMakespan = -1;   // Statistics
		Job job, nextJob = null;
		Schedule shortestSchedule = null, longestSchedule = null;
		Schedule aggregateSchedule;
		ArrayList<Job> aggregateJobs = new ArrayList<Job>();
		ArrayList<Schedule> population;
		ArrayList<Schedule> crowd = new ArrayList<Schedule>();	
			
		// Initialize path edges to 0
		for (int[] row : edgeCounterMatrix) Arrays.fill(row, 0);
		
		// Print problem and initial populations
		//System.out.println(problem);
		System.out.println("Population: " + populationSize + ", Generations: " + numGenerations 
				+ ", Elite: " + numElite + ", Mutation: " + mutationRate + " %, Crowd: " + numGARuns + "x" + topNumIndividuals);
		
		// Create crowd of topNumIndividuals out of numRuns
		/*for (int idx = 0; idx < numGARuns; idx++) {
			numAdded = 1;   // 1 for index 0 that is always added
			popIdx = 1;
			
			population = scheduleJobsGA(problem, populationSize, numGenerations, mutationRate);

			crowd.add(population.get(0));   // Add best individual of population
			totalMakespan += population.get(0).getMakespan();               //
			if (population.get(0).getMakespan() < shortestMakespan) {       // 
				shortestMakespan = population.get(0).getMakespan();         //  
				shortestSchedule = population.get(0);  						// Crowd
			} else if (population.get(0).getMakespan() > longestMakespan) { //  Statistics
				longestMakespan = population.get(0).getMakespan();          //
				longestSchedule = population.get(0);  						//
			}
			
			// Continue adding topNumIndividuals most fit UNIQUE individuals to crowd
			while (numAdded < topNumIndividuals && popIdx < problem.getNumJobs()) {
				if (population.get(popIdx++).getMakespan() == crowd.get(numAdded - 1).getMakespan()) continue;   // check if distance is same as last
				crowd.add(population.get(popIdx - 1));   // add unique individual
				totalMakespan += population.get(popIdx - 1).getMakespan();			      //
				if (population.get(popIdx - 1).getMakespan() < shortestMakespan) {	      // 
					shortestMakespan = population.get(popIdx - 1).getMakespan(); 	      //
					shortestSchedule = population.get(popIdx - 1);						  // Crowd
				} else if (population.get(popIdx - 1).getMakespan() > longestMakespan) {  //  Statistics
					longestMakespan = population.get(popIdx - 1).getMakespan();           //
					longestSchedule = population.get(popIdx - 1);  					  	  //
				}
				numAdded++;
			}
			if (numAdded != topNumIndividuals) {   // If topNumIndividuals unique individuals not found, duplicate most fit individual
				for (int i = 0; i < (topNumIndividuals - numAdded); i++) {
					crowd.add(crowd.get(0));
					totalMakespan += crowd.get(0).getMakespan();   // Crowd Statistics
				}
			}
		}
		
		// Set edge counter matrix
		for (Schedule schedule : crowd) {
			for (int idx = 1; idx < problem.getNumJobs(); idx++) {
				int smallIdx = Math.min(schedule.getJob(idx - 1).getNumber() - 1, schedule.getJob(idx).getNumber() - 1);
				int largeIdx = Math.max(schedule.getJob(idx - 1).getNumber() - 1, schedule.getJob(idx).getNumber() - 1);
				edgeCounterMatrix[smallIdx][largeIdx]++;
			}
		}
		//printMatrix(edgeCounterMatrix);

		// Aggregate edges into new array using matrix
		aggregateJobs.add(shortestSchedule.getJob(0));
		
		for (int jobIdx = 1; jobIdx < problem.getNumJobs(); jobIdx++) {
			job = aggregateJobs.get(aggregateJobs.size() - 1);
			cityNum = job.getNumber();
			nextJob = null;
			maxRowIdx = cityNum - 1; maxColIdx = cityNum - 1; maxIdx = -1;
			
			// Find most edges in row (iterate columns)
			for (int idx = cityNum; idx < problem.getNumJobs(); idx++) {
				if (aggregateJobs.contains(problem.getJobs().get(idx + 1))) continue;
				if (edgeCounterMatrix[cityNum - 1][idx] > edgeCounterMatrix[maxRowIdx][maxColIdx]) {
					// Found edge used more times
					maxColIdx = idx;
					maxIdx = idx;
				} else if (edgeCounterMatrix[cityNum - 1][idx] == edgeCounterMatrix[maxRowIdx][maxColIdx]) {
					// Found edge used the same amount - choose shortest
					
					// TODO
					
					//if (getCityDistance(job, cityList[idx]) < getCityDistance(job, cityList[maxColIdx])) {
					//	maxColIdx = idx;
					//	maxIdx = idx;
					//}
				}
			}
			
			// Find most edges in column (iterate rows)
			for (int idx = 0; idx < cityNum - 1; idx++) {
				if (aggregateJobs.contains(problem.getJobs().get(idx + 1))) continue;
				if (edgeCounterMatrix[idx][cityNum - 1] > edgeCounterMatrix[maxRowIdx][maxColIdx]) {
					// Found edge used more times
					maxRowIdx = idx;
					maxColIdx = cityNum - 1;
					maxIdx = idx;
				} else if (edgeCounterMatrix[idx][cityNum - 1] == edgeCounterMatrix[maxRowIdx][maxColIdx]) {
					// Found edge used the same amount - choose shortest
					
					// TODO
					
					//if (getCityDistance(job, cityList[idx]) < getCityDistance(job, cityList[maxRowIdx])) {
					//	maxColIdx = idx;
					//	maxIdx = idx;
					//}
				}
			}
				
			// For some reason no city found, create new edge
			if (maxIdx < 0) {
				for (Job possibleJob : problem.getJobs()) {
					if (aggregateJobs.contains(possibleJob)) continue;
					if (nextJob == null) {
						nextJob = possibleJob;
						continue;
					}
					
					// TODO
					
					//if (getCityDistance(possibleJob, job) < getCityDistance(nextJob, job)) nextJob = possibleJob;
				}
				
				//System.out.println("\nCreating edge " + city + " to " + nextCity);   // BAD!
				aggregateJobs.add(nextJob);
				continue;
			}
			
			aggregateJobs.add(problem.getJobs().get(maxIdx));
		}*/
		
		for (int cIdx = 0; cIdx < numGARuns; cIdx++) {
			population = scheduleJobsGA(problem, populationSize, numGenerations, mutationRate, numElite);
			
			System.out.println("Pop at cIdx " + cIdx + ": " + population.get(0));
		}
		
		//double mean = (totalMakespan / crowdSize);
		//double std = 0;
		//for (Schedule s : crowd) { std += Math.pow(s.getMakespan() - mean, 2); }
		//std = Math.pow(std / populationSize, .5);
		
		//aggregateSchedule = new Schedule(aggregateJobs, problem.getNumMachines());
		
		//aggregateSchedule.printAssignments();
		
		//double[] results = new double[5];
		//results[0] = shortestSchedule.getMakespan(); results[1] = mean; results[2] = (longestSchedule == null ? -1 : longestSchedule.getMakespan()); results[3] = std; results[4] = aggregateSchedule.getMakespan();

		//return results;
	}
	
	// Schedule jobs using a Genetic Algorithm
	private static ArrayList<Schedule> scheduleJobsGA(Problem problem, int populationSize, int numGenerations, double mutationRate, int numElite) {
		int parent1Idx, parent2Idx;
		double roll;
		double[] rouletteWheel;
		Random rand = new Random();
		ArrayList<Schedule> population = new ArrayList<Schedule>();
		ArrayList<Schedule> nextGenPopulation = new ArrayList<Schedule>(populationSize);
		
		if (populationSize % 2 == 1) throw new RuntimeException("\nPopulation Size must be even");
		
		if (numElite % 2 == 1) numElite++; // Ensure numElite is even
		
		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule(problem);   // Automatically randomizes
			population.add(schedule);
		}
		
//int totalMakespan;Collections.sort(population, new Comparator<Schedule>() {public int compare(Schedule s1, Schedule s2) {if (s1.getMakespan() > s2.getMakespan()) return 1; if (s1.getMakespan() < s2.getMakespan()) return -1;return 0;}});	
//totalMakespan = 0; for (Schedule s : population) {totalMakespan += s.getMakespan();}
//System.out.println("Initial Best: " + population.get(0).getMakespan() + ",  avg: " + ((float)totalMakespan / populationSize));
		
		nextGenPopulation = population;
		
		// Run Genetic Algorithm
		for (int genIdx = 0; genIdx < numGenerations; genIdx++) {
			//System.out.println("## Generation " + genIdx + " ###");
			
			// Fill wheel
			rouletteWheel = getRouletteWheel(population, numElite);

			//if (genIdx == 1 || genIdx % 10 == 0) {
				//System.out.print("G="+genIdx+" Best: " + population.get(0).getMakespan());
			//System.out.print("G" + genIdx + ":  ");
			//for (Schedule s : population) System.out.print(s.getMakespan() + ", ");
			//	totalMakespan = 0; for (Schedule s : population) {/*System.out.println("  makespan: " + s.getMakespan() + ", " + s);*/ totalMakespan += s.getMakespan();}
			//	System.out.println(",  avg: " + ((float)totalMakespan / populationSize));
				//System.out.println("  wheel: " + Arrays.toString(rouletteWheel));	
			//}
			
			//Add elite individuals
			for (int idx = 0; idx < numElite; idx++) {
				nextGenPopulation.set(idx, population.get(idx));
			}
			
			// Create new generation using crossover and mutation
			for (int childIdx = numElite; childIdx < populationSize - 1; childIdx += 2) {
				parent1Idx = parent2Idx = 0;
				
				// Choose first parent
				roll = 100 * rand.nextDouble();
				do {
					if (roll <= rouletteWheel[parent1Idx]) break;
					parent1Idx++;
				} while(parent1Idx < populationSize);
				
				// Choose second parent
				roll = 100 * rand.nextDouble();
				do {
					if (roll <= rouletteWheel[parent2Idx]) break;
					parent2Idx++;
				} while(parent2Idx < populationSize);
				if (parent1Idx == parent2Idx)   // Choose the next one if same
					parent2Idx = (parent2Idx >= populationSize - 1) ? 0 : parent2Idx + 1;

				// Cross parents into new child
				crossoverSBOX(nextGenPopulation, childIdx, population.get(parent1Idx), population.get(parent2Idx), mutationRate, problem.getNumMachines());
			}
			
			population = nextGenPopulation;   // Replace old generation with new
			
			// Sort population
			Collections.sort(population, new Comparator<Schedule>() {
				public int compare(Schedule s1, Schedule s2) {
					if (s1.getMakespan() > s2.getMakespan()) return 1; 
					if (s1.getMakespan() < s2.getMakespan()) return -1;
					return 0;
				}
			});	
		}
		
		//System.out.println("Best: " + population.get(0).getMakespan() + "  " + population.get(0));
		//population.get(0).printAssignments();
		//totalMakespan = 0; for (Schedule s : population) {totalMakespan += s.getMakespan();}
		//System.out.println("  avg: " + ((float)totalMakespan / populationSize));
		
		return population;
	}
	
	// Crossover both parents and add offspring to population
	// SBOX - blocks of at least two jobs are auto carried over to children, remaining slots are added from corresponding parent
	//			until a stop point is met, then rest is filled from opposite parent
	public static void crossoverSBOX(ArrayList<Schedule> population, int startIdx, Schedule parent1, Schedule parent2, double mutationRate, int numMachines) {
		Random rand = new Random();
		int parent1Idx = 0, parent2Idx = 0;
		int swapIdx;
		int cutIdx = rand.nextInt(parent1.getNumJobs() - 2) + 2;
		double mutate;
		boolean previousMatch = false;
		boolean firstMatch = false;
		Job tempJob;
		ArrayList<Job> child1Jobs = new ArrayList<Job>();
		ArrayList<Job> child2Jobs = new ArrayList<Job>();
		Schedule child1, child2;
		
//System.out.print("   P1=" + parent1.getMakespan() + ", P2=" + parent2.getMakespan());
		
		// Initialize children
		for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
			child1Jobs.add(null);
			child2Jobs.add(null);
		}
		
//System.out.println("P1 (" + parent1.getNumJobs() + "): " + parent1);
//System.out.println("P2 (" + parent2.getNumJobs() + "): " + parent2);
//System.out.print("  setting child index: ");

		// Set matching pairs from the parent in the children
		for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
			// Jobs at this index don't match
			if (!parent1.getJob(idx).equals(parent2.getJob(idx))) {
				previousMatch = firstMatch = false;
				continue;
			}
			
			// Jobs match
			if (previousMatch) {
				if (firstMatch) {
					child1Jobs.set(idx - 1, parent1.getJob(idx - 1));
					child2Jobs.set(idx - 1, parent2.getJob(idx - 1));
					firstMatch = false;
//System.out.print(idx - 1 + ", ");
				}
				
				child1Jobs.set(idx, parent1.getJob(idx));
				child2Jobs.set(idx, parent2.getJob(idx));
//System.out.print(idx + ", ");
			} else {
				previousMatch = true;
				firstMatch = true;
			}
		}
		
/*System.out.print("   Cut Idx = " + cutIdx);
System.out.print("\n   P1 (" + parent1.getNumJobs() + "): [");
for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
	System.out.print(parent1.getJob(idx) + ", ");
}
System.out.print("\n   P2 (" + parent2.getNumJobs() + "): [");
for (int idx = 0; idx < parent2.getNumJobs(); idx++) {
	System.out.print(parent2.getJob(idx) + ", ");
} System.out.print("\n");*/

		// Set jobs from corresponding parent until cut index
		for (int idx = 0; idx < cutIdx; idx++) {
			if (child1Jobs.get(idx) == null) child1Jobs.set(idx, parent1.getJob(idx));
			if (child2Jobs.get(idx) == null) child2Jobs.set(idx, parent2.getJob(idx));
		}
		
		// Set remaining jobs from other parent
		for (int idx = cutIdx; idx < parent1.getNumJobs(); idx++) {
			if (child1Jobs.get(idx) == null) {
				while ( child1Jobs.contains(parent2.getJob(parent2Idx)) ) parent2Idx++;
				child1Jobs.set(idx, parent2.getJob(parent2Idx));
				parent2Idx++;
			}
			if (child2Jobs.get(idx) == null) {
				while ( child2Jobs.contains(parent1.getJob(parent1Idx)) ) parent1Idx++;
				child2Jobs.set(idx, parent1.getJob(parent1Idx));
				parent1Idx++;
			}
		}
		
/*System.out.print("   C1 (" + child1Jobs.size() + "): [");
for (int idx = 0; idx < child1Jobs.size(); idx++) {
	System.out.print(child1Jobs.get(idx) == null ? "~~, " : (child1Jobs.get(idx) + ", "));
}
System.out.print("\n   C2 (" + child2Jobs.size() + "): [");
for (int idx = 0; idx < child2Jobs.size(); idx++) {
	System.out.print(child2Jobs.get(idx) == null ? "~~, " : (child2Jobs.get(idx) + ", "));
} System.out.print("\n");*/

		// Mutate children by swapping jobs
		for (int idx = 0; idx < child1Jobs.size(); idx++) {
			mutate = rand.nextDouble();
			if (mutate < mutationRate) { 
				swapIdx = rand.nextInt(child1Jobs.size() - 2) + 1;
				tempJob = child1Jobs.get(swapIdx);
				child1Jobs.set(swapIdx, child1Jobs.get(idx));
				child1Jobs.set(idx, tempJob);
			}
		}
		for (int idx = 0; idx < child2Jobs.size(); idx++) {
			mutate = rand.nextDouble();
			if (mutate < mutationRate) { 
				swapIdx = rand.nextInt(child2Jobs.size() - 2) + 1;
				tempJob = child2Jobs.get(swapIdx);
				child2Jobs.set(swapIdx, child2Jobs.get(idx));
				child2Jobs.set(idx, tempJob);
			}
		}
		
		// Create schedules from job list
		child1 = new Schedule(child1Jobs, numMachines);
		child2 = new Schedule(child2Jobs, numMachines);
		
//System.out.print("   C1=" + child1.getMakespan() + ", C2=" + child2.getMakespan() + "\n");
		
		// Set population
		population.set(startIdx, child1);
		population.set(startIdx + 1, child2);
	}
	
	// Return weighted list, preferring the lowest time
	public static double[] getRouletteWheel(ArrayList<Schedule> population, int numElite) {
		int size = population.size() - numElite;
        double runningTotal = 0, total = 0;
		double[] wheel = new double[size];
        double[] timeInverses = new double[size];

        // Set inverse array
        for (int idx = 0; idx < size; idx++) {
        	timeInverses[idx] = 1.0 / (double) population.get(idx).getMakespan();
        	total += timeInverses[idx];
        }
        
        // Set running total of inverses and assign weighted probability of selection
        for (int idx = 0; idx < size; idx++) {
        	runningTotal += timeInverses[idx];
        	wheel[idx] = 100 * runningTotal / total;
        }
        
        return wheel;
	}
	
	// Return a list of jobs from file
	public static Problem getProblemFromFile(File file) {
		Scanner scanner = null;
		ArrayList<Job> jobs = new ArrayList<Job>(); 
		String[] lineSplit;
		int numMachines = 0, jobNumber, arrivalTime;
		int[] processTimes = null;

		try {
			scanner = new Scanner(file);
			
			// Get number of machines
			if (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != 2) 
					throw new RuntimeException("\nError parsing file - Machine line does not have exactly two elements");
				numMachines = Integer.parseInt(lineSplit[1]);
			}
			
			processTimes = new int[numMachines];
			
			// Get jobs
			while (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != (3 + numMachines)) 
					throw new RuntimeException("\nError parsing file - Job line does not have 3 + numMachines elements");
				jobNumber = Integer.parseInt(lineSplit[1]);
				arrivalTime = Integer.parseInt(lineSplit[2]);
				for (int mIdx = 0; mIdx < numMachines; mIdx++) {
					processTimes[mIdx] = Integer.parseInt(lineSplit[mIdx + 3]);
				}
				jobs.add(new Job(jobNumber, arrivalTime, processTimes.clone()));
			}
		} catch (FileNotFoundException | NumberFormatException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		return new Problem(numMachines, jobs);
	}
	
	// Return all text files in folder
	public static File[] getFilesInFolder(String dirName) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		ArrayList<File> fileArray = new  ArrayList<File>();
		File[] sortedFileList;
		
		for (File file : files) {
			if (file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".txt")) {
				fileArray.add(file);
			}
		}
		
		// Sort files by path/name
		sortedFileList = fileArray.toArray(new File[fileArray.size()]);
		Arrays.sort(sortedFileList, (f1, f2) -> {
			return f1.compareTo(f2);
		});
		  
		return sortedFileList;
	}
	
	// Print file names from a list of files
	public static void printFiles(File[] fileList) {
		for (int i = 1; i <= fileList.length; i++) {
			System.out.println("File " + String.format("%2d:", i) + "\t" + fileList[i-1].getName());
		}
	}

}
