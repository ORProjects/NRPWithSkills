public class Schedule {
	public static boolean verifyFeasibility(String[][][][] schedule, int[] employeeVerificationList){
		boolean feasibility = true; // initiate the return value to false to start with
		//Data.employees[0][4] = "7560";
		//employeeVerificationList = new int[]{2,5};
		//		if(employeeVerificationList != null){ // Check if the list is NOT empty.
		//			for(int n = 0; n < employeeVerificationList.length; n++){ // For each employee to be verified in the list
		//				int consecutiveDaysOnCounter = 0; // This counter will count number of consecutive days for the employee
		//				int consecutiveDaysOffCounter = 0; // This counter will count number of consecutive days for the employee
		//				int totalWeekendCounter = 0; // This counter will count number of consecutive days for the employee
		//				int[] shiftCounter = new int[schedule[0][0].length]; // This counter will capture the total assignments per shift per employee
		//				int totalWorkTime = 0; // This counter will capture total time worked throughout the planning horizon
		//				for(int d = 0; d < schedule[0].length; d++){ // For each day in the horizon
		//					// „„„„„„„„„„„„„„„„„„„„
		//					// |||||| PART 1 |||||| Verify the maximum and minimum consecutive work we well as the minimum rest time
		//					// ””””””””””””””””””””
		//					int assignmentMade = 0; // 
		//					// *** Count number of consecutive assignments ***
		//					for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
		//						if(schedule[employeeVerificationList[n]][d][s][u].equalsIgnoreCase(Integer.toString(t))){ // If there is an assignment
		//							consecutiveDaysOnCounter = consecutiveDaysOnCounter + 1; // Increase the number of assignments
		//							assignmentMade = 1; // Mark that there is an assignment for the day
		//							shiftCounter[s] = shiftCounter[s] + 1; // Increase the number of assignments per shift
		//							totalWorkTime = totalWorkTime + Integer.parseInt(Data.shifts[s][3]); // Increase the total work time
		//						}
		//					}
		//					//*** Count number of consecutive off days ***
		//					if(assignmentMade != 1){ // If there is NO assignment
		//						consecutiveDaysOffCounter = consecutiveDaysOffCounter + 1; // Increase the number of no assignments
		//					}
		//					// *** Make a Decision ***
		//					if(consecutiveDaysOnCounter == 1){ // If a new assignment set starts, make a decision about the consecutive days off
		//						if(!(d < 2)){ // Skip decision making for the first two days
		//							if(consecutiveDaysOffCounter < Integer.parseInt(Data.employees[employeeVerificationList[n]][3])){ // If employee rests less than the required rest time
		//								feasibility = false; // Set the schedule to infeasible
		//								return feasibility; // No need to continue further
		//							}
		//						}
		//						consecutiveDaysOffCounter = 0; // Reset the consecutive days off counter
		//					}else if(consecutiveDaysOffCounter == 1){
		//						if(!(d < 2)){ // Skip decision making for the first two days
		//							// If employee works more than allowed or less than required
		//							if(consecutiveDaysOnCounter > Integer.parseInt(Data.employees[employeeVerificationList[n]][1]) || consecutiveDaysOnCounter < Integer.parseInt(Data.employees[employeeVerificationList[n]][2])){
		//								feasibility = false; // Set the schedule to infeasible
		//								return feasibility; // No need to continue further
		//							}
		//						}
		//						consecutiveDaysOnCounter = 0; // Reset the consecutive days on counter
		//					}
		//
		//					// „„„„„„„„„„„„„„„„„„„„
		//					// |||||| PART 2 |||||| Verify the maximum weekend allowance
		//					// ””””””””””””””””””””
		//					if((d+1)%7 == 0){ // If the day is the Sunday
		//						int interimWeekendCounter = 0;
		//						for(int weekend = (d - 1); weekend < (d + 1); weekend++){
		//							for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
		//								if(schedule[employeeVerificationList[n]][weekend][s].equalsIgnoreCase(Integer.toString(t))){ // If there is an assignment
		//									interimWeekendCounter = interimWeekendCounter + 1; // Increase the number of assignments for the weekend
		//								}
		//							}
		//						}
		//						if(interimWeekendCounter > 0){ // After the loop of the weekend, if Saturday or Sunday is assigned
		//							totalWeekendCounter = totalWeekendCounter + 1; // Increase the total weekend count
		//						}
		//					}
		//					// „„„„„„„„„„„„„„„„„„„„
		//					// |||||| PART 3 |||||| Verify if an assignment is made to a vacation day
		//					// ””””””””””””””””””””
		//					if(Data.requirementsEmployee[d][0][employeeVerificationList[n]] == -333 && assignmentMade == 1){ // If the day needs to be a vacation day and the employee is still assigned
		//						feasibility = false; // Set the schedule to infeasible 
		//						return feasibility; // No need to continue further
		//					}
		//					// „„„„„„„„„„„„„„„„„„„„
		//					// |||||| PART 4 |||||| Verify if the shift can be assigned (a.k.a. shift after shift) 
		//					// ””””””””””””””””””””
		//					if(d != 0){ // If it is not the first day
		//						for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
		//							if(schedule[employeeVerificationList[n]][d][s][u].equalsIgnoreCase(Integer.toString(t))){ // If there is an assignment
		//								for(int prevShift = 0; prevShift < schedule[0][0].length; prevShift++){ // For each shift
		//									if(schedule[employeeVerificationList[n]][d-1][prevShift].equalsIgnoreCase(Integer.toString(prevShift))){ // If there is an assignment on the previous day
		//										if(Data.shiftsAfterShift[prevShift][s] == 0){ // If the shift on day d is not allowed after the shift on day d-1
		//											feasibility = false; // Set the schedule to infeasible 
		//											return feasibility; // No need to continue further
		//										}
		//									}
		//								}
		//							}
		//						}
		//					}
		//				}
		//				// *** Make a Decision for total weekend assignments ***
		//				if(totalWeekendCounter > Integer.parseInt(Data.employees[employeeVerificationList[n]][6])){ // Check if total weekend assignment is greater than the allowed
		//					feasibility = false; // Set the schedule to infeasible 
		//					return feasibility; // No need to continue further
		//				}
		//				// „„„„„„„„„„„„„„„„„„„„
		//				// |||||| PART 5 |||||| Verify total shift limits
		//				// ””””””””””””””””””””
		//				for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
		//					if(shiftCounter[s] > Integer.parseInt(Data.employeeShiftLimit[employeeVerificationList[n]][s])){ // If the total count per shift is greater than the allowed
		//						feasibility = false; // Set the schedule to infeasible 
		//						return feasibility; // No need to continue further
		//					}
		//				}
		//				// „„„„„„„„„„„„„„„„„„„„
		//				// |||||| PART 6 |||||| Verify total work time limits
		//				// ””””””””””””””””””””
		//				// If employee works more than the max limit or less than the min limit
		//				if(totalWorkTime > Integer.parseInt(Data.employees[employeeVerificationList[n]][4]) || totalWorkTime < Integer.parseInt(Data.employees[employeeVerificationList[n]][5])){
		//					feasibility = false; // Set the schedule to infeasible 
		//					return feasibility; // No need to continue further
		//				}
		//			}
		//		}else{ // Verify all employees
		//			

		for(int n = 0; n < schedule.length; n++){ // For each employee to be verified in the list
			int consecutiveDaysOnCounter = 0; // This counter will count number of consecutive days for the employee
			int consecutiveDaysOffCounter = 0; // This counter will count number of consecutive days for the employee
			int totalWeekendCounter = 0; // This counter will count number of consecutive days for the employee
			int[] shiftCounter = new int[schedule[0][0].length]; // This counter will capture the total assignments per shift per employee
			int totalWorkTime = 0; // This counter will capture total time worked throughout the planning horizon
			String[] nurseSkillGroups = new String[Data.numberOfSkills]; // Capture the skill groups of the current nurse							
			for (int i = 0; i < Data.numberOfSkills; i++){ // For every skill of the current nurse								
				if(Data.employees[n][7 + i] != null){ // If employee has a skill
					for (int j = 0; j < Data.numberOfSkillGroups; j++){ // Check each skill group
						for (int l = 0; l < Data.numberOfSkills; l++){ // Check each skill in the current skill group
							// If skills in the skill group are not empty and equal to the skill of the current nurse
							if(Data.skillGroups[j][1 + l] != null && Data.skillGroups[j][1 + l].equalsIgnoreCase(Data.employees[n][7 + i])){
								nurseSkillGroups[j] = String.valueOf(j+1); // Capture the skill group
							}
						}
					}
				}
			}
			for(int d = 0; d < schedule[0].length; d++){ // For each day in the horizon
				// „„„„„„„„„„„„„„„„„„„„
				// |||||| PART 1 |||||| Verify the maximum and minimum consecutive work we well as the minimum rest time
				// ””””””””””””””””””””
				int assignmentMade = 0; //
				int assignedUnit = -1; // To capture the unit assigned to
				int assignedShift = -1; // To capture the assigned shift
				// *** Count number of consecutive assignments ***
				for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
					for(int u = 0; u < schedule[0][0][0].length; u++){ // For each unit
						if(schedule[n][d][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
							consecutiveDaysOnCounter = consecutiveDaysOnCounter + 1; // Increase the number of assignments
							assignmentMade = 1; // Mark that there is an assignment for the day
							assignedUnit = u; // Set the unit
							assignedShift = s; // Set the shift
							shiftCounter[s] = shiftCounter[s] + 1; // Increase the number of assignments per shift
							totalWorkTime = totalWorkTime + Integer.parseInt(Data.shifts[s][3]); // Increase the total work time
						}
					}
				}
				//*** Count number of consecutive off days ***
				if(assignmentMade != 1){ // If there is NO assignment
					consecutiveDaysOffCounter = consecutiveDaysOffCounter + 1; // Increase the number of no assignments
				}
				// *** Make a Decision ***
				if(consecutiveDaysOnCounter > Integer.parseInt(Data.employees[n][1])){
					feasibility = false; // Set the schedule to infeasible
					return feasibility; // No need to continue further
				}else{
					if(assignmentMade == 1 && consecutiveDaysOnCounter == 1){ // If a new assignment set starts, make a decision about the consecutive days off
						if(!(d < Integer.parseInt(Data.employees[n][3]))){ // Skip decision making for the first two days
							if(consecutiveDaysOffCounter < Integer.parseInt(Data.employees[n][3])){ // If employee rests less than the required rest time
								feasibility = false; // Set the schedule to infeasible
								return feasibility; // No need to continue further
							}
						}
						consecutiveDaysOffCounter = 0; // Reset the consecutive days off counter
					}else if(assignmentMade != 1 && consecutiveDaysOffCounter == 1){
						if(!(d < Integer.parseInt(Data.employees[n][2]))){ // Skip decision making for the first two days
							// If employee works more than allowed or less than required
							if(consecutiveDaysOnCounter > Integer.parseInt(Data.employees[n][1]) || consecutiveDaysOnCounter < Integer.parseInt(Data.employees[n][2])){
								feasibility = false; // Set the schedule to infeasible
								return feasibility; // No need to continue further
							}
						}
						consecutiveDaysOnCounter = 0; // Reset the consecutive days on counter
					}
				}
				// „„„„„„„„„„„„„„„„„„„„
				// |||||| PART 2 |||||| Verify the maximum weekend allowance
				// ””””””””””””””””””””
				if((d+1)%7 == 0){ // If the day is the Sunday
					int interimWeekendCounter = 0;
					for(int weekend = (d - 1); weekend < (d + 1); weekend++){
						for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
							for(int u = 0; u < schedule[0][0][0].length; u++){ // For each unit
								if(schedule[n][weekend][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
									interimWeekendCounter = interimWeekendCounter + 1; // Increase the number of assignments for the weekend
								}
							}
						}
					}
					if(interimWeekendCounter > 0){ // After the loop of the weekend, if Saturday or Sunday is assigned
						totalWeekendCounter = totalWeekendCounter + 1; // Increase the total weekend count
					}
				}
				// „„„„„„„„„„„„„„„„„„„„
				// |||||| PART 3 |||||| Verify if an assignment is made to a vacation day
				// ””””””””””””””””””””
				if(Data.requirementsEmployee[d][0][n] == -333 && assignmentMade == 1){ // If the day needs to be a vacation day and the employee is still assigned
					feasibility = false; // Set the schedule to infeasible 
					return feasibility; // No need to continue further
				}
				// „„„„„„„„„„„„„„„„„„„„
				// |||||| PART 4 |||||| Verify if the shift can be assigned (a.k.a. shift after shift) 
				// ””””””””””””””””””””
				if(d != 0){ // If it is not the first day
					for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
						for(int u = 0; u < schedule[0][0][0].length; u++){ // For each unit
							if(schedule[n][d][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
								for(int prevShift = 0; prevShift < schedule[0][0].length; prevShift++){ // For each shift
									for(int prevu = 0; prevu < schedule[0][0][0].length; prevu++){ // For each unit
										if(schedule[n][d-1][prevShift][prevu].equalsIgnoreCase(Integer.toString(prevu))){ // If there is an assignment on the previous day
											if(Data.shiftsAfterShift[prevShift][s] == 0){ // If the shift on day d is not allowed after the shift on day d-1
												feasibility = false; // Set the schedule to infeasible 
												return feasibility; // No need to continue further
											}
										}
									}
								}
							}
						}
					}
				}
				//Print.generateExcel(schedule, "initialSchedule"); // Export to excel
				// „„„„„„„„„„„„„„„„„„„„
				// |||||| PART 5 |||||| Verify department unit and skill group assignment
				// ””””””””””””””””””””
				if(assignmentMade == 1){ // If there is an assignment
					int skillGroupCount = 0; // Count number of matching skill groups
					for (int i = 0; i < nurseSkillGroups.length; i++){ // For every skill group of the current nurse
						if(nurseSkillGroups[i] != null){ // If nurse skill group has a valid value
							if(Data.requirementsCover[d][assignedShift][assignedUnit][4] == Integer.parseInt(nurseSkillGroups[i])){ // if it matches with the needed value for the day
								skillGroupCount = skillGroupCount + 1; // Increase the counter 		
							}
						}
					}
					if(skillGroupCount == 0){ // If there was no skill group found
						feasibility = false; // Set the schedule to infeasible
						return feasibility; // No need to continue further
					}
				}
			}
			// *** Make a Decision for total weekend assignments ***
			if(totalWeekendCounter > Integer.parseInt(Data.employees[n][6])){ // Check if total weekend assignment is greater than the allowed
				feasibility = false; // Set the schedule to infeasible 
				return feasibility; // No need to continue further
			}
			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 6 |||||| Verify total shift limits
			// ””””””””””””””””””””
			for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
				if(shiftCounter[s] > Integer.parseInt(Data.employeeShiftLimit[n][s])){ // If the total count per shift is greater than the allowed
					feasibility = false; // Set the schedule to infeasible 
					return feasibility; // No need to continue further
				}
			}
			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 7 |||||| Verify total work time limits
			// ””””””””””””””””””””
			// If employee works more than the max limit or less than the min limit
			if(totalWorkTime > Integer.parseInt(Data.employees[n][4]) || totalWorkTime < Integer.parseInt(Data.employees[n][5])){
				feasibility = false; // Set the schedule to infeasible 
				return feasibility; // No need to continue further
			}
		}




		//	}
		return feasibility;
	}


	public static int[] calculateObjective(String[][][][] schedule, int[] dayVerificationList){
		int[] dailyCost = new int[schedule[0].length+1];
		int objectiveValue = 0; // Initiate the objective value
		//		if(dayVerificationList != null){ // Check if the list is NOT empty.
		//			for(int d = 0; d < dayVerificationList.length; d++){ // For all the days in the schedule
		//				int[] employeeExamined = new int[schedule.length]; // Capture the employee whose shift is examined
		//				int[] assignmentPerShift = new int[schedule[0][0].length]; // Capture the total assigned employees per shift
		//				for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
		//					for(int n = 0; n < schedule.length; n++){ // For each employee
		//						// „„„„„„„„„„„„„„„„„„„„
		//						// |||||| PART 1 |||||| Evaluate employee preferences
		//						// ””””””””””””””””””””
		//						if(employeeExamined[n] != 1){ // If employee's shifts are already looked at, skip
		//							int assignedShift = -1; // Start the shift assignment as no shift for the time being
		//							for(int shifts = 0; shifts < schedule[0][0].length; shifts++){ // Loop all the shifts
		//								if(schedule[n][dayVerificationList[d]][shifts].equalsIgnoreCase(Integer.toString(shifts))){ // If there is an assignment
		//									assignedShift = shifts; // Capture the assigned shift
		//									assignmentPerShift[shifts] = assignmentPerShift[shifts] + 1; // Increase the total employees per shift
		//								}
		//							}
		//							if(assignedShift == -1){ // If employee is not assigned to any shift
		//								for(int shifts = 0; shifts < schedule[0][0].length; shifts++){ // Loop all the shifts
		//									if(Data.requirementsEmployee[dayVerificationList[d]][shifts][n] > 0){ // Exclude the current shift and look for requests on other shifts
		//										objectiveValue = objectiveValue + Data.requirementsEmployee[dayVerificationList[d]][shifts][n]; // Penalize for requested shift ignored
		//									}
		//								}
		//							}else{ // If employee is already assigned to a shift
		//								if(Data.requirementsEmployee[dayVerificationList[d]][assignedShift][n] < 0){ // if employee does not want the assignment
		//									objectiveValue = objectiveValue + (-1) * Data.requirementsEmployee[dayVerificationList[d]][assignedShift][n]; // Penalize for assigning
		//								}else{ // if employee wants other shifts
		//									for(int shifts = 0; shifts < schedule[0][0].length; shifts++){ // Loop all the shifts
		//										if(shifts != assignedShift && Data.requirementsEmployee[dayVerificationList[d]][shifts][n] > 0){ // Exclude the current shift and look for requests on other shifts
		//											objectiveValue = objectiveValue + Data.requirementsEmployee[dayVerificationList[d]][shifts][n]; // Penalize for requested shift ignored
		//										}
		//									}
		//								}
		//
		//							}
		//							employeeExamined[n] = 1; // Mark the employee as examined. No need to loop for the same person
		//						}
		//					}
		//					// „„„„„„„„„„„„„„„„„„„„
		//					// |||||| PART 2 |||||| Evaluate Cover Needs
		//					// ””””””””””””””””””””
		//					if(assignmentPerShift[s] < Data.requirementsCover[dayVerificationList[d]][s][0] ){ // The total assignment for the shift is less than minimum cover need
		//						// Apply the penalty for each under assignment
		//						objectiveValue = objectiveValue + (Data.requirementsCover[dayVerificationList[d]][s][0] - assignmentPerShift[s]) * Data.requirementsCover[dayVerificationList[d]][s][1];
		//					}else if(assignmentPerShift[s] > Data.requirementsCover[dayVerificationList[d]][s][2]){ // The total assignment for the shift is more than maximum cover need
		//						// Apply the penalty for each over assignment
		//						objectiveValue = objectiveValue + (assignmentPerShift[s] - Data.requirementsCover[dayVerificationList[d]][s][2]) * Data.requirementsCover[dayVerificationList[d]][s][3];
		//					}
		//				}				
		//			}
		//		}else{ // Calculate objective for the entire schedule
		int tempCost = 0;
		for(int d = 0; d <= schedule[0].length; d++){ // For all the days in the schedule
			if(d != schedule[0].length){
				int[] employeeExamined = new int[schedule.length]; // Capture the employee whose shift is examined
				int[][] shiftUnitAssignment = new int[schedule[0][0].length][schedule[0][0][0].length]; // Capture the total assigned employees per shift
				for(int n = 0; n < schedule.length; n++){ // For each employee
					// „„„„„„„„„„„„„„„„„„„„
					// |||||| PART 1 |||||| Evaluate employee preferences
					// ””””””””””””””””””””
					if(employeeExamined[n] != 1){ // If employee's shifts are already looked at, skip
						int assignedShift = -1; // Start the shift assignment as no shift for the time being
						for(int shift = 0; shift < schedule[0][0].length; shift++){ // Loop all the shifts
							for(int unit = 0; unit < schedule[0][0][0].length; unit++){ // For each unit
								if(schedule[n][d][shift][unit].equalsIgnoreCase(Integer.toString(unit))){ // If there is an assignment
									assignedShift = shift; // Capture the assigned shift
									shiftUnitAssignment[shift][unit] = shiftUnitAssignment[shift][unit] + 1; // Increase the total employees per shift
								}
							}
						}
						if(assignedShift == -1){ // If employee is not assigned to any unit
							for(int shift = 0; shift < schedule[0][0].length; shift++){ // Loop all the shifts
								if(Data.requirementsEmployee[d][shift][n] > 0){ // Exclude the current shift and look for requests on other shifts
									if(Data.requirementsEmployee[d][shift][n] == 999){ // If a day on request is violated
										objectiveValue = objectiveValue + 6; // Penalize for ignored day-on request
									}else{// If a shift on request is violated
										objectiveValue = objectiveValue + Data.requirementsEmployee[d][shift][n]; // Penalize for requested shift ignored	
									}
								}
							}
						}else{ // If employee is already assigned to a shift
							if(Data.requirementsEmployee[d][assignedShift][n] < 0){ // if employee does not want the assignment
								if(Data.requirementsEmployee[d][assignedShift][n] == -999){ // If a day off request is violated
									objectiveValue = objectiveValue + 9; // Penalize for ignored day-off request
								}else{
									objectiveValue = objectiveValue + (-1) * Data.requirementsEmployee[d][assignedShift][n]; // Penalize for assigning
								}
							}else{ // if employee wants other shifts
								for(int shift = 0; shift < schedule[0][0].length; shift++){ // Loop all the shifts
									if(Data.requirementsEmployee[d][shift][n] != 999 && shift != assignedShift && Data.requirementsEmployee[d][shift][n] > 0){ // Exclude the current shift and look for requests on other shifts
										objectiveValue = objectiveValue + Data.requirementsEmployee[d][shift][n]; // Penalize for requested shift ignored
									}
								}
							}

						}
						employeeExamined[n] = 1; // Mark the employee as examined. No need to loop for the same person
					}
				}
				for(int s = 0; s < schedule[0][0].length; s++){ // For each shift
					for(int u = 0; u < schedule[0][0][0].length; u++){ // For each unit

						// „„„„„„„„„„„„„„„„„„„„
						// |||||| PART 2 |||||| Evaluate Cover Needs
						// ””””””””””””””””””””
						if(shiftUnitAssignment[s][u] < Data.requirementsCover[d][s][u][0] ){ // The total assignment for the shift is less than minimum cover need
							// Apply the penalty for each under assignment
							objectiveValue = objectiveValue + (Data.requirementsCover[d][s][u][0] - shiftUnitAssignment[s][u]) * Data.requirementsCover[d][s][u][1];
						}else if(shiftUnitAssignment[s][u] > Data.requirementsCover[d][s][u][2]){ // The total assignment for the shift is more than maximum cover need
							// Apply the penalty for each over assignment
							objectiveValue = objectiveValue + (shiftUnitAssignment[s][u] - Data.requirementsCover[d][s][u][2]) * Data.requirementsCover[d][s][u][3];
						}
					}
				}
				if(d == 0){ // If it is the first day
					dailyCost[d] = objectiveValue; // Add objective value to the first day
					tempCost = objectiveValue;
				}else{// For other days
					dailyCost[d] = objectiveValue - tempCost; // Cost of the day is the objective minus the cost of the previous day
					tempCost = objectiveValue;
				}
			}else{
				dailyCost[d] = objectiveValue;
			}
		}
		//		}
		return dailyCost;
	}


	public static int calculateObjective(String[][][][] schedule){
		int[] objectiveValue = Schedule.calculateObjective(schedule, null);
		return objectiveValue[objectiveValue.length-1];
	}

	
	public static String[][][][] copyArrays(String[][][][] StringArray){
		String[][][][] newArray = new String[StringArray.length][StringArray[0].length][StringArray[0][0].length][StringArray[0][0][0].length]; // Define the new schedule
		for(int i = 0; i < StringArray.length; i++){
			for(int j = 0; j < StringArray[0].length; j++){
				for(int k = 0; k < StringArray[0][0].length; k++){
					for(int l = 0; l < StringArray[0][0][0].length; l++){
						newArray[i][j][k][l] = StringArray[i][j][k][l];
					}
				}
			}
		}
		return newArray;
	}
}