import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;


public class IP {
	public static String[][][][] run(int optimizationSize) throws FileNotFoundException, IOException{

		// =========================================================================
		// ==================== RUN FOR INITIAL SOLUTION ===========================
		// =========================================================================
		// „„„„„„„„„„„„„„„„„„
		// |||||| DATA |||||| 
		// ””””””””””””””””””
		Random rand = new Random();
		String [][][][] initialSchedule = null; // Initial schedule to be used while fixing and optimizing
		String [][][][] optimizedSchedule = null; // Interim schedule to be used during optimization
		int daysInWeek = 7; // Number of days in a week
		int totalEmployees = Data.numberOfEmployees; // Number of total employees
		int totalShifts = Data.numberOfShifts; // Number of shift types
		int totalHorizon = Data.numberOfDays; // Total number of days in the horizon
		int totalUnits = Data.numberOfSkillGroups; // Total departments
		String [][][][] finalSchedule = new String[totalEmployees][totalHorizon][totalShifts][totalUnits]; // Initialize the final schedule
		int optHorizon = totalHorizon;
		int optWeekend = optHorizon / daysInWeek;
		int[][][][] reqCov = Data.requirementsCover; // Define requirements for nurses and covers
		int[][][] reqEmp = null; // Define requirements for nurses and covers
		String[][] empShiftLmt = null; // Capture shift limits
		String[][] empDetail = null; // Capture employee details
		// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
		// |||||| Select Employees for Optimization ||||||
		// ”””””””””””””””””””””””””””””””””””””””””””””””
		boolean allEmployeesOptimized = false; // Indicator for optimization completion
		int[] employeesOptimized = new int [totalEmployees]; // List of nurses to be optimized
		int totalOptimizationCounter = 0; // Count how many employees are optimized during iterations
		while(allEmployeesOptimized == false){ // while there are still employees left to be optimized
			if((totalEmployees - totalOptimizationCounter) <= optimizationSize){ // If optimization size becomes greater than unprocessed employees
				optimizationSize = totalEmployees - totalOptimizationCounter; // Update the optimization size to the remaining employee count
			}
			boolean employeeSelectionCompleted = false; // Indicator for nurse selection process
			boolean employeeAlreadySelected = false; // Indicator if employee was selected earlier
			int[] employeeToBeOptimized = new int [optimizationSize]; // List of nurses to be optimized
			int selectedEmployee = -1; // Selected employee index from random generation
			int selectedtotalEmployees = 0; // Count number of selection
			while(employeeSelectionCompleted == false){ // Loop until selection is done
				selectedEmployee = rand.nextInt(totalEmployees); // Randomly select employees
				if(selectedtotalEmployees == optimizationSize){ // If size of the optimization is reached
					employeeSelectionCompleted = true; // Exit the loop
				}else{ // Otherwise
					for(int i = selectedtotalEmployees; i > 0; i--){ // Check if the employee is already selected
						if(selectedEmployee == employeeToBeOptimized[i-1]){ // If employee is already in the list
							employeeAlreadySelected = true; //Set the indicator that employee is already selected in the optimization list
						}
					}
					for(int i = totalOptimizationCounter; i > 0; i--){ // Check if the employee is already selected in the overall list
						if(selectedEmployee == employeesOptimized[i-1]){ // If employee is already in the general optimization list
							employeeAlreadySelected = true; //Set the indicator that employee is already selected
						}
					}
					if(employeeAlreadySelected == false){ // If employee was not selected before
						employeeToBeOptimized[selectedtotalEmployees] = selectedEmployee; // Capture the randomly selected employee
						employeesOptimized[totalOptimizationCounter] = selectedEmployee; // Capture the overall optimization list
						selectedtotalEmployees = selectedtotalEmployees + 1; // Increase the counter
						totalOptimizationCounter = totalOptimizationCounter + 1; // Update total optimization count to complete the final schedule
					}else{
						employeeAlreadySelected = false; // Set back the indicator after a successful addition to the optimization list
					}
				}
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Prepare employee data from selection||||||
			// ”””””””””””””””””””””””””””””””””””””””””””””””””

			empShiftLmt = new String[optimizationSize][totalShifts];
			empDetail = new String[optimizationSize][Data.employees[0].length];
			reqEmp = new int[optHorizon][totalShifts][optimizationSize];
			for(int i = 0; i < optimizationSize; i++){ // Loop all employees
				//_____________________________
				// *** Prepare shift limits ***
				//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
				for(int k = 0; k < totalShifts; k++){ // Loop all shift
					empShiftLmt[i][k] = Data.employeeShiftLimit[employeeToBeOptimized[i]][k]; // Capture the shift limit of the employee
				}
				//_________________________________
				// *** Prepare employee details ***
				//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
				for(int j = 0; j < Data.employees[0].length; j++){ // Loop all employee attributes
					empDetail[i][j] = Data.employees[employeeToBeOptimized[i]][j]; // Capture the details of the employee
				}
				//______________________________________
				// *** Prepare employee requirements ***
				//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
				for(int j = 0; j < totalHorizon; j++){ // Loop all days
					for(int k = 0; k < totalShifts; k++){ // Loop all shift
						reqEmp[j][k][i] = Data.requirementsEmployee[j][k][employeeToBeOptimized[i]];
					}
				}
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Optimization ||||||
			// ””””””””””””””””””””””””””
			optimizedSchedule = Model.solve(-1, null, null, optimizationSize, optHorizon, totalShifts, optWeekend, empShiftLmt, Data.shifts, empDetail, reqEmp, reqCov);
			if(optimizedSchedule != null){// If a solution is found
				initialSchedule = optimizedSchedule; // Update the initial solution
			}else{
				System.out.println("WAIT");
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Identify Assignments & Build Schedule ||||||
			// ”””””””””””””””””””””””””””””””””””””””””””””””””””
			for(int i = 0; i < optimizationSize; i++){ // Loop all employees
				for(int j = 0; j < totalHorizon; j++){ // Loop all employee attributes
					for(int k = 0; k < totalShifts; k++){ // Loop all shift
						for(int l = 0; l < totalUnits; l++){ // Loop all shift
							//finalSchedule[employeeToBeOptimized[i]][j][k][l] = "-1";
							finalSchedule[employeeToBeOptimized[i]][j][k][l] = optimizedSchedule[i][j][k][l];
						}
					}
				}
			}
			if(totalOptimizationCounter == totalEmployees){ // When there is no employee left to be optimized
				allEmployeesOptimized = true; // Turn the switch to true to exit the big while loop
			}
			//Print.generateExcel(finalSchedule, "finalSchedule");
			//System.out.println("STOP");			
		}		
		return finalSchedule;
	}
	
	public static String[][][][] fix(int optimizationDay, int optimizationSize, String[][][][] oldSchedule, String[][][][] newSchedule) throws FileNotFoundException, IOException{

		// =============================================================
		// ==================== FIX SOLUTION ===========================
		// =============================================================
		// „„„„„„„„„„„„„„„„„„
		// |||||| DATA |||||| 
		// ””””””””””””””””””
		String[][][][] currentSchedule = new String[oldSchedule.length][oldSchedule[0].length][oldSchedule[0][0].length][oldSchedule[0][0][0].length];
		currentSchedule = Schedule.copyArrays(oldSchedule); // Copy old schedule to the current schedule
		Random rand = new Random();
		String [][][][] optimizedSchedule = null; // Interim schedule to be used during optimization
		int totalEmployees = Data.numberOfEmployees; // Number of total employees
		String [][][][] finalSchedule = new String[totalEmployees][Data.numberOfDays][Data.numberOfShifts][Data.numberOfSkillGroups]; // Initialize the final schedule
		// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
		// |||||| Select Employees for Optimization ||||||
		// ”””””””””””””””””””””””””””””””””””””””””””””””
		boolean allEmployeesOptimized = false; // Indicator for optimization completion
		int[] employeesOptimized = new int [totalEmployees]; // List of nurses to be optimized
		int totalOptimizationCounter = 0; // Count how many employees are optimized during iterations
		while(allEmployeesOptimized == false){ // while there are still employees left to be optimized
			if((totalEmployees - totalOptimizationCounter) <= optimizationSize){ // If optimization size becomes greater than unprocessed employees
				optimizationSize = totalEmployees - totalOptimizationCounter; // Update the optimization size to the remaining employee count
			}
			boolean employeeSelectionCompleted = false; // Indicator for nurse selection process
			boolean employeeAlreadySelected = false; // Indicator if employee was selected earlier
			int[] employeeToBeOptimized = new int [optimizationSize]; // List of nurses to be optimized
			int selectedEmployee = -1; // Selected employee index from random generation
			int selectedtotalEmployees = 0; // Count number of selection
			while(employeeSelectionCompleted == false){ // Loop until selection is done
				selectedEmployee = rand.nextInt(totalEmployees); // Randomly select employees
				if(selectedtotalEmployees == optimizationSize){ // If size of the optimization is reached
					employeeSelectionCompleted = true; // Exit the loop
				}else{ // Otherwise
					for(int i = selectedtotalEmployees; i > 0; i--){ // Check if the employee is already selected
						if(selectedEmployee == employeeToBeOptimized[i-1]){ // If employee is already in the list
							employeeAlreadySelected = true; //Set the indicator that employee is already selected in the optimization list
						}
					}
					for(int i = totalOptimizationCounter; i > 0; i--){ // Check if the employee is already selected in the overall list
						if(selectedEmployee == employeesOptimized[i-1]){ // If employee is already in the general optimization list
							employeeAlreadySelected = true; //Set the indicator that employee is already selected
						}
					}
					if(employeeAlreadySelected == false){ // If employee was not selected before
						employeeToBeOptimized[selectedtotalEmployees] = selectedEmployee; // Capture the randomly selected employee
						employeesOptimized[totalOptimizationCounter] = selectedEmployee; // Capture the overall optimization list
						selectedtotalEmployees = selectedtotalEmployees + 1; // Increase the counter
						totalOptimizationCounter = totalOptimizationCounter + 1; // Update total optimization count to complete the final schedule
					}else{
						employeeAlreadySelected = false; // Set back the indicator after a successful addition to the optimization list
					}
				}
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Swap Schedules ||||||
			// ””””””””””””””””””””””””””””
			for(int i = 0; i < optimizationSize; i++){ // Loop all employees
				for(int j = 0; j < currentSchedule[0].length; j++){ // Loop all days
					for(int k = 0; k < currentSchedule[0][0].length; k++){ // Loop all shifts
						for(int l = 0; l < currentSchedule[0][0][0].length; l++){ // Loop all units
							currentSchedule[employeeToBeOptimized[i]][j][k][l] = newSchedule[employeeToBeOptimized[i]][j][k][l];
						}
					}
				}
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Optimization ||||||
			// ””””””””””””””””””””””””””
//			Print.generateExcel(currentSchedule, "particleCurrent");
//			Print.generateExcel(newSchedule, "newSchedule");
//			Print.generateExcel(currentSchedule, "particleCurrent");
//			System.out.println(Schedule.verifyFeasibility(currentSchedule, null));
//			System.out.println(Schedule.calculateObjective(currentSchedule));
			optimizedSchedule = Model.solve(optimizationDay, employeeToBeOptimized, currentSchedule, Data.numberOfEmployees, Data.numberOfDays, Data.numberOfShifts, Data.numberOfDays/7, Data.employeeShiftLimit, Data.shifts, Data.employees, Data.requirementsEmployee, Data.requirementsCover);
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Copy Optimized Schedule to Current Schedule ||||||
			// ”””””””””””””””””””””””””””””””””””””””””””””””””””””””””
			if(optimizedSchedule != null){// If a solution is found
				if(Schedule.verifyFeasibility(optimizedSchedule, null)){ // If the solution is feasible
					currentSchedule = Schedule.copyArrays(optimizedSchedule); // Copy optimized schedule to the current schedule	
				}else{
					currentSchedule = Schedule.copyArrays(oldSchedule); // Copy old schedule to the current schedule
				}
			}else{
				currentSchedule = Schedule.copyArrays(oldSchedule); // Copy old schedule to the current schedule
			}
			if(totalOptimizationCounter == totalEmployees){ // When there is no employee left to be optimized
				allEmployeesOptimized = true; // Turn the switch to true to exit the big while loop
			}
			//System.out.println("STOP");			
		}		
		return currentSchedule;
	}
}