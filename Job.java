package project6;

public class Job {
	
	// Instance Variables
	private int number;
	private int arrivalTime;
	//private int[] processTimes;
	
	// Constructor
	public Job(int number, int arrivalTime/*, int[] processTimes*/) {
		this.number = number;
		this.arrivalTime = arrivalTime;
		//this.processTimes = processTimes;
	}
	
	// Getters
	public int getNumber() {
		return this.number;
	}
	
	public int getArrivalTime() {
		return this.arrivalTime;
	}
	
	/*public int[] getProcessTime() {
		return this.processTimes;
	}*/

	// Setters

	// To String
	public String toString() {
		return "Job " + this.number + " A = " + this.arrivalTime;  //+ ", P=" + this.processTimes;
	}
	
}
