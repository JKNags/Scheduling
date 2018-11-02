package project6;

import java.util.ArrayList;

public class Problem {

	// Instance Variables
	int numMachines;
	int numJobs;
	ArrayList<Job> jobs;
	int[][] processTimes;
	
	// Constructor
	public Problem(int numMachines, ArrayList<Job> jobs, int[][] processTimes) {
		this.numMachines = numMachines;
		this.jobs = jobs;
		this.processTimes = processTimes;
		this.numJobs = jobs.size();
	}
	
	// Getters
	public int getNumMachines() {
		return this.numMachines;
	}
	
	public ArrayList<Job> getJobs() {
		return this.jobs;
	}
	
	public int[][] getProcessTimes() {
		return this.processTimes;
	}
	
	public int getNumJobs() {
		return this.numJobs;
	}
	
	// To String
	public String toString() {
		String output = "\tA\t";

		for (int i = 0; i < this.processTimes.length; i++) {
			output += "M" + i + "\t";
		}
		output += "\n";
		for (int i = 0; i < this.processTimes[0].length + 2; i++) {
			output += "-------";
		}
		output += "\n";
		for (Job job : jobs) {
			output += "J" + job.getNumber() + "\t" + job.getArrivalTime();
			for (int processTime : this.processTimes[job.getNumber()]) {
				output += "\t" + processTime;
			}
			output += "\n";
		}
		
		
		/*int time, target;
		 * for (Job job : this.jobs) {
			
			time = 0;
			while (time < job.getArrivalTime()) {
				output += "_";
				time++;
			}
			
			target = time + job.getProcessTimes();
			while (time < target) {
				output += (char) (job.getNumber() + 65);
				time++;
			}
			
			output += "\n";
		}*/
		
		return output;
	}
}
