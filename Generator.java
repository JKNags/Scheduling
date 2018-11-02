package project6;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	/*
	 * Generator
	 * Writes file of random jobs
	 * Tasks are printed under jobs as <Machine number> <Process time>
	 * Machines can be in any order
	 */

	public static void main(String[] args) {
		Random rand = new Random();
		String output = "";
		String dirName = "data";
		
		// Input Variables //
		int minNumMachines = 3;
		int maxNumMachines = 6;
		int minNumJobs = 60;
		int maxNumJobs = 90;
		int minArrivalTime = 0;
		int maxArrivalTime = 6;
		int minProcessTime = 1;
		int maxProcessTime = 8;
		
		// Print number of machines
		int numMachines = rand.nextInt(maxNumMachines - minNumMachines + 1) + minNumMachines;		
		output += "MACHINES " + numMachines + "\n";
		
		// Print number of jobs
		int numJobs = rand.nextInt(maxNumJobs - minNumJobs + 1) + minNumJobs;
		
		// Print jobs
		for (int j = 0; j < numJobs; j++) {				// JOB <number> <arrival time> {<process time for each machine>}
			output += "JOB " + j + " " + (rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime); 
			for (int m = 0; m < numMachines; m++) {
				output += " " + (rand.nextInt(maxProcessTime - minProcessTime + 1) + minProcessTime);
			}
			output += "\n";
		}
		
		System.out.println(output);
		
		try {
			int maxFileNum = 0, fileNum;
			
			// Select File Name 
			File dir = new File(dirName);
			if (!dir.exists()) dir.mkdirs();
			File[] files = dir.listFiles();
			
			for (File file : files) {
				if (!file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".txt")) continue;   // Ensure text file
				
				try {
					fileNum = Integer.parseInt(file.getName().substring(4, file.getName().indexOf("_")));
					if (fileNum > maxFileNum) maxFileNum = fileNum; 
				} catch (NumberFormatException e) {}
			}
			
			// Write new file
			String fileName = dirName + "/" + "file" + (maxFileNum + 1) + "_M" + numMachines + "_J" + numJobs + ".txt";
			File newFile = new File(fileName);
	        if (!newFile.createNewFile()){
	            System.out.println("File Already Exists!");
	        }
			 
	        System.out.println("Writing file: " + fileName);
	        
	        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	        writer.write(output);
	        writer.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
