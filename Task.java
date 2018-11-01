package project6;

public class Task {

	// Instance Variables
	private Job job;
	private int machineNum;
	private int processTime;
	
	// Constructor
	public Task(Job job, int machineNum, int processTime) {
		this.job = job;
		
		this.machineNum = machineNum;
		this.processTime = processTime;
	}
	
	// Getters
	public Job getJob() {
		return this.job;
	}
	
	public int getMachineNum() {
		return this.machineNum;
	}
	
	public int getProcessTime() {
		return this.processTime;
	}
	
	// Setters
	
	// To String
	public String toString() {
		return "(m=" + this.machineNum + ", t=" + this.processTime + ")";
	}
}
