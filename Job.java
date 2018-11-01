package project6;

import java.util.ArrayList;

public class Job {
	
	// Static Variables
	// TODO: find better way to count total number of machines PER FILE
	private static int numMachines = 0;
	
	// Instance Variables
	private int number;
	private ArrayList<Task> tasks;
	
	// Constructor
	public Job(int number) {
		tasks = new ArrayList<Task>();
		this.number = number;
	}
	
	// Getters
	public int getNumber() {
		return this.number;
	}
	
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}
	
	public static int getNumMachines() {
		return numMachines;
	}
	
	// Setters
	public void addTask(int machineNum, int processTime) {
		tasks.add(new Task(this, machineNum, processTime));
		
		if (machineNum + 1 > numMachines) numMachines = machineNum + 1; 
	}
	
	public static void resetNumMachines() {
		numMachines = 0;
	}
	
	// To String
	public String toString() {
		String output = "Job " + number + ": [";
		for (Task task : this.tasks) {
			output += task + ", ";
		}
		
		return output.substring(0, output.length() - 2) + "]";
	}
	
}
