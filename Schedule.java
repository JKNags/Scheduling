package project6;

import java.util.ArrayList;
import java.util.Random;

public class Schedule {
	
	// Instance Variables
	private MachineSchedule[] machineSchedules;
	
	// Constructor
	public Schedule() {
		int numMachines = Job.getNumMachines();
		this.machineSchedules = new MachineSchedule[numMachines];
		
		for (int machineNum = 0; machineNum < numMachines; machineNum++) {
			this.machineSchedules[machineNum] = new MachineSchedule(machineNum);
		}
	}
	
	// Getters
	
	// Setters
	public void randomize(ArrayList<Job> jobs) {
		Random rand = new Random();
		ArrayList<ArrayList<Task>> tasksByMachine = new ArrayList<ArrayList<Task>>();
		for (int idx = 0; idx < Job.getNumMachines(); idx++) tasksByMachine.add(new ArrayList<Task>());   // Initialize array
		
		// Add tasks to appropriate machine list
		for (Job job : jobs) {
			for (Task task : job.getTasks()) {
				tasksByMachine.get(task.getMachineNum()).add(task);
			}
		}
		
		String o = "";for(ArrayList<Task> t:tasksByMachine){for(Task task:t) 		// Print tasks by machine
			{o+=" J"+task.getJob().getNumber()+":"+task+",";} o=o.substring(0,o.length()-1);o+="\n";} System.out.println(o);
			
		// Randomly select machine to first schedule on
		int machineNum = rand.nextInt(Job.getNumMachines());
		
		System.out.println("M: " + machineNum);
		
		int startTime = 0;
		for (Task task : tasksByMachine.get(machineNum)) {
			add(machineNum, task, startTime);
			startTime += task.getProcessTime();
		}
		
		System.out.println(this.machineSchedules[machineNum]);
		
		// Added initial set, now add rest of machines satisfying constraints
		
	}
	
	public void add(int machineNum, Task task, int startTime) {
		System.out.println("Adding task: J" + task.getJob().getNumber() + " " + task + " to machine " + machineNum + " at T=" + startTime);
		
		this.machineSchedules[machineNum].add(task, startTime);
	}

}
