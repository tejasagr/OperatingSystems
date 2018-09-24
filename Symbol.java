/* 
 * Author: 	Tejas Agrawal
 * Date: 	September 19, 2018
 * Lab: 		Two Pass Linker
 * Class: 	Symbol
 */

package linker;

//symbol class, represents one symbol of the symbol table
public class Symbol {
	String label;
	int value, moduleNumber;
	boolean hasBeenUsed, hadDuplicate, adjustedAddress;
	
	public Symbol(String lab, int val, int modNum, boolean adjAdd) {
		//symbol name and absolute address definition
		label = lab;
		value = val;
		
		//these are for error checking purposes
		moduleNumber = modNum;
		hasBeenUsed = false;
		hadDuplicate = false;
		adjustedAddress = adjAdd;
	}
}