import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
public class Main {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
		String inputFileName = "Instances/Instance_S.xml"; // Define which problem to study
		Data.readInputFile(inputFileName); // Read the problem data
		String [][][][] initialSchedule = null, finalSchedule = null; // initiate schedules throughout the optimization
		finalSchedule = PSO.run(); // Run the Fix and Relax Algorithm and capture the schedule after its run
		boolean isFeasible = Schedule.verifyFeasibility(initialSchedule, null);
		int[] objectiveValue = Schedule.calculateObjective(initialSchedule, null);
		Print.generateExcel(initialSchedule, "initialSchedule");
		System.out.println("((((((((((END OF PROGRAM))))))))))");
	}
}