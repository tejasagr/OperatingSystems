/* 
 * Author: 	Tejas Agrawal
 * Date: 	September 19, 2018
 * Lab: 		Two Pass Linker
 * Class: 	Driver
 */

import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;


//main class that drives the program, the two-pass linker
public class Driver {
	static Scanner keyboard = new Scanner(System.in);
	static ArrayList<Module> modules = new ArrayList<Module>();
	static ArrayList<Symbol> symbolTable = new ArrayList<Symbol>();
	static String undefinedSymbol = null;
	static boolean multipleVariablesUsed = false;
	
	//main method that calls on supporting methods
	public static void main (String[] args) {
		int numOfModules = keyboard.nextInt();
		createModules(numOfModules);
		passOne();
		passTwo();
		keyboard.close();
	}
	
	//takes input from user, creates relevant module objects and populates 'modules' array
	private static void createModules(int moduleTotal) {
		int moduleCount = 0;
		
		//while loop that runs until all modules have been processed
		while (moduleCount < moduleTotal) {
			LinkedHashMap<String, Integer> definitionList = readDefinitionList();
			LinkedHashMap<String, ArrayList<Integer>> useList = readUseList();
			String[] instructions = readInstructions();
			
			//create module objects and populate modules ArrayList
			Module module = new Module(definitionList, useList, instructions);
			modules.add(module);
			
			moduleCount++;
		}
	}
	
	//reads definition list input and returns it as a HashMap of strings (keys), int (vals)
	private static LinkedHashMap<String, Integer> readDefinitionList() {
		LinkedHashMap<String, Integer> definitionList = new LinkedHashMap<String, Integer>();
		int definitionCount = keyboard.nextInt();
		
		//loop to read all definitions and add it to the HashMap
		for (int d = 0; d < definitionCount; d++) {
			String symbol = keyboard.next();
			int location = keyboard.nextInt();
			definitionList.put(symbol, location);
		}
		return definitionList;
	}
	
	//reads use list input and returns it as a HashMap of strings (keys), int arrays (vals)
	private static LinkedHashMap<String, ArrayList<Integer>> readUseList() {
		LinkedHashMap<String, ArrayList<Integer>> useList = new LinkedHashMap<String, ArrayList<Integer>>();
		int useListCount = keyboard.nextInt();
		
		//loop to read all use list pairs and add it to the HashMap
		for (int u = 0; u < useListCount; u++) {
			String extSymbol = keyboard.next();
			ArrayList<Integer> relAddressUsed = new ArrayList<Integer>();
			
			int num = keyboard.nextInt();
			
			//all addresses used are stored in an ArrayList
			while (num != -1) {
				relAddressUsed.add(num);
				num = keyboard.nextInt();
			}
			useList.put(extSymbol, relAddressUsed);
		}
		return useList;
	}
	
	//reads instructions input and returns it as an int array
	private static String[] readInstructions() {
		String[] instructions = new String[keyboard.nextInt()];
		
		//instructions array is populated one by one
		for (int i = 0; i < instructions.length; i++)
			instructions[i] = keyboard.next();
		
		return instructions;
	}
	
	//the first pass of the linker, sets base addresses for modules and sets up symbol table
	private static void passOne() {
		//the base address is a cumulative sum of the instructions of each module
		int currentBaseAddress = 0;
		
		//iterate through modules
		for (int m = 0; m < modules.size(); m++) {
			Module module = modules.get(m);
			
			//set module base address as the cumulative base address value
			module.baseAddress = currentBaseAddress;
			
			//construct symbol table from the definition lists of each module
			for (Map.Entry<String, Integer> definition : module.definitionList.entrySet()) {
				String symbolName = definition.getKey();
				int relAddress = definition.getValue();
				boolean adjustedAddress = false;
				
				//if the relative address is greater than the module size
				if (relAddress >= module.instructions.length) {
					//make it the upper bound
					relAddress = module.instructions.length-1;
					adjustedAddress = true;
				}
				
				int absAddress = relAddress + module.baseAddress;
				
				boolean wasDuplicate = false;
				//check for multiple definitions
				for (Symbol symbol : symbolTable) {
					//found duplicate, update information
					if (symbolName.equals(symbol.label)) {
						wasDuplicate = true;
						symbol.hadDuplicate = true;
						symbol.value = absAddress;
						symbol.moduleNumber = m;
						symbol.adjustedAddress = adjustedAddress;
					}
				}
				//only create a new symbol if it's not a duplicate
				if (!wasDuplicate) {
					Symbol newSymbol = new Symbol(symbolName, absAddress, m, adjustedAddress);
					symbolTable.add(newSymbol);
				}
			}
			//base address increments by the length of module just dealt with
			currentBaseAddress += module.instructions.length;
		}
		
		//print the symbol table
		System.out.println("Symbol Table");
		for (Symbol symbol : symbolTable) {
			System.out.print(symbol.label + "=" + symbol.value);
			if (symbol.hadDuplicate)
				System.out.print(" Error: This variable is multiply defined; last value used.");
			if (symbol.adjustedAddress)
				System.out.print(" Error: Definition exceeds module size; last word in module used.");
			
			System.out.println();
		}
		
		System.out.println();
	}

