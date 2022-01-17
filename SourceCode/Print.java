import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
public class Print {
	public static void generateExcel(String[][][][] schedule, String fileName) throws FileNotFoundException, IOException{
		int rowSize = schedule.length;
		int colSize = schedule[0].length;
		int size = schedule[0][0].length;
		int subSize = schedule[0][0][0].length;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("SCHEDULE");
		Font font = workbook.createFont();
		font.setBold(true);
		sheet.setDefaultColumnWidth(2);
		HSSFRow row;
		HSSFCell cell;
		HSSFPalette palette =workbook.getCustomPalette();
		palette.setColorAtIndex((short)30, (byte)242, (byte)242, (byte)242); 
		palette.setColorAtIndex((short)31, (byte)221, (byte)217, (byte)196);
		//Odd Week Cell Style
		HSSFCellStyle headerCellStyle=workbook.createCellStyle();
		headerCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setFont(font);
		//Odd Week Cell Style
		HSSFCellStyle oddWeekCellStyle=workbook.createCellStyle();
		//oddWeekCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
		//oddWeekCellStyle.setFillForegroundColor(palette.getColor(30).getIndex());
		oddWeekCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		oddWeekCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		oddWeekCellStyle.setBorderBottom(BorderStyle.THIN);
		oddWeekCellStyle.setBorderTop(BorderStyle.THIN);
		oddWeekCellStyle.setBorderRight(BorderStyle.THIN);
		oddWeekCellStyle.setBorderLeft(BorderStyle.THIN);
		oddWeekCellStyle.setAlignment(HorizontalAlignment.CENTER);
		//Even Week Cell Style
		HSSFCellStyle evenWeekCellStyle=workbook.createCellStyle();
		//evenWeekCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
		evenWeekCellStyle.setFillForegroundColor(palette.getColor(31).getIndex());
		evenWeekCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		evenWeekCellStyle.setBorderBottom(BorderStyle.THIN);
		evenWeekCellStyle.setBorderTop(BorderStyle.THIN);
		evenWeekCellStyle.setBorderRight(BorderStyle.THIN);
		evenWeekCellStyle.setBorderLeft(BorderStyle.THIN);
		evenWeekCellStyle.setAlignment(HorizontalAlignment.CENTER);
		//Unavailable Cell Style
		HSSFCellStyle UnavailableCellStyle=workbook.createCellStyle();
		UnavailableCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		UnavailableCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		UnavailableCellStyle.setBorderBottom(BorderStyle.THIN);
		UnavailableCellStyle.setBorderTop(BorderStyle.THIN);
		UnavailableCellStyle.setBorderRight(BorderStyle.THIN);
		UnavailableCellStyle.setBorderLeft(BorderStyle.THIN);
		UnavailableCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		
		for (int i = 0; i < rowSize+1; i++){ // For each row
			row = sheet.createRow(i);
			for (int j = 0; j < colSize+2; j++){ // For each cell
				cell = row.createCell(j);
				if(!((i==0)&&(j == 0))){//Skip the first cell
					if(i==0){
						if(j != (colSize + 1)){
							cell.setCellStyle(headerCellStyle);
							if(j%7==1){
								cell.setCellValue("Mo");
							}else if(j%7==2){
								cell.setCellValue("Tu");
							}else if(j%7==3){
								cell.setCellValue("We");
							}else if(j%7==4){
								cell.setCellValue("Th");
							}else if(j%7==5){
								cell.setCellValue("Fr");
							}else if(j%7==6){
								cell.setCellValue("Sa");
							}else if(j%7==0){
								cell.setCellValue("Su");
							}	
						}
					}else{
						if(j == 0){
							cell.setCellValue(Data.employees[i-1][0]);
							cell.setCellStyle(headerCellStyle);
						}else if(j != (colSize + 1)){
							if(((int)(j-1)/7) % 2 == 0){// If it is the odd week
								cell.setCellStyle(oddWeekCellStyle);
							}else{// Otherwise the even week
								cell.setCellStyle(evenWeekCellStyle);
							}
							for (int k = 0; k < size; k++){ // For each cell value
								for (int l = 0; l < subSize; l++){ // For each cell value
								if(!(schedule[i-1][j-1][k][l] == null || schedule[i-1][j-1][k][l].equalsIgnoreCase("0.0") || schedule[i-1][j-1][k][l].equalsIgnoreCase("-0.0"))){
									if(schedule[i-1][j-1][k][l].equalsIgnoreCase("N/A")){
										cell.setCellValue(schedule[i-1][j-1][k][l]);
										cell.setCellStyle(UnavailableCellStyle);
									}else{
										cell.setCellValue(Data.shifts[k][0] + "." + l);
										//cell.setCellValue(schedule[i-1][j-1][k]);
									}
								}
								}
							}
						}
//						else{
//							int assignedNurseCounter = 0;
//							int totalTimePerEmployee = 0;
//							for (int column = 0; column < colSize; column++){ // For each cell
//								for (int k = 0; k < size; k++){ // For each cell value
//									for (int l = 0; l < subSize; l++){ // For each cell value
//									if(!(schedule[i-1][column][k] == null || schedule[i-1][column][k][l].equalsIgnoreCase("0.0") || schedule[i-1][column][k][l].equalsIgnoreCase("-0.0") || schedule[i-1][column][k][l].equalsIgnoreCase("N/A"))){
//										
//										totalTimePerEmployee = totalTimePerEmployee + Integer.parseInt(Data.shifts[Integer.parseInt(schedule[i-1][column][k][l])][3]);
//										
//										assignedNurseCounter = assignedNurseCounter + 1;
//									}
//									}
//								}
//							}
//							//cell.setCellValue(assignedNurseCounter);
//							cell.setCellValue(totalTimePerEmployee);
//						}
					}
				}
			}
		} 
		sheet.setColumnWidth(colSize+1, 2000);
		workbook.write(new FileOutputStream(fileName + ".xls"));
		workbook.close();
		
		
	}
	

}
