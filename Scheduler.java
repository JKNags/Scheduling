package project6;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Scheduler {
	/*
	 * Scheduler
	 * Reads jobs from file
	 * Creates minimum schedule
	 */
	
	int numMachines;
	
	public static void main(String[] args) {
		String dirName = "data";
		File[] files = getFilesInFolder(dirName);
		//int choice = 0;
		//Scanner inputScanner = new Scanner(System.in);		
		
		//while (true) {			
			System.out.println("\n***********************************************");
			System.out.println("Select file number. Enter 0 to Exit.");
			printFiles(files);
			
			// TODO: catch first line
			//???while (!inputScanner.hasNextLine()) inputScanner.nextLine();
			
			// TODO: enable choice
			//choice = inputScanner.nextInt();   // Get selection
			
			//if (choice >= 1 && choice <= files.length) {
				
				long startTime = System.nanoTime();
				
				//if (choice >= 1 && choice <= files.length) {
					//scheduleJobs(files[choice - 1]);
					scheduleJobs(files[0]);
				//}
				
				long stopTime = System.nanoTime();
				System.out.println("Duration:  " + (stopTime - startTime));
				
			//} else if (choice == 0) break;
		//}

		//inputScanner.close();
	}
	
	// Schedule jobs
	private static void scheduleJobs(File file) {
		Problem problem = getProblemDefinition(file);
		ArrayList<Schedule> population = new ArrayList<Schedule>();
		int populationSize = 1;
		
		System.out.println(problem);

		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule(problem);
			schedule.randomize();
			
			population.add(schedule);
			schedule.printAssignments();
		}
		
	}
	
	// Return a list of jobs from file
	public static Problem getProblemDefinition(File file) {
		Scanner scanner = null;
		ArrayList<Job> jobs = new ArrayList<Job>(); 
		String[] lineSplit;
		int numMachines = 0, numJobs = 0, jobNumber, arrivalTime;
		int[][] processTimes = null;

		try {
			scanner = new Scanner(file);
			
			// Get number of machines
			if (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != 2) 
					throw new RuntimeException("\nError parsing file - Machine line does not have exactly two elements");
				numMachines = Integer.parseInt(lineSplit[1]);
			}
			
			// Get number of jobs
			if (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != 2) 
					throw new RuntimeException("\nError parsing file - Jobs line does not have exactly two elements");
				numJobs = Integer.parseInt(lineSplit[1]);
			}
			
			processTimes = new int[numJobs][numMachines];
			
			// Get jobs
			while (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != (3 + numMachines)) 
					throw new RuntimeException("\nError parsing file - Job line does not have 2 + numMachines elements");
				jobNumber = Integer.parseInt(lineSplit[1]);
				arrivalTime = Integer.parseInt(lineSplit[2]);
				for (int mIdx = 0; mIdx < numMachines; mIdx++) {
					processTimes[jobNumber][mIdx] = Integer.parseInt(lineSplit[mIdx + 3]);
				}
				jobs.add(new Job(jobNumber, arrivalTime));
			}
		} catch (FileNotFoundException | NumberFormatException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		return new Problem(numMachines, jobs, processTimes);
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
