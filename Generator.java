package project6;

import java.util.ArrayList;
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
		int minNumJobs = 2;
		int maxNumJobs = 5;
		int minTasks = 1;
		int maxTasks = 3;
		int minTime = 1;
		int maxTime = 4;
		
		int numJobs = rand.nextInt(maxNumJobs - minNumJobs + 1) + minNumJobs;
		
		for (int j = 0; j < numJobs; j++) {
			output += "JOB " + j + "\n";
			
			int numTasks = rand.nextInt(maxTasks - minTasks + 1) + minTasks;
			ArrayList<Integer> array = new ArrayList<Integer>(); 
			for (int t = 0; t < numTasks; t++) { array.add(t); }
			
			for (int t = 0; t < numTasks; t++) {
				int idx = rand.nextInt(numTasks - t);
				int taskNum = array.get(idx);
				array.remove(idx);
				
				output += taskNum++ + " " + (rand.nextInt(maxTime - minTime + 1) + minTime) + "\n";
			}
		}
		
		System.out.println(output);
		
		try {
			int maxFileNum = 0;
			
			// Select File Name 
			File dir = new File(dirName);
			if (!dir.exists()) dir.mkdirs();
			File[] files = dir.listFiles();
			
			for (File file : files) {
				if (!file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".txt")) continue;
				
				int fileNum = Integer.parseInt(file.getName().substring(4, file.getName().indexOf(".txt")));
				if (fileNum > maxFileNum) maxFileNum = fileNum; 
			}
			
			// Write new file
			String fileName = dirName + "/" + "file" + (maxFileNum + 1) + ".txt";
			File newFile = new File(fileName);
	        if (!newFile.createNewFile()){
	            System.out.println("File Already Exists!");
	        }
			 
	        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	        writer.write(output);
	        writer.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		

	}

}
