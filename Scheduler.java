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
					scheduleJobs(files[choice - 1], 10, 1, 0.1);
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
		Schedule child;
		ArrayList<Schedule> population = new ArrayList<Schedule>();
		ArrayList<Schedule> nextGenPopulation = new ArrayList<Schedule>(populationSize);
		
		// Print problem and initial populations
		System.out.println(problem);
		System.out.println("Population: " + populationSize + ", Generations: " + numGenerations + ", Elite: " + numElite);
		
		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule(problem.getNumMachines());
			schedule.randomize(problem);
			population.add(schedule);
		}
		
		// Run Genetic Algorithm
		for (int genIdx = 0; genIdx < numGenerations; genIdx++) {
			
			// Fill wheel
			rouletteWheel = getRouletteWheel(population, numElite);

			//Add elite individuals
			for (int idx = 0; idx < numElite; idx++) {
				nextGenPopulation.set(idx, population.get(idx));
			}
			
			// Create new generation using crossover and mutation
			for (int childIdx = numElite; childIdx < populationSize; childIdx++) {
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
				crossover(nextGenPopulation, childIdx, population.get(parent1Idx), population.get(parent2Idx), mutationRate);
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
			
			System.out.println("## Generation " + genIdx + " ###");
			for (Schedule s : population) System.out.println("  makespan: " + s.getMakespan() + ", " + s);
			
			System.out.println("  wheel: " + Arrays.toString(rouletteWheel));
		}
			
	}
	
	// Crossover both parents and add offspring to population
	public static void crossover(ArrayList<Schedule> population, int index, Schedule parent1, Schedule parent2, double mutationRate) {
		//TODO: not static, work on
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
