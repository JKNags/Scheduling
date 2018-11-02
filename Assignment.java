package project6;

public class Assignment {

	// Instance Variables
	private Job job;
	private int startTime;
	private int processTime;
	
	// Constructor
	public Assignment(Job job, int startTime, int processTime) {
		this.job = job;
		this.startTime = startTime;
		this.processTime = processTime;
	}
	
	// Getters
	public Job getJob() {
		return this.job;
	}
	
	public int getStartTime() {
		return this.startTime;
	}
	
	public int getStopTime() {
		return this.startTime + this.processTime;
	}
	
	public int getProcessTime() {
		return this.processTime;
	}
}
