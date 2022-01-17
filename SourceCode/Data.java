import java.io.IOException;
import java.lang.reflect.Array;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
public class Data {
	// Define all the global variables
	public static int numberOfDays, numberOfShifts, numberOfEmployees, minRestTime, numberOfSkills, numberOfSkillGroups;
	public static String[] planningHorizon;
	public static String[][] employees;
	public static String[][] shifts, employeeShiftLimit, skills, skillGroups;
	public static int[][][] requirementsEmployee;
	public static int[][][][] requirementsCover;
	public static int[][] shiftsAfterShift;
	public static void readInputFile(String fileNameForXML) throws ParseException{
		int counter = 0; // Counter to be used throughout the code
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fileNameForXML);
			// ====================================================================
			// ======================= PLANNING DATES  ============================
			// ====================================================================
			// This part describes the planning horizon
			NodeList parent = doc.getElementsByTagName("StartDate"); // Read the elements with Tag
			planningHorizon = new String[2]; // Initiate the array and its length
			for(int i = 0; i < parent.getLength(); i++){ // Loop all the items
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					planningHorizon[0] = currentParent.getTextContent(); 
				}
			}
			parent = doc.getElementsByTagName("EndDate"); // Read the elements with Tag
			for(int i = 0; i < parent.getLength(); i++){ // Loop all the items
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					planningHorizon[1] = currentParent.getTextContent(); 
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH); // Define date format
			long tempNumber = dateFormat.parse(planningHorizon[1]).getTime()-dateFormat.parse(planningHorizon[0]).getTime();
			numberOfDays = (int) (tempNumber/(1000 * 60 * 60 * 24) + 1);

			// ====================================================================
			// =========================== SKILLS  ================================
			// ====================================================================
			// This part describes the skill details
			parent = doc.getElementsByTagName("Skills"); // Read the elements with Tag
			//int aykut = parent.getLength();
			skills = null; // Initiate the shift array
			int totalChildren=0;
			for(int i = 0; i < 1; i++){ // There will always be 1 main Skills tag
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getChildNodes();
					// „„„„„„„„„„„„„„„„„„„
					// |||||| SKILL ||||||
					// ”””””””””””””””””””
					if(totalChildren==0){
						totalChildren = currentParent.getElementsByTagName("Skill").getLength();
						numberOfSkills = totalChildren; // Assign total number of shifts
						skills = new String[totalChildren][2]; // Initiate the array and its length
					}
					int skillCounter = 0; // Set the counter to properly assign
					for(int j = 0; j < children.getLength(); j++){// Loop all the items
						counter = 0; // Set the counter to properly assign
						Node currentC = children.item(j); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							if(currentChildren.getNodeName().equalsIgnoreCase("Skill")){ // If the node is the "Skill"
								skills[skillCounter][counter] = currentChildren.getAttribute("ID"); // Get the ID
								counter = counter + 1;
								NodeList grandChildren = currentChildren.getChildNodes();
								// ============================= Color =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										if(currentGrandChildren.getNodeName().equalsIgnoreCase("Label")){ // If the node is the "Color"
											skills[skillCounter][counter] = currentGrandChildren.getTextContent(); // Get the shift name
											counter = counter + 1;
										}
									}
								}
								skillCounter = skillCounter + 1;
							}
						}
					}

				}
			}
			System.out.println("End of Skills...");

			// ====================================================================
			// ======================= SKILLS GROUPS  =============================
			// ====================================================================
			// This part describes the skill details
			parent = doc.getElementsByTagName("SkillGroups"); // Read the elements with Tag
			//int aykut = parent.getLength();
			skillGroups = null; // Initiate the shift array
			totalChildren=0;
			for(int i = 0; i < parent.getLength(); i++){ // There will always be 1 main Skill Groups tag
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getChildNodes();
					// „„„„„„„„„„„„„„„„„„„
					// |||||| SKILL ||||||
					// ”””””””””””””””””””
					if(totalChildren==0){
						totalChildren = currentParent.getElementsByTagName("SkillGroup").getLength();
						numberOfSkillGroups = totalChildren; // Assign total number of shift groups
						skillGroups = new String[totalChildren][currentParent.getElementsByTagName("Skill").getLength()+1]; // Initiate the array and its length
					}
					int skillGroupsCounter = 0; // Set the counter to properly assign
					for(int j = 0; j < children.getLength(); j++){// Loop all the items
						counter = 0; // Set the counter to properly assign
						Node currentC = children.item(j); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							if(currentChildren.getNodeName().equalsIgnoreCase("SkillGroup")){ // If the node is the "Skill Group"
								skillGroups[skillGroupsCounter][counter] = currentChildren.getAttribute("ID"); // Get the ID
								counter = counter + 1;
								NodeList grandChildren = currentChildren.getChildNodes();
								// ============================= Color =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										if(currentGrandChildren.getNodeName().equalsIgnoreCase("Skill")){ // If the node is the "Skill"
											skillGroups[skillGroupsCounter][counter] = currentGrandChildren.getTextContent(); // Get the shift name
											counter = counter + 1;
										}
									}
								}
								skillGroupsCounter = skillGroupsCounter + 1;
							}
						}
					}

				}
			}
			System.out.println("End of Skill Groups...");

			// ====================================================================
			// =========================== SHIFTS  ================================
			// ====================================================================
			// This part describes the shift details
			parent = doc.getElementsByTagName("ShiftTypes"); // Read the elements with Tag
			shifts = null; // Initiate the shift array
			totalChildren=0;
			for(int i = 0; i < parent.getLength(); i++){ // Loop all the items
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getChildNodes();
					// „„„„„„„„„„„„„„„„„„„
					// |||||| SHIFT ||||||
					// ”””””””””””””””””””
					if(totalChildren==0){
						totalChildren = currentParent.getElementsByTagName("Shift").getLength();
						numberOfShifts = totalChildren; // Assign total number of shifts
						shifts = new String[totalChildren][4]; // Initiate the array and its length
					}
					int shiftCounter = 0; // Set the counter to properly assign
					for(int j = 0; j < children.getLength(); j++){// Loop all the items
						counter = 0; // Set the counter to properly assign
						Node currentC = children.item(j); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							if(currentChildren.getNodeName().equalsIgnoreCase("Shift")){ // If the node is the "Shift"
								shifts[shiftCounter][counter] = currentChildren.getAttribute("ID"); // Get the shift name
								counter = counter + 1;
								NodeList grandChildren = currentChildren.getChildNodes();
								// ============================= Color =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										if(currentGrandChildren.getNodeName().equalsIgnoreCase("Color")){ // If the node is the "Color"
											shifts[shiftCounter][counter] = currentGrandChildren.getTextContent(); // Get the shift name
											counter = counter + 1;
										}
									}
								}
								// ============================= Start Time =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										if(currentGrandChildren.getNodeName().equalsIgnoreCase("StartTime")){ // If the node is the "StartTime"
											shifts[shiftCounter][counter] = currentGrandChildren.getTextContent().split(":")[0]+ ":00"; // Get the shift name

											counter = counter + 1;
										}
									}
								}
								// ============================= Duration =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										if(currentGrandChildren.getNodeName().equalsIgnoreCase("Duration")){ // If the node is the "Duration"
											shifts[shiftCounter][counter] = currentGrandChildren.getTextContent(); // Get the shift name
											counter = counter + 1;
										}
									}
								}
								shiftCounter = shiftCounter + 1;
							}
						}
					}

				}
			}
			System.out.println("End of Shifts...");
			// ====================================================================
			// ========================= EMPLOYEES  ===============================
			// ====================================================================
			parent = doc.getElementsByTagName("Contract"); // Read the elements with Tag
			totalChildren = parent.getLength(); // Counter to identify number of elements
			numberOfEmployees = totalChildren-1;
			employees = new String[numberOfEmployees][7 + numberOfSkills];; // Initiate the shift array
			counter = 0;
			employeeShiftLimit = new String[numberOfEmployees][numberOfShifts];
			for(int i = 0; i < parent.getLength(); i++){ // Loop all the items
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					if(!currentParent.getAttribute("ID").equalsIgnoreCase("All")){ // Ignore value All
						employees[counter][0] = currentParent.getAttribute("ID");
						NodeList children = currentParent.getChildNodes();				
						// „„„„„„„„„„„„„„„„„„„„
						// |||||| Limits ||||||
						// ””””””””””””””””””””
						for(int j = 0; j < children.getLength(); j++){// Loop all the items
							Node currentC = children.item(j); // The first element is the current child
							if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
								Element currentChildren = (Element) currentC;  // Cast it
								if(currentChildren.getNodeName().equalsIgnoreCase("MaxTot")){ // If the node is the "MaxTot"
									for(int s = 0; s < numberOfShifts; s++){											
										if(currentChildren.getAttribute("shift").equalsIgnoreCase(shifts[s][0])){
											employeeShiftLimit[counter][s] = currentChildren.getAttribute("value");
										}
									}
								}else if(currentChildren.getNodeName().equalsIgnoreCase("ValidShifts")){ // If the node is the "MaxTot"
									for(int s = 0; s < numberOfShifts; s++){	
										if(currentChildren.getAttribute("shift").contains(shifts[s][0])){
											if(employeeShiftLimit[counter][s] == null){
												employeeShiftLimit[counter][s] =  String.valueOf(numberOfDays);
											}
										}else{
											employeeShiftLimit[counter][s] =  "0";
										}
										if(currentChildren.getAttribute("shift").equalsIgnoreCase(shifts[s][0])){
											employeeShiftLimit[counter][s] = currentChildren.getAttribute("value");
										}
									}
								}else if(currentChildren.getNodeName().equalsIgnoreCase("MaxSeq")){ // If the node is the "MinSeq"
									employees[counter][1] = currentChildren.getAttribute("value");
								}else if(currentChildren.getNodeName().equalsIgnoreCase("MinSeq") &&(currentChildren.getAttribute("shift").equalsIgnoreCase("$"))){ // If the node is the "MinSeq"
									employees[counter][2] = currentChildren.getAttribute("value");
								}else if(currentChildren.getNodeName().equalsIgnoreCase("MinSeq") &&(currentChildren.getAttribute("shift").equalsIgnoreCase("-"))){ // If the node is the "MinSeq"
									employees[counter][3] = currentChildren.getAttribute("value");
								}else if(currentChildren.getNodeName().equalsIgnoreCase("WorkLoad")){ // If the node is the "Count"
									NodeList grandChildren = currentChildren.getElementsByTagName("Count");	
									int tess = grandChildren.getLength();
									for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
										Node currentGC = grandChildren.item(k); // The first element is the current child
										if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
											Element currentGrandChildren = (Element) currentGC;  // Cast it
											employees[counter][4+k] = currentGrandChildren.getTextContent();
										}
									}
								}else if(currentChildren.getNodeName().equalsIgnoreCase("Patterns")){ // If the node is the "Patterns"
									NodeList grandChildren = currentChildren.getElementsByTagName("Count");	
									for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
										Node currentGC = grandChildren.item(k); // The first element is the current child
										if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
											Element currentGrandChildren = (Element) currentGC;  // Cast it
											employees[counter][6+k] = currentGrandChildren.getTextContent();
										}
									}
								}
							}
						}
						counter = counter + 1; // Increse the counter
					}else{
						NodeList children = currentParent.getElementsByTagName("MinRestTime");	
						for(int j = 0; j < children.getLength(); j++){// Loop all the items
							Node currentC = children.item(j); // The first element is the current child
							if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
								Element currentChildren = (Element) currentC;  // Cast it
								minRestTime = Integer.parseInt(currentChildren.getTextContent());
							}
						}
					}

				}
			}
			for(int i = 0; i < numberOfEmployees; i++){
				if(employees[i][2]==null){
					employees[i][2] = "0";
				}
			}

			// This part describes the skill details of employees

			parent = doc.getElementsByTagName("Employees"); // Read the elements with Tag
			for(int i = 0; i < parent.getLength(); i++){ // Loop all the items
				Node currentP = parent.item(i);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getChildNodes();
					// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
					// |||||| Employees Skill Details ||||||
					// ”””””””””””””””””””””””””””””””””””””
					counter = 0;
					for(int j = 0; j < children.getLength(); j++){// Loop all the items
						counter = 0; // Set the counter to properly assign
						Node currentC = children.item(j); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							if(currentChildren.getNodeName().equalsIgnoreCase("Employee")){ // If the node is the "Shift"
								// ============================= Identify the current employee =============================
								int currentEmployee = 0;
								for(int l = 0; l < numberOfEmployees; l++){
									if(currentChildren.getAttribute("ID").equalsIgnoreCase(employees[l][0])){
										currentEmployee = l;
									}
								}
								NodeList grandChildren = currentChildren.getElementsByTagName("Skill");
								// ============================= Skill =============================
								for(int k = 0; k < grandChildren.getLength(); k++){// Loop all the items
									Node currentGC = grandChildren.item(k); // The first element is the current child
									if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
										Element currentGrandChildren = (Element) currentGC;  // Cast it
										employees[currentEmployee][7 + counter] = currentGrandChildren.getTextContent(); // Get the shift name
//										for(int sg = 0; sg < numberOfSkillGroups; sg++){
//											for(int sk = 0; sk < numberOfSkillGroups; sk++){
//												if(skillGroups[sg][sk].equalsIgnoreCase(currentGrandChildren.getTextContent())){
//													employees[currentEmployee][7 + counter] = skillGroups[sg][0]; // Get the shift name
//												}
//											}
//										}
										counter = counter + 1;
									}
								}
							}
						}
					}
				}
			}
			System.out.println("End of Employees...");
			// ======================================================================================
			// =========================== REQUIREMENTS / REQUESTS  =================================
			// ======================================================================================
			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Fixed Assignments ||||||
			// ”””””””””””””””””””””””””””””””
			String tempMatrixFixedAssign[][] = null;
			parent = doc.getElementsByTagName("FixedAssignments"); // Read the elements with Tag
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getElementsByTagName("EmployeeID");
					tempMatrixFixedAssign = new String[children.getLength()][2];
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixFixedAssign[y][0] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Day"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixFixedAssign[y][1] = currentChildren.getTextContent();
						}
					}
				}
			}
			System.out.println("End of Fixed Assignments...");
			// „„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Day On ||||||
			// ”””””””””””””””””””””””
			String tempMatrixDayOn[][] = null;
			parent = doc.getElementsByTagName("DayOn"); // Read the elements with Tag
			tempMatrixDayOn = new String[parent.getLength()][3];
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					tempMatrixDayOn[x][0] = currentParent.getAttribute("weight");
					NodeList children = currentParent.getElementsByTagName("EmployeeID");
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixDayOn[x][1] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Day"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixDayOn[x][2] = currentChildren.getTextContent();
						}
					}
				}
			}
			System.out.println("End of Day On...");
			// „„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Day Off ||||||
			// ”””””””””””””””””””””””
			String tempMatrixDayOff[][] = null;
			parent = doc.getElementsByTagName("DayOff"); // Read the elements with Tag
			tempMatrixDayOff = new String[parent.getLength()][3];
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					tempMatrixDayOff[x][0] = currentParent.getAttribute("weight");
					NodeList children = currentParent.getElementsByTagName("EmployeeID");
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixDayOff[x][1] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Day"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixDayOff[x][2] = currentChildren.getTextContent();
						}
					}
				}
			}
			System.out.println("End of Day Off...");
			// „„„„„„„„„„„„„„„„„„„„„„
			// |||||| Shift On ||||||
			// ””””””””””””””””””””””
			String tempMatrixShiftOn[][] = null;
			parent = doc.getElementsByTagName("ShiftOn"); // Read the elements with Tag
			tempMatrixShiftOn = new String[parent.getLength()][4];
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					tempMatrixShiftOn[x][0] = currentParent.getAttribute("weight");
					NodeList children = currentParent.getElementsByTagName("EmployeeID");
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOn[x][1] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Day"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOn[x][2] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Shift"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOn[x][3] = currentChildren.getTextContent();
						}
					}
				}
			}
			System.out.println("End of Shift On...");
			// „„„„„„„„„„„„„„„„„„„„„„„
			// |||||| Shift Off ||||||
			// ”””””””””””””””””””””””
			String tempMatrixShiftOff[][] = null;
			parent = doc.getElementsByTagName("ShiftOff"); // Read the elements with Tag
			tempMatrixShiftOff = new String[parent.getLength()][4];
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					tempMatrixShiftOff[x][0] = currentParent.getAttribute("weight");
					NodeList children = currentParent.getElementsByTagName("EmployeeID");
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOff[x][1] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Day"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOff[x][2] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Shift"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixShiftOff[x][3] = currentChildren.getTextContent();
						}
					}
				}
			}
			System.out.println("End of Shift Off...");
			// „„„„„„„„„„„„„„„„„„„
			// |||||| Cover ||||||
			// ”””””””””””””””””””
			String tempMatrixCover[][] = null;
			parent = doc.getElementsByTagName("DateSpecificCover"); // Read the elements with Tag
			tempMatrixCover = new String[doc.getElementsByTagName("Cover").getLength()][7 + numberOfSkills];
			counter = 0;
			for(int x = 0; x < parent.getLength(); x++){ // Loop all the items
				Node currentP = parent.item(x);
				if(currentP.getNodeType()==Node.ELEMENT_NODE){ // Make sure it is an element node
					Element currentParent = (Element) currentP;  // Cast it
					NodeList children = currentParent.getElementsByTagName("Shift");
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixCover[counter + x + y][0] = String.valueOf(x);
							tempMatrixCover[counter + x + y][1] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Min"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixCover[counter + x + y][2] = currentChildren.getTextContent();
							tempMatrixCover[counter + x + y][3] = currentChildren.getAttribute("weight");
						}
					}
					children = currentParent.getElementsByTagName("Max"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixCover[counter + x + y][4] = currentChildren.getTextContent();
							tempMatrixCover[counter + x + y][5] = currentChildren.getAttribute("weight");
						}
					}
					children = currentParent.getElementsByTagName("SkillGroup"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							tempMatrixCover[counter + x + y][6] = currentChildren.getTextContent();
						}
					}
					children = currentParent.getElementsByTagName("Cover"); 
					for(int y = 0; y < children.getLength(); y++){// Loop all the items
						Node currentC = children.item(y); // The first element is the current child
						if(currentC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
							Element currentChildren = (Element) currentC;  // Cast it
							//tempMatrixCover[counter + x][7 + y] = currentChildren.getTextContent();

							NodeList grandChildren = currentChildren.getElementsByTagName("Skill");	
							//int tess = grandChildren.getLength();
							for(int z = 0; z < grandChildren.getLength(); z++){// Loop all the items
								Node currentGC = grandChildren.item(z); // The first element is the current child
								if(currentGC.getNodeType()==Node.ELEMENT_NODE){// Make sure it is an element node
									Element currentGrandChildren = (Element) currentGC;  // Cast it
									tempMatrixCover[counter + x + y][7+z] = currentGrandChildren.getTextContent();
								}
							}


						}
					}
					counter = counter + children.getLength() - 1;
				}
			}
			System.out.println("End of Cover...");

			// „„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| SHIFT & EMPLOYEE REST ||||||
			// ”””””””””””””””””””””””””””””””””””

			shiftsAfterShift = new int[numberOfShifts][numberOfShifts];

			for(int i = 0; i < numberOfShifts; i++){ // Loop shifts
				String myTime = shifts[i][2]; // Capture the start time of a shift
				SimpleDateFormat df = new SimpleDateFormat("HH:mm");
				Date d = df.parse(myTime); 
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				cal.add(Calendar.MINUTE, Integer.parseInt(shifts[i][3]) + minRestTime);
				String nextShiftCanStart = df.format(cal.getTime());


				Date nextShiftCannStart = df.parse(nextShiftCanStart); 


				for(int j = 0; j < numberOfShifts; j++){ // Loop shifts
					Date nextShiftStart = df.parse(shifts[j][2]); 
					if(nextShiftStart.equals(nextShiftCannStart) || nextShiftStart.after(nextShiftCannStart)){
						shiftsAfterShift[i][j] = 1;
					}else{
						shiftsAfterShift[i][j] = 0;
					}
				}
			}

			System.out.println("End of Shifts after a Shift...");



			// „„„„„„„„„„„„„„„„„„„„„„„„„
			// |||||| COMBINE ALL ||||||
			// ”””””””””””””””””””””””””
			int coverSize = 5 + numberOfSkills;
			requirementsCover = new int[numberOfDays][numberOfShifts][numberOfSkillGroups][coverSize];
			requirementsEmployee = new int[numberOfDays][numberOfShifts][numberOfEmployees];
			for(int i = 0; i < numberOfDays; i++){ // Loop days
				for(int j = 0; j < numberOfShifts; j++){ // Loop shifts
					// ============================= Shift Level =============================
					// === Covers ===
					for(int k = 0; k < numberOfSkillGroups; k++){
					for(int x = 0; x < tempMatrixCover.length; x++){
						
							if(tempMatrixCover[x][0].equalsIgnoreCase(String.valueOf(i)) && tempMatrixCover[x][1].equalsIgnoreCase(shifts[j][0]) && tempMatrixCover[x][6].equalsIgnoreCase(skillGroups[k][1])){
								requirementsCover[i][j][k][0] = Integer.parseInt(tempMatrixCover[x][2]);
								requirementsCover[i][j][k][1] = Integer.parseInt(tempMatrixCover[x][3]);
								requirementsCover[i][j][k][2] = Integer.parseInt(tempMatrixCover[x][4]);
								requirementsCover[i][j][k][3] = Integer.parseInt(tempMatrixCover[x][5]);
								requirementsCover[i][j][k][4] = Integer.parseInt(tempMatrixCover[x][6]);
								for(int y = 0; y < numberOfSkills; y++){
									if(tempMatrixCover[x][7 + y] != null){
										requirementsCover[i][j][k][5 + y] = Integer.parseInt(tempMatrixCover[x][7 + y]); 
									}
								}
							}
						}
					}
					for(int k = 0; k < numberOfEmployees; k++){ // Loop employees
						// ============================= Employee Level =============================
						// === Fixed Assignments ===
						for(int x = 0; x < tempMatrixFixedAssign.length; x++){
							if(tempMatrixFixedAssign[x][0].equalsIgnoreCase(employees[k][0]) && tempMatrixFixedAssign[x][1].equalsIgnoreCase(String.valueOf(i))){
								requirementsEmployee[i][j][k] = -333; 
							}
						}
						// === Day On Request ===
						for(int x = 0; x < tempMatrixDayOn.length; x++){
							if(tempMatrixDayOn[x][1].equalsIgnoreCase(employees[k][0]) && tempMatrixDayOn[x][2].equalsIgnoreCase(String.valueOf(i))){
								requirementsEmployee[i][j][k] = 999; 
							}
						}
						// === Day Off Request ===
						for(int x = 0; x < tempMatrixDayOff.length; x++){
							if(tempMatrixDayOff[x][1].equalsIgnoreCase(employees[k][0]) && tempMatrixDayOff[x][2].equalsIgnoreCase(String.valueOf(i))){
								requirementsEmployee[i][j][k] = -999;
							}
						}
						// === Shift On Request ===
						for(int x = 0; x < tempMatrixShiftOn.length; x++){
							if(tempMatrixShiftOn[x][1].equalsIgnoreCase(employees[k][0]) && tempMatrixShiftOn[x][2].equalsIgnoreCase(String.valueOf(i)) && tempMatrixShiftOn[x][3].equalsIgnoreCase(shifts[j][0])){
								requirementsEmployee[i][j][k] = Integer.parseInt(tempMatrixShiftOn[x][0]); 
							}
						}
						// === Shift Off Request ===
						for(int x = 0; x < tempMatrixShiftOff.length; x++){
							if(tempMatrixShiftOff[x][1].equalsIgnoreCase(employees[k][0]) && tempMatrixShiftOff[x][2].equalsIgnoreCase(String.valueOf(i)) && tempMatrixShiftOff[x][3].equalsIgnoreCase(shifts[j][0])){
								requirementsEmployee[i][j][k] = Integer.parseInt(tempMatrixShiftOff[x][0]) * -1;
							}
						}
					}
				}
			}
			//employeeShiftLimit[5][0] = "1"; 
			System.out.println("End of Data Read...");
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pe) {
			pe.printStackTrace();
		}
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//		}
	}
}