	//the second pass of the linker, calculates the result value and prints it
	private static void passTwo() {
		System.out.println("Memory Map");
		
		//iterate through each module's instructions
		for (Module module : modules) {
			for (int i = 0; i < module.instructions.length; i++) {
				String opcode = module.instructions[i].substring(0, 1);
				int addressField = Integer.parseInt(module.instructions[i].substring(1, 4));
				int addressType = Integer.parseInt(module.instructions[i].substring(4));
				boolean absAddressAdjusted = false;
				boolean symbolNotAssigned = false;
				boolean symbolDoesntExist = false;
				
				//handle each instruction differently based on address type
				
				//absolute address
				if( addressType == 2 && addressField > 299) {
					addressField = 299;
					absAddressAdjusted = true;
				}
				
				//relative address
				if ( addressType == 3 ) {
					addressField += module.baseAddress;
					if (addressField > 299) {
						addressField = 299;
						absAddressAdjusted = true;
					}
				}
				
				//external address
				else if ( addressType == 4 )
					addressField = accessSymbolTable(module, i);
				
				//dealing with error cases, value of 111 used
				if (addressField == -1 || addressField == -2) {
					if(addressField == -1)
						symbolNotAssigned = true;
					else if (addressField == -2)
						symbolDoesntExist = true;
					addressField = 111;
				}
					
				
				//print results and relevant errors
				String result = opcode + String.format("%03d", addressField);
				System.out.print((i + module.baseAddress) + ":\t" + result);
				if(absAddressAdjusted)
					System.out.print(" Error: Absolute address exceeds machine size; largest legal value used.");
				if(symbolNotAssigned)
					System.out.print(" Error: Instruction has no symbol mentioned in the use list; 111 used.");
				if(symbolDoesntExist)
					System.out.print(" Error: "+undefinedSymbol+" is not defined; 111 used.");
				if(multipleVariablesUsed) {
					System.out.print(" Error: Multiple variables used in instruction; all but last ignored.");
					multipleVariablesUsed = false; //reset for next use
				}
				System.out.println();
			}
		}
		
		//print out warnings for symbols that were never used
		System.out.println();
		for(Symbol symbol : symbolTable) {
			if(!symbol.hasBeenUsed) {
				System.out.println(	"Warning: " + symbol.label + " was defined in module "
									+ symbol.moduleNumber + " but never used.");
			}
		}
	}

	//method used to access symbol table for external addresses
	private static int accessSymbolTable (Module module, int instrNum) {
		String symbolName = null;
		
		//go through use pairs in current module's user list
		for (Map.Entry<String, ArrayList<Integer>> usePair : module.useList.entrySet()) {
			ArrayList<Integer> instrUsed = usePair.getValue();
			
			//within pair, iterate through instructions used to check which symbol is used
			for (int i = 0; i < instrUsed.size(); i++) {
				if (instrNum == instrUsed.get(i)) {
					if (symbolName != null)
						multipleVariablesUsed = true;
					symbolName = usePair.getKey();
				}
			}
			
		}
		
		if (symbolName == null) {
			//we never found the symbolName case, return error
			//this means that there was no symbol assigned in the use list
			return -1;
		}
		
		//we did find the symbolName case
		else {
			for (Symbol symbol : symbolTable) {
				//symbol located and return the value
				if (symbolName.equals(symbol.label)) {
					symbol.hasBeenUsed = true;
					return symbol.value;
				}
			}
			//symbol was never located, we only reach here if no return happened earlier
			//this means that the symbol was assigned, but it wasn't defined
			undefinedSymbol = symbolName;
			return -2;
		}
	}
}