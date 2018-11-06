package project6;

public class Job {
	
	// Instance Variables
	private int number;
	private int arrivalTime;
	private int[] processTimes;
	private int frequencyAfter;
	
	// Constructor
	public Job(int number, int arrivalTime, int[] processTimes) {
		this.number = number;
		this.arrivalTime = arrivalTime;
		this.processTimes = processTimes;
	}
	
	// Getters
	public int getNumber() {
		return this.number;
	}
	
	public int getArrivalTime() {
		return this.arrivalTime;
	}
	
	public int[] getProcessTimes() {
		return this.processTimes;
	}
	
	public int getProcessTime(int machineNum) {
		return this.processTimes[machineNum];
	}
	
	public int getFrequencyAfter() {
		return this.frequencyAfter;
	}
	
	// Setters
	public void setFrequencyAfter(int frequency) {
		this.frequencyAfter = frequency;
	}

	// To String
	public String toString() {
		return "J" + this.number;
	}
	
}
