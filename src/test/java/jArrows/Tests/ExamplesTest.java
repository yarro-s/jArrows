package jArrows.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Arrow;



public class ExamplesTest {

	@Test
	public void testSimpleChainedActions() {
		Arrow<Integer, String> act1 = 
				Action.of((Integer i) -> i*5)
					  .join(i -> (i > 10) ? i/8.5 : i*7.5)
					  .join(i -> (i > 5) ? "Big" : "Small");
		
		assertEquals(act1.apply(3), "Small");
		assertEquals(act1.apply(1), "Big");
	}
	
	@Test
	public void testActionWithClonedInputs() {
		Arrow<String, String> act1 = 
				Action.of((String i) -> i+", World!")
					  .join(i -> i+" Weee...")
					  .cloneInput(i -> 5.5)
					  .join(p -> 
					  		p.left() +" ^^ "+ String.valueOf(p.right()));
		
		assertEquals("Hello, World! Weee... ^^ 5.5", act1.apply("Hello"));
	}

}
