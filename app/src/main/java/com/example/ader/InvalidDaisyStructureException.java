/**
 * 
 */
package com.example.ader;

/**
 * Invalid Daisy Structure Exception should be thrown when the ncc.html has
 * serious errors.
 */
@SuppressWarnings("serial")
public class InvalidDaisyStructureException extends Exception {

	public InvalidDaisyStructureException(String message) {
		super(message);
	}

}
