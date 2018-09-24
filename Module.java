/* 
 * Author: 	Tejas Agrawal
 * Date: 	September 19, 2018
 * Lab: 		Two Pass Linker
 * Class: 	Module
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;

//module class: each object represents one module
public class Module {
	LinkedHashMap<String, Integer> definitionList;
	LinkedHashMap<String, ArrayList<Integer>> useList;
	String[] instructions;
	int baseAddress;
	
	//constructor, variables apart from base address are set, BA set in passOne of driver method
	public Module(LinkedHashMap<String, Integer> dL, LinkedHashMap<String, ArrayList<Integer>> uL,
			String[] insArray) {
		definitionList = dL;
		useList = uL;
		instructions = insArray;
	}
}