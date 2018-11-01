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
	
	public static void main(String[] args) {
		String dirName = "data";
		File[] files = getFilesInFolder(dirName);
		int choice = 0;
		Scanner inputScanner = new Scanner(System.in);		
		
		while (true) {			
			System.out.println("***********************************************");
			System.out.println("\nSelect file number. Enter 0 to Exit.");
			printFiles(files);
			
			choice = inputScanner.nextInt();   // Get selection
			
			if (choice >= 1 && choice <= files.length) {
				
				long startTime = System.nanoTime();
				
				if (choice >= 1 && choice <= files.length) {
					scheduleJobs(files[choice - 1]);
				}
				
				long stopTime = System.nanoTime();
				System.out.println("Duration:  " + (stopTime - startTime));
				
			} else if (choice == 0) break;
		}

		inputScanner.close();
	}
	
	// Schedule jobs
	private static void scheduleJobs(File file) {
		ArrayList<Job> jobs = getJobsFromFile(file);
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		int populationSize = 1;
		
		for (Job j : jobs) {
			System.out.println(j);
		}
		
		System.out.println("Num Machines: " + Job.getNumMachines());
		
		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule();
			schedule.randomize(jobs);
			
			schedules.add(schedule);
		}
		
	}
	
	// Return a list of jobs from file
	public static ArrayList<Job> getJobsFromFile(File file) {
		Scanner scanner = null;
		ArrayList<Job> jobs = new ArrayList<Job>(); 
		Job job = null;
		String[] lineSplit;
		
		Job.resetNumMachines();
		
		try {
			scanner = new Scanner(file);
			
			while (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != 2) 
					throw new RuntimeException("\nError parsing file - line does not have exactly two elements");
				
				if (lineSplit[0].equals("JOB")) {   // Found new job
					job = new Job(Integer.parseInt(lineSplit[1]));
					jobs.add(job);
				} else {   // Found task for above job
					job.addTask(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		return jobs;
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
