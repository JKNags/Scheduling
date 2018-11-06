package project6;

import java.util.ArrayList;
import java.util.Random;

public class Schedule {
	
	// Instance Variables
	private ArrayList<Job> jobs;
	private ArrayList<ArrayList<Assignment>> assignments;
	private int makespan;
	
	// Constructors
	public Schedule(Problem problem) {
		this.jobs = new ArrayList<Job>();
		this.assignments = new ArrayList<ArrayList<Assignment>>();
		for (int idx = 0; idx < problem.getNumMachines(); idx++) this.assignments.add(new ArrayList<Assignment>());
		randomize(problem);
	}
	
	public Schedule(ArrayList<Job> jobs, int numMachines) {
		this.jobs = jobs;
		this.assignments = new ArrayList<ArrayList<Assignment>>();
		for (int idx = 0; idx < numMachines; idx++) this.assignments.add(new ArrayList<Assignment>());
		createAssignments(numMachines);
	}
	
	// Getters
	public ArrayList<Job> getJobs() {
		return this.jobs;
	}
	
	public Job getJob(int idx) {
		if (this.jobs.size() == 0 || idx < 0 || idx > this.jobs.size()) return null;
		return this.jobs.get(idx);
	}
	
	public int getNumJobs() {
		return this.jobs.size();
	}
	
	public int getMakespan() {
		return this.makespan;
	}
	
	// Setters
	
	// Create assignments for current job order
	private void createAssignments(int numMachines) {
		int time = 0, processTime;
		
		for (Job job : this.jobs) {
			time = job.getArrivalTime();
			
			for (int machineNum = 0; machineNum < numMachines; machineNum++) {
			
				if (this.assignments.get(machineNum).size() > 0) {
					// scan time is either this jobs arrival time or the stop time of the last job on this machine
					time = Math.max(time, this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime());
				}
				
				processTime = job.getProcessTime(machineNum);
				this.assignments.get(machineNum).add(new Assignment(job, time, processTime));
				time += processTime;
			}
		}
		
		this.makespan = time;
	}
	
	// Randomly shuffle problem set 
	private void randomize(Problem problem) {
		int time = 0, scanTime = 0, processTime;
		int randInt;
		Random rand = new Random();
		Job job = null;
		@SuppressWarnings("unchecked")   //TODO what??
		ArrayList<Job> incompleteJobs = (ArrayList<Job>) problem.getJobs().clone();
		ArrayList<Job> availableJobs = new ArrayList<Job>();
		
		this.jobs.clear();
		for (ArrayList<Assignment> a : this.assignments) a.clear();
		
		do {
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
			
			// Set jobs for each machine
			for (int machineNum = 0; machineNum < problem.getNumMachines(); machineNum++) {
			
				if (this.assignments.get(machineNum).size() > 0) {
					// scan time is either the stop time from the previous machine or the last stop time of the current machine
					scanTime = Math.max(scanTime, this.assignments.get(machineNum).get(this.assignments.get(machineNum).size() - 1).getStopTime());
				}
				
				processTime = job.getProcessTime(machineNum);
				this.assignments.get(machineNum).add(new Assignment(job, scanTime, processTime));
				scanTime += processTime;
			}
			availableJobs.remove(job);
			incompleteJobs.remove(job);
			
			time++;
		} while (incompleteJobs.size() != 0);
		
		this.makespan = scanTime;
	}
	
	public void printAssignments() {
		String output = "";
		int machineNum = 0;
		int time, endTime;
		int padding = Integer.toString(Math.max(this.makespan, 10 + this.jobs.size())).length() + 2;
		
		output += "makespan " + this.makespan + "\n\t";
		for (int i = 0; i < this.makespan; i++) output += String.format("%-" + padding + "d", i); output += "\n\t";
		for (int i = 0; i < this.makespan; i++) for (int j = 0; j < padding; j++) output += "="; output += "\n";
		
		for (ArrayList<Assignment> machineAssignments : this.assignments) {
			time = 0;
			output += "M" + machineNum + "\t";
			
			for (Assignment assignment : machineAssignments) {
				while (time < assignment.getStartTime()) {
					output += String.format("%-" + padding + "s", "-");
					time++;
				}
				endTime = time + assignment.getProcessTime();
				while (time < endTime) {
					output += String.format("%-" + padding + "s", "J" + assignment.getJob().getNumber());
					time++;
				}
			}
			
			while (time < this.makespan) {
				output += String.format("%-" + padding + "s", "-");
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
		return this.makespan + " " + this.jobs.toString();
	}

}
