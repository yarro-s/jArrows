package jArrows.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Arrow;


class Person {
	protected final String name;
	protected final long id;
	
	protected boolean checkPassed;
	
	public boolean isPassed() {
		return checkPassed;
	}
	
	public Person(String name, long id) {
		this.name = name;
		this.id = id;
		this.checkPassed = false;
	}
	
	public Person() {
		this.name = "trespasser";
		this.id = 0;
		this.checkPassed = false;
	}
	
	public Person verifyID() {
		this.checkPassed = id < 255464 && id > 15435;
		return this;
	}
	
	public Person verifyName() {
		this.checkPassed =  name.equalsIgnoreCase("John") 
				|| name.equalsIgnoreCase("Tomy")
				|| name.equalsIgnoreCase("Jane");
		return this;
	}
}

public class TutorialTest {

	@Test
	public void testSimpleTutorialExample() {
		Arrow<Integer, String> numProcessor = 
			Action.of((Integer i) -> 5*i) // at the beginning the type is declared explicitly
				  .join(i -> i/10.5)      // returns double 
				  .join(String::valueOf); // return a string representation
		
		// returns the string of (5*10)/10.5 = "4.762..."
		String procRes = numProcessor.apply(10); 
		
		assertEquals(procRes, String.valueOf(5*10/10.5));
	}
	
	@Test
	public void testOOPTutorialExample() {
		
		Arrow<String, Person> personVerifier = 
			Action.of((String name) ->     // pass forth a new person
						new Person(name, 214653))
				  .split(Person::verifyID, Person::verifyName) // check in parallel
				  .join(p -> p.left.isPassed() && p.right.isPassed() // if both checked - ok
						  ? p.left : new Person());
						 
		Person verifiedPerson1 = personVerifier.apply("Jane");
		Person verifiedPerson2 = personVerifier.apply("Dave");
		
		assertTrue(verifiedPerson1.isPassed());
		assertTrue(!verifiedPerson2.isPassed());
	}

}
