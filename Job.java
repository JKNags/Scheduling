package project6;

import java.util.ArrayList;

public class Job {
	
	// Instance Variables
	private int number;
	private ArrayList<Integer> taskMachineNum;
	private ArrayList<Integer> taskProcessTime;			// TODO: make new Task object?
	
	// Constructor
	public Job(int number) {
		this.number = number;
		taskMachineNum = new ArrayList<Integer>();
		taskProcessTime = new ArrayList<Integer>();
	}
	
	// Getters
	public int getNumber() {
		return this.number;
	}
	
	// Setters
	public void addTask(int machineNum, int processTime) {
		taskMachineNum.add(machineNum);
		taskProcessTime.add(processTime);
	}
	
	// To String
	public String toString() {
		String output = "Task " + number + ": [";
		for (int idx = 0; idx < taskMachineNum.size(); idx++) {
			output += "(m=" + taskMachineNum.get(idx) + ", t=" + taskProcessTime.get(idx) + "), ";
		}
		
		return output.substring(0, output.length() - 2) + "]";
	}
	
}
