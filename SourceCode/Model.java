import java.io.*;
import ilog.concert.*;
import ilog.cplex.*;

public class Model {
	public static String [][][][] solve(int optimizationDay, int[] optimizationList, String [][][][] initialSchedule, int numberOfNurses, int numberOfDays, int numberOfShifts, int numberOfWeekends, String shiftLimit[][], String shifts[][], String employeeDetails[][], int employeeReq[][][], int coverReq[][][][]) throws FileNotFoundException, IOException{
		// ====================================================================
		// ========================= DATA =====================================
		// ====================================================================
		String [][][][] finalSchedule = null;
		int numberOfUnits = Data.numberOfSkillGroups; // Set number of departmental units
		// Data is passed from the calling class 

		try{
			// ====================================================================
			// =========================    PARAMETERS    =========================
			// ====================================================================

			//optimizationSize = optimizationList.length; // Capture the size of the optimization list

			// ====================================================================
			// ========================= DEFINE THE MODEL =========================
			// ====================================================================

			IloCplex cplex = new IloCplex();

			// ====================================================================
			// ===================== DECISION VARIABLES ===========================
			// ====================================================================

			// *** X Variable ***

			IloNumVar[][][][] x = new IloNumVar[numberOfNurses][numberOfDays][numberOfShifts][]; // Define the variable X
			for (int n = 0; n < numberOfNurses; n++){ // For all nurses
				for (int d = 0; d < numberOfDays; d++){ // For all days
					for (int s = 0; s < numberOfShifts; s++){ // For all shifts
						x[n][d][s] = cplex.boolVarArray(numberOfUnits); // X is numberOfNurses by numberOfDays by numberOfShifts by numberOfUnits
					}
				}
			}

			// *** K Variable ***

			IloNumVar[][] k = new IloNumVar[numberOfNurses][]; // Define the variable K
			for (int n = 0; n < numberOfNurses; n++){
				k[n] = cplex.boolVarArray(numberOfWeekends); // K is numberOfNurses by numberOfWeekends
			}

			// *** U Variable ***

			IloNumVar[][][] un = new IloNumVar[numberOfDays][numberOfShifts][]; // Define the variable U - Understaffing
			for (int d = 0; d < numberOfDays; d++){
				for (int s = 0; s < numberOfShifts; s++){
					un[d][s] = cplex.intVarArray(numberOfUnits, 0, Integer.MAX_VALUE); // U is numberOfDays by numberOfShifts by numberOfUnits
				}
			}			

			// *** O Variable ***

			IloNumVar[][][] ov = new IloNumVar[numberOfDays][numberOfShifts][]; // Define the variable O - Overstaffing
			for (int d = 0; d < numberOfDays; d++){
				for (int s = 0; s < numberOfShifts; s++){
					ov[d][s] = cplex.intVarArray(numberOfUnits, 0, Integer.MAX_VALUE); // O is numberOfDays by numberOfShifts by numberOfUnits
				}
			}

			// *** MS Variable ***

			IloNumVar[][][] ms = new IloNumVar[numberOfDays][numberOfShifts][]; // Define the variable MS - Number of nurses missing preferred skill
			for (int d = 0; d < numberOfDays; d++){
				for (int s = 0; s < numberOfShifts; s++){
					ms[d][s] = cplex.intVarArray(numberOfUnits, 0, Integer.MAX_VALUE);  // ms is numberOfDays by numberOfShifts
				}
			}

			System.out.println("END OF DECISION VARIABLES...");

			// ====================================================================
			// ======================== FIX VARIABLES =============================
			// ====================================================================

			if(initialSchedule != null){
				for (int n = 0; n < numberOfNurses; n++){ // For all nurses
					boolean nurseInOptList = false; // Mark the current nurse as not in optimization list
					for (int i = 0; i < optimizationList.length; i++){ // For all nurses
						if(n == optimizationList[i]){
							nurseInOptList = true; // The current nurse is in the optimization list
						}
					}
					if(nurseInOptList == true){ // If the nurse is in the optimization list
						for (int d = 0; d < numberOfDays; d++){ // For all days
							if(d == optimizationDay){ // If the current day is the optimization day
								for (int s = 0; s < numberOfShifts; s++){ // For all shifts
									for (int u = 0; u < numberOfUnits; u++){ // For each unit
										if(initialSchedule[n][d][s][u].equalsIgnoreCase(Integer.toString(u))){ // If a shift is assigned
											x[n][d][s][u].setLB(1.0);
											x[n][d][s][u].setUB(1.0);
										}else{
											x[n][d][s][u].setLB(0.0);
											x[n][d][s][u].setUB(0.0);
										}
									}
								}
							}
						}	
					}else{ // Otherwise
						for (int d = 0; d < numberOfDays; d++){ // For all days
							for (int s = 0; s < numberOfShifts; s++){ // For all shifts
								for (int u = 0; u < numberOfUnits; u++){ // For each unit
									if(initialSchedule[n][d][s][u].equalsIgnoreCase(Integer.toString(u))){ // If a shift is assigned
										x[n][d][s][u].setLB(1.0);
										x[n][d][s][u].setUB(1.0);
									}else{
										x[n][d][s][u].setLB(0.0);
										x[n][d][s][u].setUB(0.0);
									}
								}
							}
						}
					}
				}
			}

			// ====================================================================
			// ========================= CONSTRAINTS ==============================
			// ====================================================================

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 1 |||||| Only one shift assignment is allowed per day per employee 
			// ””””””””””””””””””””

			// Expression Part

			IloLinearNumExpr[][] totalNurseOnDay = new IloLinearNumExpr[numberOfNurses][numberOfDays];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					totalNurseOnDay[n][d] = cplex.linearNumExpr(); // Initiate the expression
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							totalNurseOnDay[n][d].addTerm(1.0, x[n][d][s][u]); // Add all days together
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					cplex.addLe(totalNurseOnDay[n][d], 1); // Sum of shifts must be less than or equal to 1 
				}
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 2 |||||| Certain shifts cannot follow others - WIP
			// ””””””””””””””””””””			

			// Expression Part

			IloLinearNumExpr[][][] totalNurseOnDayOnShift = new IloLinearNumExpr[numberOfNurses][numberOfDays][numberOfShifts];
			IloLinearNumExpr[][][] totalNurseOnNextDayOnShift = new IloLinearNumExpr[numberOfNurses][numberOfDays][numberOfShifts];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						totalNurseOnDayOnShift[n][d][s] = cplex.linearNumExpr(); // Initiate the expression
						totalNurseOnNextDayOnShift[n][d][s] = cplex.linearNumExpr(); // Initiate the expression
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							totalNurseOnDayOnShift[n][d][s].addTerm(1.0, x[n][d][s][u]); // Add
							if(d != (numberOfDays-1)){
								totalNurseOnNextDayOnShift[n][d][s].addTerm(1.0, x[n][d+1][s][u]); // Add
							}
						}
					}
				}
			}

			// Constraint Part
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < (numberOfDays - 1); d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						for (int s1 = 0; s1 < numberOfShifts; s1++){ // For each shift
							if(Data.shiftsAfterShift[s][s1]==0){
								cplex.addLe(cplex.sum(totalNurseOnDayOnShift[n][d][s], totalNurseOnNextDayOnShift[n][d][s1]), 1.0);
							}
						}
					}
				}
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 3 |||||| There is a maximum limitation of a shift per employee
			// ””””””””””””””””””””

			// Expression Part

			IloLinearNumExpr[][] totalShiftOnNurse = new IloLinearNumExpr[numberOfNurses][numberOfShifts];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					totalShiftOnNurse[n][s] = cplex.linearNumExpr();
					for (int d = 0; d < numberOfDays; d++){ // For each day
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							totalShiftOnNurse[n][s].addTerm(1.0, x[n][d][s][u]); // Add
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					cplex.addLe(totalShiftOnNurse[n][s], Integer.parseInt(shiftLimit[n][s])); // Sum of the assigned shifts must be less than or equal to the limit
				}
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 4 |||||| The minimum and the maximum work time
			// ””””””””””””””””””””

			// Expression Part

			IloLinearNumExpr[] totalWorkTime = new IloLinearNumExpr[numberOfNurses];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				totalWorkTime[n] = cplex.linearNumExpr();
				for (int d = 0; d < numberOfDays; d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							totalWorkTime[n].addTerm(Integer.parseInt(shifts[s][3]), x[n][d][s][u]); // Add all shifts together multiplied with shift length
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				cplex.addLe(totalWorkTime[n], Integer.parseInt(employeeDetails[n][4])); // Total time worked must be less than or equal to the max limit
				cplex.addGe(totalWorkTime[n], Integer.parseInt(employeeDetails[n][5])); // Total time worked must be greater than or equal to the min limit
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 5 |||||| Maximum number of shifts an employee can work without taking a day off
			// ””””””””””””””””””””

			// Expression Part

			IloLinearNumExpr[][] consecutiveDaysWorked = new IloLinearNumExpr[numberOfNurses][numberOfDays];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < (numberOfDays - Integer.parseInt(employeeDetails[n][1])); d++){ // For each day
					consecutiveDaysWorked[n][d] = cplex.linearNumExpr();
					for (int j = d; j <= (d + Integer.parseInt(employeeDetails[n][1])); j++){ // For maximum days
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								consecutiveDaysWorked[n][d].addTerm(1.0, x[n][j][s][u]); // Add all shifts to consecutive days expression
							}
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < (numberOfDays - Integer.parseInt(employeeDetails[n][1])); d++){ // For each day
					cplex.addLe(consecutiveDaysWorked[n][d], Integer.parseInt(employeeDetails[n][1])); // Each day must be in line with the maximum consecutive shift rule
				}
			}

			System.out.println("JUST STOP...");

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 6 |||||| Minimum consecutive shifts
			// ””””””””””””””””””””

			// Expression Part - 1

			IloLinearNumExpr[][][] firstDayAssignment = new IloLinearNumExpr[numberOfNurses][numberOfNurses][numberOfDays]; // Capture all the assignments for a nurse in a day
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][2]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						firstDayAssignment[n][m][d] = cplex.linearNumExpr();
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								firstDayAssignment[n][m][d].addTerm(1.0, x[n][d][s][u]); // Add all shifts together
							}
						}
					}
				}
			}

			// Expression Part - 2

			IloLinearNumExpr[][][] middleDaysAssignment = new IloLinearNumExpr[numberOfNurses][numberOfNurses][numberOfDays]; // Capture all the assignments for a nurse in a day
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][2]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						middleDaysAssignment[n][m][d] = cplex.linearNumExpr();
						for (int j = (d+1); j <= (d + m); j++){ // For minimum consecutive work
							for (int s = 0; s < numberOfShifts; s++){ // For each shift
								for (int u = 0; u < numberOfUnits; u++){ // For each unit
									middleDaysAssignment[n][m][d].addTerm(-1.0, x[n][j][s][u]); // Add all shifts together and multiply with -1.0
								}
							}
						}
					}
				}
			}

			// Expression Part - 3

			IloLinearNumExpr[][][] lastDayAssignment = new IloLinearNumExpr[numberOfNurses][numberOfNurses][numberOfDays]; // Capture all the assignments for a nurse in a day
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][2]) - 1); m++){ // For each minimum work day
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						lastDayAssignment[n][m][d] = cplex.linearNumExpr();
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								lastDayAssignment[n][m][d].addTerm(1.0, x[n][d+m+1][s][u]); // Add all shifts together
							}
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][2]) - 1); m++){ // For each minimum work limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						cplex.addGe(cplex.sum(firstDayAssignment[n][m][d],cplex.sum(m, middleDaysAssignment[n][m][d]), lastDayAssignment[n][m][d]), 1);
					}
				}
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 7 |||||| Minimum consecutive days off
			// ””””””””””””””””””””

			// Expression Part - 1

			firstDayAssignment = new IloLinearNumExpr[numberOfNurses][numberOfNurses][numberOfDays]; // Capture all the assignments for a nurse in a day
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][3]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						firstDayAssignment[n][m][d] = cplex.linearNumExpr();
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								firstDayAssignment[n][m][d].addTerm(1.0, x[n][d][s][u]); // Add all shifts together
							}
						}
					}
				}
			}

			// Expression Part - 2

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][3]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						middleDaysAssignment[n][m][d] = cplex.linearNumExpr();
						for (int j = (d+1); j <= (d + m); j++){ // For minimum consecutive work
							for (int s = 0; s < numberOfShifts; s++){ // For each shift
								for (int u = 0; u < numberOfUnits; u++){ // For each unit
									middleDaysAssignment[n][m][d].addTerm(1.0, x[n][j][s][u]); // Add all shifts together
								}
							}
						}
					}
				}
			}

			// Expression Part - 3

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][3]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						lastDayAssignment[n][m][d] = cplex.linearNumExpr();
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								lastDayAssignment[n][m][d].addTerm(-1.0, x[n][d+m+1][s][u]); // Add all shifts together
							}
						}
					}
				}
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int m = 1; m <= (Integer.parseInt(employeeDetails[n][3]) - 1); m++){ // For each minimum limit
					for (int d = 0; d < (numberOfDays - (m + 1)); d++){ // For each day
						cplex.addGe(cplex.sum(cplex.diff(1, firstDayAssignment[n][m][d]),middleDaysAssignment[n][m][d], cplex.sum(1, lastDayAssignment[n][m][d])), 1);
					}
				}
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 8 |||||| Maximum number of weekends
			// ””””””””””””””””””””

			// Expression Part - 1 - Saturday and Sunday

			IloLinearNumExpr[][] saturdayAssignments = new IloLinearNumExpr[numberOfNurses][numberOfWeekends];
			IloLinearNumExpr[][] sundayAssignments = new IloLinearNumExpr[numberOfNurses][numberOfWeekends];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int w = 0; w < numberOfWeekends; w++){ // For each weekend
					saturdayAssignments[n][w] = cplex.linearNumExpr();
					sundayAssignments[n][w] = cplex.linearNumExpr();
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							saturdayAssignments[n][w].addTerm(1.0, x[n][7*(w+1)-2][s][u]); // Add all saturday shifts together
							sundayAssignments[n][w].addTerm(1.0, x[n][7*(w+1)-1][s][u]); // Add all sunday shifts together
						}
					}
				}
			}

			// Expression Part - 2			

			IloLinearNumExpr[] totalWeekends = new IloLinearNumExpr[numberOfNurses];
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				totalWeekends[n] = cplex.linearNumExpr();
				for (int w = 0; w < numberOfWeekends; w++){ // For each weekend
					totalWeekends[n].addTerm(1.0, k[n][w]); // Add all the weekends
				}	
			}

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int w = 0; w < numberOfWeekends; w++){ // For each weekend
					cplex.addLe(k[n][w], cplex.sum(saturdayAssignments[n][w], sundayAssignments[n][w]));
					cplex.addLe(cplex.sum(saturdayAssignments[n][w], sundayAssignments[n][w]), cplex.prod(2, k[n][w]));
				}
				cplex.addLe(totalWeekends[n], Integer.parseInt(employeeDetails[n][6]));
			}

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 9 |||||| Predefined vacations
			// ””””””””””””””””””””

			// Constraint Part

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						for (int u = 0; u < numberOfUnits; u++){ // For each unit
							if(employeeReq[d][s][n]==-333){
								cplex.addEq(x[n][d][s][u], 0);
							}
						}
					}
				}
			}

			// „„„„„„„„„„„„„„„„„„„„„
			// |||||| PART 10 |||||| Required skill groups
			// ”””””””””””””””””””””

			// Constraint Part

			int skillGroupCounter; // To count number of skill group matches
			int nurseSkillGroup = 0; // To capture the skill group of a nurse 
			for (int d = 0; d < numberOfDays; d++){ // For each day
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					for (int u = 0; u < numberOfUnits; u++){ // For each unit
						for (int n = 0; n < numberOfNurses; n++){ // For each nurse
							skillGroupCounter = 0; // Reset the counter for skill groups							
							for (int i = 0; i < Data.numberOfSkills; i++){ // For every skill of the current nurse								
								if(employeeDetails[n][7 + i] != null){ // If employee has a skill
									for (int j = 0; j < Data.numberOfSkillGroups; j++){ // Check each skill group
										for (int l = 0; l < Data.numberOfSkills; l++){ // Check each skill in the current skill group
											// If skills in the skill group are not empty and equal to the skill of the current nurse
											if(Data.skillGroups[j][1 + l] != null && Data.skillGroups[j][1 + l].equalsIgnoreCase(employeeDetails[n][7 + i])){
												nurseSkillGroup = (j+1); // Capture the skill group
											}
										}
									}
									if(nurseSkillGroup == coverReq[d][s][u][4]){ // If the skill group of the nurse equals to the skill required for the cover
										skillGroupCounter = skillGroupCounter + 1; // Increase the counter
									}
								}
							}
							if(skillGroupCounter == 0){ // If the skill group does not exist at all for the current nurse
								cplex.addEq(x[n][d][s][u], 0); // Assignment cannot be made to that nurse
							}
						}
					}
				}
			}
			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 11 |||||| Cover Req - Assigning appropriate number of employees to certain shifts
			// ””””””””””””””””””””			

			// Expression Part
			IloLinearNumExpr[][][] totalEmployeesAssigned = new IloLinearNumExpr[numberOfDays][numberOfShifts][numberOfUnits];
			for (int d = 0; d < numberOfDays; d++){ // For each day
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					for (int u = 0; u < numberOfUnits; u++){ // For each unit
						totalEmployeesAssigned[d][s][u] = cplex.linearNumExpr();
						for (int n = 0; n < numberOfNurses; n++){ // For each nurse
							totalEmployeesAssigned[d][s][u].addTerm(1.0, x[n][d][s][u]); // Add all the employees to the expression
						}
					}
				}
			}

			// Constraint Part

			for (int d = 0; d < numberOfDays; d++){ // For each day
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					for (int u = 0; u < numberOfUnits; u++){ // For each unit
						cplex.addEq(cplex.sum(totalEmployeesAssigned[d][s][u], cplex.prod(-1.0, ov[d][s][u]), un[d][s][u]), coverReq[d][s][u][0]);
					}
				}
			}

			//			// „„„„„„„„„„„„„„„„„„„„
			//			// |||||| PART 12 |||||| Calculation of employees with missing preferred skillset
			//			// ””””””””””””””””””””			
			//
			//			// Expression Part
			//			totalEmployeesAssigned = new IloLinearNumExpr[numberOfDays][numberOfShifts][numberOfUnits];
			//			IloLinearNumExpr[][][] totalEmployeesWithPreferredSkillAssigned = new IloLinearNumExpr[numberOfDays][numberOfShifts][numberOfUnits];
			//			for (int d = 0; d < numberOfDays; d++){ // For each day
			//				for (int s = 0; s < numberOfShifts; s++){ // For each shift
			//					for (int u = 0; u < numberOfUnits; u++){ // For each unit
			//						totalEmployeesAssigned[d][s][u] = cplex.linearNumExpr();
			//						totalEmployeesWithPreferredSkillAssigned[d][s][u] = cplex.linearNumExpr();
			//						for (int n = 0; n < numberOfNurses; n++){ // For each nurse
			//							totalEmployeesAssigned[d][s][u].addTerm(1.0, x[n][d][s][u]); // Add all the employees to the expression
			//							for (int sk = 0; sk < Data.numberOfSkills; sk++){ // For each skill
			//								if(employeeDetails[n][7 + sk] != null){
			//									if(coverReq[d][s][u][5 + sk] == Integer.parseInt(employeeDetails[n][7 + sk])){
			//										totalEmployeesWithPreferredSkillAssigned[d][s][u].addTerm(1.0, x[n][d][s][u]); // Add all the employees to the expression
			//									}
			//								}
			//							}
			//						}
			//					}
			//				}
			//			}
			//
			//			// Constraint Part
			//
			//			for (int d = 0; d < numberOfDays; d++){ // For each day
			//				for (int s = 0; s < numberOfShifts; s++){ // For each shift
			//					for (int u = 0; u < numberOfUnits; u++){ // For each unit
			//						cplex.addEq(totalEmployeesAssigned[d][s][u], cplex.sum(totalEmployeesWithPreferredSkillAssigned[d][s][u], ms[d][s][u]));
			//					}
			//				}
			//			}

			System.out.println("END OF CONSTRAINTS...");

			// ====================================================================
			// ========================= OBJECTIVE ================================
			// ====================================================================

			IloNumExpr objective = cplex.numExpr(); // Objective expression
			IloNumExpr expr = cplex.numExpr(); // Expression to be used in calculations
			IloNumExpr unitTotal = cplex.numExpr(); // Expression to be used in calculations
			//int weightBelowSkill = 0;
			int weightDayOff = 9;
			int weightDayOn = 6;

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 1 |||||| Shift On Request Violation 
			// ””””””””””””””””””””

			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						if(employeeReq[d][s][n]>0 && employeeReq[d][s][n] != 999){
							unitTotal = cplex.numExpr();
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								unitTotal = cplex.sum(unitTotal, x[n][d][s][u]);
							}
							expr = cplex.sum(expr, cplex.prod(employeeReq[d][s][n], cplex.diff(1, unitTotal)));
						}
					}
				}
			}
			objective = expr;

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 2 |||||| Shift Off Request Violation 
			// ””””””””””””””””””””

			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						if(employeeReq[d][s][n] < 0 && employeeReq[d][s][n] != -333 && employeeReq[d][s][n] != -999){
							unitTotal = cplex.numExpr();
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								unitTotal = cplex.sum(unitTotal, x[n][d][s][u]);
							}
							expr = cplex.sum(expr, cplex.prod(-1*employeeReq[d][s][n], unitTotal));
						}
					}
				}
			}
			objective = cplex.sum(objective,expr); // Update the objective function

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 3 |||||| The number of staff below the needed amount for the shift 
			// ””””””””””””””””””””

			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			for (int d = 0; d < numberOfDays; d++){ // For each day
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					for (int u = 0; u < numberOfUnits; u++){ // For each unit
						expr = cplex.sum(expr, cplex.prod(coverReq[d][s][u][1], un[d][s][u]));
					}
				}
			}
			objective = cplex.sum(objective,expr); // Update the objective function

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 4 |||||| Over-staffing for a given shift in a given day 
			// ””””””””””””””””””””

			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			for (int d = 0; d < numberOfDays; d++){ // For each day
				for (int s = 0; s < numberOfShifts; s++){ // For each shift
					for (int u = 0; u < numberOfUnits; u++){ // For each unit
						expr = cplex.sum(expr, cplex.prod(coverReq[d][s][u][3], ov[d][s][u]));
					}
				}
			}
			objective = cplex.sum(objective,expr); // Update the objective function			

			//			// „„„„„„„„„„„„„„„„„„„„
			//			// |||||| PART 5 |||||| Penalty for missing preferred skillset 
			//			// ””””””””””””””””””””
			//
			//			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			//			for (int d = 0; d < numberOfDays; d++){ // For each day
			//				for (int s = 0; s < numberOfShifts; s++){ // For each shift
			//					for (int u = 0; u < numberOfUnits; u++){ // For each unit
			//						expr = cplex.sum(expr, cplex.prod(weightBelowSkill, ms[d][s][u]));
			//					}
			//				}
			//			}
			//			objective = cplex.sum(objective,expr); // Update the objective function	

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 6 |||||| Day On Request Violation 
			// ””””””””””””””””””””

			int currentNurse;
			int currentDay;
			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					unitTotal = cplex.numExpr();
					currentNurse = -1;
					currentDay = -1;
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						if(employeeReq[d][s][n] == 999){
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								unitTotal = cplex.sum(unitTotal, x[n][d][s][u]);
								currentNurse = n; 
								currentDay = d;
							}
						}
					}
					if(n == currentNurse && d == currentDay){
						expr = cplex.sum(expr, cplex.prod(weightDayOn, cplex.diff(1, unitTotal)));
					}
				}
			}
			objective = cplex.sum(objective,expr); // Update the objective function

			// „„„„„„„„„„„„„„„„„„„„
			// |||||| PART 7 |||||| Day Off Request Violation 
			// ””””””””””””””””””””

			expr = cplex.numExpr(); // Re-initiate expression for the next part of the objective
			for (int n = 0; n < numberOfNurses; n++){ // For each nurse
				for (int d = 0; d < numberOfDays; d++){ // For each day
					unitTotal = cplex.numExpr();
					for (int s = 0; s < numberOfShifts; s++){ // For each shift
						if(employeeReq[d][s][n] == -999){
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								unitTotal = cplex.sum(unitTotal, x[n][d][s][u]);
							}
						}
					}
					expr = cplex.sum(expr, cplex.prod(weightDayOff, unitTotal));
				}
			}
			objective = cplex.sum(objective,expr); // Update the objective function

			System.out.println("END OF OBJECTIVE...");

			// ====================================================================
			// ========================= SOLVE MODEL ==============================
			// ====================================================================

			cplex.addMinimize(objective); // Minimize the objective value

			//cplex.setParam(IloCplex.Param.Preprocessing.Presolve.PreInd, false);
			cplex.setParam(IloCplex.Param.MIP.Tolerances.Integrality, 0.00);
			cplex.setParam(IloCplex.Param.TimeLimit, 100);
			//cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.50);
			//cplex.exportModel("Instance_01.lp");
			long startTime = System.currentTimeMillis();

			if (cplex.solve()){
				System.out.println("Objective value is: " + cplex.getObjValue());
				finalSchedule = new String [numberOfNurses][numberOfDays][numberOfShifts][numberOfUnits];
				for (int n = 0; n < numberOfNurses; n++){ // For each nurse
					for (int d = 0; d < numberOfDays; d++){ // For each day
						for (int s = 0; s < numberOfShifts; s++){ // For each shift
							for (int u = 0; u < numberOfUnits; u++){ // For each unit
								if(cplex.getValue(x[n][d][s][u])==1){
									finalSchedule[n][d][s][u] = Integer.toString(u);
								}else{
									if(employeeReq[d][s][n]==-333){
										finalSchedule[n][d][s][u] = "N/A";
									}else{
										finalSchedule[n][d][s][u] = Double.toString(cplex.getValue(x[n][d][s][u]));	
										if(!finalSchedule[n][d][s][u].equalsIgnoreCase("0.0")){
											finalSchedule[n][d][s][u] = Integer.toString(u);
										}
									}
								}
							}
						}
					}
				} 
				//Print.generateExcel(finalSchedule, "finalSchedule"); // Export to excel
				System.out.println("Algorithm Time: " + (System.currentTimeMillis() - startTime)/1000.0);
				System.out.println("MODEL SOLVED SUCCESSFULLY");
			}else{
				System.out.println("Model not solved");
				//				Iterator it = cplex.rangeIterator();
				//				while(it.hasNext()){
				//					IloRange range = (IloRange) it.next();
				//					System.out.println("Constraint: " + range.getName());
				//					IloLinearNumExprIterator it2 =
				//							((IloLinearNumExpr) range.getExpr()).linearIterator();
				//					while (it2.hasNext()) {
				//						System.out.println("\tVariable "
				//								+ it2.nextNumVar().getName()
				//								+ " has coefficient "
				//								+ it2.getValue());
				//					}
				//					  // get range bounds, checking for +/- infinity
				//					  // (allowing for some rounding)
				//					  String lb = (range.getLB() <= Double.MIN_VALUE + 1) ?
				//					              "-infinity" : Double.toString(range.getLB());
				//					  String ub = (range.getUB() >= Double.MAX_VALUE - 1) ?
				//					              "+infinity" : Double.toString(range.getUB());
				//					  System.out.println("\t" + lb + " <= LHS <= " + ub);
				//				}
			}
			//			System.out.println("STOP HERE IN MODEL...");
			cplex.endModel();
			cplex.clearModel();
			cplex.end();
		}catch(IloException exc){
			exc.printStackTrace();
		}
		return finalSchedule;
	}
}