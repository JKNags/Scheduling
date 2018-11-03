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
			
			choice = inputScanner.nextInt();   // Get selection
			
			if (choice >= 1 && choice <= files.length) {
				
				long startTime = System.nanoTime();
				
				if (choice >= 1 && choice <= files.length) {
					// File, populationSize, numGenerations, mutationRate
					scheduleJobs(files[choice - 1], 100, 500, 0.05);
				}
				
				long stopTime = System.nanoTime();
				System.out.println("Duration:  " + (stopTime - startTime));
				
			} else if (choice == 0) break;
		}

		inputScanner.close();
	}
	
	// Schedule jobs using a Genetic Algorithm
	private static void scheduleJobs(File file, int populationSize, int numGenerations, double mutationRate) {
		Problem problem = getProblemFromFile(file);
		int parent1Idx, parent2Idx;
		int numElite = Math.max(1, (int) (populationSize * 0.1));
		double roll;
		double[] rouletteWheel;
		Random rand = new Random();
		ArrayList<Schedule> population = new ArrayList<Schedule>();
		ArrayList<Schedule> nextGenPopulation = new ArrayList<Schedule>(populationSize);
		
		if (populationSize % 2 == 1) throw new RuntimeException("\nPopulation Size must be even");
		
		int totalMakespan;
		
		// Print problem and initial populations
		System.out.println(problem);
		System.out.println("Population: " + populationSize + ", Generations: " + numGenerations + ", Elite: " + numElite);
		
		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule(problem);   // Automatically randomizes
			population.add(schedule);
		}
		
		nextGenPopulation = population;
		
		// Run Genetic Algorithm
		for (int genIdx = 0; genIdx < numGenerations; genIdx++) {
			//System.out.println("## Generation " + genIdx + " ###");
			
			// Fill wheel
			rouletteWheel = getRouletteWheel(population, numElite);

			if (genIdx == 1 || genIdx % 10 == 0) {
				System.out.print("G=1 Best: " + population.get(0).getMakespan());
				totalMakespan = 0; for (Schedule s : population) {/*System.out.println("  makespan: " + s.getMakespan() + ", " + s);*/ totalMakespan += s.getMakespan();}
				System.out.println(",  avg: " + ((float)totalMakespan / populationSize));
				//System.out.println("  wheel: " + Arrays.toString(rouletteWheel));	
			}
			
			//Add elite individuals
			for (int idx = 0; idx < numElite; idx++) {
				nextGenPopulation.set(idx, population.get(idx));
			}
			
			// Create new generation using crossover and mutation
			for (int childIdx = numElite; childIdx < populationSize / 2; childIdx++) {
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
		
		System.out.println("Best: " + population.get(0).getMakespan() + "  " + population.get(0));
		population.get(0).printAssignments();
		totalMakespan = 0; for (Schedule s : population) {/*System.out.println("  makespan: " + s.getMakespan() + ", " + s);*/ totalMakespan += s.getMakespan();}
		System.out.println("  avg: " + ((float)totalMakespan / populationSize));
	}
	
	// Crossover both parents and add offspring to population
	// SBOX - blocks of at least two jobs are auto carried over to children, remaining slots are added from corresponding parent
	//			until a stop point is met, then rest is filled from opposite parent
	public static void crossoverSBOX(ArrayList<Schedule> population, int index, Schedule parent1, Schedule parent2, double mutationRate, int numMachines) {
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
		
		// Initialize children
		for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
			child1Jobs.add(null);
			child2Jobs.add(null);
		}
		
//System.out.println("P1 (" + parent1.getNumJobs() + "): " + parent1);
//System.out.println("P2 (" + parent2.getNumJobs() + "): " + parent2);
//System.out.print("setting child index: ");

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
System.out.print("\nC1 (" + child1Jobs.size() + "): [");
for (int idx = 0; idx < child1Jobs.size(); idx++) {
	System.out.print(child1Jobs.get(idx) == null ? "~~, " : (child1Jobs.get(idx) + ", "));
}
System.out.print("\nC2 (" + child2Jobs.size() + "): [");
for (int idx = 0; idx < child2Jobs.size(); idx++) {
	System.out.print(child2Jobs.get(idx) == null ? "~~, " : (child2Jobs.get(idx) + ", "));
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
/*System.out.print("C1 (" + child1Jobs.size() + "): [");
for (int idx = 0; idx < child1Jobs.size(); idx++) {
	System.out.print(child1Jobs.get(idx) == null ? "~~, " : (child1Jobs.get(idx) + ", "));
}
System.out.print("\nC2 (" + child2Jobs.size() + "): [");
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
		for (int idx = 0; idx < child1Jobs.size(); idx++) {
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
		
		// Set population
		population.set((index * 2), child1);
		population.set((index * 2) + 1, child2);
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
