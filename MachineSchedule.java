package project6;

import java.util.ArrayList;

public class MachineSchedule {
	
	// Instance Variables
	private int machineNum;
	private ArrayList<Task> tasks;
	private ArrayList<Integer> taskStartTime;
	
	// Constructor
	public MachineSchedule(int machineNum) {
		this.machineNum = machineNum;
		this.tasks = new ArrayList<Task>();
		this.taskStartTime = new ArrayList<Integer>();
	}
	
	// Getters
	
	// Setters
	public void add(Task task, int startTime) {
		this.tasks.add(task);
		this.taskStartTime.add(startTime);
	}
	
	// To String
	public String toString() {
		String output = "";
		
		int idx = 0;
		for (int taskIdx = 0; taskIdx < this.tasks.size(); taskIdx++) {
			while (idx < this.taskStartTime.get(taskIdx)) {
				System.out.print("_");
				idx++;
			}
			int targetTime = idx + this.tasks.get(taskIdx).getProcessTime();
			while (idx < targetTime) {
				System.out.print((char) (this.tasks.get(taskIdx).getJob().getNumber() + 65));
				idx++;
			}
		}
		
		return output;
	}

}
