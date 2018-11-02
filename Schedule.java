package project6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Schedule {
	
	// Instance Variables
	//private Problem problem;
	private ArrayList<Job> jobs;
	private ArrayList<ArrayList<Assignment>> assignments;
	private int makespan;
	
	// Constructor
	public Schedule(int numMachines/*Problem problem*/) {
		//this.problem = problem;
		this.jobs = new ArrayList<Job>();
		this.assignments = new ArrayList<ArrayList<Assignment>>();
		for (int idx = 0; idx < numMachines; idx++) this.assignments.add(new ArrayList<Assignment>());
	}
	
	// Getters
	public ArrayList<Job> getJobs() {
		return this.jobs;
	}
	
	public int getMakespan() {
		return this.makespan;
	}
	
	// Setters
	
	// Randomly shuffle problem set 
	public void randomize(Problem problem) {
		int time = 0, scanTime = 0, processTime;
		int randInt;
		Random rand = new Random();
		int[] numAssignments = new int[problem.getNumJobs()];
		Arrays.fill(numAssignments, 0);
		Job job = null;
		@SuppressWarnings("unchecked")   //TODO what??
		ArrayList<Job> incompleteJobs = (ArrayList<Job>) problem.getJobs().clone();
		ArrayList<Job> availableJobs = new ArrayList<Job>();
		
		for (ArrayList<Assignment> a : this.assignments) a.clear();
		
		while (true) {
			// Add any new jobs for current time
			for (Job newJob : incompleteJobs) {
				if (newJob.getArrivalTime() == time) availableJobs.add(newJob);
			}
			if (availableJobs.size() == 0) {time++; continue;}
			
			// Select random available job
			randInt = rand.nextInt(availableJobs.size());
			job = availableJobs.get(randInt);
			this.jobs.add(job);
			scanTime = time;
//System.out.println("Randomly Selected Job " + job.getNumber());
			for (int machineNum = 0; machineNum < problem.getNumMachines(); machineNum++) {
			
//System.out.print("\tM=" + machineNum + ",  ScanTime=" + scanTime );
				if (this.assignments.get(machineNum).size() > 0) {
					// scan time is either the stop time from the previous machine or the last stop time of the current machine
					scanTime = Math.max(scanTime, this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime());
//System.out.print(",  Added " + (this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime()) + " to scanTime =="+scanTime);
				}
				
				processTime = job.getProcessTime(machineNum);
//System.out.println(", ProcessTime=" + processTime);
				this.assignments.get(machineNum).add(new Assignment(job, scanTime, processTime));
				scanTime += processTime;
			}
			availableJobs.remove(job);
			incompleteJobs.remove(job);
			
			time++;
			if (incompleteJobs.size() == 0) break;
		}
		
		this.makespan = scanTime;
	}
	
	public void printAssignments() {
		String output = "";
		int machineNum = 0;
		int time, endTime;
		
		output += "makespan " + this.makespan + "\n\t";
		for (int idx = 0; idx < this.makespan; idx++) output += String.format("%-3d ", idx); output += "\n\t";
		for (int idx = 0; idx < this.makespan; idx++) output += "===="; output += "\n";
		
		for (ArrayList<Assignment> machineAssignments : this.assignments) {
			time = 0;
			output += "M" + machineNum + "\t";
			
			for (Assignment assignment : machineAssignments) {
				while (time < assignment.getStartTime()) {
					output += String.format("%-3s ", "-");
					time++;
				}
				endTime = time + assignment.getProcessTime();
				while (time < endTime) {
					output += String.format("%-3c ", (char) (assignment.getJob().getNumber() + 65));
					time++;
				}
			}
			
			while (time < this.makespan) {
				output += String.format("%-3s ", "-");
				time++;
			}
			
			machineNum++;
			output += "\n";
		}
		
		System.out.println("Schedule");
		System.out.println(output);
	}
	
	// To String
	public String toString() {
		return this.jobs.toString();
	}

}
