import java.io.IOException;
import java.util.Random;

public class PSO {
	// Define all the global variables
	@SuppressWarnings("unused")
	public static String[][][][] run() throws IOException{
		// ==============================================================
		// ====================      PSO      ===========================
		// ==============================================================
		// „„„„„„„„„„„„„„„„„„
		// |||||| DATA |||||| 
		// ””””””””””””””””””
		Random rand = new Random(); // Initiate random number generator
		String [][][][] optimizedSchedule = null, initialSchedule = null, bestGlobalSchedule = null; // initiate schedules
		int particleSize = 1; // Number of particles in the swarm
		int bestGlobalFitness = 0; // Number of particles in the swarm
		int bestGlobalParticle = -1; // Capture the index of the best particle
		Double maxVelocity = 4.0; // Maximum velocity to be used
		Double inertiaWeight = 1.2; // A.k.a. w. Typically between 0.8 and 1.2
		Double cognitionWeight = 1.0; // A.k.a. c1. Suggested to be 2 in the literature.
		Double socialWeight = 2.0; // A.k.a. c2. Suggested to be 2 in the literature.
		int maxIterations = 100; // To exit out from the loop
		String [][][][][] particles = new String [particleSize][Data.numberOfEmployees][Data.numberOfDays][Data.numberOfShifts][Data.numberOfSkillGroups]; // Particle schedule
		String [][][][][] particlesNew = new String [particleSize][Data.numberOfEmployees][Data.numberOfDays][Data.numberOfShifts][Data.numberOfSkillGroups]; // Particle schedule with change
		String [][][][][] particlesBest = new String [particleSize][Data.numberOfEmployees][Data.numberOfDays][Data.numberOfShifts][Data.numberOfSkillGroups]; // Particle best schedules
		boolean[] particleFeasibility = new boolean[particleSize]; // To store the feasibility of each particle
		int[] particleFitness = new int [particleSize]; // // To store the objective value of each particle
		int[] particleBestFitness = new int [particleSize]; // // To store the objective value of each particle
		Double[][][] currentParticleVelocities = new Double[particleSize][Data.numberOfEmployees][Data.numberOfShifts]; // Store current velocities
		try{
			for(int i = 0; i < particleSize; i++){ // For each particle in the swarm
				// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
				// |||||| INITIAL SOLUTION PHASE USING INTEGER PROGRAMMING|||||| 
				// ”””””””””””””””””””””””””””””””””””””””””””””””””””””””””””””
				initialSchedule = null; // Clear the initial schedule
				//Generate a random number for number of employee's to be optimized
				int optimizationSize = rand.nextInt((int) Math.ceil((double) (Data.numberOfEmployees * 0.2))) + 3; // (int) Math.ceil((double) (Data.numberOfEmployees * 0.25)); // Optimize 25 - 50 % of the employees at a time )+ 
				initialSchedule = IP.run(10); // Run the Fix and Relax Algorithm and capture the schedule after its run
				// ============================= Construct particles ============================= 
				for (int n = 0; n < Data.numberOfEmployees; n++){ // For each nurse
					for (int d = 0; d < Data.numberOfDays; d++){ // For each day
						for (int s = 0; s < Data.numberOfShifts; s++){ // For each shift
							for (int u = 0; u < Data.numberOfSkillGroups; u++){ // For each unit
								particles[i][n][d][s][u] = initialSchedule[n][d][s][u]; // Copy the initial schedule to the particle
								particlesNew[i][n][d][s][u] = initialSchedule[n][d][s][u]; // Copy the initial schedule to the particle
								particlesBest[i][n][d][s][u] = initialSchedule[n][d][s][u]; // Copy the initial schedule to the particle
							}
						}
					}
				}
				particleFeasibility[i] = Schedule.verifyFeasibility(initialSchedule, null); // Check feasibility and store
				int[] objectiveValue = Schedule.calculateObjective(initialSchedule, null); // Calculate objective value of the particle
				particleFitness[i] = objectiveValue[Data.numberOfDays]; // Store the objective value
				particleBestFitness[i] = objectiveValue[Data.numberOfDays]; // Store the objective value as the local best for each particle
				// ============================= Capture the best one from the particles =============================
				if(i == 0){ // If it is the first particle
					bestGlobalFitness = particleFitness[i]; // Capture the first particle's fitness as the best fitness
					bestGlobalSchedule = Schedule.copyArrays(initialSchedule); // Copy the initial schedule
					bestGlobalParticle = i; // Capture the index of the best particle
				}else{// Otherwise
					if(particleFitness[i] < bestGlobalFitness){ // If the current fitness is better 
						bestGlobalFitness = particleFitness[i]; // Set as the best fitness
						bestGlobalSchedule = Schedule.copyArrays(initialSchedule); // Copy the initial schedule
						bestGlobalParticle = i; // Capture the index of the best particle
					}
				}
				//Print.generateExcel(initialSchedule, "initialSchedule_" + i + "_" + paricleFitness[i] + "_Feasible_" + particleFeasibility[i]);
				//Print.generateExcel(particles[0], "particle0");
//				System.out.println(Schedule.calculateObjective(particles[0]));
			}
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| THE MAT-HEURISTIC ALGORITHM|||||| 
			// ””””””””””””””””””””””””””””””””””””””””
			for(int it = 0; it < maxIterations; it++){ // Loop until the maximum iteration is reached
				//_____________________________
				// *** Random Day Selection ***
				//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
				int selectedDay = rand.nextInt(particles[0][0].length); // Select a random day
				for(int i = 0; i < particleSize; i++){ // Loop all particles
					//_________________________________
					// *** Encode Current Positions ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					int[][] encodedCurrentPositions = new int[particles[0].length][particles[0][0][0].length]; // List to keep encoded current position data
					int[][] encodedCurrentBestPositions = new int[particles[0].length][particles[0][0][0].length]; // List to keep encoded current best position data
					int[][] encodedCurrentGlobalBestPositions = new int[particles[0].length][particles[0][0][0].length]; // List to keep encoded global best position data
					int[] previousShiftAssignments = new int [particles[0].length]; // To modify the schedule after decoding.
					for (int n = 0; n < particles[0].length; n++){ // For each employee
						int assignedShiftCurrentPosition = -1; // Capture the shift the employee was assigned on that selected day for the current schedule
						int assignedShiftCurrentBestPosition = -1; // Capture the shift the employee was assigned on that selected day for the current best schedule
						int assignedShiftGlobalBestPosition = -1; // Capture the shift the employee was assigned on that selected day for the global best schedule
						for (int s = 0; s < particles[0][0][0].length; s++){ // For each shift
							for (int u = 0; u < particles[0][0][0][0].length; u++){ // For each unit
								if(particles[i][n][selectedDay][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
									assignedShiftCurrentPosition = s; // set the shift
								}
								if(particlesBest[i][n][selectedDay][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
									assignedShiftCurrentBestPosition = s; // set the shift
								}
								if(bestGlobalSchedule[n][selectedDay][s][u].equalsIgnoreCase(Integer.toString(u))){ // If there is an assignment
									assignedShiftGlobalBestPosition = s; // set the shift
								}
							}
						}
						for (int s = 0; s < particles[0][0][0].length; s++){ // For each shift
							if(s == assignedShiftCurrentPosition){ // If that is the shift employee was assigned on the current schedule
								encodedCurrentPositions[n][s] = 1; // Add "1"
							}else{
								encodedCurrentPositions[n][s] = 0; // Add "0"
							}
							if(s == assignedShiftCurrentBestPosition){ // If that is the shift employee was assigned on the current best schedule
								encodedCurrentBestPositions[n][s] = 1; // Add "1"
							}else{
								encodedCurrentBestPositions[n][s] = 0; // Add "0"
							}
							if(s == assignedShiftGlobalBestPosition){ // If that is the shift employee was assigned on the global best schedule
								encodedCurrentGlobalBestPositions[n][s] = 1; // Add "1"
							}else{
								encodedCurrentGlobalBestPositions[n][s] = 0; // Add "0"
							}
						}
						previousShiftAssignments[n] = assignedShiftCurrentPosition; // Keep the assignment to be used during decoding
					}
					//_____________________________
					// *** Calculate Velocities ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					for (int n = 0; n < particles[0].length; n++){ // For each employee
						for (int s = 0; s < particles[0][0][0].length; s++){ // For each shift
							// Calculate current velocities to be used in position calculation
							currentParticleVelocities[i][n][s] = ((it == 0) ? 0 : currentParticleVelocities[i][n][s]) + cognitionWeight * rand.nextDouble() * (encodedCurrentBestPositions[n][s] - encodedCurrentPositions[n][s]) + socialWeight * rand.nextDouble()*(encodedCurrentGlobalBestPositions[n][s] - encodedCurrentPositions[n][s]);
							if(currentParticleVelocities[i][n][s] > maxVelocity){ // If the generated value is greater than the upper limit
								currentParticleVelocities[i][n][s] = maxVelocity; // Set the current velocity as the upper limit
							}
							if(currentParticleVelocities[i][n][s] < (-maxVelocity)){ // If the generated value is less than the lower limit
								currentParticleVelocities[i][n][s] = (-maxVelocity); // Set the current velocity as the lower limit
							}
						}
					}
					//________________________________
					// *** Calculate New Positions ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					for (int n = 0; n < particles[0].length; n++){ // For each employee
						for (int s = 0; s < particles[0][0][0].length; s++){ // For each shift
							if(rand.nextDouble() < (1 / (1 + (Math.exp(-currentParticleVelocities[i][n][s]))))){ // Probabilistically check agains the sigmoid function
								if(particles[i][n][selectedDay][s][0].equalsIgnoreCase("N/A")){ // If it is a vacation day
									encodedCurrentPositions[n][s] = 0; // Set zero
								}else{ // Otherwise
									encodedCurrentPositions[n][s] = 1; // Set one when random number is smaller	
								}
							}else{ // Otherwise
								encodedCurrentPositions[n][s] = 0; // Set zero otherwise
							}
						}
					}
					//_____________________________
					// *** Decode New Positions ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					for (int n = 0; n < particles[0].length; n++){ // For each employee
						// ================= Identify the suitable units for the employee =================
						int[] availableUnits = new int[particles[0][0][0][0].length]; // Capture the units employee can be assigned to keep unit part feasible
						for(int sk = 0; sk < Data.numberOfSkills; sk++){ // Loop all skills of the employee
							if(Data.employees[n][7 + sk] != null){ // if the skill is not empty
								for(int sg = 0; sg < Data.numberOfSkillGroups; sg++){ // Check all skill groups
									availableUnits[sg] = -1; // Initiate value to -1 to start with to eliminate any index confusion
									for(int SGsk = 0; SGsk < Data.numberOfSkills; SGsk++){ // Loop all skills of the skill group
										if(Data.skillGroups[sg][1 + SGsk] != null){ // if the skill in the current skill group is not empty
											if(Data.skillGroups[sg][1 + SGsk].equalsIgnoreCase(Data.employees[n][7 + sk])){ // The values match
												availableUnits[sg] = sg; // Capture the index of the skill group (aka unit) 
											}
										}
									}
								}
							}
						}
						boolean nurseAlreadyAssigned = false; // Eliminate double assignment 
						for (int s = 0; s < particles[0][0][0].length; s++){ // For each shift
							if(encodedCurrentPositions[n][s] == 1){ // If there is a new shift assignment
								if(nurseAlreadyAssigned == false){ // And this is the first shift assignment for that day for the nurse
									if(previousShiftAssignments[n] != -1){ // There was an assignment before
										for (int u = 0; u < particles[0][0][0][0].length; u++){ // For each unit
											particlesNew[i][n][selectedDay][s][u] 	= particles[i][n][selectedDay][previousShiftAssignments[n]][u]; // Set previous shift assignment's unit data
										}
										nurseAlreadyAssigned = true; // Turn the switch on, so that there can't be another assignment
									}else{ // When this is the first ever assignment, shift type must be chosen

										while(true){
											int randomUnit = rand.nextInt(availableUnits.length); // Randomly select the unit to be assigned from possible units
											if(availableUnits[randomUnit] != -1){ // If the unit is a unit employee can be assigned
												for (int u = 0; u < particles[0][0][0][0].length; u++){ // For each unit
													if(availableUnits[randomUnit] == u){ // If that is the right unit to be assigned
														particlesNew[i][n][selectedDay][s][u] = Integer.toString(u); // Zero out the value
													}else{ // Otherwise
														particlesNew[i][n][selectedDay][s][u] = "0.0"; // Zero out the value
													}
												}
												break;
											}
										}
									}
								}
							}else{ // If there is no shift assignment for that shift
								for (int u = 0; u < particles[0][0][0][0].length; u++){ // For each unit
									if(previousShiftAssignments[n] != -1){
										if(!particles[i][n][selectedDay][previousShiftAssignments[n]][u].equalsIgnoreCase("N/A")){ // Make sure it is not an off day already
											particlesNew[i][n][selectedDay][s][u] = "0.0"; // Set the empty value
										}
									}
								}
							}
						}
					}
					//_________________________________
					// *** Fix Infeasible Schedules ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					//Print.generateExcel(particlesNew[i], "startingSchedule");
					//System.out.println(Schedule.verifyFeasibility(particlesNew[i], null));
					//System.out.println(Schedule.calculateObjective(particlesNew[i]));
					int optSize = rand.nextInt((int) Math.ceil((double) (Data.numberOfEmployees * 0.5)))+ (int) Math.ceil((double) (Data.numberOfEmployees * 0.25)); // Optimize 25 - 50 % of the employees at a time
					optimizedSchedule = IP.fix(selectedDay, optSize, particles[i], particlesNew[i]); // Run the Fix and Relax Algorithm and capture the schedule after its run
					particlesNew[i] = Schedule.copyArrays(optimizedSchedule); // Copy optimized schedule into the new particle
					//__________________________________
					// *** Update Personal Best Data ***
					//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
					particleFeasibility[i] = Schedule.verifyFeasibility(particlesNew[i], null); // Evaluate the feasibility of the particle
					if(particleFeasibility[i]){ // If the new particle is feasible
						particles[i] = Schedule.copyArrays(particlesNew[i]); // Update the current particle	
						particleFitness[i] = Schedule.calculateObjective(particles[i]); // Update the new objective value of the particle
						if(particleFitness[i] < particleBestFitness[i]){ // If the new particle's objective value is better than its own best
							particlesBest[i] = Schedule.copyArrays(particles[i]); // Update the particle's best schedule
							particleBestFitness[i] = particleFitness[i]; // Update the fitness of the particle's best fitness
						}
						//________________________________
						// *** Update Global Best Data ***
						//‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
						if(particleFitness[i] < bestGlobalFitness){ // If the particle's objective value even better than the global best objective value
							bestGlobalParticle = i; // Capture the particle as the best global particle
							bestGlobalSchedule = Schedule.copyArrays(particles[i]); // Set the particle's schedule as the best global schedule
							bestGlobalFitness = particleFitness[i]; // Set the fitness of the particle as the best global fitness
							System.out.println("*********************************************************************");
							System.out.println("*************** ITERATION " + it + " ***********************************");
							System.out.println("*********** NEW GLOBAL BEST PARTICLE > " + i + " < (" + bestGlobalFitness + ") ***************");
							System.out.println("*********************************************************************");
						}
					}else{ // Otherwise
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						System.out.println("*********** NEW PARTICLE > " + i + " < IS NOT FEASIBLE***************");
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						break;
					}
//					if(1 != 1){
//						Print.generateExcel(particles[i], "particleCurrent");
//						Print.generateExcel(particlesNew[i], "particleNew");
//						Print.generateExcel(particlesBest[i], "particlesBest");
//						Print.generateExcel(bestGlobalSchedule, "bestGlobalSchedule");
//						
//						System.out.println(Schedule.verifyFeasibility(particles[0], null));
//						System.out.println(Schedule.calculateObjective(particles[0]));
//						
//						System.out.println(Schedule.verifyFeasibility(bestGlobalSchedule, null));
//						System.out.println(Schedule.calculateObjective(bestGlobalSchedule));
//						
//					}
					//System.out.println("WAIT");					
				}
			}
			//System.out.println("WAIT");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return optimizedSchedule;
	}
}