package project6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Schedule {
	
	// Instance Variables
	private Problem problem;
	private ArrayList<ArrayList<Assignment>> assignments;
	private int makespan;
	
	// Constructor
	public Schedule(Problem problem) {
		this.problem = problem;
		this.assignments = new ArrayList<ArrayList<Assignment>>();
		for (int idx = 0; idx < problem.getNumMachines(); idx++) this.assignments.add(new ArrayList<Assignment>());
	}
	
	// Getters
	
	// Setters
	
	// Randomly shuffle problem set 
	public void randomize() {
		int time = 0, scanTime = 0, processTime;
		int randInt;
		Random rand = new Random();
		int[] numAssignments = new int[problem.getNumJobs()];
		Arrays.fill(numAssignments, 0);
		Job job = null;
		@SuppressWarnings("unchecked")   // ??
		ArrayList<Job> incompleteJobs = (ArrayList<Job>) problem.getJobs().clone();
		ArrayList<Job> availableJobs = new ArrayList<Job>();
		
		for (ArrayList<Assignment> a : this.assignments) a.clear();
		
		//int tmpIdx = 0;int[] tmpA = {1, 2, 0, 3, 4};
		while (true) {
			// Add any new jobs for current time
			for (Job newJob : incompleteJobs) {
				if (newJob.getArrivalTime() == time) availableJobs.add(newJob);
			}
			if (availableJobs.size() == 0) {time++; continue;}
			
			// Select random available job
			randInt = rand.nextInt(availableJobs.size());
			job = availableJobs.get(randInt); //job = problem.jobs.get(tmpA[tmpIdx++]); 
			scanTime = time;
//System.out.println("Randomly Selected Job " + job.getNumber());
			for (int machineNum = 0; machineNum < problem.getNumMachines(); machineNum++) {
				
//System.out.print("\tM=" + machineNum + ",  ScanTime=" + scanTime );
				if (this.assignments.get(machineNum).size() > 0) {
					scanTime = Math.max(scanTime, this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime());
//System.out.print(",  Added " + (this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime()) + " to scanTime =="+scanTime);
				}
				
				processTime = problem.getProcessTimes()[job.getNumber()][machineNum];
//System.out.println(", ProcessTime=" + processTime);
				this.assignments.get(machineNum).add(new Assignment(job, scanTime, processTime));
				scanTime += processTime;
			}
			availableJobs.remove(job);
			incompleteJobs.remove(job);
			
			this.makespan = scanTime;
			
			time++;
			if (incompleteJobs.size() == 0) break;
		}
		
		//this.makespan = time;
	}
	
	public void printAssignments() {
		String output = "";
		int machineNum = 0;
		int time, endTime;
		
		output += "makespan " + this.makespan + "\n\t";
		for (int idx = 0; idx < this.makespan; idx++) output += String.format("%-3d ", idx); output += "\n\t";
		for (int idx = 0; idx < this.makespan; idx++) output += "---"; output += "\n";
		
		for (ArrayList<Assignment> machineAssignments : this.assignments) {
			time = 0;
			output += "M" + machineNum + "\t";
			
			for (Assignment assignment : machineAssignments) {
				while (time < assignment.getStartTime()) {
					output += String.format("%-3s ", "_");
					time++;
				}
				endTime = time + problem.getProcessTimes()[assignment.getJob().getNumber()][machineNum];
				while (time < endTime) {
					output += String.format("%-3c ", (char) (assignment.getJob().getNumber() + 65));
					time++;
				}
				/*while (time < this.makespan) {
					output += String.format("%-3s ", "_");
					time++;
				}*/
			}
			
			machineNum++;
			output += "\n";
		}
		
		System.out.println("Schedule");
		System.out.println(output);
	}

}
