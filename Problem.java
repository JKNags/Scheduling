package project6;

import java.util.ArrayList;

public class Problem {

	// Instance Variables
	int numMachines;
	int numJobs;
	ArrayList<Job> jobs;
	
	// Constructor
	public Problem(int numMachines, ArrayList<Job> jobs) {
		this.numMachines = numMachines;
		this.jobs = jobs;
		this.numJobs = jobs.size();
	}
	
	// Getters
	public int getNumMachines() {
		return this.numMachines;
	}
	
	public ArrayList<Job> getJobs() {
		return this.jobs;
	}
	
	public int getNumJobs() {
		return this.numJobs;
	}
	
	// To String
	public String toString() {
		String output = "\tA\t";

		for (int i = 0; i < this.numMachines; i++) {
			output += "M" + i + "\t";
		}
		output += "\n";
		for (int i = 0; i < this.numMachines + 2; i++) {
			output += "-------";
		}
		output += "\n";
		for (Job job : jobs) {
			output += "J" + job.getNumber() + "\t" + job.getArrivalTime();
			for (int processTime : job.getProcessTimes()) {
				output += "\t" + processTime;
			}
			output += "\n";
		}
		
		return output;
	}
}
