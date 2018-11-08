package project6;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Scheduler extends Application {
	/*
	 * Scheduler
	 * Reads jobs from file
	 * Creates minimum schedule
	 */
	
	// Global Variables 
	String dirName = "data";
	ListView<String> lvFileNames;
	TextField tfPopulationSize;
	TextField tfNumGenerations;
	TextField tfMutationPercent;
	TextField tfElitism;
	TextField tfGARuns;
	TextField tfTopIndividuals;
	TextArea taAggregateAssignments;
	TextField tfAggregateMakespan;
	TextField tfShortestMakespan;
	TextField tfDifference;
	TextField tfMeanMakespan;
	TextField tfStdDevMakespan;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage)  {
		// Start button
		Button btnStart = new Button("Start");
		btnStart.setOnAction(this::btnStartOnAction);
		
		// File list view
		lvFileNames = new ListView<String>(getFileNames());
		lvFileNames = new ListView<String>(getFileNames());
		lvFileNames.getSelectionModel().select(0);
		
		// Controls
		Label lblPopulationSize = new Label("Population");
		tfPopulationSize = new TextField("50");
		Label lblNumGenerations = new Label("Generations");
		tfNumGenerations = new TextField("100");
		Label lblMutationPercent = new Label("Mutation %");
		tfMutationPercent = new TextField("1");
		Label lblElitism = new Label("Elitism %");
		tfElitism = new TextField("10");
		Label lblGARuns = new Label("GA Runs");
		tfGARuns = new TextField("10");
		Label lblTopIndividuals = new Label("Top Individuals");
		tfTopIndividuals = new TextField("1");
		
		// Results
		Label lblAggregateAssignments = new Label("Aggregate Job Assignments");
		taAggregateAssignments = new TextArea();
		taAggregateAssignments.setEditable(false);
		taAggregateAssignments.setStyle("-fx-font-family: monospace");
		Label lblAggregateMakespan = new Label("Aggregate");
		tfAggregateMakespan = new TextField();
		Label lblShortestMakespan = new Label("Shortest");
		tfShortestMakespan = new TextField();
		Label lblMeanMakespan = new Label("Mean");
		Label lblDifference = new Label("Difference");
		tfDifference = new TextField();
		tfMeanMakespan = new TextField();
		Label lblStdDevMakespan = new Label("Std. Dev.");
		tfStdDevMakespan = new TextField();
		
		// Grid Pane of all elements
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPrefSize(800, 400);

		gridPane.add(lvFileNames, 0, 0, 1, 10);
		gridPane.add(btnStart, 0, 11, 1, 1);
		GridPane.setHalignment(btnStart, HPos.CENTER);
		gridPane.add(lblPopulationSize, 1, 0, 1, 1);
		gridPane.add(tfPopulationSize, 1, 1, 1, 1);
		gridPane.add(lblNumGenerations, 2, 0, 1, 1);
		gridPane.add(tfNumGenerations, 2, 1, 1, 1);
		gridPane.add(lblMutationPercent, 3, 0, 1, 1);
		gridPane.add(tfMutationPercent, 3, 1, 1, 1);
		gridPane.add(lblElitism, 4, 0, 1, 1);
		gridPane.add(tfElitism, 4, 1, 1, 1);
		gridPane.add(lblGARuns, 5, 0, 1, 1);
		gridPane.add(tfGARuns, 5, 1, 1, 1);
		gridPane.add(lblTopIndividuals, 6, 0, 1, 1);
		gridPane.add(tfTopIndividuals, 6, 1, 1, 1);
		
		gridPane.add(lblAggregateAssignments, 1, 4, 2, 1);
		gridPane.add(taAggregateAssignments, 1, 5, 6, 5);
		gridPane.add(lblAggregateMakespan, 1, 10, 1, 1);
		gridPane.add(tfAggregateMakespan, 1, 11, 1, 1);
		gridPane.add(lblShortestMakespan, 2, 10, 1, 1);
		gridPane.add(tfShortestMakespan, 2, 11, 1, 1);
		gridPane.add(lblDifference, 3, 10, 1, 1);
		gridPane.add(tfDifference, 3, 11, 1, 1);
		gridPane.add(lblMeanMakespan, 4, 10, 1, 1);
		gridPane.add(tfMeanMakespan, 4, 11, 1, 1);
		gridPane.add(lblStdDevMakespan, 5, 10, 1, 1);
		gridPane.add(tfStdDevMakespan, 5, 11, 1, 1);	
        
		// Add elements to stage
        Group root = new Group(gridPane);
        Scene scene = new Scene(root, 800, 400, Color.rgb(240, 240, 240));
        
        primaryStage.setTitle("Flow Shop Scheduling - Wisdom of Crowds");
        primaryStage.setScene(scene);
        primaryStage.show(); 
	}
	
	private void btnStartOnAction(ActionEvent event) {
		int populationSize, numGenerations, numGARuns, topNumIndividuals;
		double mutationPercent, elitePercent;
		double[] results;
		Problem problem = getProblem(lvFileNames.getSelectionModel().getSelectedItem());
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		
		try {
			// Parse input controls
			populationSize = Integer.parseInt(this.tfPopulationSize.getText());
			numGenerations = Integer.parseInt(this.tfNumGenerations.getText());
			numGARuns = Integer.parseInt(this.tfGARuns.getText());
			topNumIndividuals = Integer.parseInt(this.tfTopIndividuals.getText());
			mutationPercent = Double.parseDouble(this.tfMutationPercent.getText()) / 100.0;
			elitePercent = Double.parseDouble(this.tfElitism.getText()) / 100.0;
			
			// Run Wisdom of Crowds scheduling
			results = scheduleWOC(problem, populationSize, numGenerations, mutationPercent, elitePercent, numGARuns, topNumIndividuals);
			
			// Print results
			//							   Aggregate		   Shortest			   Mean				   Std Dev
			System.out.println("Makespan " + results[0] + "\t" + results[1] + "\t" + results[2] + "\t" + results[3]);
			this.tfAggregateMakespan.setText(Integer.toString((int) results[0]));
			this.tfShortestMakespan.setText(Integer.toString((int) results[1]));
			this.tfDifference.setText((int) (results[0] - results[1]) + " (" + decimalFormat.format(Math.abs(results[0] - results[1]) / ((results[0] + results[1]) / 200.0)) + "%)"); 
			this.tfMeanMakespan.setText(decimalFormat.format(results[2]));
			this.tfStdDevMakespan.setText(decimalFormat.format(results[3]));
			
			if (results[0] > results[1]) this.tfAggregateMakespan.setStyle("-fx-control-inner-background: INDIANRED");
			else if (results[0] < results[1]) this.tfAggregateMakespan.setStyle("-fx-control-inner-background: GREEN");
			else this.tfAggregateMakespan.setStyle("-fx-control-inner-background: YELLOW");
			
		} catch (NumberFormatException e) {
			System.out.println("Input Poorly Formatted " + e.getMessage());
		}
	}
	
	// Return all text files in directory in a format compatible for a ListView
	public ObservableList<String> getFileNames() {
		File folder = new File(this.dirName);
		File[] fileList = folder.listFiles();
		ArrayList<String> strFileList = new ArrayList<String>();
		String[] strSortedFileList = null;
		ObservableList<String> obFileList = FXCollections.observableArrayList();
		
		try {
			for (File file : fileList) {
				if (file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".txt")) {
					strFileList.add(file.getName());
				}
			}
			
			// Sort files by name
			strSortedFileList = strFileList.toArray(new String[strFileList.size()]);
			Arrays.sort(strSortedFileList, (f1, f2) -> {
				return f1.compareTo(f2);
			});
			
	        obFileList.addAll(strSortedFileList);
	        
		} catch (NullPointerException e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		return obFileList;
	}
	
	// Schedule and aggregate using Wisdom of Crowds
	private double[] scheduleWOC(Problem problem, int populationSize, int numGenerations, 
										double mutationPercent, double elitePercent, int numGARuns, int topNumIndividuals) {
		int crowdSize = numGARuns * topNumIndividuals;   // Total number of individuals in the crowd
		int numElite = Math.max(1, (int) (populationSize * elitePercent));   // Number passed on to next generation without crossover
		int crowdTotalMakespan = 0;
		double crowdMeanMakespan, crowdStdDevMakespan = 0;
		int[][] orderingCounterMatrix = new int[problem.getNumJobs()][problem.getNumJobs()];   // Matrix to count relative ordering of jobs
		Schedule schedule;   // An individual
		Schedule shortestMakespanSchedule = null;
		Schedule aggregateSchedule;
		ArrayList<Job> aggregateJobs;
		ArrayList<Schedule> population;
		ArrayList<Schedule> crowd = new ArrayList<Schedule>();
			
		// Initialize path edges to 0
		for (int[] row : orderingCounterMatrix) Arrays.fill(row, 0);
		
		// Print problem and initial populations
		//System.out.println(problem);
		System.out.println(problem.getNumJobs() + " Jobs, Population: " + populationSize + ", Generations: " + numGenerations + ", Elite: " + numElite 
				+ ", Mutation: " + (mutationPercent*100) + "%, Crowd: " + numGARuns + "x" + topNumIndividuals + "=" + crowdSize);
		
		// Get population from multiple GA runs
		for (int pIdx = 0; pIdx < numGARuns; pIdx++) {
			population = scheduleJobsGA(problem, populationSize, numGenerations, mutationPercent, numElite);
			
			// Add best individuals of population to crowd
			for (int iIdx = 0; iIdx < topNumIndividuals; iIdx++) {
				schedule = population.get(iIdx); 
				crowd.add(schedule);   
				
				crowdTotalMakespan += schedule.getMakespan();   // Add make span to total
				if (shortestMakespanSchedule == null || schedule.getMakespan() < shortestMakespanSchedule.getMakespan()) 
					shortestMakespanSchedule = schedule;   // Set shortest make span schedule
				
				// Iterate over jobs and count relative ordering compared to all jobs beforehand
				for (int jobIdx = problem.getNumJobs() - 1; jobIdx > 0; jobIdx--) {
					for (int priorJobIdx = jobIdx - 1; priorJobIdx >= 0; priorJobIdx--) {
						// Increment number of times row comes after column
						orderingCounterMatrix[schedule.getJob(jobIdx).getNumber()][schedule.getJob(priorJobIdx).getNumber()]++;
					}
				}
			}			
		}
			
		//printMatrix(orderingCounterMatrix, problem);
		
		aggregateJobs = problem.getJobs();   // Set aggregate jobs as the ordered list of jobs
		
		// Set frequency of times each job appears after the others
		for (int rowIdx = 0; rowIdx < problem.getNumJobs(); rowIdx++) {
			aggregateJobs.get(rowIdx).setFrequencyAfter(IntStream.of(orderingCounterMatrix[rowIdx]).sum());
		}	
		
		// Sort aggregate jobs by frequency
		Collections.sort(aggregateJobs, new Comparator<Job>() {
			public int compare(Job j1, Job j2) {				
				if (j1.getFrequencyAfter() > j2.getFrequencyAfter()) return 1; 
				if (j1.getFrequencyAfter() < j2.getFrequencyAfter()) return -1; 
				if (j1.getTotalProcessTime() > j2.getTotalProcessTime()) return 1;
				if (j1.getTotalProcessTime() < j2.getTotalProcessTime()) return -1;
				return 0;
			}
		});	
		
		// Ensure first jobs starts at time 0 (assuming one does)
		/*if (aggregateJobs.get(0).getArrivalTime() != 0) {
			for (int idx = 1; idx < problem.getNumJobs(); idx++) {
				if (aggregateJobs.get(idx).getArrivalTime() == 0) {
					swapJobs(aggregateJobs, 0, idx);
					break;
				}
			}
		}*/
		
		// Create aggregate schedule from jobs
		aggregateSchedule = new Schedule(aggregateJobs, problem.getNumMachines());

		// Generate crowd statistics
	 	crowdMeanMakespan = ((double) crowdTotalMakespan / crowdSize);
	 	for (Schedule s : crowd) { crowdStdDevMakespan += Math.pow(s.getMakespan() - crowdMeanMakespan, 2); }
	 	crowdStdDevMakespan = Math.pow(crowdStdDevMakespan / crowdSize, .5);
		
		double[] results = new double[4];
		results[0] = aggregateSchedule.getMakespan(); 
		results[1] = shortestMakespanSchedule.getMakespan();  
		results[2] = crowdMeanMakespan; 
		results[3] = crowdStdDevMakespan;

		System.out.println("Agg:  " + aggregateSchedule.getJobs());
		System.out.println("Best: " + shortestMakespanSchedule.getJobs());
		
		//this.taAggregateAssignments.setText(aggregateSchedule.getAssignmentsString());

		return results;
	}
	
	@SuppressWarnings("unused")
	private void printMatrix(int[][] orderingCounterMatrix, Problem problem) {
		System.out.print("     ");
		for (int colNum = 0; colNum < problem.getNumJobs(); colNum++) {System.out.print(String.format("%-3s", colNum));} System.out.print("\n");
		for (int i = 0; i < problem.getNumJobs(); i++) {System.out.print("=====");} 
		for (int row = 0; row < problem.getNumJobs(); row++) {
			System.out.print(String.format("\n%-5s", row));
			for (int col = 0; col < problem.getNumJobs(); col++)
				if (row == col) System.out.print("   ");
				else System.out.print(String.format("%-3s", orderingCounterMatrix[row][col]));
		} System.out.print("\n");
	}
	
	// Schedule jobs using a Genetic Algorithm
	private static ArrayList<Schedule> scheduleJobsGA(Problem problem, int populationSize, int numGenerations, double mutationPercent, int numElite) {
		int parent1Idx, parent2Idx;
		double roll;
		double[] rouletteWheel;
		Random rand = new Random();
		ArrayList<Schedule> population = new ArrayList<Schedule>();
		ArrayList<Schedule> nextGenPopulation = new ArrayList<Schedule>(populationSize);
		
		if (populationSize % 2 == 1) throw new RuntimeException("\nPopulation Size must be even");
		
		if (numElite % 2 == 1) numElite++; // Ensure numElite is even
		
		// Create the initial population
		for (int idx = 0; idx < populationSize; idx++) {
			Schedule schedule = new Schedule(problem);   // Automatically randomizes
			population.add(schedule);
		}
		
		// Sort population
		Collections.sort(population, new Comparator<Schedule>() {
			public int compare(Schedule s1, Schedule s2) {
				if (s1.getMakespan() > s2.getMakespan()) return 1; 
				if (s1.getMakespan() < s2.getMakespan()) return -1;
				return 0;
			}
		});	
		
		nextGenPopulation = population;
		
		// Run Genetic Algorithm
		for (int genIdx = 0; genIdx < numGenerations; genIdx++) {
			//System.out.println("## Generation " + genIdx + " ###");
			
			// Fill wheel
			rouletteWheel = getRouletteWheel(population, numElite);
			
			//Add elite individuals
			for (int idx = 0; idx < numElite; idx++) {
				nextGenPopulation.set(idx, population.get(idx));
			}
			
			// Create new generation using crossover and mutation
			for (int childIdx = numElite; childIdx < populationSize - 1; childIdx += 2) {
				parent1Idx = parent2Idx = 0;
				
				// Choose first parent
				roll = 100 * rand.nextDouble();
				do {
					if (roll <= rouletteWheel[parent1Idx]) break;
					parent1Idx++;
				} while(parent1Idx < populationSize);
				
				// Choose second parent
				roll = 100 * rand.nextDouble();
				do {
					if (roll <= rouletteWheel[parent2Idx]) break;
					parent2Idx++;
				} while(parent2Idx < populationSize);
				if (parent1Idx == parent2Idx)   // Choose the next one if same
					parent2Idx = (parent2Idx >= populationSize - 1) ? 0 : parent2Idx + 1;

				// Cross parents into new child
				crossoverSBOX(nextGenPopulation, childIdx, population.get(parent1Idx), population.get(parent2Idx), mutationPercent, problem.getNumMachines());
			}
			
			population = nextGenPopulation;   // Replace old generation with new
			
			// Sort population
			Collections.sort(population, new Comparator<Schedule>() {
				public int compare(Schedule s1, Schedule s2) {
					if (s1.getMakespan() > s2.getMakespan()) return 1; 
					if (s1.getMakespan() < s2.getMakespan()) return -1;
					return 0;
				}
			});	
		}

		return population;
	}
	
	// Crossover both parents and add offspring to population
	// SBOX - blocks of at least two jobs are auto carried over to children, remaining slots are added from corresponding parent
	//			until a stop point is met, then rest is filled from opposite parent
	public static void crossoverSBOX(ArrayList<Schedule> population, int startIdx, Schedule parent1, Schedule parent2, double mutationPercent, int numMachines) {
		Random rand = new Random();
		int parent1Idx = 0, parent2Idx = 0;
		int swapIdx;
		int cutIdx = rand.nextInt(parent1.getNumJobs() - 2) + 2;
		double mutate;
		boolean previousMatch = false;
		boolean firstMatch = false;
		ArrayList<Job> child1Jobs = new ArrayList<Job>();
		ArrayList<Job> child2Jobs = new ArrayList<Job>();
		Schedule child1, child2;

		// Initialize children
		for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
			child1Jobs.add(null);
			child2Jobs.add(null);
		}

		// Set matching pairs from the parent in the children
		for (int idx = 0; idx < parent1.getNumJobs(); idx++) {
			// Jobs at this index don't match
			if (!parent1.getJob(idx).equals(parent2.getJob(idx))) {
				previousMatch = firstMatch = false;
				continue;
			}
			
			// Jobs match
			if (previousMatch) {
				if (firstMatch) {
					child1Jobs.set(idx - 1, parent1.getJob(idx - 1));
					child2Jobs.set(idx - 1, parent2.getJob(idx - 1));
					firstMatch = false;
				}
				
				child1Jobs.set(idx, parent1.getJob(idx));
				child2Jobs.set(idx, parent2.getJob(idx));
			} else {
				previousMatch = true;
				firstMatch = true;
			}
		}

		// Set jobs from corresponding parent until cut index
		for (int idx = 0; idx < cutIdx; idx++) {
			if (child1Jobs.get(idx) == null) child1Jobs.set(idx, parent1.getJob(idx));
			if (child2Jobs.get(idx) == null) child2Jobs.set(idx, parent2.getJob(idx));
		}
		
		// Set remaining jobs from other parent
		for (int idx = cutIdx; idx < parent1.getNumJobs(); idx++) {
			if (child1Jobs.get(idx) == null) {
				while ( child1Jobs.contains(parent2.getJob(parent2Idx)) ) parent2Idx++;
				child1Jobs.set(idx, parent2.getJob(parent2Idx));
				parent2Idx++;
			}
			if (child2Jobs.get(idx) == null) {
				while ( child2Jobs.contains(parent1.getJob(parent1Idx)) ) parent1Idx++;
				child2Jobs.set(idx, parent1.getJob(parent1Idx));
				parent1Idx++;
			}
		}
		
		// Mutate children by swapping jobs
		for (int idx = 0; idx < child1Jobs.size(); idx++) {
			mutate = rand.nextDouble();
			if (mutate < mutationPercent) { 
				swapIdx = rand.nextInt(child1Jobs.size() - 2) + 1;
				swapJobs(child1Jobs, swapIdx, idx);
			}
		}
		for (int idx = 0; idx < child2Jobs.size(); idx++) {
			mutate = rand.nextDouble();
			if (mutate < mutationPercent) { 
				swapIdx = rand.nextInt(child2Jobs.size() - 2) + 1;
				swapJobs(child2Jobs, swapIdx, idx);
			}
		}
		
		// Create schedules from job list
		child1 = new Schedule(child1Jobs, numMachines);
		child2 = new Schedule(child2Jobs, numMachines);
		
		// Set population
		population.set(startIdx, child1);
		population.set(startIdx + 1, child2);
	}
	
	// Return weighted list, preferring the lowest time
	public static double[] getRouletteWheel(ArrayList<Schedule> population, int numElite) {
		int size = population.size() - numElite;
		if (size <= 1) return new double[] {100};
        double runningTotal = 0, total = 0;
		double[] wheel = new double[size];
        double[] timeInverses = new double[size];

        // Set inverse array
        for (int idx = 0; idx < size; idx++) {
        	timeInverses[idx] = 1.0 / (double) population.get(idx).getMakespan();
        	total += timeInverses[idx];
        }
        
        // Set running total of inverses and assign weighted probability of selection
        for (int idx = 0; idx < size; idx++) {
        	runningTotal += timeInverses[idx];
        	wheel[idx] = 100 * runningTotal / total;
        }
        
        return wheel;
	}
	
	// Return a list of jobs from file
	public Problem getProblem(String fileName) {
		Scanner scanner = null;
		ArrayList<Job> jobs = new ArrayList<Job>(); 
		String[] lineSplit;
		int numMachines = 0, jobNumber, arrivalTime;
		int[] processTimes = null;

		try {
			scanner = new Scanner(new File(this.dirName + "/" + fileName));
			
			// Get number of machines
			if (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != 2) 
					throw new RuntimeException("\nError parsing file - Machine line does not have exactly two elements");
				numMachines = Integer.parseInt(lineSplit[1]);
			}
			
			processTimes = new int[numMachines];
			
			// Get jobs
			while (scanner.hasNext()) {
				lineSplit = scanner.nextLine().split(" ");
				if (lineSplit.length != (3 + numMachines)) 
					throw new RuntimeException("\nError parsing file - Job line does not have 3 + numMachines elements");
				jobNumber = Integer.parseInt(lineSplit[1]);
				arrivalTime = Integer.parseInt(lineSplit[2]);
				for (int mIdx = 0; mIdx < numMachines; mIdx++) {
					processTimes[mIdx] = Integer.parseInt(lineSplit[mIdx + 3]);
				}
				jobs.add(new Job(jobNumber, arrivalTime, processTimes.clone()));
			}
		} catch (FileNotFoundException | NumberFormatException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		return new Problem(numMachines, jobs);
	}
	
	// Swap jobs
	private static void swapJobs(ArrayList<Job> jobs, int idx1, int idx2) {
		Job tempJob = jobs.get(idx1);
		jobs.set(idx1, jobs.get(idx2));
		jobs.set(idx2, tempJob);
	}
	
	// Print file names from a list of files
	public static void printFiles(File[] fileList) {
		for (int i = 1; i <= fileList.length; i++) {
			System.out.println("File " + String.format("%2d:", i) + "\t" + fileList[i-1].getName());
		}
	}

}
